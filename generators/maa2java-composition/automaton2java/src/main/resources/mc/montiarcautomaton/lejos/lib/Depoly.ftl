${tc.params("de.montiarcautomaton.lejosgenerator.helper.ComponentHelper helper", "String _package", "String name", "String depolyName")}
package ${_package};

import lejos.nxt.Button;

public class ${depolyName} {  
  final static int CYCLE_TIME = 50; // in ms
  
  public static void main(String[] args) {
    final ${name} cmp = new ${name}();
    
    cmp.init();
    
    long time;
    
    while (!Thread.interrupted() && Button.readButtons() == 0) {
      time = System.currentTimeMillis();
      cmp.compute();
      cmp.update();
      while ((System.currentTimeMillis() - time) < CYCLE_TIME) {
        Thread.yield();
      }
    }
  }
}
