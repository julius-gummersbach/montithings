<#-- (c) https://github.com/MontiCore/monticore -->
<#include "/template/input/helper/GeneralPreamble.ftl">
<#assign cdeImportStatementOpt = TypesHelper.getCDEReplacement(port, config)>
<#assign fullImportStatemantName = cdeImportStatementOpt.get().getSymbol().getFullName()?split(".")>
<#assign adapterName = fullImportStatemantName[0]+"Adapter">
<#assign cdSimpleName = cdeImportStatementOpt.get().getSymbol().getFullName()?keep_after_last(".")>
