<#function toJavaType type>
    <#local typeName = type.name>
    <#if type.baseType?has_content>
        <#return toJavaType(type.baseType)>
    <#elseif typeName == "string">
        <#return "String">
    <#elseif typeName == "error">
        <#return "Error">
    <#elseif typeName == "array">
        <#return "List<${toJavaType(type.nestedType)}>">
    <#elseif typeName == "byte-array">
        <#return "ByteArrayWrapper">
    <#elseif typeName == "address" >
        <#return "AionAddress">
    <#elseif typeName="request">
        <#return "Request">
    <#elseif typeName=="ecRecoverParams">
        <#return "EcRecoverParams">
    <#elseif typeName=="version_string">
        <#return "VersionType">
    <#elseif typeName=="long">
        <#return "Long">
    <#elseif typeName=="int">
        <#return "Integer">
    <#elseif typeName=="bigint">
        <#return "BigInteger">
    <#elseif typeName=="response">
        <#return "Response">
    <#elseif typeName=="any">
        <#return "Object">
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

<#function toJavaConverter converterType>
    <#if converterType.nestedType?has_content>
        <#return "${toCamelCase(converterType.nestedType.name)}ListConverter">
    <#else >
        <#return "${toCamelCase(converterType.name)}Converter">
    </#if>
</#function>

<#function toJavaConverterFromName name>
    <#return "${toCamelCase(name)}Converter">
</#function>

<#function toCamelCase typeName>

    <#if typeName == "string">
        <#return "String">
    <#elseif typeName == "data_hex_string">
        <#return "DataHexString">
    <#elseif typeName == "byte-array">
        <#return "ByteArrayWrapper">
    <#elseif typeName == "hex_string">
        <#return "HexString">
    <#elseif typeName == "long_hex_string">
        <#return "LongHexString">
    <#elseif typeName == "int_hex_string">
        <#return "IntHexString">
    <#elseif typeName == "address" >
        <#return "AionAddress">
    <#elseif typeName="request">
        <#return "Request">
    <#elseif typeName=="ecRecoverParams">
        <#return "EcRecoverParams">
    <#elseif typeName=="version_string">
        <#return "VersionType">
    <#elseif typeName=="long">
        <#return "Long">
    <#elseif typeName=="int">
        <#return "Integer">
    <#elseif typeName=="bigint">
        <#return "BigInteger">
    <#elseif typeName=="response">
        <#return "Response">
    <#elseif typeName=="error">
        <#return "Error">
    <#elseif typeName=="any">
        <#return "Object">
    <#else >
        <#return "">
    </#if>
</#function>
