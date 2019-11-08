<#import "../java_macros.ftl" as macros/>
package org.aion.rpc.client;

import java.math.BigInteger;
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
* GENERATED: ${date}
*
*****************************************************************************/
public class RPCClientMethods{

    private final Provider provider;
    private final IDGeneratorStrategy generator;

    public RPCClientMethods(final Provider provider, IDGeneratorStrategy generator){
        this.provider = provider;
        this.generator = generator;
    }
<#list methods as method>

    public final ${macros.toJavaType(method.returnType)} ${method.name}(<#list method.param.fields as parameter>${macros.toJavaType(parameter.type)} ${parameter.fieldName}<#if parameter_has_next>,</#if></#list>){
        ${macros.toJavaType(method.param)} params= new ${macros.toJavaType(method.param)}(<#list method.param.fields as parameter>${parameter.fieldName}<#if parameter_has_next> ,</#if></#list>);
        Request request = new Request(generator.generateID(), "${method.name}", new ParamUnion(params), VersionType.Version2);

        return provider.execute(request, ${macros.resultExtractorFromName(method.returnType)});
    }
</#list>
<#list methods as method>

    public final <O> CompletableFuture<O> ${method.name}(<#list method.param.fields as parameter>${macros.toJavaType(parameter.type)} ${parameter.fieldName},</#list> BiFunction<${macros.toJavaType(method.returnType)}, RPCError, O> asyncTask){
        ${macros.toJavaType(method.param)} params= new ${macros.toJavaType(method.param)}(<#list method.param.fields as parameter>${parameter.fieldName}<#if parameter_has_next> ,</#if></#list>);
        Request request = new Request(generator.generateID(), "${method.name}", new ParamUnion(params), VersionType.Version2);

        return provider.executeAsync(request, ${macros.resultExtractorFromName(method.returnType)}, asyncTask);
    }
</#list>
}