/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components.head.generics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.AllGenericParametersOfSuperClassSet;
import montiarc.cocos.MontiArcCoCos;
import montiarc.cocos.TypeParameterNamesUnique;
import montiarc.helper.SymbolPrinter;

/**
 * This class checks all context conditions related to the definition of generic type parameters
 *
 * @author Crispin Kirchner, Andreas Wortmann
 */
public class GenericsTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.head.generics";
  
  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testTypeParameterNamesUniqueValid() {
    checkValid(PACKAGE + "." + "TypeParameterNamesUnique");
  }
  
  @Test
  public void testTypeParameterNamesUniqueInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new TypeParameterNamesUnique()),
        loadComponentAST(PACKAGE + "." + "TypeParameterNamesAbiguous"),
        new ExpectedErrorInfo(1, "xMA006"));
  }
  
  @Test
  public void testGarage() {
    checkValid(PACKAGE + "." + "Garage");
  }
  
  @Test
  public void testSubCompExtendsGenericComparableCompValid() {
    checkValid(PACKAGE + "." + "SubCompExtendsGenericComparableCompValid");
  }
  
  @Ignore("TODO remove when new JavaDSL is integrated") 
  @Test
  public void testcomponentExtendsGenericComponent() {
    checkValid(PACKAGE + "." + "ComponentExtendsGenericComponent");
    checkValid(PACKAGE + "." + "ComponentExtendsGenericComponent2");
    checkValid(PACKAGE + "." + "ComponentExtendsGenericComponent3");
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ComponentExtendsGenericComponent4");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new AllGenericParametersOfSuperClassSet()), node, new ExpectedErrorInfo(1,"xMA087"));
    node = loadComponentAST(PACKAGE + "." + "ComponentExtendsGenericComponent5");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new AllGenericParametersOfSuperClassSet()), node, new ExpectedErrorInfo(1,"xMA088"));
    node = loadComponentAST(PACKAGE + "." + "ComponentExtendsGenericComponent6");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new AllGenericParametersOfSuperClassSet()), node, new ExpectedErrorInfo(1,"xMA089"));
  }
  
  @Ignore("TODO implement coco! Check model for expected errors and coco reference.")
  @Test
  public void testSubCompExtendsGenericCompInvalid0(){
    checkInvalid(MontiArcCoCos.createChecker(), loadComponentAST(PACKAGE + "." + "SubCompExtendsGenericCompInvalid0"), new ExpectedErrorInfo());
  }
  
  @Ignore("TODO implement coco! Check model for expected errors and coco reference.")
  @Test
  public void testSubCompExtendsGenericCompInvalid1(){
    checkInvalid(MontiArcCoCos.createChecker(), loadComponentAST(PACKAGE + "." + "SubCompExtendsGenericCompInvalid1"), new ExpectedErrorInfo());
  }
  
  @Ignore("TODO implement coco! Check model for expected errors and coco reference.")
  @Test
  public void testSubCompExtendsGenericCompInvalid2(){
    checkInvalid(MontiArcCoCos.createChecker(), loadComponentAST(PACKAGE + "." + "SubCompExtendsGenericCompInvalid2"), new ExpectedErrorInfo());
  }
  
  @Test
  public void testSubCompExtendsGenericCompValid() {
    checkValid(PACKAGE + "." + "SubCompExtendsGenericCompValid");
  }
  
  @Test
  public void SubSubCompExtendsGenericComparableCompValid() {
    checkValid(PACKAGE + "." + "SubSubCompExtendsGenericComparableCompValid"); 
  }
  
  /**
   * TODO: ValueSymbol!?
   */
  @Test
  public void testUsingComplexGenericParams() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "UsingComplexGenericParams");
    
    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    
    ComponentInstanceSymbol delay = (ComponentInstanceSymbol) comp.getSpannedScope()
        .resolve("cp", ComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(delay);
    assertEquals("cp", delay.getName());
    
    assertEquals(2, delay.getConfigArguments().size());
    assertEquals("new int[] {1, 2, 3}",
        SymbolPrinter.printConfigArgument(delay.getConfigArguments().get(0)));
    // TODO value symbol
    // assertEquals(Kind.ConstructorCall, delay.getConfigArguments().get(0).getKind());
    // assertEquals("1",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(0).getValue());
    // assertEquals("2",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(1).getValue());
    // assertEquals("3",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(2).getValue());
    
    assertEquals("new HashMap<List<K>, List<V>>()",
        SymbolPrinter.printConfigArgument(delay.getConfigArguments().get(1)));
    // TODO value symbol
    // assertEquals(Kind.ConstructorCall, delay.getConfigArguments().get(1).getKind());
    // ArcdTypeReferenceEntry typeRef = delay.getConfigArguments().get(1).getType();
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(0).getType().getName());
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(1).getType().getName());
    // assertEquals("K",
    // typeRef.getTypeParameters().get(0).getTypeParameters().get(0).getType().getName());
    // assertEquals("V",
    // typeRef.getTypeParameters().get(1).getTypeParameters().get(0).getType().getName());
  }
}
