<#import "../java_macros.ftl" as macros>
package org.aion.rpc.types;

import java.math.BigInteger;
import java.util.Arrays;
import org.aion.rpc.errors.RPCExceptions.*;
import org.aion.types.AionAddress;
import org.aion.util.bytes.ByteUtil;
/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
*
*****************************************************************************/
public class RPCTypes{

    public static final class ByteArray{
        private final byte[] bytes;

        public ByteArray(byte[] bytes) {
            this.bytes = bytes;
        }

        public byte[] toBytes(){
            return Arrays.copyOf(bytes, bytes.length);
        }

        @Override
        public String toString() {
            return "0x"+ ByteUtil.toHexString(bytes);
        }
    }

<#list compositeTypes as composite_type>
    <#if composite_type.comments?has_content>
    /**
    <#list composite_type.comments as comment>
    * ${comment}
    </#list>
    */
    </#if>
    public static final class ${macros.toJavaType(composite_type)} {
    <#list composite_type.fields as field>
        public final ${macros.toJavaType(field.type)} ${field.fieldName};
    </#list>

        public ${macros.toJavaType(composite_type)}(<#list composite_type.fields as field>${macros.toJavaType(field.type)} ${field.fieldName} <#if field_has_next>,</#if></#list>){
            <#list composite_type.fields as field><#if field.required=="true" >
            if(${field.fieldName}==null) throw ${macros.toJavaException("ParseError")}.INSTANCE;
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
            if(x==null) throw ${macros.toJavaException("ParseError")}.INSTANCE;
            <#list enum.values as value>
            if(x.equals("${value.value}")){
                return ${value.name};
            }else</#list>
                throw ${macros.toJavaException("ParseError")}.INSTANCE;
        }
    }
</#list>

<#list paramTypes as paramType>
    <#if paramType.comments?has_content>
        /**
        <#list paramType.comments as comment>* ${comment}</#list>
        */
    </#if>
    public static final class ${macros.toJavaType(paramType)} {
    <#list paramType.fields as field>
        public final ${macros.toJavaType(field.type)} ${field.fieldName};
    </#list>

        public ${macros.toJavaType(paramType)}(<#list paramType.fields as field>${macros.toJavaType(field.type)} ${field.fieldName} <#if field_has_next>,</#if></#list>){
    <#list paramType.fields as field><#if field.required=="true" >
            if(${field.fieldName}==null) throw ${macros.toJavaException("ParseError")}.INSTANCE;
    </#if>
            this.${field.fieldName}=${field.fieldName};
    </#list>
        }
    }
</#list>
}
