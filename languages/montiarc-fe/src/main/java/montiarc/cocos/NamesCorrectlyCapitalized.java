package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTAutomatonBehaviorCoCo;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._cocos.MontiArcASTStateCoCo;
import montiarc._symboltable.MontiArcModelNameCalculator;

import java.util.List;

/**
 * CoCo that checks the correct capitalization of component elements.
 *
 * @author Gerrit Leonhardt, Andreas Wortmann, Michael Mutert
 * @implements [RRW14a] C2: The names of variables and ports start with
 * lowercase letters. (p. 31, Lst. 6.5) Context condition
 * for checking, if all fields of an IO-Automaton start with
 * a lower case letter.
 * @implements [Wor16] AC6: The names of automata start with capital letters.
 * (p. 101, Lst. 5.16)
 * @implements [Wor16] AC8: State names begin with a capital letter.
 * (p. 101, Lst. 5.18)
 */
public class NamesCorrectlyCapitalized
    implements MontiArcASTComponentCoCo,
                   MontiArcASTStateCoCo,
                   MontiArcASTAutomatonBehaviorCoCo,
                   MontiArcASTJavaPBehaviorCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {

    // Ensures, that component names start in upper-case.
    // This is required for inner components, see MontiArcModelNameCalculator.
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xMA055 Component names must be in upper-case",
          node.get_SourcePositionStart());
    }

    for (ASTVariableDeclaration varDecl : node.getVariables()) {
      for (String name : varDecl.getNames())
        if (!Character.isLowerCase(name.charAt(0))) {
          Log.warn(
              "0xMA018 The name of variable " + name + " should start with a lowercase letter.",
              varDecl.get_SourcePositionStart());
        }
    }

    for (ASTPort port : node.getPorts()) {
      for (String name : port.getNames()) {
        if (!Character.isLowerCase(name.charAt(0))) {
          Log.error("0xMA077: The name of the port should start with a lowercase letter.",
              port.get_SourcePositionStart());
        }
      }
    }

    final List<ASTParameter> parameters = node.getHead().getParameters();
    for (ASTParameter parameter : parameters) {
      if (!Character.isLowerCase(parameter.getName().charAt(0))) {
        Log.error(String.format("0xMA092: The name of the parameter '%s' should start with a lowercase letter.", parameter.getName()),
            parameter.get_SourcePositionStart());
      }
    }

  }

  @Override
  public void check(ASTState node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error(
          String.format("0xMA021 The name of state %s should start with " +
                            "an uppercase letter.", node.getName()),
          node.get_SourcePositionStart());
    }
  }

  @Override
  public void check(ASTAutomatonBehavior node) {
    if (node.getName().isPresent()) {
      if (!Character.isUpperCase(node.getName().get().charAt(0))) {
        Log.error(String.format("0xMA015 The name of the automaton %s " +
                                    "should start with an uppercase letter.",
            node.getName().get()),
            node.get_SourcePositionStart());
      }
    }
  }

  @Override
  public void check(ASTJavaPBehavior node) {
    if (node.getName().isPresent()) {
      if (!Character.isUpperCase(node.getName().get().charAt(0))) {
        Log.error(
            String.format("0xMA174 The name of the AJava compute block " +
                              "'%s' should start with an uppercase letter.",
                node.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
}
