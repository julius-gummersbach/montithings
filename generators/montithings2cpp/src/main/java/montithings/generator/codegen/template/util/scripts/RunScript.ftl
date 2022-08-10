<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "sensorActuatorPorts", "hwcPythonScripts", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>

<#if brokerIsDDS && splittingModeIsDistributed>
echo "Starting DCPSInfoRepo..."
docker run --name dcpsinforepo --rm -d -p 12345:12345 registry.git.rwth-aachen.de/monticore/montithings/core/openddsdcpsinforepo
echo "Waiting 5 seconds..."
sleep 5
echo "Starting components..."
</#if>



<#if brokerIsMQTT && hwcPythonScripts?size!=0>
  exec bash -c 'export PYTHONPATH=$PYTHONPATH:../../python; python3 -u "python/sensoractuatormanager.py" > "python/sensoractuatormanager.log" 2>&1 &' '{}' \;
</#if>

# Run Python Ports
if [ -d "hwc" ]; then
echo starting python ports...
find hwc -name "*.py" -exec bash -c 'export PYTHONPATH=$PYTHONPATH:../../python; python3 -u "$0" > "$0.log" 2>&1 &' '{}' \;
sleep 2 # wait for interpreted code to be ready - control MQTT ports MUST be subscribed to work
echo python ports started
fi

<#list instances as pair >
  <#if brokerIsMQTT>
  ./${pair.getKey().fullName} --name ${pair.getValue()} --brokerHostname localhost --brokerPort 1883  --localHostname localhost > ${pair.getValue()}.log 2>&1 &
  <#elseif brokerIsDDS>
    <#if splittingModeIsDistributed>
      ./${pair.getKey().fullName} --name ${pair.getValue()} --DCPSConfigFile dcpsconfig.ini --DCPSInfoRepo localhost:12345 > ${pair.getValue()}.log 2>&1 &
    <#else>
      ./${pair.getKey().fullName} --name ${pair.getValue()} --DCPSConfigFile dcpsconfig.ini > ${pair.getValue()}.log 2>&1 &
    </#if>
  <#else>
  ./${pair.getKey().fullName} --name ${pair.getValue()} --managementPort ${config.getComponentPortMap().getManagementPort(pair.getValue())} --dataPort ${config.getComponentPortMap().getCommunicationPort(pair.getValue())} > ${pair.getValue()}.log 2>&1 &
  </#if>
</#list>
<#if brokerIsMQTT>
  <#list sensorActuatorPorts as port >
  ./${port} --name ${port} --brokerPort 1883 --localHostname localhost > ${port}.log 2>&1 &
  </#list>
</#if>

