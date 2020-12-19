/* (c) https://github.com/MontiCore/monticore */
package montithings;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.cd4code.resolver.CD4CodeResolver;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.check.DefsTypeBasic;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.adapters.ArcCD4CodeResolver;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings._parser.MontiThingsParser;
import montithings._symboltable.IMontiThingsArtifactScope;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings._symboltable.IMontiThingsScope;
import montithings._symboltable.MontiThingsSymbolTableCreatorDelegator;
import montithings.cocos.MontiThingsCoCos;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MontiThingsTool {

  protected MontiThingsCoCoChecker mtChecker;
  protected CD4CodeCoCoChecker cdChecker;
  protected boolean isSymTabInitialized;
  protected String mtFileExtension = "mt";
  protected String cdFileExtension = "cd";

  public MontiThingsTool() {
    this(MontiThingsCoCos.createChecker(), new CD4CodeCoCos().createNewChecker());
  }

  public MontiThingsTool(@NotNull MontiThingsCoCoChecker mtChecker, @NotNull CD4CodeCoCoChecker cdChecker) {
    Preconditions.checkArgument(mtChecker != null);
    Preconditions.checkArgument(cdChecker != null);
    this.mtChecker = mtChecker;
    this.cdChecker = cdChecker;
    this.isSymTabInitialized = false;
  }

  protected String getMTFileExtension() {
    return this.mtFileExtension;
  }

  protected String getCDFileExtension() {
    return this.cdFileExtension;
  }

  protected MontiThingsCoCoChecker getMTChecker() {
    return this.mtChecker;
  }

  protected CD4CodeCoCoChecker getCdChecker() {
    return this.cdChecker;
  }

  public IMontiThingsGlobalScope processModels(@NotNull Path... modelPaths) {
    Preconditions.checkArgument(modelPaths != null);
    Preconditions.checkArgument(!Arrays.asList(modelPaths).contains(null));
    ModelPath mp = new ModelPath(Arrays.asList(modelPaths));
    ICD4CodeGlobalScope cd4CGlobalScope = CD4CodeMill.cD4CodeGlobalScopeBuilder()
      .setModelPath(mp)
      .setModelFileExtension(this.getCDFileExtension())
      .build();
    IMontiThingsGlobalScope montiThingsGlobalScope = MontiThingsMill.montiThingsGlobalScopeBuilder()
      .setModelPath(mp)
      .setModelFileExtension(this.getMTFileExtension())
      .build();
    resolvingDelegates(montiThingsGlobalScope, cd4CGlobalScope);
    addBasicTypes(montiThingsGlobalScope);
    this.processModels(cd4CGlobalScope);
    this.processModels(montiThingsGlobalScope);
    return montiThingsGlobalScope;
  }

  protected void resolvingDelegates(@NotNull IMontiThingsGlobalScope montiThingsGlobalScope, @NotNull ICD4CodeGlobalScope cd4CGlobalScope) {
    CD4CodeResolver cd4CodeResolver = new ArcCD4CodeResolver(cd4CGlobalScope);
    montiThingsGlobalScope.addAdaptedFieldSymbolResolver(cd4CodeResolver);
    montiThingsGlobalScope.addAdaptedTypeSymbolResolver(cd4CodeResolver);
  }

  public void processModels(@NotNull IMontiThingsGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    this.createSymbolTable(scope).stream().map(as -> (ASTMACompilationUnit) as.getAstNode()).forEach(a -> a.accept(this.getMTChecker()));
  }

  public void processModels(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    this.createSymbolTable(scope).stream().flatMap(a -> a.getSubScopes().stream())
      .map(as -> (ASTCDPackage) as.getSpanningSymbol().getAstNode()).forEach(a -> a.accept(this.getCdChecker()));
  }

  public Collection<IMontiThingsArtifactScope> createSymbolTable(@NotNull IMontiThingsGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    Collection<IMontiThingsArtifactScope> result = new HashSet<>();
    for (ASTMACompilationUnit ast : parseModels(scope)) {
      MontiThingsSymbolTableCreatorDelegator symTab = new MontiThingsSymbolTableCreatorDelegator(scope);
      result.add(symTab.createFromAST(ast));
    }
    return result;
  }

  public Collection<ICD4CodeArtifactScope> createSymbolTable(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    Collection<ICD4CodeArtifactScope> result = new HashSet<>();
    for (ASTCDCompilationUnit ast : parseModels(scope)) {
      CD4CodeSymbolTableCreatorDelegator symTab = new CD4CodeSymbolTableCreatorDelegator(scope);
      result.add(symTab.createFromAST(ast));
    }
    return result;
  }

  public Collection<ASTMACompilationUnit> parseModels(@NotNull IMontiThingsGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    return scope.getModelPath().getFullPathOfEntries().stream().flatMap(p -> parseMT(p).stream()).collect(Collectors.toSet());
  }

  public Collection<ASTCDCompilationUnit> parseModels(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    return scope.getModelPath().getFullPathOfEntries().stream().flatMap(p -> parseCD(p).stream()).collect(Collectors.toSet());
  }

  public Collection<ASTMACompilationUnit> parseMT(@NotNull Path path) {
    Preconditions.checkArgument(path != null);
    try {
      return Files.walk(path).filter(Files::isRegularFile).filter(f -> f.getFileName().toString().endsWith(this.getMTFileExtension())).map(f -> parseMT(f.toString()))
        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error("Could not access " + path.toString() + ", there were I/O exceptions.");
    }
    return new HashSet<>();
  }

  public Collection<ASTCDCompilationUnit> parseCD(@NotNull Path path) {
    Preconditions.checkArgument(path != null);
    try {
      return Files.walk(path).filter(Files::isRegularFile).filter(f -> f.getFileName().toString().endsWith(this.getCDFileExtension())).map(f -> parseCD(f.toString()))
        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error("Could not access " + path.toString() + ", there were I/O exceptions.");
    }
    return new HashSet<>();
  }

  public Optional<ASTMACompilationUnit> parseMT(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    MontiThingsParser p = new MontiThingsParser();
    Optional<ASTMACompilationUnit> compUnit;
    try {
      compUnit = p.parse(filename);
      return compUnit;
    } catch (IOException e) {
      Log.error("Could not access " + filename + ", there were I/O exceptions.");
    }
    return Optional.empty();
  }

  public Optional<ASTCDCompilationUnit> parseCD(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    CD4CodeParser p = new CD4CodeParser();
    Optional<ASTCDCompilationUnit> cd;
    try {
      cd = p.parse(filename);
      return cd;
    } catch (IOException e) {
      Log.error("Could not access " + filename + ", there were I/O exceptions.");
    }
    return Optional.empty();
  }

  @Deprecated
  public boolean checkCoCos(@NotNull ASTArcBasisNode node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.isSymTabInitialized, "Please initialize symbol-table before "
      + "checking cocos.");
    this.getMTChecker().checkAll(node);
    if (Log.getErrorCount() != 0) {
      Log.debug("Found " + Log.getErrorCount() + " errors in node " + node + ".", "XX");
      return false;
    }
    return true;
  }

  /**
   * Loads a ComponentSymbol with the passed componentName. The componentName is the full qualified
   * name of the component model. Modelpaths are folders relative to the project path and containing
   * the packages the models are located in. When the ComponentSymbol is resolvable it is returned.
   * Otherwise the optional is empty.
   *
   * @param componentName Name of the component
   * @param modelPaths    Folders containing the packages with models
   * @return an {@code Optional} of the loaded component type
   */
  @Deprecated
  public Optional<ComponentTypeSymbol> loadComponentSymbolWithoutCocos(String componentName,
    File... modelPaths) {
    IMontiThingsScope s = initSymbolTable(modelPaths);
    return s.resolveComponentType(componentName);
  }

  @Deprecated
  public Optional<ComponentTypeSymbol> loadComponentSymbolWithCocos(String componentName,
    File... modelPaths) {
    Optional<ComponentTypeSymbol> compSym = loadComponentSymbolWithoutCocos(componentName,
      modelPaths);

    compSym.ifPresent(componentTypeSymbol -> this.checkCoCos(componentTypeSymbol.getAstNode()));

    return compSym;
  }

  /**
   * Loads the AST of the passed model with name componentName. The componentName is the fully
   * qualified. Modelpaths are folders relative to the project path and containing the packages the
   * models are located in. When the ComponentSymbol is resolvable it is returned. Otherwise the
   * optional is empty.
   *
   * @param modelPath The model path containing the package with the model
   * @param model     the fully qualified model name
   * @return the AST node of the model
   */
  @Deprecated
  public Optional<ASTComponentType> getAstNode(String modelPath, String model) {
    // ensure an empty log
    Log.getFindings().clear();
    Optional<ComponentTypeSymbol> comp = loadComponentSymbolWithoutCocos(model,
      Paths.get(modelPath).toFile());

    if (!comp.isPresent()) {
      Log.error("Model could not be resolved!");
      return Optional.empty();
    }

    if (!comp.get().isPresentAstNode()) {
      Log.debug("Symbol not linked with node.", "XX");
      return Optional.empty();
    }
    return Optional.of(comp.get().getAstNode());
  }

  /**
   * Initializes the Symboltable by introducing scopes for the passed modelpaths. It does not create
   * the symbol table! Symbols for models within the modelpaths are not added to the symboltable
   * until resolve() is called. Modelpaths are relative to the project path and do contain all the
   * packages the models are located in. E.g. if model with fqn a.b.C lies in folder
   * src/main/resources/models/a/b/C.arc, the modelpath is src/main/resources.
   *
   * @param modelPaths paths of all folders containing models
   * @return The initialized symbol table
   */
  @Deprecated
  public IMontiThingsScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }
    final ModelPath mp = new ModelPath(p);
    IMontiThingsGlobalScope montiThingsGlobalScope = MontiThingsMill.montiThingsGlobalScopeBuilder().setModelPath(mp).setModelFileExtension(this.getMTFileExtension()).build();
    ICD4CodeGlobalScope cd4CodeGlobalScope = CD4CodeMill.cD4CodeGlobalScopeBuilder().setModelPath(mp).setModelFileExtension(this.getCDFileExtension()).build();
    this.resolvingDelegates(montiThingsGlobalScope, cd4CodeGlobalScope);
    this.addBasicTypes(montiThingsGlobalScope);
    isSymTabInitialized = true;
    return montiThingsGlobalScope;
  }

  public void addBasicTypes(@NotNull IMontiThingsScope scope) {
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._boolean);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._char);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._short);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._String);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._int);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._long);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._float);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._double);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._null);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._Object);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._array);
  }

  /**
   * Initializes the Symboltable by introducing scopes for the passed modelpaths. It does not create
   * the symbol table! Symbols for models within the modelpaths are not added to the symboltable
   * until resolve() is called. Modelpaths are relative to the project path and do contain all the
   * packages the models are located in. E.g. if model with fqn a.b.C lies in folder
   * src/main/resources/models/a/b/C.arc, the modelPath is src/main/resources.
   *
   * @param modelPath The model path for the symbol table
   * @return the initialized symbol table
   */
  @Deprecated
  public IMontiThingsScope initSymbolTable(String modelPath) {
    return initSymbolTable(Paths.get(modelPath).toFile());
  }
}
