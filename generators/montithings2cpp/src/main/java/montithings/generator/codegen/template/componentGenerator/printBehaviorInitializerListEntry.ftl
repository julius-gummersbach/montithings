<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
${Identifier.getBehaviorImplName()}(${compname}Impl${Utils.printFormalTypeParameters(comp, false)}(
<#if comp.hasParameters()>
    <#list comp.getParameters() as param >
        ${param.getName()}<#sep>,</#sep>
    </#list>
</#if>
))