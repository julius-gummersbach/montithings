/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.Before;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

public class AJavaCorrectnessTest extends AbstractCoCoTest {

  @Before
  public void setup(){
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testUsedPortsExist() {
    checkValid("", "contextconditions.valid.UsedPortsExist");
  }
  
  @Test
  public void testNewVariableDecl() {    
    checkValid("", "contextconditions.valid.NewVarDecl");
  }
  
  @Test
  public void testUsedPortsNotExist() {
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.UsedPortsNotExist");
    AbstractCoCoTestExpectedErrorInfo expectedErrors = new AbstractCoCoTestExpectedErrorInfo(2, "xMA030");
    // error occurs in symboltable only. Therefore no CoCo check via checkInvalid
    expectedErrors.checkExpectedPresent(Log.getFindings(), "");
  }
  
  @Test
  public void testComponentWithAJavaAndAutomaton() {
    ASTMontiArcNode node = getAstNode("","contextconditions.invalid.ComponentWithAJavaAndAutomaton");
    checkInvalid(MontiArcCoCos.createChecker(), node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA050"));
  }

  @Test
  public void testChangeOfIncomingPort() {
    ASTMontiArcNode node = getAstNode("" + "contextconditions", "invalid.ChangesIncomingPortInCompute");
    checkInvalid(MontiArcCoCos.createChecker(), node, new AbstractCoCoTestExpectedErrorInfo(4, "xMA078"));
  }
  
}
