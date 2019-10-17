<#import "../java_macros.ftl" as macros>
package org.aion.api.server.rpc3.types;

import org.aion.api.server.rpc3.RPCExceptions.ParseErrorRPCException;
import org.aion.util.types.ByteArrayWrapper;
import org.aion.types.AionAddress;
/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
*
*****************************************************************************/
public class RPCTypes{
<#list compositeTypes as composite_type>
    <#if composite_type.comments?has_content>
    /**
    <#list composite_type.comments as comment>
    * ${comment}
    </#list>
    */
    </#if>
    public static class ${macros.toJavaType(composite_type)} {
    <#list composite_type.fields as field>
        public final ${macros.toJavaType(field.type)} ${field.fieldName};
    </#list>

        public ${macros.toJavaType(composite_type)}(<#list composite_type.fields as field>${macros.toJavaType(field.type)} ${field.fieldName} <#if field_has_next>,</#if></#list>){
            <#list composite_type.fields as field><#if field.required=="true" >
            if(${field.fieldName}==null) throw new ${macros.toJavaException("ParseError")}();
            </#if>
            this.${field.fieldName}=${field.fieldName};
            </#list>
        }
    }
</#list>

<#list enumTypes as enum >
    <#if enum.comments?has_content>
        /**
        <#list composite_type.comments as comment>* ${comment}</#list>
        */
    </#if>
    public enum ${macros.toJavaType(enum)}{
        <#list enum.values as value>
        ${value.name}("${value.value}")<#if value_has_next>,</#if></#list>;
        public final ${macros.toJavaType(enum.internalType)} x;
        ${macros.toJavaType(enum)}(${macros.toJavaType(enum.internalType)} x){
            this.x = x;
        }

        public static ${macros.toJavaType(enum)} fromString(String x){
            <#list enum.values as value>
            if(x.equals("${value.value}")){
                return ${value.name};
            }else</#list>
                throw new ${macros.toJavaException("ParseError")}();
        }
    }
</#list>

<#list paramTypes as paramType>
    <#if paramType.comments?has_content>
        /**
        <#list paramType.comments as comment>* ${comment}</#list>
        */
    </#if>
    public static class ${macros.toJavaType(paramType)} {
    <#list paramType.fields as field>
        public final ${macros.toJavaType(field.type)} ${field.fieldName};
    </#list>

        public ${macros.toJavaType(paramType)}(<#list paramType.fields as field>${macros.toJavaType(field.type)} ${field.fieldName} <#if field_has_next>,</#if></#list>){
    <#list paramType.fields as field><#if field.required=="true" >
            if(${field.fieldName}==null) throw new ${macros.toJavaException("ParseError")}();
    </#if>
            this.${field.fieldName}=${field.fieldName};
    </#list>
        }
    }
</#list>
}
