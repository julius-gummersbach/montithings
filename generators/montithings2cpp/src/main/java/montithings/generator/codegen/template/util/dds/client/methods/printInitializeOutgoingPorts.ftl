<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
std::string topic = "";

bool isRecordingEnabled = false;
<#if config.getRecordingMode().toString() == "ON">
    isRecordingEnabled = true;
</#if>

<#list comp.getPorts() as p>
    <#if p.isOutgoing()>
        <#assign type = ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)>
        // outgoing port ${p.getName()}
        comp->getInterface()->addOutPort${p.getName()?cap_first}(new DDSPort<${type}>(*this, OUTGOING, comp->getInstanceName() + ".${p.getName()}/out", "${p.getName()}", isRecordingEnabled, false));

        <#if !comp.isAtomic()>
            <#list comp.getAstNode().getConnectors() as connector>
                <#list connector.getTargetList() as target>
                    <#if target.getQName() == p.getName()>
                        // port "${p.getName()}" is target port of a subcomponent
                        topic =  comp->getInstanceName() + ".${connector.getSource().getQName()}" + "/out";
                        CLOG(DEBUG, "DDS") << "Creating additional port for incoming port ${p.getName()} to forward data to subcomponents " << topic;

                        comp->getInterface()->addInPort${p.getName()?cap_first}(new DDSPort<${type}>(*this, INCOMING, topic, "${p.getName()}", isRecordingEnabled, false));
                    </#if>
                </#list>
            </#list>
        </#if>
    </#if>
</#list>