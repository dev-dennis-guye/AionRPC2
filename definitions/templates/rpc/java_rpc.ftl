<#import "../java_macros.ftl" as macros/>
package org.aion.rpc.server;

import static org.aion.rpc.errors.RPCExceptions.*;

import java.math.BigInteger;
import java.util.Set;
import org.aion.rpc.types.RPCTypes.*;
import org.aion.rpc.types.RPCTypesConverter.*;
import org.aion.types.AionAddress;
import org.aion.util.types.ByteArrayWrapper;
/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
* GENERATED: ${date}
*
*****************************************************************************/
public interface RPCServerMethods extends RPC{

    static ResultUnion execute(Request request, RPCServerMethods rpc){
        ResultUnion res;
    <#if errors?has_content>
        try{
    </#if>
            //check that the request can be fulfilled by this class
            <#list methods as method>
            if(request.method.equals("${method.name}")){
                ${macros.toJavaType(method.param)} params= ${macros.toJavaConverter(method.param)}.decode(request.params.encode());
                if (params==null) throw ${macros.toJavaException("InvalidParams")}.INSTANCE;
                ${macros.toJavaType(method.returnType)} result = rpc.${method.name}(<#list method.param.fields as parameter>params.${parameter.fieldName}<#if parameter_has_next>,</#if></#list>);
                res = result == null ? null : new ResultUnion(result);
            }else
            </#list>
                throw ${macros.toJavaException("MethodNotFound")}.INSTANCE;
    <#if errors?has_content>
        }
        catch(RPCException e){
            throw e;
        }
        catch(Exception e){
            throw ${macros.toJavaException("InternalError")}.INSTANCE;
        }
</#if>
        return res;
    }

    static Set<String> listMethods(){
        return Set.of(<#list methods as method> "${method.name}"<#if method_has_next>,</#if></#list>);
    }

    <#list methods as method>
    ${macros.toJavaType(method.returnType)} ${method.name}(<#list method.param.fields as parameter>${macros.toJavaType(parameter.type)} ${parameter.fieldName}<#if parameter_has_next>,</#if></#list>);
    </#list>
}
