// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import arcbasis._ast.*;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import cdlangextension._ast.ASTCDEImportStatement;
import cdlangextension._symboltable.CDEImportStatementSymbol;
import cdlangextension._symboltable.DepLanguageSymbol;
import cdlangextension._symboltable.ICDLangExtensionScope;
import clockcontrol._ast.ASTCalculationInterval;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import conditionbasis._ast.ASTCondition;
import conditioncatch._ast.ASTConditionCatch;
import de.monticore.ast.ASTNode;
import de.monticore.cd4code._symboltable.CD4CodeScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import genericarc._ast.ASTArcTypeParameter;
import montiarc._ast.ASTArcSync;
import montiarc._ast.ASTArcTiming;
import montithings._ast.*;
import montithings._symboltable.MontiThingsArtifactScope;
import montithings._visitor.MontiThingsPrettyPrinterDelegator;
import montithings.generator.codegen.ConfigParams;
import montithings.generator.codegen.util.Utils;
import montithings.generator.visitor.GuardExpressionVisitor;
import montithings.generator.visitor.NoDataComparisionsVisitor;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import montithings.util.GenericBindingUtil;
import org.apache.commons.lang3.tuple.Pair;
import portextensions._ast.ASTAnnotatedPort;
import portextensions._ast.ASTBufferedPort;
import portextensions._ast.ASTSyncStatement;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._ast.ASTPrecondition;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static montithings.generator.helper.TypesHelper.getConversionFactor;
import static montithings.generator.helper.TypesHelper.java2cppTypeString;

/**
 * Helper class used in the template to generate target code of atomic or
 * composed components.
 */
public class ComponentHelper {

  public static List<ImportStatement> getImports(ComponentTypeSymbol symbol) {
    while (symbol.getOuterComponent().isPresent()) {
      symbol = symbol.getOuterComponent().get();
    }
    ASTComponentType ast = symbol.getAstNode();
    return ((MontiThingsArtifactScope) ast.getEnclosingScope()).getImportsList();
  }

  public static String printCPPTypeName(SymTypeExpression expression) {
    if (expression instanceof SymTypeOfNumericWithSIUnit) {
      expression = ((SymTypeOfNumericWithSIUnit) expression).getNumericType();
    }
    return java2cppTypeString(expression.print());
  }

  public static String printCPPTypeName(SymTypeExpression expression, ComponentTypeSymbol comp, ConfigParams config) {
    if (expression.getTypeInfo() instanceof CDTypeSymbol) {
      return printCdFQN(comp, expression.getTypeInfo(), config);
    }
    if (expression instanceof SymTypeOfNumericWithSIUnit) {
      expression = ((SymTypeOfNumericWithSIUnit) expression).getNumericType();
    }
    return java2cppTypeString(expression.print());
  }

  /**
   * @return A list of String representations of the actual type arguments
   * assigned to the super component
   */
  public static List<String> getSuperCompActualTypeArguments(ComponentTypeSymbol component) {
    // TODO: Write me
    final List<String> paramList = new ArrayList<>();
    if (component.isPresentParentComponent()) {
      final ComponentTypeSymbol componentSymbolReference = component.getParent();
/*      final List<ActualTypeArgument> actualTypeArgs = componentSymbolReference
        .getActualTypeArguments();
      String componentPrefix = this.component.getFullName() + ".";
      for (ActualTypeArgument actualTypeArg : actualTypeArgs) {
        final String printedTypeArg = SymbolPrinter.printTypeArgument(actualTypeArg);
        if (printedTypeArg.startsWith(componentPrefix)) {
          paramList.add(printedTypeArg.substring(componentPrefix.length()));
        } else {
          paramList.add(printedTypeArg);
        }
      }*/
    }
    return paramList;
  }

  /**
   * Set of components used as generic type argument as include string
   *
   * @param comp     component that gets the new includes
   * @param instance component instance that assigns component to generic
   * @return Set of components used as generic type argument as include string.
   * Is empty if include is not needed.
   */
  public static Set<String> includeGenericComponent(arcbasis._symboltable.ComponentTypeSymbol comp,
    ComponentInstanceSymbol instance) {
    ASTComponentInstantiation instantiation = getInstantiation(instance);
    if (instantiation.getMCType() instanceof ASTMCBasicGenericType) {
      List<ASTMCTypeArgument> types = new ArrayList<>(
        ((ASTMCBasicGenericType) instantiation.getMCType()).getMCTypeArgumentList());
      return includeGenericComponentIterate(comp, instance, types);
    }
    return new HashSet<>();
  }

  public static Set<String> includeGenericComponentIterate(
    arcbasis._symboltable.ComponentTypeSymbol comp, ComponentInstanceSymbol instance,
    List<ASTMCTypeArgument> types) {
    HashSet<String> result = new HashSet<>();
    for (int i = 0; i < types.size(); i++) {
      String typeName = printTypeArgument(types.get(i));
      ComponentTypeSymbol boundComponent = GenericBindingUtil.getComponentFromString(
        GenericBindingUtil
          .getEnclosingMontiArcArtifactScope((MontiThingsArtifactScope) comp.getEnclosingScope()),
        typeName);
      if (boundComponent != null) {
        result.add(getPackagePath(comp, boundComponent) + typeName);
        if (types.get(i) instanceof ASTMCBasicGenericType) {
          result.addAll(includeGenericComponentIterate(comp, instance,
            ((ASTMCBasicGenericType) types.get(i)).getMCTypeArgumentList()));
        }
      }
    }
    return result;
  }

  public static boolean portUsesCdType(PortSymbol portSymbol) {
    return portSymbol.getTypeInfo() instanceof CDTypeSymbol;
  }

  public static String printCdPortFQN(ComponentTypeSymbol componentSymbol,
    PortSymbol portSymbol, ConfigParams config) {
    if (!portUsesCdType(portSymbol)) {
      throw new IllegalArgumentException(
        "Can't print namespace of non-CD type " + portSymbol.getType().getTypeInfo().getFullName());
    }
    TypeSymbol cdTypeSymbol = portSymbol.getType().getTypeInfo();
    return printCdFQN(componentSymbol, cdTypeSymbol, config);
  }

  public static String printCdFQN(ComponentTypeSymbol componentSymbol,
    TypeSymbol typeSymbol, ConfigParams config) {
    String packageName = ((CD4CodeScope)typeSymbol.getEnclosingScope()).getPackageName();
    String typeName = typeSymbol.getName();
    return packageName.replaceAll("\\.", "::") + "::" + typeName;
  }

  /**
   * Gets the c++ import statement for a given port type if available.
   *
   * @param portSymbol port using a class diagram type.
   * @param config     config containing a cdlangextension, that is used to search for import statements.
   * @return c++ import statement of the port type if specified in the cde model. Otherwise empty.
   */
  public static Optional<ASTCDEImportStatement> getCDEReplacement(PortSymbol portSymbol,
    ConfigParams config) {
    if (!portUsesCdType(portSymbol)) {
      return Optional.empty();
    }
    TypeSymbol typeSymbol = portSymbol.getTypeInfo();
    return getCDEReplacement(typeSymbol, config);
  }

  public static Optional<ASTCDEImportStatement> getCDEReplacement(TypeSymbol typeSymbol,
    ConfigParams config) {
    ICDLangExtensionScope scope = config.getCdLangExtensionScope();

    if (scope != null && typeSymbol instanceof CDTypeSymbol) {
      Optional<CDEImportStatementSymbol> cdeImportStatementSymbol = scope
        .resolveASTCDEImportStatement("Cpp", (CDTypeSymbol) typeSymbol);
      if (cdeImportStatementSymbol.isPresent() && cdeImportStatementSymbol.get()
        .isPresentAstNode()) {
        return Optional.of(cdeImportStatementSymbol.get().getAstNode());
      }
    }
    return Optional.empty();
  }

  /**
   * Determines the name of the type of the port represented by its symbol. This
   * takes in to account whether the port is inherited and possible required
   * renamings due to generic type parameters and their actual arguments.
   *
   * @param comp       Symbol of the component which contains the port
   * @param portSymbol Symbol of the port for which the type name should be
   *                   determined.
   * @return The String representation of the type of the port.
   */
  public static String getRealPortTypeString(ComponentTypeSymbol comp, PortSymbol portSymbol) {
    if(portSymbol.getType() instanceof SymTypeOfNumericWithSIUnit){
      return ((SymTypeOfNumericWithSIUnit) portSymbol.getType()).getNumericType().getTypeInfo().getName();
    }
    else {
      // TODO: call portSymbol.getType().print() once MontiArc fixes its types
      //return portSymbol.getType().print();
      return portSymbol.getType().getTypeInfo().getName();
    }
  }

  public static boolean isInterfaceComponent(ComponentTypeSymbol comp) {
    if (comp.getAstNode() instanceof ASTMTComponentType) {
      ASTMTComponentType astmtComponentType = (ASTMTComponentType) comp.getAstNode();
      return astmtComponentType.getMTComponentModifier().isInterface();
    }
    return false;
  }

  public static String printPackageNamespaceForComponent(
    arcbasis._symboltable.ComponentTypeSymbol comp) {
    List<String> packages = ComponentHelper.getPackages(comp);
    if (isInterfaceComponent(comp)) {
      return "";
    }
    StringBuilder namespace = new StringBuilder("montithings::");
    for (String packageName : packages) {
      namespace.append(packageName).append("::");
    }
    return namespace.toString();
  }

  private static final HashMap<String, String> PRIMITIVE_TYPES = new HashMap<String, String>() {
    {
      put("int", "Integer");
      put("double", "Double");
      put("boolean", "Boolean");
      put("byte", "Byte");
      put("char", "Character");
      put("long", "Long");
      put("float", "Float");
      put("short", "Short");
    }
  };

  /**
   * Boxes datatype if applicable.
   *
   * @param datatype String representation of the datatype to box.
   * @return The boxed datatype.
   */
  public static String autobox(String datatype) {
    String[] tokens = datatype.split("\\b");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tokens.length; i++) {
      if (PRIMITIVE_TYPES.containsKey(tokens[i])) {
        tokens[i] = autoboxType(tokens[i]);
      }
      sb.append(tokens[i]);
    }
    return sb.toString();
  }

  private static String autoboxType(String datatype) {
    String autoBoxedTypeName = datatype;
    if (PRIMITIVE_TYPES.containsKey(datatype)) {
      autoBoxedTypeName = PRIMITIVE_TYPES.get(datatype);
    }
    return autoBoxedTypeName;
  }

  /**
   * Calculates the values of the parameters of a {@link ComponentInstanceSymbol}.
   * This takes default values for parameters into account and adds them as
   * required. Default values are only added from left to right in order. <br/>
   * Example: For a component with parameters
   * <code>String stringParam, Integer integerParam = 2, Object objectParam = new Object()</code>
   * that is instanciated with parameters <code>"Test String", 5</code> this
   * method adds <code>new Object()</code> as the last parameter.
   *
   * @param param The {@link ComponentInstanceSymbol} for which the parameters
   *              should be calculated.
   * @return The parameters.
   */
  public static List<String> getParamValues(ComponentInstanceSymbol param) {
    List<ASTExpression> configArguments = param.getArguments();
    MontiThingsPrettyPrinterDelegator printer = CppPrettyPrinter.getPrinter();

    List<String> outputParameters = new ArrayList<>();
    for (int i = 0; i < configArguments.size(); i++) {
      SymTypeExpression requiredType = param.getType().getAstNode().getHead().getArcParameter(i).getSymbol().getType();
      String prettyprint;
      if(requiredType instanceof SymTypeOfNumericWithSIUnit){
        prettyprint = Utils.printSIExpression(configArguments.get(i), requiredType);
      }
      else {
        prettyprint = printer.prettyprint(configArguments.get(i));
      }
      outputParameters.add(autobox(prettyprint));
    }

    // Append the default parameter values for as many as there are left
    final List<VariableSymbol> configParameters = param.getType().getParameters();

    // Calculate the number of missing parameters
    int numberOfMissingParameters = configParameters.size() - configArguments.size();

    if (numberOfMissingParameters > 0) {
      // Get the AST node of the component and the list of parameters in the AST
      final ASTComponentType astNode = param.getType().getAstNode();
      final List<ASTArcParameter> parameters = astNode.getHead().getArcParameterList();

      // Retrieve the parameters from the node and add them to the list
      for (int counter = 0; counter < numberOfMissingParameters; counter++) {
        // Fill up from the last parameter
        final ASTArcParameter astParameter = parameters.get(parameters.size() - 1 - counter);
        String prettyprint;
        if(astParameter.getSymbol().getType() instanceof SymTypeOfNumericWithSIUnit){
          prettyprint = Utils.printSIExpression(astParameter.getDefault(), astParameter.getSymbol().getType());
        }
        else {
          prettyprint = printer.prettyprint(astParameter.getDefault());
        }
        outputParameters.add(outputParameters.size() - counter, prettyprint);
      }
    }

    return outputParameters;
  }

  public static String printConstructorArguments(arcbasis._symboltable.ComponentTypeSymbol comp) {
    String result = "";
    MontiThingsPrettyPrinterDelegator printer = CppPrettyPrinter.getPrinter();
    List<ASTArcParameter> parameters = comp.getAstNode().getHead().getArcParameterList();

    for (int i = 0; i < parameters.size(); i++) {
      ASTArcParameter param = parameters.get(i);
      result += java2cppTypeString(printNumericType(param.getSymbol().getType()));
      ;
      result += " ";
      result += param.getName();
      if (param.isPresentDefault()) {
        result += " = ";
        if(param.getSymbol().getType() instanceof SymTypeOfNumericWithSIUnit){
          result += Utils.printSIExpression(param.getDefault(), param.getSymbol().getType());
        }
        else {
          result += printer.prettyprint(param.getDefault());
        }
      }
      if (i < parameters.size() - 1) {
        result += ", ";
      }
    }

    return result;
  }

  public static String printTypeArguments(List<TypeVarSymbol> types) {
    List<String> typeNames = new ArrayList<>();
    for (TypeVarSymbol type : types) {
      typeNames.add(type.getName());
    }
    return String.join(", ", typeNames);
  }

  public static String printASTTypeArguments(List<ASTArcTypeParameter> types) {
    List<String> typeNames = new ArrayList<>();
    for (ASTArcTypeParameter type : types) {
      typeNames.add(type.getName());
    }
    return String.join(", ", typeNames);
  }

  /**
   * Prints a list of actual type arguments.
   *
   * @param typeArguments The actual type arguments to print
   * @return The printed actual type arguments
   */
  public static String printActualTypeArguments(List<ASTMCTypeArgument> typeArguments) {
    if (typeArguments.size() > 0) {
      String result = "<" +
        typeArguments.stream().map(ComponentHelper::printTypeArgumentIterate)
          .collect(Collectors.joining(", ")) +
        ">";
      return result;
    }
    return "";
  }

  /**
   * Prints an actual type argument with sub arguments.
   *
   * @param arg The actual type argument to print
   * @return The printed actual type argument
   */
  public static String printTypeArgumentIterate(ASTMCTypeArgument arg) {
    if (arg instanceof ASTMCBasicGenericType) {
      return printTypeArgument(arg) + printActualTypeArguments(
        ((ASTMCBasicGenericType) arg).getMCTypeArgumentList());
    }
    else {
      return printTypeArgument(arg);
    }
  }

  /**
   * Prints an actual type argument.
   *
   * @param arg The actual type argument to print
   * @return The printed actual type argument
   */
  public static String printTypeArgument(ASTMCTypeArgument arg) {
    if (arg instanceof ASTMCGenericType) {
      return ((ASTMCGenericType) arg).printWithoutTypeArguments();
    }
    return java2cppTypeString(
      arg.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())));
  }

  /**
   * Print the type of the specified subcomponent.
   *
   * @param instance The instance of which the type should be printed
   * @return The printed subcomponent type
   */
  public static String getSubComponentTypeName(
    arcbasis._symboltable.ComponentInstanceSymbol instance) {
    String result = "";
    final ComponentTypeSymbol componentType = instance.getType();

    String packageName = Utils.printPackageWithoutKeyWordAndSemicolon(
      componentType);
    if (!packageName.equals("")) {
      result = packageName + ".";
    }
    result += componentType.getName();
    if (Utils.hasTypeParameter(componentType)) {
      result += printASTTypeArguments(
        Utils.getTypeParameters(componentType));
    }
    return result;
  }

  public static String getSubComponentTypeNameWithoutPackage(
    arcbasis._symboltable.ComponentInstanceSymbol instance,
    ConfigParams config) {
    return getSubComponentTypeNameWithoutPackage(instance, config, true);
  }

  public static String getSubComponentTypeNameWithoutPackage(ComponentInstanceSymbol instance,
    ConfigParams config, boolean printTypeParameters) {
    String result = "";
    final ComponentTypeSymbol componentType = instance.getType();
    //Use the bound name if present.
    Optional<ComponentTypeSymbol> implementation = config.getBinding(instance);
    if (implementation.isPresent()) {
      result += implementation.get().getName();
    }
    else {
      implementation = config.getBinding(componentType);
      if (implementation.isPresent()) {
        result += implementation.get().getName();
      }
      else {
        result += componentType.getName();
      }
    }

    if (Utils.hasTypeParameter(componentType) && printTypeParameters) {
      // format simple component type name to full component type name
      ASTComponentInstantiation instantiation = getInstantiation(instance);
      if (instantiation.getMCType() instanceof ASTMCBasicGenericType) {
        List<ASTMCTypeArgument> types = new ArrayList<>(
          ((ASTMCBasicGenericType) instantiation.getMCType()).getMCTypeArgumentList());
        //TODO: we still need the following call
        //types = addTypeParameterComponentPackage(instance, types);
        result += printActualTypeArguments(types);
      }
    }
    return result;
  }

  /**
   * Determine whether the port of the given connector is an incoming or outgoing
   * port.
   *
   * @param cmp        The component defining the connector
   * @param portAccess the portaccess to evaluate
   * @return true, if the port is an incoming port. False, otherwise.
   */
  public static boolean isIncomingPort(arcbasis._symboltable.ComponentTypeSymbol cmp,
    arcbasis._ast.ASTPortAccess portAccess) {
    Optional<arcbasis._symboltable.PortSymbol> port;
    String portNameUnqualified = portAccess.getPort();
    // port is of subcomponent
    if (portAccess.isPresentComponent()) {
      String subCompName = portAccess.getComponent();
      Optional<ComponentInstanceSymbol> subCompInstance = cmp.getSpannedScope()
        .resolveComponentInstance(subCompName);
      ComponentTypeSymbol subComp = subCompInstance.get().getType();
      port = subComp.getSpannedScope().resolvePort(portNameUnqualified);
    }
    else {
      port = cmp.getSpannedScope().resolvePort(portNameUnqualified);
    }

    return port.map(PortSymbol::isIncoming).orElse(false);
  }



  public static String getRealPortCppTypeString(ComponentTypeSymbol comp, PortSymbol port,
    ConfigParams config) {
    if (ComponentHelper.portUsesCdType(port)) {
      return printCdPortFQN(comp, port, config);
    }
    else {
      return java2cppTypeString(getRealPortTypeString(comp, port));
    }
  }

  /**
   * @param hwcPath
   * @param comp
   * @param resourcePortName
   * @return Returns true if a handwritten implementation for the IPC Server exists
   */
  public static Boolean existsIPCServerHWCClass(File hwcPath,
    arcbasis._symboltable.ComponentTypeSymbol comp,
    String resourcePortName) {
    String fqCompName = comp.getPackageName() + "." + comp.getName();
    File implLocation = Paths.get(hwcPath.toString() + File.separator
      + fqCompName.replaceAll("\\.", Matcher.quoteReplacement(File.separator))
      + "-" + StringTransformations.capitalize(resourcePortName) + File.separator
      + StringTransformations.capitalize(resourcePortName) + "Server.cpp").toFile();
    return implLocation.isFile();
  }

  protected static String getCalculationIntervalUnit(
    arcbasis._symboltable.ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTCalculationInterval.class)
      .first()
      .transform(e -> e.getTimeUnit().toString())
      .or("MSEC");
  }

  protected static int getCalculationInterval(arcbasis._symboltable.ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTCalculationInterval.class)
      .first()
      .transform(e -> e.getInterval().getValue())
      .or(50);
  }

  public static boolean hasUpdateInterval(ComponentTypeSymbol comp) {
    return !elementsOf(comp)
      .filter(ASTCalculationInterval.class)
      .isEmpty();
  }

  /**
   * Gets a string that corresponds to the update interval of the component in CPP code
   *
   * @param comp
   * @return CPP duration
   */
  public static String getExecutionIntervalMethod(arcbasis._symboltable.ComponentTypeSymbol comp) {
    int interval = getCalculationInterval(comp);
    String intervalUnit = getCalculationIntervalUnit(comp);
    String method = "std::chrono::milliseconds(" + interval + ")";

    switch (intervalUnit) {
      case "MSEC":
        method = "std::chrono::milliseconds(" + interval + ")";
        break;
      case "SEC":
        method = "std::chrono::seconds(" + interval + ")";
        break;
      case "MIN":
        method = "std::chrono::seconds(" + interval * 60 + ")";

    }
    return method;
  }

  public static String getExecutionIntervalInMillis(
    arcbasis._symboltable.ComponentTypeSymbol comp) {
    int interval = getCalculationInterval(comp);
    String intervalUnit = getCalculationIntervalUnit(comp);

    switch (intervalUnit) {
      case "MSEC":
        return "" + interval;
      case "SEC":
        return "" + interval * 1000;
      case "MIN":
        return "" + interval * 60 * 1000;
    }
    return "50";
  }

  /**
   * Returns true iff comp contains at least one buffered port
   */
  public static Boolean usesBatchMode(arcbasis._symboltable.ComponentTypeSymbol comp) {
    // TODO: MontiArc 6
    return false;
    /*
        ((ASTComponentType) comp.getAstNode().get())
        .getBody()
        .getElementList()
        .stream()
        .filter(e -> e instanceof ASTControlBlock)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .anyMatch(e -> e instanceof ASTBatchStatement);
     */
  }

  public static Boolean hasSyncGroups(arcbasis._symboltable.ComponentTypeSymbol comp) {
    return getSyncGroups(comp).size() > 0;
  }

  /**
   * Returns all synchronization groups as lists of strings for easier code generation
   *
   * @param comp
   * @return
   */
  public static List<List<String>> getSyncGroups(arcbasis._symboltable.ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTSyncStatement.class)
      .transform(ASTSyncStatement::getSyncedPortList)
      .toList();
  }

  /**
   * Returns ports that don't appear in any synchronization group
   *
   * @param comp
   * @return
   */
  public static List<arcbasis._symboltable.PortSymbol> getPortsNotInSyncGroup(
    arcbasis._symboltable.ComponentTypeSymbol comp) {
    List<String> portsInSyncGroups = getSyncGroups(comp).stream()
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
    return comp.getAllIncomingPorts().stream()
      .filter(p -> !portsInSyncGroups.contains(p.getName()))
      .collect(Collectors.toList());
  }

  /**
   * Returns all NameExpressions that appear in the guard of the execution statement
   *
   * @param node
   * @return
   */
  public static List<de.monticore.expressions.expressionsbasis._ast.ASTNameExpression> getGuardExpressionElements(
    de.monticore.expressions.expressionsbasis._ast.ASTExpression node) {
    GuardExpressionVisitor visitor = new GuardExpressionVisitor();
    node.accept(visitor);
    return visitor.getExpressions();
  }

  /**
   * Get import for subpackages
   *
   * @param subPackagesPath
   * @return
   */
  public static String getSubPackageImports(File[] subPackagesPath) {
    String packageNames = "";
    String start = "\"./";
    String endCpp = "/*.cpp\"\n";
    String endH = "/*.h\"\n";

    for (File subPackage : subPackagesPath) {
      /**
       * Example of build String with 2 subpackages:
       *
       * \"./packageName1/*.cpp\"\n
       * \"./packageName1/*.h\"\n
       * \"./packageName2/*.cpp\"\n
       * \"./packageName2/*.h\"\n
       */
      packageNames += start + subPackage.getName() + endCpp;
      packageNames += start + subPackage.getName() + endH;
    }
    return packageNames;
  }

  public static String getSubPackageIncludes(File[] subPackagesPath) {
    String packageNames = "";
    String start = "include_directories(./";
    String end = ")\n";

    for (File subPackage : subPackagesPath) {
      /**
       * Example of build String with 2 subpackages:
       *
       * include_directories(./packageName1)\n
       * include_directories(./packageName2)\n
       */
      packageNames += start + subPackage.getName() + end;
    }
    return packageNames;
  }

  public static String getPackagePath(arcbasis._symboltable.ComponentTypeSymbol comp,
    arcbasis._symboltable.ComponentInstanceSymbol subComp) {
    return getPackagePath(comp, subComp.getType());
  }

  public static String getPackagePath(arcbasis._symboltable.ComponentTypeSymbol comp,
    arcbasis._symboltable.ComponentTypeSymbol subComp) {
    // Get package name of subcomponent
    String subCompPackageName = subComp.getPackageName();
    // Check if subcomponent is in different package than parent component
    if (!subCompPackageName.equals(comp.getPackageName())) {
      // Split packageName
      String[] path = subCompPackageName.split("\\.");
      // Build correct package path
      String correctPath = "";
      boolean leaveFirstOut = true;
      for (String dir : path) {
        if (leaveFirstOut) {
          leaveFirstOut = false;
          continue;
        }
        correctPath += dir + "/";
      }
      // Return correct path
      return correctPath;
    }
    // If subcomponent is in the same package as component, then no package path before class import required
    return "";
  }

  public static List<String> getPackages(arcbasis._symboltable.ComponentTypeSymbol component) {
    String packageName = component.getPackageName();
    String[] packages = packageName.split("\\.");
    return Arrays.asList(packages);
  }

  public static String printJavaBlock(
    de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock block) {
    MontiThingsPrettyPrinterDelegator printer = CppPrettyPrinter.getPrinter();
    return printer.prettyprint(block);
  }

  /* ============================================================ */
  /* =================== MontiThings Adapter ==================== */
  /* ============================================================ */

  public static FluentIterable<arcbasis._ast.ASTArcElement> elementsOf(
    arcbasis._symboltable.ComponentTypeSymbol component) {
    return FluentIterable.from(component.getAstNode().getBody().getArcElementList());
  }

  public static List<conditionbasis._ast.ASTCondition> getConditions(
    arcbasis._symboltable.ComponentTypeSymbol component) {
    List<ASTCondition> conditions = new ArrayList<>();

    // get uncatched conditions
    conditions.addAll(elementsOf(component)
      .filter(ASTMTCondition.class)
      .transform(ASTMTCondition::getCondition)
      .toList());

    // get catched conditions
    List<ASTMTCatch> catchedConditions = elementsOf(component).filter(ASTMTCatch.class).toList();
    conditions.addAll(catchedConditions.stream()
      .map(c -> c.getConditionCatch().getCondition())
      .collect(Collectors.toList()));
    return conditions;
  }

  public static Optional<conditioncatch._ast.ASTConditionCatch> getCatch(
    arcbasis._symboltable.ComponentTypeSymbol component,
    conditionbasis._ast.ASTCondition condition) {
    Optional<ASTConditionCatch> result = Optional.empty();
    List<ASTMTCatch> catchedConditions = elementsOf(component).filter(ASTMTCatch.class).toList();
    Optional<ASTMTCatch> mtcatch = catchedConditions.stream()
      .filter(c -> c.getConditionCatch().getCondition() == condition)
      .findFirst();
    if (mtcatch.isPresent()) {
      result = Optional.of(mtcatch.get().getConditionCatch());
    }
    return result;
  }

  public static List<prepostcondition._ast.ASTPrecondition> getPreconditions(
    arcbasis._symboltable.ComponentTypeSymbol component) {
    List<ASTCondition> conditions = getConditions(component);

    return conditions.stream()
      .filter(c -> c instanceof ASTPrecondition)
      .map(c -> (ASTPrecondition) c)
      .collect(Collectors.toList());
  }

  public static List<prepostcondition._ast.ASTPostcondition> getPostconditions(
    arcbasis._symboltable.ComponentTypeSymbol component) {
    List<ASTCondition> conditions = getConditions(component);

    return conditions.stream()
      .filter(c -> c instanceof ASTPostcondition)
      .map(c -> (ASTPostcondition) c)
      .collect(Collectors.toList());
  }

  public static List<montithings._ast.ASTMTCatch> getCatchedConditions(
    arcbasis._symboltable.ComponentTypeSymbol component) {
    return elementsOf(component).filter(ASTMTCatch.class).toList();
  }

  public static boolean retainState(ComponentTypeSymbol component) {
    return !elementsOf(component).filter(ASTMTRetainState.class).isEmpty();
  }

  public static List<montiarc._ast.ASTArcTiming> getTiming(
    arcbasis._symboltable.ComponentTypeSymbol component) {
    return elementsOf(component).filter(ASTArcTiming.class).toList();
  }

  public static boolean isTimesync(arcbasis._symboltable.ComponentTypeSymbol component) {
    return getTiming(component).stream()
      .filter(e -> e.getArcTimeMode() instanceof ASTArcSync)
      .collect(Collectors.toSet()).size() > 0;
  }

  public static boolean hasBehavior(arcbasis._symboltable.ComponentTypeSymbol component) {
    return !elementsOf(component).filter(ASTBehavior.class).isEmpty();
  }

  public static boolean isApplication(arcbasis._symboltable.ComponentTypeSymbol component) {
    ASTMTComponentType ast = (ASTMTComponentType) component.getAstNode();
    return ast.getMTComponentModifier().isApplication();
  }

  public static List<arcbasis._symboltable.PortSymbol> getPortsInGuardExpression(
    de.monticore.expressions.expressionsbasis._ast.ASTExpression node) {
    List<PortSymbol> ports = new ArrayList<>();

    for (ASTNameExpression guardExpressionElement : getGuardExpressionElements(node)) {
      String name = guardExpressionElement.getName();
      IArcBasisScope s = (IArcBasisScope) node.getEnclosingScope();
      Optional<PortSymbol> port = s.resolvePort(name);
      port.ifPresent(ports::add);
    }
    return ports;
  }

  public static boolean portIsComparedToNoData(
    de.monticore.expressions.expressionsbasis._ast.ASTExpression e, String portName) {
    NoDataComparisionsVisitor visitor = new NoDataComparisionsVisitor();
    e.accept(visitor);
    return visitor.getFoundExpressions().stream()
      .map(ASTNameExpression::getName)
      .anyMatch(n -> n.equals(portName));
  }

  /**
   * Returns true iff the port appears in a batch expression
   */
  public static Boolean isBatchPort(arcbasis._symboltable.PortSymbol port,
    arcbasis._symboltable.ComponentTypeSymbol component) {
    // TODO: Fix for MontiArc 6
    return false;
  }

  /**
   * Workaround for the fact that MontiArc returns parameters twice
   */
  public static List<VariableSymbol> getFields(ComponentTypeSymbol component) {
    return component.getFields().stream().collect(Collectors.toList());
  }

  public static String printStatementBehavior(ComponentTypeSymbol component) {
    List<ASTBehavior> behaviors = elementsOf(component).filter(ASTBehavior.class).toList();
    Preconditions.checkArgument(!behaviors.isEmpty(),
      "0xMT800 Trying to print behavior of component \"" + component.getName()
        + "\" that has no behavior.");
    Preconditions.checkArgument(behaviors.size() == 1,
      "0xMT801 Trying to print behavior of component \"" + component.getName()
        + "\" which has multiple conflicting behaviors.");

    return printJavaBlock(behaviors.get(0).getMCJavaBlock());
  }

  /**
   * Returns all ports that appear in any batch statements
   *
   * @return unsorted list of all ports for which a batch statement exists
   */
  public static List<arcbasis._symboltable.PortSymbol> getPortsInBatchStatement(
    arcbasis._symboltable.ComponentTypeSymbol component) {
    List<ASTAnnotatedPort> bufferPorts = elementsOf(component)
      .filter(ASTAnnotatedPort.class)
      .filter(p -> p.getPortAnnotation() instanceof ASTBufferedPort)
      .toList();
    List<String> bufferPortsNames = new ArrayList<>();
    for (ASTAnnotatedPort port : bufferPorts) {
      for (ASTPortDeclaration decl : port.getPortDeclarationList()) {
        for (ASTPort p : decl.getPortList()) {
          bufferPortsNames.add(p.getName());
        }
      }
    }

    List<PortSymbol> ports = new ArrayList<>();
    IArcBasisScope s = component.getSpannedScope();
    for (String bufferedPort : bufferPortsNames) {
      Optional<PortSymbol> resolve = s.resolvePort(bufferedPort);
      resolve.ifPresent(ports::add);
    }
    return ports;
  }

  /**
   * Find all ports of a component that DON'T appear in any batch statement
   */
  public static List<arcbasis._symboltable.PortSymbol> getPortsNotInBatchStatements(
    arcbasis._symboltable.ComponentTypeSymbol component) {
    List<PortSymbol> result = component.getAllPorts();
    result.removeAll(getPortsInBatchStatement(component));
    return result;
  }

  public static List<cdlangextension._ast.ASTCDEImportStatement> getImportStatements(
    java.lang.String name, montithings.generator.codegen.ConfigParams config) {
    ICDLangExtensionScope cdLangScope = config.getCdLangExtensionScope();
    List<DepLanguageSymbol> depLanguageSymbols = cdLangScope.resolveDepLanguageMany(name + ".Cpp");
    List<ASTCDEImportStatement> importStatements = new ArrayList<>();
    for (DepLanguageSymbol depLanguageSymbol : depLanguageSymbols) {
      importStatements.addAll(depLanguageSymbol.getAstNode().getCDEImportStatementList());
    }
    return importStatements;
  }

  public static List<Pair<ComponentTypeSymbol, String>>
  getInstances(arcbasis._symboltable.ComponentTypeSymbol topComponent) {
    return getInstances(topComponent, topComponent.getFullName());
  }

  protected static List<Pair<ComponentTypeSymbol, String>> getInstances(
    ComponentTypeSymbol component, String packageName) {
    List<Pair<ComponentTypeSymbol, String>> instances = new ArrayList<>();
    instances.add(Pair.of(component, packageName));

    for (ComponentInstanceSymbol subcomp : component.getSubComponents()) {
      instances.addAll(
        getInstances(subcomp.getType(), packageName + "." + subcomp.getName()));
    }

    return instances;
  }

  public static ASTComponentInstantiation getInstantiation(ComponentInstanceSymbol instance) {
    ASTNode node = instance.getEnclosingScope().getSpanningSymbol().getAstNode();
    if (!(node instanceof ASTComponentType)) {
      Log.error("0xMT0789 instance is not spanned by ASTComponentType.");
    }
    Optional<ASTComponentInstantiation> result = ((ASTComponentType) node)
      .getSubComponentInstantiations()
      .stream().filter(i -> i.getComponentInstanceList().contains(instance.getAstNode()))
      .findFirst();
    if (!result.isPresent()) {
      Log.error("0xMT0790 instance not found.");
    }
    return result.get();
  }

  private static String printNumericType(SymTypeExpression symTypeExpression) {
    if (symTypeExpression instanceof SymTypeOfNumericWithSIUnit)
      return ((SymTypeOfNumericWithSIUnit) symTypeExpression)
              .getNumericType().print();
    else return symTypeExpression.print();
  }

  public static double getConversionFactorFromSourceAndTarget(ASTPortAccess source, ASTPortAccess target) {
    Optional<PortSymbol> pss = getPortSymbolFromPortAccess(source);
    if (pss.isPresent() && pss.get().getType() instanceof SymTypeOfNumericWithSIUnit){
      Optional<PortSymbol> pst = getPortSymbolFromPortAccess(target);
      if(pst.isPresent()){
        return getConversionFactor(((SymTypeOfNumericWithSIUnit) pss.get().getType()).getUnit(),
                ((SymTypeOfNumericWithSIUnit) pst.get().getType()).getUnit());
      }
    }
    return 1;
  }

  public static boolean isSIUnitPort(ASTPortAccess portAccess){
    Optional<PortSymbol> ps = getPortSymbolFromPortAccess(portAccess);
    if(ps.isPresent() && ps.get().getType() instanceof SymTypeOfNumericWithSIUnit){
      return true;
    }
    return false;
  }

  public static boolean isSIUnitPort(PortSymbol portSymbol){
    if(portSymbol.getType() instanceof SymTypeOfNumericWithSIUnit){
      return true;
    }
    return false;
  }

  public static Optional<PortSymbol> getPortSymbolFromPortAccess(ASTPortAccess portAccess){
    if (!portAccess.isPresentComponent()){
      return Optional.of(portAccess.getPortSymbol());
    }
    return portAccess.getComponentSymbol().getType().getPort(portAccess.getPort());
  }

  public static List<String> getSIUnitPortNames(ComponentTypeSymbol comp){
    List names = new ArrayList();
    for (PortSymbol ps : comp.getAllIncomingPorts()){
      if(ps.getType() instanceof SymTypeOfNumericWithSIUnit){
        names.add(ps.getName());
      }
    }
    return names;
  }
}
