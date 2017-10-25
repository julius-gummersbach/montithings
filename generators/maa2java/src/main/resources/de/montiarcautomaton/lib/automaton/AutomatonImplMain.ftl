${tc.params("de.montiarcautomaton.generator.helper.AutomatonHelper helper", "String _package", "java.util.Collection<de.monticore.symboltable.ImportStatement> imports",
"String name", "String resultName", "String inputName", "String implName",
"java.util.Collection<montiarc._symboltable.PortSymbol> portsIn",
"de.montiarcautomaton.generator.helper.ComponentHelper compHelper",
"java.util.Collection<montiarc._symboltable.VariableSymbol> variables", "java.util.Collection<montiarc._symboltable.StateSymbol> states",
"java.util.Collection<de.monticore.symboltable.types.JFieldSymbol> configParams",
"java.util.List<montiarc._ast.ASTValueInitialization> initializations")}
package ${_package};

import ${_package}.${resultName};
import ${_package}.${inputName};
<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;

public class ${implName} implements IComputable<${inputName}, ${resultName}> {
  private static enum State {
    <#list states><#items as state>${state.getName()}<#sep>, </#sep></#items>;</#list>
  }
  
  // holds the current state of the automaton
  private State currentState;
  
  // variables
  <#list variables as variable>
  private ${compHelper.printVariableTypeName(variable)} ${variable.getName()};
  </#list>
  
  // config parameters
  <#list configParams as param>
  private final ${helper.getParamTypeName(param)} ${param.getName()};
  </#list>
  
  public ${implName}(<#list configParams as param>${helper.getParamTypeName(param)} ${param.getName()}<#sep>, </#list>) {
  	<#list configParams as param>
  	this.${param.getName()} = ${param.getName()};
  	</#list>
  }  

  @Override
  public ${resultName} getInitialValues() {
    final ${resultName} result = new ${resultName}();
    
    // variable initialization
    <#list initializations as init>
      ${helper.printInit(init)}
    </#list>

    // initial reaction
    <#list helper.getInitialReaction(helper.getInitialState()) as assignment>
    result.set${assignment.getLeft()?cap_first}(${assignment.getRight()});
    </#list>
    
    // initial state
    currentState = State.${helper.getInitialState().getName()};
    
    return result;
  }

  @Override
  public ${resultName} compute(${inputName} input) {
    // inputs
    <#list portsIn as port>
  	final ${helper.getPortTypeName(port)} ${port.getName()} = input.get${port.getName()?cap_first}();
  	</#list>
  
    final ${resultName} result = new ${resultName}();
    
    // first current state to reduce stimuli and guard checks
    switch (currentState) {
    <#list states as state>
      case ${state.getName()}:
      <#list helper.getTransitions(state) as transition>
        // transition: ${transition.toString()}
        if (${helper.getGuard(transition)!"true"}
        <#list helper.getStimulus(transition) as assignment>
        && <#if assignment.getRight() == "null">cmd == null<#else>${assignment.getLeft()} != null && ${assignment.getLeft()}.equals(${assignment.getRight()})</#if> <#else>&& true
        </#list>) {
          // reaction
          <#list helper.getReaction(transition) as assignment>
          <#if assignment.isVariable(assignment.getLeft())>
          ${assignment.getLeft()} = ${assignment.getRight()};
          <#else>
          result.set${assignment.getLeft()?cap_first}(${assignment.getRight()});
          </#if>
          </#list>
          
          //Log.log("${implName}", "${transition.toString()}");
             
          // state change
          currentState = State.${transition.getTarget().getName()};
          break;
        }
      </#list>  
        break;
    </#list>
    }
    
    return result;
  }
  
}