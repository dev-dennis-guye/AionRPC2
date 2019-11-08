<#import "../java_macros.ftl" as macros>
package org.aion.api.server.rpc3;
/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
* GENERATED: ${date}
*
*****************************************************************************/
public interface RPCTestUtilsInterface{
    <#list methods as method>
    <#list method.errors as error>
    Request ${method.name}_${error.error_class}()();
    </#list>
    Request ${method.name}_succeeds();
    RPC ${method.name}RPC();
    </#list>
}
