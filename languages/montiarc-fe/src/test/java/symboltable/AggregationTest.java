/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

/**
 * @author Crispin Kirchner, Andreas Wortmann
 * @version $Revision$, $Date$
 */
public class AggregationTest extends AbstractSymboltableTest {
  
  private final String MODEL_PATH = "src/test/resources/symboltable";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testComponentWithCDType() {
    Scope symTab = createSymTab(MODEL_PATH);
    
    CDSymbol types = symTab.<CDSymbol> resolve("aggregation.Types", CDSymbol.KIND).orElse(null);
    assertNotNull(types);
    
    ComponentSymbol component = symTab
        .<ComponentSymbol> resolve("aggregation.ComponentWithCDType", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(component);
  }
  
  @Test
  public void testFormalTypeParameterComponentWithCDType() {
    Scope symTab = createSymTab(MODEL_PATH);
    
    CDSymbol types = symTab.<CDSymbol> resolve("aggregation.Types", CDSymbol.KIND).orElse(null);
    assertNotNull(types);
    
    ComponentSymbol component = symTab
        .<ComponentSymbol> resolve("aggregation.FTPComponentWithCDType", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(component);
  }
  
  @Test
  public void testCD() {
    final ModelPath modelPath = new ModelPath(Paths.get(MODEL_PATH));
    
    ModelingLanguageFamily family = new ModelingLanguageFamily();
    CD4AnalysisLanguage lang = new CD4AnalysisLanguage();
    family.addModelingLanguage(lang);
    GlobalScope scope = new GlobalScope(modelPath, family);
    
    Optional<CDSymbol> cd = scope.<CDSymbol> resolve("aggregation.Units",
        CDSymbol.KIND);
    assertTrue(cd.isPresent());
    Optional<CDTypeSymbol> quantity = scope.<CDTypeSymbol> resolve("aggregation.Units.Quantity",
        CDTypeSymbol.KIND);
    assertTrue(quantity.isPresent());
    Optional<JTypeSymbol> quantityAsJType = scope.<JTypeSymbol> resolve("aggregation.Units.Quantity",
        JTypeSymbol.KIND);
    assertTrue(quantityAsJType.isPresent());
  }
  
  @Test
  public void testCDusingMAWithCD() {
    Scope symTab = createSymTab(MODEL_PATH);
    Optional<CDSymbol> cd = symTab.<CDSymbol> resolve("aggregation.Units",
        CDSymbol.KIND);
    assertTrue(cd.isPresent());
    Optional<CDTypeSymbol> quantity = symTab.<CDTypeSymbol> resolve("aggregation.Units.Quantity",
        CDTypeSymbol.KIND);
    assertTrue(quantity.isPresent());
  }
  
  @Test
  /**
   * A.arc: import Domain; component A { ports in Room r; } Domain.cd: classdiagram Domain { class
   * Room { int x; int y; } }
   */
  public void test() {
    Scope symTab = createSymTab(MODEL_PATH);
    
    ComponentSymbol simulationSymbol = symTab
        .<ComponentSymbol> resolve("aggregation.Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationSymbol);
    
    PortSymbol speed = simulationSymbol.getIncomingPort("speed").orElse(null);
    assertNotNull(speed);
    
    JTypeReference<? extends JTypeSymbol> ref = speed.getTypeReference();
    assertTrue(ref.existsReferencedSymbol());
//    JTypeSymbol typeSymbol = ref.getReferencedSymbol();
//    assertTrue(typeSymbol instanceof CDTypeSymbol);
  }
  
  @Test
  public void testSuperClass() {
    Scope scope = createSymTab("src/test/resources/symboltable");
    
    ComponentSymbol simulationComponent = scope
        .<ComponentSymbol> resolve("superclass.Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationComponent);
    
    ComponentSymbol errorComponent = simulationComponent
        .getInnerComponent("ErrorFilter")
        .orElse(null);
    assertNotNull(errorComponent);
    
    PortSymbol messagePort = errorComponent.getIncomingPort("msgs").orElse(null);
    assertNotNull(messagePort);
    
    JTypeReference<?> messageTypeRef = messagePort.getTypeReference();
    assertTrue(messageTypeRef.existsReferencedSymbol());
    assertTrue(messageTypeRef instanceof CommonJTypeReference);
    
    JTypeSymbol messageType = messageTypeRef.getReferencedSymbol();
//    assertTrue(messageType instanceof CDTypeSymbol);
    assertTrue(messageType.isAbstract());
    assertFalse(messageType.isInterface());
    assertFalse(messageType.isInnerType());
    
    PortSymbol errorPort = errorComponent
        .getOutgoingPort("errors")
        .orElse(null);
    assertNotNull(errorPort);
    
    JTypeReference<?> errorTypeRef = errorPort.getTypeReference();
    assertTrue(errorTypeRef.existsReferencedSymbol());
    
    JTypeSymbol errorType = errorTypeRef.getReferencedSymbol();
//    assertTrue(errorType instanceof CDTypeSymbol);
    
    messageTypeRef = errorType.getSuperClass().orElse(null);
//    assertTrue(messageTypeRef instanceof CDTypeSymbolReference);
    assertEquals("MyMessage", messageTypeRef.getName());
    assertTrue(messageTypeRef.existsReferencedSymbol());
    
    messageType = messageTypeRef.getReferencedSymbol();
    assertEquals("superclass.Data.MyMessage", messageType.getFullName());
    assertEquals("MyMessage", messageType.getName());
  }
  
  @Test
  public void testFields() {
    Scope scope = createSymTab("src/test/resources/symboltable");
    
    ComponentSymbol simulationSymbol = scope
        .<ComponentSymbol> resolve("superclass.Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationSymbol);
    
    ComponentSymbol errorFilterSymbol = simulationSymbol
        .getInnerComponent("ErrorFilter")
        .orElse(null);
    assertNotNull(errorFilterSymbol);
    
    PortSymbol msgsPort = errorFilterSymbol.getIncomingPort("msgs").orElse(null);
    assertNotNull(msgsPort);
    
    JTypeReference<?> msgsTypeRef = msgsPort.getTypeReference();
    assertTrue(msgsTypeRef.existsReferencedSymbol());
    
    JTypeSymbol msgsType = msgsTypeRef.getReferencedSymbol();
    List<?> fields = msgsType.getFields();
    assertEquals(2, fields.size());
    
    assertTrue(msgsType.getField("content").isPresent());
    assertTrue(msgsType.getField("timestamp").isPresent());
  }
  
  @Ignore
  @Test
  public void testMethod() {
    Scope symTab = createSymTab(MODEL_PATH);
    
    ComponentSymbol simulationSymbol = symTab
        .<ComponentSymbol> resolve("superclass.Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationSymbol);
    
    PortSymbol msgsPort = simulationSymbol.getIncomingPort("msgs").orElse(null);
    assertNotNull(msgsPort);
    
    JTypeReference<?> msgTypeRef = msgsPort.getTypeReference();
    assertTrue(msgTypeRef.existsReferencedSymbol());
    
    JTypeSymbol msgType = msgTypeRef.getReferencedSymbol();
    List<? extends JMethodSymbol> methods = msgType.getMethods();
    assertFalse(methods.isEmpty());
    
    assertEquals("toString", methods.get(0).getName());
  }
  
  @Test
  public void testInterface() {
    Scope scope = createSymTab("src/test/resources/symboltable");
    
    ComponentSymbol simulationSymbol = scope
        .<ComponentSymbol> resolve("superclass.Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationSymbol);
    
    PortSymbol msgsPort = simulationSymbol.getIncomingPort("msgs").orElse(null);
    assertNotNull(msgsPort);
    
    JTypeReference<?> msgsTypeRef = msgsPort.getTypeReference();
    assertTrue(msgsTypeRef.existsReferencedSymbol());
    
    JTypeSymbol msgsType = msgsTypeRef.getReferencedSymbol();
    assertNotNull(msgsType);
    
    assertFalse(msgsType.getInterfaces().isEmpty());
    JTypeReference<?> firstInterfaceRef = msgsType.getInterfaces().get(0);
    assertTrue(firstInterfaceRef.existsReferencedSymbol());
    
    assertEquals("Traceable", firstInterfaceRef.getName());
  }
  
  @Test
  public void testKind() {
    Scope scope = createSymTab("src/test/resources/symboltable");
    
    ComponentSymbol simulationSymbol = scope
        .<ComponentSymbol> resolve("superclass.Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationSymbol);
    
    PortSymbol msgsPort = simulationSymbol.getIncomingPort("msgs").orElse(null);
    assertNotNull(msgsPort);
    
    JTypeSymbol msgsType = msgsPort.getTypeReference().getReferencedSymbol();
    
    assertTrue(msgsType.isKindOf(JTypeSymbol.KIND));
    assertTrue(msgsType.isKindOf(CDTypeSymbol.KIND));
  }
    
}