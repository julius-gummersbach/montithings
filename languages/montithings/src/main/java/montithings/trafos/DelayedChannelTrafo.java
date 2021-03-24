// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTPortAccess;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionBuilder;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpressionBuilder;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpressionBuilder;
import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteralBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatementBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatementBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlockBuilder;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._ast.ASTBehavior;
import montithings._visitor.FindConnectionsVisitor;
import montithings.util.TrafoUtil;

import javax.json.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Transformer for delaying channels.
 * <p>
 * Connections which should be delayed are intercepted by a new component which just forwards the messages,
 * but adds a certain delay.
 */

public class DelayedChannelTrafo extends BasicTransformations implements MontiThingsTrafo {
    protected static final String TOOL_NAME = "DelayedChannelTrafo";

    private Collection<ASTMACompilationUnit> additionalTrafoModels;

    private final ReplayDataHandler dataHandler;

    private final File modelPath;

    public DelayedChannelTrafo(File modelPath, File replayDataFile) {
        this.dataHandler = new ReplayDataHandler(replayDataFile);
        this.modelPath = modelPath;
    }

    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation: Delayed Channels: " + targetComp.getComponentType().getName(), TOOL_NAME);

        this.additionalTrafoModels = new ArrayList<>();
        List<ASTMACompilationUnit> allModels = new ArrayList<>();
        allModels.addAll(originalModels);
        allModels.addAll(addedModels);

        FindConnectionsVisitor visitor = new FindConnectionsVisitor();
        targetComp.accept(visitor);
        List<FindConnectionsVisitor.Connection> connections = visitor.getConnections();

        for (FindConnectionsVisitor.Connection connection : connections) {
            transform(allModels, targetComp, connection.source, connection.target);
        }

        return this.additionalTrafoModels;
    }

    public void transform(Collection<ASTMACompilationUnit> models, ASTMACompilationUnit comp, ASTPortAccess portSource, ASTPortAccess portTarget) throws Exception {
        // A source or target port can either be declared locally or within a subcomponent.
        String sourceTypeName = TrafoUtil.getPortOwningComponentType(comp, portSource);

        // Name of the added component, e.g. hierarchy.Example.SourceValueSinkValueDelay
        String channelInterceptorComponentName =
                TrafoUtil.replaceDotsWithCamelCase(portSource.getQName()) +
                        TrafoUtil.replaceDotsWithCamelCase(portTarget.getQName()) +
                        "Delay";

        channelInterceptorComponentName = TrafoUtil.capitalize(channelInterceptorComponentName);

        ASTMCQualifiedName fullyQName = TrafoUtil.copyASTMCQualifiedName(comp.getPackage());
        fullyQName.addParts(channelInterceptorComponentName);

        // Adds instantiation statement, e.g. "SourceValueSinkValueDelay sourcevaluesinkvaluedelay";
        addSubComponentInstantiation(comp, fullyQName, channelInterceptorComponentName.toLowerCase(), createEmptyArguments());

        // Find out the port type. Therefore, first get the component of the source and search for the port.
        // This is only done with the source port as port types have to match anyway
        // TODO remove null if possible
        ASTMCType portType = null;
        try {
            String qName = TrafoUtil.getFullyQNameFromImports(modelPath, comp, sourceTypeName).getQName();
            ASTMACompilationUnit compSource = TrafoUtil.getComponentByName(models, qName);
            portType = TrafoUtil.getPortTypeByName(compSource, portSource.getPort());

            // actually creates the model of the intercepting component
            ASTMACompilationUnit channelInterceptorComponent = createCompilationUnit(comp.getPackage(), channelInterceptorComponentName);

            String qCompSourceName = comp.getPackage() + "." + comp.getComponentType().getName() + "." + portSource.getComponent();
            String qCompTargetName = comp.getPackage() + "." + comp.getComponentType().getName() + "." + portTarget.getComponent();
            List<Long> delays = dataHandler.getDelays(qCompSourceName, portSource.getPort(), qCompTargetName, portTarget.getPort());

            addBehavior(channelInterceptorComponent, delays);

            addPort(channelInterceptorComponent,
                    "in",
                    false,
                    portType);
            addPort(channelInterceptorComponent,
                    "out",
                    true,
                    portType);

            this.additionalTrafoModels.add(channelInterceptorComponent);

            // Replaces the old connection
            removeConnection(comp, portSource, portTarget);
            addConnection(comp, portSource.getQName(), channelInterceptorComponentName.toLowerCase() + "." + "in");
            addConnection(comp, channelInterceptorComponentName.toLowerCase() + "." + "out", portTarget.getQName());

        } catch (ClassNotFoundException e) {
            String test = "asd";
            //TODO
        } catch (NoSuchElementException e) {
            // model was not found. it is probably a generic type. in this case search for the port within the interfaces
            if (TrafoUtil.isGeneric(comp, sourceTypeName)) {
                for (String iface : TrafoUtil.getInterfaces(comp, sourceTypeName)) {
                    ASTMACompilationUnit ifaceComp = TrafoUtil.getComponentByName(models, comp.getPackage() + "." + iface);
                    try {
                        portType = TrafoUtil.getPortTypeByName(ifaceComp, portSource.getPort());
                    } catch (Exception e1) {
                        //ignore, check next iface
                    }
                }
            }
        }
        if (portType == null) {
            throw new NoSuchElementException("No such port instance found which is named " + portSource.getPort());
        }
    }

    void addBehavior(ASTMACompilationUnit comp, List<Long> delays) {
        /*
            int index = 0;

            if (index == 0) {
                delay(299);
            }
            if (index == 1) {
                delay(762);
            }
            if ...

            index = index + 1;
        */
        ASTMCJavaBlockBuilder javaBlockBuilder = MontiThingsMill.mCJavaBlockBuilder();

        // Initiate index variable
        addIntFieldDeclaration(comp, "index", 0);

        int index = 0;
        for (long delay : delays) {
            long delayMs = delay / 1000000;
            javaBlockBuilder.addMCBlockStatement(addDelayIfStatement(index, delayMs));
            index++;
        }

        // implement index += 1;
        javaBlockBuilder.addMCBlockStatement(addIncrementIndexStatement());

        // implement out = in;
        javaBlockBuilder.addMCBlockStatement(addInOutAssignment());


        ASTBehavior behavior = addEmptyBehavior(comp);
        behavior.setMCJavaBlock(javaBlockBuilder.build());
    }

    private ASTMCBlockStatement addInOutAssignment() {
        ASTNameExpression inNameExpression = MontiThingsMill.nameExpressionBuilder().setName("in").build();
        ASTNameExpression outNameExpression = MontiThingsMill.nameExpressionBuilder().setName("out").build();

        ASTAssignmentExpressionBuilder assignmentBuilder = MontiThingsMill.assignmentExpressionBuilder();
        assignmentBuilder.setLeft(outNameExpression);
        assignmentBuilder.setOperator(ASTConstantsAssignmentExpressions.EQUALS);
        assignmentBuilder.setRight(inNameExpression);

        ASTExpressionStatementBuilder assignmentExpressionStatementBuilder = MontiThingsMill.expressionStatementBuilder();
        assignmentExpressionStatementBuilder.setExpression(assignmentBuilder.build());

        return assignmentExpressionStatementBuilder.build();
    }

    private ASTMCBlockStatement addIncrementIndexStatement() {
        ASTNameExpression indexNameExpression = MontiThingsMill.nameExpressionBuilder().setName("index").build();

        ASTNatLiteral oneNatLiteral = MontiThingsMill.natLiteralBuilder().setDigits("1").build();
        ASTLiteralExpressionBuilder incrementIndexExpressionBuilder = MontiThingsMill.literalExpressionBuilder();
        incrementIndexExpressionBuilder.setLiteral(oneNatLiteral);

        ASTAssignmentExpressionBuilder incrementAssignmentExpressionBuilder = MontiThingsMill.assignmentExpressionBuilder();
        incrementAssignmentExpressionBuilder.setLeft(indexNameExpression);
        incrementAssignmentExpressionBuilder.setOperator(ASTConstantsAssignmentExpressions.PLUSEQUALS);
        incrementAssignmentExpressionBuilder.setRight(incrementIndexExpressionBuilder.build());

        ASTExpressionStatementBuilder incrementExpressionStatementBuilder = MontiThingsMill.expressionStatementBuilder();
        incrementExpressionStatementBuilder.setExpression(incrementAssignmentExpressionBuilder.build());

        return incrementExpressionStatementBuilder.build();
    }

    private ASTMCBlockStatement addDelayIfStatement(int index, long delay) {
        ASTNameExpression indexNameExpression = MontiThingsMill.nameExpressionBuilder().setName("index").build();

        // Building condition
        ASTNatLiteralBuilder rightNatLiteralBuilder = MontiThingsMill.natLiteralBuilder();
        rightNatLiteralBuilder.setDigits(String.valueOf(index));

        ASTLiteralExpressionBuilder rightExpressionBuilder = MontiThingsMill.literalExpressionBuilder();
        rightExpressionBuilder.setLiteral(rightNatLiteralBuilder.build());

        ASTEqualsExpressionBuilder conditionBuilder = MontiThingsMill.equalsExpressionBuilder();
        conditionBuilder.setLeft(indexNameExpression);
        conditionBuilder.setOperator("==");
        conditionBuilder.setRight(rightExpressionBuilder.build());

        // Building then statement
        ASTNameExpression delayNameExpression = MontiThingsMill.nameExpressionBuilder().setName("delay").build();

        ASTNatLiteral delayNatLiteral = MontiThingsMill.natLiteralBuilder().setDigits(String.valueOf(delay)).build();
        ASTLiteralExpression delayLiteralExpression = MontiThingsMill.literalExpressionBuilder().setLiteral(delayNatLiteral).build();

        ASTArgumentsBuilder delayArgsBulder = MontiThingsMill.argumentsBuilder();
        delayArgsBulder.addExpression(delayLiteralExpression);

        ASTCallExpressionBuilder thenCallDelayExpressionBuilder = MontiThingsMill.callExpressionBuilder();
        thenCallDelayExpressionBuilder.setExpression(delayNameExpression);
        thenCallDelayExpressionBuilder.setArguments(delayArgsBulder.build());
        thenCallDelayExpressionBuilder.setName("test");

        ASTExpressionStatementBuilder thenExpressionStatementBuilder = MontiThingsMill.expressionStatementBuilder();
        thenExpressionStatementBuilder.setExpression(thenCallDelayExpressionBuilder.build());

        ASTMCJavaBlockBuilder thenStatementBuilder = MontiThingsMill.mCJavaBlockBuilder();
        thenStatementBuilder.addMCBlockStatement(thenExpressionStatementBuilder.build());

        ASTIfStatementBuilder ifStatementBuilder = MontiThingsMill.ifStatementBuilder();
        ifStatementBuilder.setCondition(conditionBuilder.build());
        ifStatementBuilder.setThenStatement(thenStatementBuilder.build());
        ifStatementBuilder.setElseStatementAbsent();

        return ifStatementBuilder.build();
    }
}
