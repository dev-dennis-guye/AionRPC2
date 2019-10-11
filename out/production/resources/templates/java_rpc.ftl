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
                if(requestObject.method.contains("${method.name}"))
                    return ${class_name}Codec
                            .${macros.toJavaType(method.return)}
                            .encode(${method.name}(
                                                    ${class_name}Codec
                                                                .${macros.toJavaType(method.param)}
                                                                .decode(request.params)
                                                    )
                                    );
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
        protected abstract ${macros.toJavaType(method.return)} ${method.name}(${macros.toJavaType(method.param)} params);
    </#list>
}