<#function toJavaType typeName>
    <#if typeName == "string">
        <#return "String">
    <#elseif typeName == "data_hex_string">
        <#return "String">
    <#elseif typeName == "hex_string">
        <#return "String">
    <#elseif typeName == "address" >
        <#return "AionAddress">
    <#elseif typeName="request">
        <#return "Request">
    <#elseif typeName=="ecRecoverParams">
        <#return "EcRecoverParams">
    <#elseif typeName=="version_string">
        <#return "VersionType">
    <#else >
        <#return "">
    </#if>
</#function>

<#function toJavaClassName className>
    <#if className=="personal">
        <#return "Personal"/>
    <#else>
        <#return "">
    </#if>
</#function>

<#function toJavaException exceptionName>
    <#return "${exceptionName}RPCException">
</#function>