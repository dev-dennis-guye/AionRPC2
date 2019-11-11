<#import "../java_macros.ftl" as macros>
package org.aion.rpc.types;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.aion.rpc.errors.RPCExceptions.*;
import org.aion.rpc.types.RPCTypesConverter.*;
import org.aion.types.AionAddress;
import org.aion.util.bytes.ByteUtil;
/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
* GENERATED: ${date}
*
*****************************************************************************/
public class RPCTypes{

    public static final class ByteArray{
        private final byte[] bytes;

        public ByteArray(byte[] bytes) {
            if (bytes == null) {
                throw new NullPointerException("Byte array is null");
            }
            this.bytes = bytes;
        }

        public ByteArray(String hexString){
            if (hexString == null) throw new NullPointerException("Hex String is null");
            this.bytes = ByteUtil.hexStringToBytes(hexString);
        }

        public byte[] toBytes(){
            return Arrays.copyOf(bytes, bytes.length);
        }

        @Override
        public String toString() {
            return "0x"+ ByteUtil.toHexString(bytes);
        }

        @Override
        public boolean equals(Object that){
            if (that==null || !(that instanceof ByteArray) )return false;
            else return Arrays.equals(this.bytes, ((ByteArray)that).bytes);
        }

        public int hashCode(){
            return Arrays.hashCode(this.bytes);
        }

        public static ByteArray wrap(byte[] bytes){
            return new ByteArray(bytes);
        }

        public static ByteArray wrap(String hexString){
            return new ByteArray(hexString);
        }
    }

<#list unionTypes as unionType>
    <#if unionType.comments?has_content>
    /**
    <#list unionType.comments as comment>
    * ${comment}
    </#list>
    */
    </#if>
    public static final class ${macros.toJavaType(unionType)}{
        <#list unionType.unionElements as unionElement >
        public final ${macros.toJavaType(unionElement.type)} ${unionElement.name};
        </#list>
        private ${macros.toJavaType(unionType)}(<#list unionType.unionElements as unionElement >${macros.toJavaType(unionElement.type)} ${unionElement.name} <#if unionElement_has_next>,</#if></#list>){
            <#list unionType.unionElements as unionElement >
            this.${unionElement.name}=${unionElement.name};
            </#list>
        }

        <#list unionType.unionElements as unionElement >
        public ${macros.toJavaType(unionType)}(${macros.toJavaType(unionElement.type)} ${unionElement.name}){
            this(<#list 0..unionType.unionElements?size-1 as i><#if i==unionElement_index>${unionElement.name}<#else>null</#if><#if i_has_next>,</#if ></#list>);
            if(${unionElement.name} == null) throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
        }
        </#list>

        <#list unionType.unionElements as unionElement >
        public static ${macros.toJavaType(unionType)} wrap(${macros.toJavaType(unionElement.type)} ${unionElement.name}){
            if(${unionElement.name} == null) throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            else return new ${macros.toJavaType(unionType)}(${unionElement.name});
        }
        </#list>

        public Object encode(){
            <#list unionType.unionElements as unionElement>
            if(this.${unionElement.name} != null) return ${macros.toJavaConverter(unionElement.type)}.encode(${unionElement.name});
            </#list>
            throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
        }

        public static ${macros.toJavaType(unionType)} decode(Object object){
            <#list unionType.unionElements as unionElement>
            try{
                return new ${macros.toJavaType(unionType)}(${macros.toJavaConverter(unionElement.type)}.decode(object));
            }catch(Exception e){}
            </#list>
            throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
        }
    }

</#list>
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
        <#if field.defaultValue?has_content>private final ${macros.toJavaType(field.type)} ${field.fieldName}DefaultValue=${macros.toJavaConverter(field.type)}.decode("${field.defaulValue}");</#if>
    </#list>

        public ${macros.toJavaType(paramType)}(<#list paramType.fields as field>${macros.toJavaType(field.type)} ${field.fieldName} <#if field_has_next>,</#if></#list>){
    <#list paramType.fields as field><#if field.required=="true" >
            if(${field.fieldName}==null) throw ${macros.toJavaException("ParseError")}.INSTANCE;
    </#if>
            this.${field.fieldName}=<#if field.defaultValue?has_content> ${field.fieldName}==null? ${field.fieldName}DefaultValue:</#if> ${field.fieldName};
    </#list>
        }
    }
</#list>
}
