<#import "../rpc/java_macros.ftl" as macros/>
package org.aion.api.server.rpc3;

public class RPCExceptions{
    <#list errors as error>
        public static class ${macros.toJavaException(error.error_class)} extends RuntimeException{
            public ${macros.toJavaException(error.error_class)}(){
                super("{code:${error.code},message:'${error.message}'");
            }
        }
    </#list>
}