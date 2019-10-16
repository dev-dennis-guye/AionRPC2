<#import "../java_macros.ftl" as macros>
package org.aion.api.server.rpc3.types;

public class RPCTypes{
<#list compositeTypes as composite_type>
    <#if composite_type.comments?has_content>
    /**
    <#list composite_type.comments as comment>
    * comment
    </#list>
    */
    </#if>
    public static class ${macros.toJavaType(composite_type.name)} {
    <#list composite_type.fields as field>
        private ${macros.toJavaType(field.type.name)} ${field.fieldName};
    </#list>

    public ${macros.toJavaType(composite_type.name)}(<#list composite_type.fields as field>${macros.toJavaType(field.type.name)} ${field.fieldName} <#if field_has_next>,</#if></#list>){
        <#list composite_type.fields as field><#if field.required=="true" >
        if(${field.fieldName}==null) throw new ${macros.toJavaException("ParseError")}();
        </#if>
        this.${field.fieldName}=${field.fieldName};
        </#list>
    }

    ${macros.toJavaType(composite_type.name)}(){}

    <#list composite_type.fields as field>
        public void set${field.fieldName}(${macros.toJavaType(field.type.name)} ${field.fieldName}){
        <#if field.required=="true" >if(${field.fieldName}==null) throw new ${macros.toJavaException("ParseError")}();</#if>
        this.${field.fieldName}=${field.fieldName};
        }

        public ${macros.toJavaType(field.type.name)} get${field.fieldName}(){
            return this.${field.fieldName};
        }
    </#list>
    }
</#list>

<#list enumTypes as enum >
    <#if enum.comments?has_content>
        /**
        <#list enum.comments as comment>
            * comment
        </#list>
        */
    </#if>
    public enum ${macros.toJavaType(enum.name)}{
        <#list enum.values as value>
        ${value.name}("${value.value}")<#if value_has_next>,</#if></#list>;
        public final ${macros.toJavaType(enum.internalType.name)} x;
        ${macros.toJavaType(enum.name)}(${macros.toJavaType(enum.internalType.name)} x){
            this.x = x;
        }

        public fromString(String x){
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
        <#list paramType.comments as comment>
            * comment
        </#list>
        */
    </#if>
    public static class ${macros.toJavaType(paramType.name)} {
    <#list paramType.fields as field>
        private ${macros.toJavaType(field.type.name)} ${field.fieldName};
    </#list>

        public ${macros.toJavaType(paramType.name)}(<#list paramType.fields as field>${macros.toJavaType(field.type.name)} ${field.fieldName} <#if field_has_next>,</#if></#list>){
    <#list paramType.fields as field><#if field.required=="true" >
            if(${field.fieldName}==null) throw new ${macros.toJavaException("ParseError")}();
    </#if>
            this.${field.fieldName}=${field.fieldName};
    </#list>
        }

    ${macros.toJavaType(paramType.name)}(){}

    <#list paramType.fields as field>
        public void set${field.fieldName}(${macros.toJavaType(field.type.name)} ${field.fieldName}){
            <#if field.required=="true" >if(${field.fieldName}==null) throw new ${macros.toJavaException("ParseError")}();</#if>
            this.${field.fieldName}=${field.fieldName};
        }

        public ${macros.toJavaType(field.type.name)} get${field.fieldName}(){
            return this.${field.fieldName};
        }
    </#list>
    }
</#list>
}