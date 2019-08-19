/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.codegen.xtend.util.Utils

/**
 * Generates the deployment class for a component.
 *
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 *
 */
class Deploy {
    def static generateDeploy(ComponentSymbol comp) {
    var name = comp.name;
    return '''
      «Utils.printPackage(comp)»
      
      public class Deploy«name» {
        final static int CYCLE_TIME = 50; // in ms
          
        public static void main(String[] args) {
          final «name» cmp = new «name»();
          
          cmp.setUp();
          cmp.init();
                   
          long time;
             
          while (!Thread.interrupted()) {
            time = System.currentTimeMillis();
            cmp.compute();
            cmp.update();
            while((System.currentTimeMillis()-time) < CYCLE_TIME){
              Thread.yield();
            }
          }
        }
      }
    '''
  }
}