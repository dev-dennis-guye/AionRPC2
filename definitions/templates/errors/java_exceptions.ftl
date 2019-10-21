<#import "../java_macros.ftl" as macros/>
package org.aion.api.server.rpc3;

import org.aion.api.server.rpc3.types.RPCTypes.Error;
import org.aion.api.server.rpc3.types.RPCTypesConverter.ErrorConverter;
/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
*
*****************************************************************************/
public class RPCExceptions{
    public static RPCException fromCode(int code){
        <#list errors as error>if(code == ${error.code}){
            return ${macros.toJavaException(error.error_class)}.INSTANCE;
        }
        else </#list>
            return ${macros.toJavaException("InternalError")}.INSTANCE;
    }

    public abstract static class RPCException extends RuntimeException{

        private Error error;
        protected RPCException(String message){
            super(message);
            this.error = ErrorConverter.decode(message);
        }
        public Error getError(){
            return error;
        }
    }

    <#list errors as error>
    public static class ${macros.toJavaException(error.error_class)} extends RPCException{
        public static final ${macros.toJavaException(error.error_class)} INSTANCE = new ${macros.toJavaException(error.error_class)}();
        private ${macros.toJavaException(error.error_class)}(){
            super("{\"code\":${error.code},\"message\":\"${error.message}\"}");
        }
    }

    </#list>
}
