<#import "../java_macros.ftl" as macros/>
<#global class_name = macros.toJavaClassName(rpc)/>
package org.aion.rpc.client;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import org.aion.rpc.types.RPCTypes.*;
import org.aion.rpc.types.RPCTypesConverter.*;
import org.aion.types.AionAddress;
import org.aion.util.types.ByteArrayWrapper;

/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
*
*****************************************************************************/
public class ${class_name}{

    private final Provider provider;

    public ${class_name}(final Provider provider){
        this.provider = provider;
    }
<#list methods as method>

    public final ${macros.toJavaType(method.returnType)} ${method.name}(<#list method.param.fields as parameter>${macros.toJavaType(parameter.type)} ${parameter.fieldName}<#if parameter_has_next>,</#if></#list>){
        ${macros.toJavaType(method.param)} params= new ${macros.toJavaType(method.param)}(<#list method.param.fields as parameter>${parameter.fieldName}<#if parameter_has_next> ,</#if></#list>);
        Request request = new Request(0, "${rpc}_${method.name}", ${macros.toJavaConverter(method.param)}.encode(params), VersionType.Version2);

        return provider.execute(request, ${macros.toJavaConverter(method.returnType)}::decode);
    }
</#list>
<#list methods as method>

    public final <O> CompletableFuture<O> ${method.name}(<#list method.param.fields as parameter>${macros.toJavaType(parameter.type)} ${parameter.fieldName},</#list> BiFunction<${macros.toJavaType(method.returnType)}, RPCError, O> asyncTask){
        ${macros.toJavaType(method.param)} params= new ${macros.toJavaType(method.param)}(<#list method.param.fields as parameter>${parameter.fieldName}<#if parameter_has_next> ,</#if></#list>);
        Request request = new Request(0, "${rpc}_${method.name}", ${macros.toJavaConverter(method.param)}.encode(params), VersionType.Version2);

        return provider.executeAsync(request, ${macros.toJavaConverter(method.returnType)}::decode, asyncTask);
    }
</#list>
}