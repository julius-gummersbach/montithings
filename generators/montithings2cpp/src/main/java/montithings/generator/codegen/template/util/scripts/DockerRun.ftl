<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign instances = ComponentHelper.getInstances(comp)>



rm -f dockerStop.sh
<#if config.getSplittingMode().toString() == "OFF">
    ${tc.includeArgs("template.util.scripts.DockerRunCommand", [comp.getFullName(), comp.getFullName()?lower_case, config])}
<#else>
  <#list instances as pair >
      ${tc.includeArgs("template.util.scripts.DockerRunCommand", [pair.getKey().fullName, pair.getValue(), config])}
  </#list>
</#if>

<#if config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
  # Start DCPSInfoRepo
  CONTAINER=$(docker run --name dcpsinforepo -d -p 12345:12345 registry.git.rwth-aachen.de/monticore/montithings/core/openddsdcpsinforepo)
  echo docker stop $CONTAINER >> dockerStop.sh
</#if>
chmod +x dockerStop.sh