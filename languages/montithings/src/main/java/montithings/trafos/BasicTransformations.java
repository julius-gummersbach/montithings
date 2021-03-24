package montithings.trafos;

import arcbasis._ast.*;
import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteralBuilder;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteralBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.types.mcbasictypes._ast.*;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMACompilationUnitBuilder;
import montithings.MontiThingsMill;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTBehaviorBuilder;
import montithings._ast.ASTMTComponentModifierBuilder;
import montithings._ast.ASTMTComponentTypeBuilder;
import montithings._auxiliary.ComfortableArcMillForMontiThings;
import montithings.util.TrafoUtil;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static montithings.util.GenericBindingUtil.printSimpleType;


public abstract class BasicTransformations {
    /**
     * @param comp   AST of component which is modified
     * @param source Qualified port name on the left side, e.g. source.value
     * @param target Qualified port name on the right side, e.g. sink.value
     */
    protected void addConnection(ASTMACompilationUnit comp, String source, String target) {
        ASTConnectorBuilder connectorBuilder = ComfortableArcMillForMontiThings.connectorBuilder();
        connectorBuilder.setSource(source);
        connectorBuilder.addTarget(target);
        comp.getComponentType().getBody().addArcElement(connectorBuilder.build());
    }

    /**
     * Searches and removes connection defined in the given component with the source port on the left hand side and
     * the target port at the right hand side. If the connection is not defined, nothing will be removed.
     *
     * @param comp   AST of component which is modified
     * @param source AST of source port
     * @param target AST of target port
     */
    protected void removeConnection(ASTMACompilationUnit comp, ASTPortAccess source, ASTPortAccess target) {
        Optional<ASTConnector> match =
                comp.getComponentType().getConnectors().stream()
                        .filter(conn -> conn.getTargetList().contains(target))
                        .filter(conn -> conn.getSource().equals(source))
                        .findFirst();
        match.ifPresent(astConnector -> comp.getComponentType().getBody().removeArcElement(astConnector));
    }


    /**
     * Add a port to the given component.
     *
     * @param comp       AST of component which is modified
     * @param name       Name of the port
     * @param isOutgoing Defines the direction of the port
     * @param type       Defines the type of the port
     */
    protected void addPort(ASTMACompilationUnit comp, String name, Boolean isOutgoing, ASTMCType type) {
        ASTPortDeclarationBuilder portDeclarationBuilder = ComfortableArcMillForMontiThings.portDeclarationBuilder();
        portDeclarationBuilder
                .setIncoming(!isOutgoing)
                .addPort(name)
                .setMCType(type);

        ASTComponentInterfaceBuilder astComponentInstanceBuilder = ComfortableArcMillForMontiThings.componentInterfaceBuilder();

        astComponentInstanceBuilder.addPortDeclaration(portDeclarationBuilder.build());
        ASTComponentInterface build = astComponentInstanceBuilder.build();
        comp.getComponentType().getBody().addArcElement(build);
    }

    /**
     * Creates and returns a new empty compilation unit.
     *
     * @param packageId Package which the new component should belong to
     * @param typeName  Name of the component type, e.g. Source
     * @return ASTMACompilationUnit of the newly created component
     */
    protected ASTMACompilationUnit createCompilationUnit(ASTMCQualifiedName packageId, String typeName) {
        ASTMACompilationUnitBuilder compBuilder = MontiThingsMill.mACompilationUnitBuilder();
        compBuilder.setPackage(packageId);

        ASTComponentHeadBuilder headBuilder = ComfortableArcMillForMontiThings.componentHeadBuilder();
        ASTComponentBodyBuilder bodyBuilder = ComfortableArcMillForMontiThings.componentBodyBuilder();
        ASTMTComponentModifierBuilder componentModifier = MontiThingsMill.mTComponentModifierBuilder();

        ASTMTComponentTypeBuilder typeBuilder = MontiThingsMill.mTComponentTypeBuilder();
        typeBuilder.setHead(headBuilder.build());
        typeBuilder.setBody(bodyBuilder.build());
        typeBuilder.setName(typeName);
        typeBuilder.setMTComponentModifier(componentModifier.build());
        compBuilder.setComponentType(typeBuilder.build());

        return compBuilder.build();
    }

    /**
     * Adds a given qualified name to the imports of the given component
     *
     * @param packageId ASTMCQualifiedName of the imported component
     * @param comp      Component which should import the packageId
     */
    protected void addImportStatement(ASTMCQualifiedName packageId, ASTMACompilationUnit comp) {
        ASTMCImportStatement importStatement = MontiThingsMill.mCImportStatementBuilder()
                .setMCQualifiedName(packageId).build();

        comp.addImportStatement(importStatement);
    }

    /**
     * Adds an empty behavior java block to the given component.
     *
     * @param comp AST of component which is modified
     * @return empty behavior block
     */
    protected ASTBehavior addEmptyBehavior(ASTMACompilationUnit comp) {
        ASTMCJavaBlock javaBlock = MontiThingsMill.mCJavaBlockBuilder().build();

        ASTBehaviorBuilder behavior = MontiThingsMill.behaviorBuilder();
        behavior.setMCJavaBlock(javaBlock);
        ASTBehavior build = behavior.build();
        comp.getComponentType().getBody().addArcElement(build);

        return build;
    }


    /**
     * @param comp         AST of component which is modified
     * @param qName        Type name
     * @param instanceName Instance name
     * @param args         Instance arguments
     * @return
     */
    protected ASTComponentInstantiation addSubComponentInstantiation(ASTMACompilationUnit comp, ASTMCQualifiedName qName, String instanceName, ASTArguments args) {
        ASTComponentInstantiationBuilder instantiationBuilder = ComfortableArcMillForMontiThings.componentInstantiationBuilder();

        instantiationBuilder.addInstance(instanceName);
        instantiationBuilder.getComponentInstance(0).setArguments(args);
        instantiationBuilder.setMCType(createCompilationUnitType(qName.getBaseName()));

        ASTComponentInstantiation instantiation = instantiationBuilder.build();
        comp.getComponentType().getBody().addArcElement(instantiation);

        addImportStatement(qName, comp);

        return instantiation;
    }

    protected void addIntFieldDeclaration(ASTMACompilationUnit comp, String name, int value) {
        ASTNatLiteralBuilder natLiteralBuilder = MontiThingsMill.natLiteralBuilder();
        natLiteralBuilder.setDigits(String.valueOf(value));

        ASTLiteralExpressionBuilder literalExpressionBuilder = MontiThingsMill.literalExpressionBuilder();
        literalExpressionBuilder.setLiteral(natLiteralBuilder.build());

        ASTMCPrimitiveTypeBuilder mcPrimitiveTypeBuilder = MontiThingsMill.mCPrimitiveTypeBuilder();
        mcPrimitiveTypeBuilder.setPrimitive(ASTConstantsMCBasicTypes.INT);

        ASTArcFieldDeclarationBuilder fieldDeclarationBuilder = MontiThingsMill.arcFieldDeclarationBuilder();
        fieldDeclarationBuilder.addArcField(name, literalExpressionBuilder.build());
        fieldDeclarationBuilder.setMCType(mcPrimitiveTypeBuilder.build());

        comp.getComponentType().getBody().addArcElement(fieldDeclarationBuilder.build());
    }

    /**
     * Searches and replaces the types of matching instantiations
     *
     * @param comp      AST of component which is modified
     * @param modelPath Path of the models which is used to gather the fully qualified name of components
     * @param type      The type which should get replaced
     * @param newType   The new type
     */
    protected void replaceComponentInstantiationType(ASTMACompilationUnit comp, File modelPath, String type, String newType) throws Exception {
        for (ASTComponentInstantiation subComponentInstantiation : comp.getComponentType().getSubComponentInstantiations()) {
            String typeSubComp = printSimpleType(subComponentInstantiation.getMCType());
            if (type.equals(typeSubComp)) {
                subComponentInstantiation.setMCType(createCompilationUnitType(newType));
            }
        }

        ASTMCQualifiedName qNameOldType = TrafoUtil.getFullyQNameFromImports(modelPath, comp, type);
        ASTMCQualifiedName qNameNewType = TrafoUtil.copyASTMCQualifiedName(qNameOldType);
        qNameNewType.removeParts(qNameNewType.sizeParts() - 1);
        qNameNewType.addParts(newType);
        addImportStatement(qNameNewType, comp);
    }

    /**
     * Returns all instantiations of a given type within the given component
     *
     * @param comp AST of component which is searched in
     * @param type The type as a string which is searched for
     * @return List<ASTComponentInstantiation> with the given type
     */
    protected List<ASTComponentInstantiation> getInstantiationsByType(ASTMACompilationUnit comp, String type) {
        return comp.getComponentType().getSubComponentInstantiations().stream()
                .filter(i -> printSimpleType(i.getMCType()).equals(type))
                .collect(Collectors.toList());
    }

    protected ASTArguments createEmptyArguments() {
        ASTArgumentsBuilder builder = new ASTArgumentsBuilder();
        return builder.build();
    }

    protected ASTArguments createArguments(List<String> args) {
        ASTStringLiteralBuilder stringLiteralBuilder = new ASTStringLiteralBuilder();
        ASTLiteralExpressionBuilder literalExpressionBuilder = new ASTLiteralExpressionBuilder();
        ASTArgumentsBuilder argumentsBuilder = new ASTArgumentsBuilder();

        for (String arg : args) {
            stringLiteralBuilder.setSource(arg);
            literalExpressionBuilder.setLiteral(stringLiteralBuilder.build());
            argumentsBuilder.addExpression(literalExpressionBuilder.build());
        }

        return argumentsBuilder.build();
    }

    /**
     * Creates a ASTMCQualifiedType with the given name.
     *
     * @param name Name of the type
     * @return Corresponding ASTMCQualifiedType
     */
    private ASTMCQualifiedType createCompilationUnitType(String name) {
        ASTMCQualifiedName astmcQualifiedName = MontiThingsMill.mCQualifiedNameBuilder()
                .setPartsList(Collections.singletonList(name))
                .build();
        return MontiThingsMill.mCQualifiedTypeBuilder().
                setMCQualifiedName(astmcQualifiedName).build();
    }

}
