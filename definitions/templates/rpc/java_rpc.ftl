<#import "../java_macros.ftl" as macros/>
<#global class_name = macros.toJavaClassName(rpc)/>
package org.aion.rpc.server;

import static org.aion.rpc.errors.RPCExceptions.InternalErrorRPCException;
import static org.aion.rpc.errors.RPCExceptions.InvalidParamsRPCException;
import static org.aion.rpc.errors.RPCExceptions.InvalidRequestRPCException;
import static org.aion.rpc.errors.RPCExceptions.MethodNotFoundRPCException;
import static org.aion.rpc.errors.RPCExceptions.ParseErrorRPCException;

import java.util.Set;
import org.aion.rpc.types.RPCTypes.EcRecoverParams;
import org.aion.rpc.types.RPCTypes.Request;
import org.aion.rpc.types.RPCTypesConverter.AionAddressConverter;
import org.aion.rpc.types.RPCTypesConverter.EcRecoverParamsConverter;
import org.aion.types.AionAddress;
import org.aion.util.types.ByteArrayWrapper;
/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
*
*****************************************************************************/
public interface ${class_name}RPC{

    default Object execute(Request request){
        Object res;
    <#if errors?has_content>
        try{
    </#if>
            //check that the request can be fulfilled by this class
            <#list methods as method>
            if(request.method.equals("${rpc}_${method.name}")){
                ${macros.toJavaType(method.param)} params;
                params=${macros.toJavaConverter(method.param)}.decode(request.params);
                ${macros.toJavaType(method.returnType)} result = this.${rpc}_${method.name}(<#list method.param.fields as parameter>params.${parameter.fieldName}<#if parameter_has_next>,</#if></#list>);
                res = ${macros.toJavaConverter(method.returnType)}.encode(result);
            }else
            </#list>
                throw ${macros.toJavaException("MethodNotFound")}.INSTANCE;
    <#if errors?has_content>
        }
        catch(<#list errors as error >${macros.toJavaException(error.error_class)}<#if error_has_next> |</#if></#list> e){
            throw e;
        }
        catch(Exception e){
            throw ${macros.toJavaException("InternalError")}.INSTANCE;
        }
</#if>
        return res;
    }

    boolean isExecutable(String method);

    default Set<String> listMethods(){
        return Set.of(<#list methods as method> "${rpc}_${method.name}"<#if method_has_next>,</#if></#list>);
    }

    <#list methods as method>
    ${macros.toJavaType(method.returnType)} ${rpc}_${method.name}(<#list method.param.fields as parameter>${macros.toJavaType(parameter.type)} ${parameter.fieldName}<#if parameter_has_next>,</#if></#list>);
    </#list>
}
