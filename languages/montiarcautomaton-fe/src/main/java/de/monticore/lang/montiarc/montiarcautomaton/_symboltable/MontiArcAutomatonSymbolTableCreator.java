/* generated from model null*/
/* generated by template symboltable.SymbolTableCreator*/




package de.monticore.lang.montiarc.montiarcautomaton._symboltable;

import java.util.Deque;

import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.automaton.ioautomatonjava._symboltable.IOAutomatonJavaSymbolTableCreator;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcSymbolTableCreator;
import de.monticore.lang.montiarc.montiarcautomaton._ast.ASTAutomatonDefinition;
import de.monticore.lang.montiarc.montiarcautomaton._visitor.CommonMontiArcAutomatonDelegatorVisitor;
import de.monticore.lang.montiarc.montiarcautomaton._visitor.MontiArcAutomatonDelegatorVisitor;
import de.monticore.lang.montiarc.montiarcautomaton._visitor.MontiArcAutomatonVisitor;
import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class MontiArcAutomatonSymbolTableCreator extends CommonSymbolTableCreator
         implements MontiArcAutomatonVisitor {

  // TODO doc
  private final MontiArcAutomatonDelegatorVisitor visitor = new CommonMontiArcAutomatonDelegatorVisitor();

  public MontiArcAutomatonSymbolTableCreator(
    final ResolvingConfiguration resolverConfig, final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
    initSuperSTC(resolverConfig);
  }

  public MontiArcAutomatonSymbolTableCreator(final ResolvingConfiguration resolverConfig, final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
    initSuperSTC(resolverConfig);
  }

  MontiArcSymbolTableCreator maSTC;
  IOAutomatonJavaSymbolTableCreator automatonSTC;
  MontiArcBehaviorSymbolTableCreator behaviorSTC;
  
  private void initSuperSTC(ResolvingConfiguration resolverConfig) {
    maSTC = new MontiArcSymbolTableCreator(resolverConfig, scopeStack);
    automatonSTC = new IOAutomatonJavaSymbolTableCreator(resolverConfig, scopeStack);
    behaviorSTC = new MontiArcBehaviorSymbolTableCreator(resolverConfig, scopeStack);
    
    visitor.set_de_monticore_lang_montiarc_montiarcautomaton__visitor_MontiArcAutomatonVisitor(this);
    visitor.set_de_monticore_lang_montiarc_montiarc__visitor_MontiArcVisitor(maSTC);
    visitor.set_de_monticore_automaton_ioautomatonjava__visitor_IOAutomatonJavaVisitor(automatonSTC);
    visitor.set_de_monticore_lang_montiarc_montiarcbehavior__visitor_MontiArcBehaviorVisitor(behaviorSTC);
  }
  
  /**
  * Creates the symbol table starting from the <code>rootNode</code> and
  * returns the first scope that was created.
  *
  * @param rootNode the root node
  * @return the first scope that was created
  */
  public Scope createFromAST(de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit rootNode) {
    Log.errorIfNull(rootNode, "0xA7004_750 Error by creating of the MontiArcAutomatonSymbolTableCreator symbol table: top ast node is null");
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }

  @Override
  public MutableScope getFirstCreatedScope() {
    return maSTC.getFirstCreatedScope();
  }
  
  private MontiArcAutomatonVisitor realThis = visitor;

  @Override
  public MontiArcAutomatonVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MontiArcAutomatonVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
  
  @Override
  public void visit(ASTAutomatonDefinition node) {
    AutomatonSymbol automaton = new AutomatonSymbol(node.getName());
    addToScopeAndLinkWithNode(automaton, node); // introduces new scope
  }
  
  @Override
  public void endVisit(ASTAutomatonDefinition node) {
    removeCurrentScope();
  }

}
