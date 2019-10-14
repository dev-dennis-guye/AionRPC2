<#import "java_macros.ftl" as macros/>
<#global class_name = macros.toJavaClassName(rpc)/>
package org.aion.api.server.rpc3;

import static org.aion.api.server.rpc3.RPCExceptions.*;

public abstract class ${class_name}RPC{

    public Object execute(String requestString){
        <#if errors?has_content>
        try{
        </#if>

        RequestObject request = ${class_name}Codec.RequestObject.decode(requestString);
        //check that the request can be fulfilled by this class
        String interfaceName = request.method.split("_")[0];
        if(interfaceName.equals(${rpc})){
            <#list methods as method>
                if(requestObject.method.contains("${method.name}")){
                    ${macros.toJavaType(method.param.name)} params=${class_name}Codec
                    .${macros.toJavaType(method.param.name)}.decode(request.params);

                    ${macros.toJavaType(method.returnType.name)} result = ${method.name}(
                        <#list method.param.fields as parameter>
                            params.${parameter.fieldName}
                            <#if parameter_has_next>
                                ,
                            </#if>
                        </#list>
                    );

                    return ${class_name}Codec
                            .${macros.toJavaType(method.returnType.name)}
                            .encode(result);
                }
                else

            </#list>
                    throw new ${macros.toJavaException("MethodNotFound")}();
        }
        else{
            throw new ${macros.toJavaException("InternalError")}();
        }
        <#if errors?has_content>
        }
        <#list errors as error >
        catch(${macros.toJavaException(error.error_class)} e){
            return e.getMessage;
        }
        </#list>
        </#if>
    }

    <#list methods as method>
        protected abstract ${macros.toJavaType(method.returnType.name)} ${method.name}(
            <#list method.param.fields as parameter>
                ${macros.toJavaType(parameter.type.name)} ${parameter.fieldName}
                <#if parameter_has_next>
                    ,
                </#if>
            </#list>
        );
    </#list>
}