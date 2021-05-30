<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">

    <#if config.getMessageBroker().toString() == "DDS">
        logTracerInterface = new LogTracerDDSClient(argc, &argv,
            instanceName, false, true, true, false);
    <#elseif config.getMessageBroker().toString() == "MQTT">
        logTracerInterface = new LogTracerMQTTClient(instanceName, false);
    </#if>

logTracer = new LogTracer(instanceName, *logTracerInterface);
</#if>
