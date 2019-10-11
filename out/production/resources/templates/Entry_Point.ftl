package org.aion.api.server.rpc3;
import java.util.Map;
import java.util.HashMap;
/**
*THIS CLASS IS AUTO GENERATED DO NOT EDIT DIRECTLY.
*/
public class EntryPoint {
    Map<String, MethodHandler> handlerMap;
    public EntryPoint(){
        Map<String,MethodHandler> temporaryHandlerMap = new HashMap<>();
        <#list handlers as handler>
            temporaryHandlerMap.put("${handler.apiIdentifier}", new ${handler.className}RPC());
        </#list>
        handlerMap = Collections.unModifiableMap(temporaryHandlerMap);
    }

    public JsonObject call(String requestString){
        RequestObject request = RequestCodec.decode(requestString);
        String domainKey = requestObject.method.split("_")[0];

        <#list methods as method>
            if(request.method.equals("${method.name}")){
                <#if method.params?has_content>
                    return ${method.name}(${method.params.type}Codec.decode(requestObject.params));
                <#else>
                    return ${method.name}();
                </#if>
            } else

        </#list> if (handlerMap.containsKey(domainKey)){
            return handlerMap.get(domainKey).call(request)
        }
        else {
            return null;
        }
    }

    <#list method as method>
        <#if method.params?has_content>
            <#if method.value?has_content>
            protected final ${method.type} ${method.name}(){
                return ${method.value};
            }
            <#else>
            protected abstract ${method.type} ${method.name}();
            </#if>
        <#else >
            protected abstract ${method.type} ${method.name}(${method.params.type} param);
        </#if>
    </#list>
}