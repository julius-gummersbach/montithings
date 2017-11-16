
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTComponentBodyCoCo;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * Context condition for guaranteeing that the AJava initialization block only occurs if there is a compute block
 *
 * @author Michael Mutert
 */
public class InitBlockOnlyOnEmbeddedAJava implements MontiArcASTComponentCoCo {
  @Override
  public void check(ASTComponent node) {
    ASTJavaPInitializer init = null;
    boolean hasBehaviour = false;

    // Save the init block if there is one and determine whether there is a behaviour
    for(ASTElement element: node.getBody().getElements()) {
      if(element instanceof ASTJavaPInitializer) {
        init = (ASTJavaPInitializer) element;
      } else if (element instanceof ASTJavaPBehavior) {
        hasBehaviour = true;
      }
    }

    // Throw an error if there is an init block present without a behaviour block.
    if(init != null && !hasBehaviour) {
      Log.error("0xMA063 The component " + node.getName() + " contains an AJava initialization block without a behaviour block.", init.get_SourcePositionStart());
    }
  }
}
