<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("comp", "config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if config.getRecordingMode().toString() == "ON">
    if (montithings::library::hwcinterceptor::isRecording)
    {
      auto timeStartCalc = std::chrono::high_resolution_clock::now();
      montithings::library::hwcinterceptor::setLastComputeTs(timeStartCalc.time_since_epoch().count());

    <#-- if statement is closed in the corresponding RecorderComputationMeasurementEnd.ftl file -->
</#if>
