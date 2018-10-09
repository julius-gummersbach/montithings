package de.montiarcautomaton.generator.helper;

import de.monticore.ast.ASTNode;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._symboltable.*;
import montiarc.helper.SymbolPrinter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class used in the template to generate target code of atomic or
 * composed components.
 *
 * @author Gerrit Leonhardt
 */
public class ComponentHelper {
  public static String DEPLOY_STEREOTYPE = "deploy";
  
  private final ComponentSymbol component;
  
  protected final ASTComponent componentNode;
  
  private final IndentPrinter pr = new IndentPrinter();
  
  private final TypesPrettyPrinterConcreteVisitor typesPrinter = new de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor(
      pr);
  
  private final JavaDSLPrettyPrinter javaPrinter = new JavaDSLPrettyPrinter(pr);
  
  public ComponentHelper(ComponentSymbol component) {
    this.component = component;
    if ((component.getAstNode().isPresent())
        && (component.getAstNode().get() instanceof ASTComponent)) {
      componentNode = (ASTComponent) component.getAstNode().get();
    }
    else {
      componentNode = null;
    }
  }
  
  public String printInitialValue(ASTParameter parameter) {
    String value;
    if (parameter.isPresentDefaultValue()) {
      ASTValuation defaultValue = parameter.getDefaultValue();
      value = javaPrinter.prettyprint(defaultValue.getExpression());
    }
    else {
      value = parameter.getName();
    }
    return value;
  }

  /**
   * Prints the type of the given port respecting inherited ports and the
   * actual type values
   *
   * @param port Symbol of the port of which to determine the type
   * @return The string representation of the type
   */
  public String getPortTypeName(PortSymbol port) {
    return determinePortTypeName(this.component, port);
  }

  /**
   * Determines the name of the type of the port represented by its symbol.
   * This takes in to account whether the port is inherited and possible required
   * renamings due to generic type parameters and their actual arguments.
   *
   * @param portSymbol Symbol of the port for which the type name should be determined.
   * @return The String representation of the type of the port.
   */
  public static String determinePortTypeName(ComponentSymbol componentSymbol, PortSymbol portSymbol){
    final JTypeReference<? extends JTypeSymbol> typeReference = portSymbol.getTypeReference();
    if(!typeReference.existsReferencedSymbol()){
      return ""; // TODO Better error handling
    }

    if(componentSymbol.getPorts().contains(portSymbol) ||
           !componentSymbol.getSuperComponent().isPresent()){
      // A. Component has no super component or the port is defined in the
      // extending component
      //    Therefore, there are no special cases and the name can be used from
      //    the typeReference, even if it is a generic type parameter of the
      //    defining component
//      return ComponentHelper.autobox(printTypeReference(typeReference));
      // TODO: This is a temporary workaround until MC 5.0.0.1 is used that fixes the JTypeSymbolsHelper
      TypesPrettyPrinterConcreteVisitor typesPrinter =
          new TypesPrettyPrinterConcreteVisitor(new IndentPrinter());
      final ASTPort astNode = (ASTPort) portSymbol.getAstNode().get();
      return ComponentHelper.autobox(typesPrinter.prettyprint(astNode.getType()));
      // End of temp workaround

    } else {
      // TODO Refactor stack and maps to fields of the Helper or remove stack and only save last element
      // B. Component has a super component
      // B.2 Port is inherited from the super component (it is not necessarily
      //      defined exactly in the super component)

      // Build the super component hierarchy stack up to the point where
      // the port is defined
      Deque<ComponentSymbolReference> superComponentStack = new ArrayDeque<>();

      // This map contains the string representation of all actual type arguments
      // for each component where types have been pushed through the
      // inheritance hierarchy
      Map<String, Map<String, String>> actualTypeArgStringsMap = new HashMap<>();

      // Fill the stack for the inheritance hierarchy
      // The starting component pushes its super component onto the stack and
      // adds the type arguments to the map without any changes, as type parameters
      // from the initial component do not have to be replaced
      ComponentSymbol currentComponent = componentSymbol;
      Map<String, String> identity = new HashMap<>();
      for (JTypeSymbol typeParam : currentComponent.getFormalTypeParameters()) {
        identity.put(typeParam.getName(), typeParam.getName());
      }
      actualTypeArgStringsMap.put(currentComponent.getFullName(), identity);

      // Note: At this point the super component of the initial component is
      //  on the stack and the map for the super component is filled

      while(currentComponent != null &&
                currentComponent.getSuperComponent().isPresent() &&
                !currentComponent.getPorts().contains(portSymbol)){
        Map<String, String> superCompTypeArgs = new HashMap<>();
        final ComponentSymbolReference superCompRef =
            currentComponent.getSuperComponent().get();
        superComponentStack.push(superCompRef);
        final ComponentSymbol superCompSymbol =
            superCompRef.getReferencedSymbol();

        // Fill the map for the super component
        for (JTypeSymbol typeSymbol : superCompSymbol.getFormalTypeParameters()) {
          final int index = superCompSymbol.getFormalTypeParameters().indexOf(typeSymbol);
          final ActualTypeArgument actualTypeArg =
              superCompRef.getActualTypeArguments().get(index);
          String resultingTypeArgument =
              insertTypeParamValueIntoTypeArg(actualTypeArg.getType(),
                  actualTypeArgStringsMap.get(currentComponent.getFullName()));
          superCompTypeArgs.put(typeSymbol.getName(), resultingTypeArgument);
        }
        actualTypeArgStringsMap.put(superCompSymbol.getFullName(), superCompTypeArgs);

        // Prepare the next iteration. Keeps the component at the highest
        // point in the inheritance hierarchy that defines the port
        currentComponent = superComponentStack.peek();
      }

      // Note: At this point the currentComponent is the one that defines the
      // port of which the type is to be determined.
      // The stack contains all super components of the starting component
      // Each component has a map which contains all actual type arguments with
      // the types from the hierarchy replacing type parameters

      // Replace all type parameters of the defining component which occurr in
      // the port type by the actual type argument
      String portTypeString =
          insertTypeParamValueIntoTypeArg(
              typeReference,
              actualTypeArgStringsMap.get(currentComponent.getFullName()));
      return portTypeString;
    }
  }

  /**
   * Converts the given type reference {@param toAnalyze} into a String
   * representation where all occurrences of type arguments from the extending
   * or embedding component are replaced by their actual values. The actual values
   * are given as the second argument {@param typeParamMap}.
   * <br/>
   *
   * Example: For a given type argument {@code Map<T,K>} as an
   * {@link TypeReference} object and the {@link Map}
   * {@code ["T" -> "String", "K -> "Integer"]}
   * the resulting String is {@code Map<String, Integer>}.
   *
   * @param toAnalyze The type argument where the type parameters should be replaced.
   * @param typeParamMap The map used to replace the type parameters
   * @return The resulting String representation with replaced type parameters
   */
  public static String insertTypeParamValueIntoTypeArg(
      TypeReference<? extends TypeSymbol> toAnalyze,
      Map<String, String> typeParamMap){

    StringBuilder result = new StringBuilder();
    result.append(toAnalyze.getName());
    if(toAnalyze.getActualTypeArguments().isEmpty()){
      // There are no type arguments. Check if the type itself is a type parameter
      if(typeParamMap.containsKey(toAnalyze.getName())){
        return typeParamMap.get(toAnalyze.getName());
      } else {
        return toAnalyze.getName();
      }
    } else {
      // There are type arguments and therefore they are processed recursively
      // and reprinted
      result.append("<");
      result.append(
          toAnalyze.getActualTypeArguments().stream()
              .map(typeArg -> insertTypeParamValueIntoTypeArg(typeArg.getType(), typeParamMap))
              .collect(Collectors.joining(", ")));
      result.append(">");
    }
    for (int i = 0; i < toAnalyze.getDimension(); i++) {
      result.append("[]");
    }
    return result.toString();
  }

  /**
   * Prints a type reference with dimension and type arguments.
   *
   * @param reference The type reference to print
   * @return The printed type reference
   */
  public static String printTypeReference(TypeReference<? extends TypeSymbol> reference){
    return insertTypeParamValueIntoTypeArg(reference, new HashMap<>());
  }

  /**
   * Prints an actual type argument.
   * @param arg The actual type argument to print
   * @return The printed actual type argument
   */
  public static String printTypeArgument(ActualTypeArgument arg){
    return printTypeReference(arg.getType());
  }

  /**
   * Prints a list of actual type arguments.
   * @param typeArguments The actual type arguments to print
   * @return The printed actual type arguments
   */
  public static String printTypeArguments(List<ActualTypeArgument> typeArguments){
    if(typeArguments.size() > 0) {
      StringBuilder result = new StringBuilder("<");
      result.append(
          typeArguments.stream()
              .map(ComponentHelper::printTypeArgument)
              .collect(Collectors.joining(", ")));
      result.append(">");
      return result.toString();
    }
    return "";
  }

  private static HashMap<String, String> PRIMITIVE_TYPES = new HashMap<String, String>() {
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

  // TODO @MP in Templates übernehmen
  public String printPortTypeName(PortSymbol var) {
    String name = var.getName();
    Optional<ASTType> optType = findPortTypeByName(name);
    return printTypeName(optType);
  }
  
  private Optional<ASTType> findPortTypeByName(String name) {
    for (ASTElement e : componentNode.getBody().getElementList()) {
      if (e instanceof ASTInterface) {
        ASTInterface itf = (ASTInterface) e;
        for (ASTPort port : itf.getPortsList()) {
          if (port.getNameList().contains(name)) {
            return Optional.of(port.getType());
          }
        }
      }
    }
    return Optional.empty();
  }
  
  // TODO @MP in Templates übernehmen
  public String printVariableTypeName(VariableSymbol var) {
    String name = var.getName();
    Optional<ASTType> optType = findVariableTypeByName(name);
    return printTypeName(optType);
  }
  
  private Optional<ASTType> findVariableTypeByName(String name) {
    for (ASTElement e : componentNode.getBody().getElementList()) {
      if (e instanceof ASTVariableDeclaration) {
        ASTVariableDeclaration variableDeclaration = (ASTVariableDeclaration) e;
        if (variableDeclaration.getNameList().contains(name)) {
          return Optional.of(variableDeclaration.getType());
        }
      }
    }
    return Optional.empty();
  }
  
  public String printParamTypeName(JFieldSymbol param) {
    String name = param.getName();
    Optional<ASTType> optType = findParamTypeByName(name);
    return printTypeName(optType);
  }
  
  private Optional<ASTType> findParamTypeByName(String name) {
    for (ASTParameter p : componentNode.getHead().getParameterList()) {
      if (name.equals(p.getName())) {
        return Optional.of(p.getType());
      }
    }
    return Optional.empty();
  }
  
  private String printTypeName(Optional<ASTType> optType) {
    if (optType.isPresent()) {
      return autobox(typesPrinter.prettyprint(optType.get()).replaceAll(" ", ""));
    }
    else {
      Log.error("XX");
      return "TYPE-NOT-FOUND";
    }
  }
  
  public String getVariableTypeName(ASTComponent comp, VariableSymbol var) {
    return printFqnTypeName(comp, var.getTypeReference());
  }
  
  public String printInit(ASTValueInitialization init) {
    String ret = "";
    JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
    String name = Names.getQualifiedName(init.getQualifiedName().getPartList());
    ret += name;
    ret += " = ";
    ret += printer.prettyprint(init.getValuation().getExpression());
    ret += ";";
    
    return ret;
    
  }
  
  public String getParamTypeName(JFieldSymbol param) {
    return getParamTypeName(componentNode, param);
  }
  
  public String getParamTypeName(ASTComponent comp, JFieldSymbol param) {
    return printFqnTypeName(comp, param.getType());
  }
  
  /**
   * Calculates the values of the parameters of a
   * {@link ComponentInstanceSymbol}. This takes default values for parameters
   * into account and adds them as required. Default values are only added from
   * left to right in order. <br/>
   * Example: For a component with parameters
   * <code>String stringParam, Integer integerParam = 2, Object objectParam = new Object()</code>
   * that is instanciated with parameters <code>"Test String", 5</code> this
   * method adds <code>new Object()</code> as the last parameter.
   *
   * @param param The {@link ComponentInstanceSymbol} for which the parameters
   * should be calculated.
   * @return The parameters.
   */
  public Collection<String> getParamValues(ComponentInstanceSymbol param) {
    // final List<ValueSymbol<TypeReference<TypeSymbol>>> configArguments =
    // param.getConfigArguments();
    List<ASTExpression> configArguments = param.getConfigArguments();
    JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
    
    List<String> outputParameters = new ArrayList<>();
    for (ASTExpression configArgument : configArguments) {
      final String prettyprint = printer.prettyprint(configArgument);
      outputParameters.add(autobox(prettyprint));
    }
    
    // Append the default parameter values for as many as there are left
    final List<JFieldSymbol> configParameters = param.getComponentType().getConfigParameters();
    
    // Calculate the number of missing parameters
    int numberOfMissingParameters = configParameters.size() - configArguments.size();
    
    if (numberOfMissingParameters > 0) {
      // Get the AST node of the component and the list of parameters in the AST
      final ASTComponent astNode = (ASTComponent) param.getComponentType().getReferencedSymbol()
          .getAstNode().get();
      final List<ASTParameter> parameters = astNode.getHead().getParameterList();
      
      // Retrieve the parameters from the node and add them to the list
      for (int counter = 0; counter < numberOfMissingParameters; counter++) {
        // Fill up from the last parameter
        final ASTParameter astParameter = parameters.get(parameters.size() - 1 - counter);
        final String prettyprint = printer
            .prettyprint(astParameter.getDefaultValue().getExpression());
        outputParameters.add(outputParameters.size() - counter, prettyprint);
      }
    }
    
    return outputParameters;
  }
  
  public String getSubComponentTypeName(ComponentInstanceSymbol instance) {
    
//    if (instance.getComponentType().getAstNode().isPresent()) {
//      return typesPrinter
//          .prettyprint((ASTTypesNode) instance.getComponentType().getAstNode().get());
//    }
//    else {
//      return instance.getComponentType().getName();
//    }
    String result = "";
    final ComponentSymbolReference componentTypeReference = instance.getComponentType();
    result += componentTypeReference.getFullName();
    if (componentTypeReference.hasActualTypeArguments()){
      result += printTypeArguments(componentTypeReference.getActualTypeArguments());
    }
    return result;
  }
  
  public boolean isIncomingPort(ComponentSymbol cmp, ConnectorSymbol conn, boolean isSource,
      String portName) {
    String subCompName = getConnectorComponentName(conn, isSource);
    String portNameUnqualified = getConnectorPortName(conn, isSource);
    Optional<PortSymbol> port = Optional.empty();
    // port is of subcomponent
    if (portName.contains(".")) {
      Optional<ComponentInstanceSymbol> subCompInstance = cmp.getSpannedScope()
          .<ComponentInstanceSymbol> resolve(subCompName, ComponentInstanceSymbol.KIND);
      Optional<ComponentSymbol> subComp = subCompInstance.get().getComponentType()
          .getReferencedComponent();
      port = subComp.get().getSpannedScope().<PortSymbol> resolve(portNameUnqualified,
          PortSymbol.KIND);
    }
    else {
      port = cmp.getSpannedScope().<PortSymbol> resolve(portName, PortSymbol.KIND);
    }
    
    if (port.isPresent()) {
      return port.get().isIncoming();
    }
    
    return false;
  }
  
  /**
   * Returns the component name of a connection.
   *
   * @param conn the connection
   * @param isSource <tt>true</tt> for source component, else <tt>false>tt>
   * @return
   */
  public String getConnectorComponentName(ConnectorSymbol conn, boolean isSource) {
    final String name;
    if (isSource) {
      name = conn.getSource();
    }
    else {
      name = conn.getTarget();
    }
    if (name.contains(".")) {
      return name.split("\\.")[0];
    }
    return "this";
    
  }
  
  /**
   * Returns the port name of a connection.
   *
   * @param conn the connection
   * @param isSource <tt>true</tt> for source component, else <tt>false>tt>
   * @return
   */
  public String getConnectorPortName(ConnectorSymbol conn, boolean isSource) {
    final String name;
    if (isSource) {
      name = conn.getSource();
    }
    else {
      name = conn.getTarget();
    }
    
    if (name.contains(".")) {
      return name.split("\\.")[1];
    }
    return name;
  }
  
  /**
   * Returns <tt>true</tt> if the component is deployable.
   *
   * @return
   */
  public boolean isDeploy() {
    if (component.getStereotype().containsKey(DEPLOY_STEREOTYPE)) {
      if (!component.getConfigParameters().isEmpty()) {
        throw new RuntimeException("Config parameters are not allowed for a depolyable component.");
      }
      return true;
    }
    return false;
  }
  
  /**
   * Prints the type of the reference including dimensions.
   *
   * @param ref
   * @return
   */
  protected String printFqnTypeName(ASTComponent comp, JTypeReference<? extends JTypeSymbol> ref) {
    String name = ref.getName();
    if (isGenericTypeName(comp, name)) {
      return name;
    }
    Collection<JTypeSymbol> sym = ref.getEnclosingScope().<JTypeSymbol> resolveMany(ref.getName(),
        JTypeSymbol.KIND);
    if (!sym.isEmpty()) {
      name = sym.iterator().next().getFullName();
    }
    for (int i = 0; i < ref.getDimension(); ++i) {
      name += "[]";
    }
    return autobox(name);
  }
  
  /**
   * Checks whether the given typeName for the component comp is a generic
   * parameter.
   *
   * @param comp
   * @param typeName
   * @return
   */
  private boolean isGenericTypeName(ASTComponent comp, String typeName) {
    if (comp == null) {
      return false;
    }
    if (comp.getHead().isPresentGenericTypeParameters()) {
      List<ASTTypeVariableDeclaration> parameterList = comp.getHead().getGenericTypeParameters()
          .getTypeVariableDeclarationList();
      for (ASTTypeVariableDeclaration type : parameterList) {
        if (type.getName().equals(typeName)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean hasSuperComp() {
    return component.getSuperComponent().isPresent();
  }
  
  public String getSuperComponentFqn() {
    if (component.getSuperComponent().isPresent()) {
      return component.getSuperComponent().get().getFullName();
    }
    return "ERROR";
  }

  public boolean superCompGeneric(){
    if(component.getSuperComponent().isPresent()){
      final ComponentSymbolReference componentSymbolReference = component.getSuperComponent().get();
      return componentSymbolReference.hasActualTypeArguments();
    }
    return false;
  }

  /**
   *
   * @return A list of String represenations of the actual type arguments
   * assigned to the super component
   */
  public List<String> getSuperCompActualTypeArguments() {
    final List<String> paramList = new ArrayList<>();
    if(component.getSuperComponent().isPresent()) {
      final ComponentSymbolReference componentSymbolReference = component.getSuperComponent().get();
      final List<ActualTypeArgument> actualTypeArgs = componentSymbolReference.getActualTypeArguments();
      String componentPrefix = this.component.getFullName() + ".";
      for (ActualTypeArgument actualTypeArg : actualTypeArgs) {
        final String printedTypeArg = SymbolPrinter.printTypeParameters(actualTypeArg);
        if(printedTypeArg.startsWith(componentPrefix)) {
          paramList.add(printedTypeArg.substring(componentPrefix.length()));
        } else {
          paramList.add(printedTypeArg);
        }
      }
    }
    return paramList;
  }
  
  public static Optional<ASTJavaPInitializer> getComponentInitialization(ComponentSymbol comp) {
    Optional<ASTJavaPInitializer> ret = Optional.empty();
    Optional<ASTNode> ast = comp.getAstNode();
    if (ast.isPresent()) {
      ASTComponent compAST = (ASTComponent) ast.get();
      for (ASTElement e : compAST.getBody().getElementList()) {
        if (e instanceof ASTJavaPInitializer) {
          ret = Optional.of((ASTJavaPInitializer) e);
          
        }
      }
    }
    return ret;
  }
  
  public boolean isGeneric() {
    return componentNode.getHead().isPresentGenericTypeParameters();
  }
  
  public List<String> getGenericParameters() {
    List<String> output = new ArrayList<>();
    if (isGeneric()) {
      List<ASTTypeVariableDeclaration> parameterList = componentNode.getHead()
          .getGenericTypeParameters().getTypeVariableDeclarationList();
      for (ASTTypeVariableDeclaration variableDeclaration : parameterList) {
        output.add(variableDeclaration.getName());
      }
    }
    return output;
  }

  public List<String> getInheritedParams(){
    List<String> result = new ArrayList<>();
    final List<JFieldSymbol> configParameters = component.getConfigParameters();
    if(component.getSuperComponent().isPresent()){
      final ComponentSymbolReference superCompReference = component.getSuperComponent().get();
      final List<JFieldSymbol> superConfigParams = superCompReference.getReferencedSymbol().getConfigParameters();
      if(!configParameters.isEmpty()){
        for (int i = 0; i < superConfigParams.size(); i++) {
          result.add(configParameters.get(i).getName());
        }
      }
    }
    return result;
  }

  public List<PortSymbol> getSuperInPorts() {
    return component.getSuperComponent().isPresent()
        ? component.getSuperComponent().get().getAllIncomingPorts()
        : Collections.emptyList();
  }
  
  public List<PortSymbol> getAllInPorts() {
    return component.getAllIncomingPorts();
  }
  
  public List<PortSymbol> getSuperOutPorts() {
    return component.getSuperComponent().isPresent()
        ? component.getSuperComponent().get().getAllOutgoingPorts()
        : Collections.emptyList();
  }
  
  public List<PortSymbol> getAllOutPorts() {
    return component.getAllOutgoingPorts();
  }

  /**
   * Checks whether component parameter, variable, subcomponent instance, or
   * port names contain the identifier given as the parameter.
   * @param identifier The name to check
   * @return True, iff. there is at least one identifier that is equal to the
   * given identifier
   */
  public boolean containsIdentifier(String identifier){

    for (PortSymbol portSymbol : component.getPorts()) {
      if(portSymbol.getName().equals(identifier)){
        return true;
      }
    }

    for (JFieldSymbol jFieldSymbol : component.getConfigParameters()) {
      if(jFieldSymbol.getName().equals(identifier)){
        return true;
      }
    }

    for (VariableSymbol variableSymbol : component.getVariables()) {
      if(variableSymbol.getName().equals(identifier)){
        return true;
      }
    }

    for (ComponentInstanceSymbol instanceSymbol : component.getSubComponents()) {
      if(instanceSymbol.getName().equals(identifier)){
        return true;
      }
    }

    return false;
  }
}
