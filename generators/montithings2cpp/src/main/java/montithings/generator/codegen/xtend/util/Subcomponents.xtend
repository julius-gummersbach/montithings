// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import montithings.generator.helper.ComponentHelper
import montithings._symboltable.ComponentSymbol
import montiarc._symboltable.ComponentInstanceSymbol
import java.util.HashMap
import java.util.HashSet

class Subcomponents {
	
  def static String printIncludes(ComponentSymbol comp, String compname, HashMap<String, String> interfaceToImplementation) {
  	var HashSet<String> compIncludes = new HashSet<String>()
    for (subcomponent : comp.subComponents) {
      compIncludes.add('''#include "«ComponentHelper.getPackagePath(comp, subcomponent)»«ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, interfaceToImplementation, false)».h"''')
	}
	return '''
	«FOR include : compIncludes»
	«include»
	«ENDFOR»
	#include "«compname»Input.h"
	#include "«compname»Result.h"
	'''
  }

  def static String printVars(ComponentSymbol comp, HashMap<String, String> interfaceToImplementation) {
    return '''
      «FOR subcomponent : comp.subComponents»
        «var type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, interfaceToImplementation)»
        «printPackageNamespace(comp, subcomponent)»«type» «subcomponent.name»;
      «ENDFOR»
    '''
  }


  def static String printInitializerList(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp)
    return '''
      «FOR subcomponent : comp.subComponents.filter[x | !(new ComponentHelper(comp)).getParamValues(x).isEmpty] SEPARATOR ','»
        «subcomponent.name»(
          «FOR param : helper.getParamValues(subcomponent) SEPARATOR ','»
            «param»
          «ENDFOR»)
      «ENDFOR»
    '''
  }
  
  def static String printPackageNamespace(ComponentSymbol comp, ComponentInstanceSymbol subcomp) {
  	var subcomponentType = subcomp.componentType.referencedSymbol
  	var fullNamespaceSubcomponent = printPackageNamespaceForComponent(subcomponentType)
  	var fullNamespaceEnclosingComponent = printPackageNamespaceForComponent(comp)
  	if (!fullNamespaceSubcomponent.equals(fullNamespaceEnclosingComponent) && 
  		fullNamespaceSubcomponent.startsWith(fullNamespaceEnclosingComponent)) {
  		return fullNamespaceSubcomponent.split(fullNamespaceEnclosingComponent).get(1)
  	} else {
  		return fullNamespaceSubcomponent
  	}
  }
  
  def static String printPackageNamespaceForComponent(montiarc._symboltable.ComponentSymbol comp) {
  	var packages = ComponentHelper.getPackages(comp);
  	var namespace = "montithings::"
  	for (packageName : packages) {
  		namespace += packageName + "::"
  	}
  	return namespace
  }
}
