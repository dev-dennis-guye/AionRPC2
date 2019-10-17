<#import "../java_macros.ftl" as macros/>
<#global class_name = macros.toJavaClassName(rpc)/>
package org.aion.api.server.rpc3;

import static org.aion.api.server.rpc3.RPCExceptions.*;
import static org.aion.api.server.rpc3.types.RPCTypes.*;
import static org.aion.api.server.rpc3.types.RPCTypesConverter.*;
import org.aion.util.types.ByteArrayWrapper;
import org.aion.types.AionAddress;

/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
*
*****************************************************************************/
public abstract class ${class_name}RPC{

    <#if comments?has_content>
    /**
    <#list comments as comment> *${comment}</#list>
     */
    </#if>
    public String execute(String requestString){
        <#if errors?has_content>
        try{
        </#if>

            Request request = RequestConverter.decode(requestString);
            //check that the request can be fulfilled by this class
            String interfaceName = request.method.split("_")[0];
            if(interfaceName.equals("${rpc}")){
            <#list methods as method>
                if(request.method.equals("${method.name}")){
                    ${macros.toJavaType(method.param)} params=${macros.toJavaConverter(method.param.name)}.decode(request.params);

                    ${macros.toJavaType(method.returnType)} result = ${method.name}(<#list method.param.fields as parameter>params.${parameter.fieldName}<#if parameter_has_next>,</#if></#list>);
                    return ${macros.toJavaConverter(method.returnType.name)}.encode(result);
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
        catch(<#list errors as error >${macros.toJavaException(error.error_class)}<#if error_has_next> |</#if></#list> e){
            return e.getMessage();
        }
        </#if>
    }

    <#list methods as method>
    protected abstract ${macros.toJavaType(method.returnType)} ${method.name}(<#list method.param.fields as parameter>${macros.toJavaType(parameter.type)} ${parameter.fieldName}<#if parameter_has_next>,</#if></#list>);
    </#list>
}
