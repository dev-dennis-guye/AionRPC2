<#import "../java_macros.ftl" as macros/>
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
public final class RPCEntryPoint{
    private final RPC rpc;
    public RPCEntryPoint(RPC rpc){
        this.rpc = rpc;
    }
    public String execute(String requestString){
        <#if errors?has_content>
        try{
        </#if>
            Request request;
            try{
                request = RequestConverter.decode(requestString);
            } catch(Exception e){
                throw new ${macros.toJavaException("InvalidRequest")}();
            }
            //check that the request can be fulfilled by this class
            <#list methods as method>
            if(request.method.equals("${method.name}")){
                ${macros.toJavaType(method.param)} params;
                try{
                    params=${macros.toJavaConverter(method.param)}.decode(request.params);
                }catch(Exception e){
                    throw new ${macros.toJavaException("InvalidParams")}();
                }
                ${macros.toJavaType(method.returnType)} result = rpc.${method.name}(<#list method.param.fields as parameter>params.${parameter.fieldName}<#if parameter_has_next>,</#if></#list>);
                return ResponseConverter.encode(new Response(request.id, ${macros.toJavaConverter(method.returnType)}.encode(result), VersionType.Version2);
            }
            else
            </#list>
                throw new ${macros.toJavaException("MethodNotFound")}();
        <#if errors?has_content>
        }
        catch(<#list errors as error >${macros.toJavaException(error.error_class)}<#if error_has_next> |</#if></#list> e){
            return e.getMessage();
        }
        catch(Exception e){
            return new ${macros.toJavaException("InternalError")}().getMessage();
        }
        </#if>
    }

}