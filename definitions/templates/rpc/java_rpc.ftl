<#import "../java_macros.ftl" as macros/>
<#global class_name = macros.toJavaClassName(rpc)/>
package org.aion.api.server.rpc3;

import static org.aion.api.server.rpc3.RPCExceptions.*;
import static org.aion.api.server.rpc3.types.RPCTypes.*;
import static org.aion.api.server.rpc3.types.RPCTypesConverter.*;
import org.aion.util.types.ByteArrayWrapper;
import org.aion.types.AionAddress;
import java.util.Set;

/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
*
*****************************************************************************/
public interface ${class_name}RPC{

    default String execute(Request request){
        Object res;
    <#if errors?has_content>
        try{
    </#if>
            //check that the request can be fulfilled by this class
            <#list methods as method>
            if(request.method.equals("${method.name}")){
                ${macros.toJavaType(method.param)} params;
                try{
                    params=${macros.toJavaConverter(method.param.name)}.decode(request.params);
                }catch(Exception e){
                    throw new ${macros.toJavaException("InvalidParams")}();
                }
                ${macros.toJavaType(method.returnType)} result = this.${method.name}(<#list method.param.fields as parameter>params.${parameter.fieldName}<#if parameter_has_next>,</#if></#list>);
                res = ${macros.toJavaConverter(method.returnType.name)}.encode(result);
            }else
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
        return ResponseConverter.encode(new Response(request.id, res , VersionType.Version2));
    }

    boolean isExecutable(String method);

    default Set<String> listMethods(){
        return Set.of(<#list methods as method> "${method.name}"<#if method_has_next>,</#if></#list>);
    }

    <#list methods as method>
    ${macros.toJavaType(method.returnType)} ${method.name}(<#list method.param.fields as parameter>${macros.toJavaType(parameter.type)} ${parameter.fieldName}<#if parameter_has_next>,</#if></#list>);
    </#list>
}
