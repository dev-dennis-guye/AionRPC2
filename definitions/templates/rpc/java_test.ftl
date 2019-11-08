<#import "../java_macros.ftl" as macros>
package org.aion.api.server.rpc3;
//TODO clean up the imports
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import java.math.BigInteger;
import org.aion.crypto.HashUtil;
import org.aion.rpc.errors.RPCExceptions;
import org.aion.rpc.types.RPCTypes.AddressParams;
import org.aion.rpc.types.RPCTypes.ByteArray;
import org.aion.rpc.types.RPCTypes.MinerStats;
import org.aion.rpc.types.RPCTypes.ParamUnion;
import org.aion.rpc.types.RPCTypes.Request;
import org.aion.rpc.types.RPCTypes.ResultUnion;
import org.aion.rpc.types.RPCTypes.SubmitBlockParams;
import org.aion.rpc.types.RPCTypes.VersionType;
import org.aion.rpc.types.RPCTypesConverter.AionAddressConverter;
import org.aion.rpc.types.RPCTypesConverter.RequestConverter;
import org.aion.rpc.types.RPCTypes.BlockEnum;
import org.aion.rpc.types.RPCTypes.BlockSpecifier;
import org.aion.rpc.types.RPCTypes.BlockSpecifierUnion;
import org.aion.rpc.types.RPCTypes.ByteArray;
import org.aion.rpc.types.RPCTypes.ParamUnion;
import org.aion.rpc.types.RPCTypes.Request;
import org.aion.rpc.types.RPCTypes.ResultUnion;
import org.aion.rpc.types.RPCTypes.VersionType;
import org.aion.rpc.types.RPCTypesConverter.BlockDetailsConverter;
import org.aion.rpc.types.RPCTypesConverter.BlockSpecifierConverter;
import org.aion.types.AionAddress;
import org.aion.types.Log;
import org.aion.util.types.ByteArrayWrapper;
import org.aion.zero.impl.types.AionBlock;
import org.aion.zero.impl.types.AionTxInfo;
import org.aion.zero.impl.types.StakingBlock;
import org.aion.zero.impl.types.StakingBlockHeader;
import java.math.BigInteger;
import org.aion.crypto.HashUtil;
import org.aion.rpc.errors.RPCExceptions;
import org.aion.rpc.types.RPCTypes.AddressParams;
import org.aion.rpc.types.RPCTypes.ByteArray;
import org.aion.rpc.types.RPCTypes.MinerStats;
import org.aion.rpc.types.RPCTypes.ParamUnion;
import org.aion.rpc.types.RPCTypes.Request;
import org.aion.rpc.types.RPCTypes.ResultUnion;
import org.aion.rpc.types.RPCTypes.SubmitBlockParams;
import org.aion.rpc.types.RPCTypes.VersionType;
import org.aion.rpc.types.RPCTypesConverter.AionAddressConverter;
import org.aion.rpc.types.RPCTypesConverter.RequestConverter;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import java.util.Arrays;
import org.aion.rpc.errors.RPCExceptions.UnsupportedUnityFeatureRPCException;
import org.aion.rpc.types.RPCTypes.ByteArray;
import org.aion.rpc.types.RPCTypes.ParamUnion;
import org.aion.rpc.types.RPCTypes.Request;
import org.aion.rpc.types.RPCTypes.SubmitSeedParams;
import org.aion.rpc.types.RPCTypes.SubmitSignatureParams;
import org.aion.rpc.types.RPCTypes.VersionType;
import org.aion.rpc.types.RPCTypes.VoidParams;
import org.aion.types.AionAddress;
import org.aion.zero.impl.blockchain.AionImpl;
import org.junit.Before;
import org.junit.Test;
/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
* GENERATED: ${date}
*
*****************************************************************************/
public class RPCMethodsTest{
    private RPCTestUtilsInterface testUtils= RPCTestUtils.getUtils();
<#list methods as method>
    <#if method.errors?has_content>
    <#list method.errors as error>
    public void ${method.name}TestErrors${macros.toJavaException(error.error_class)}(){
        String method = "${method.name}";
        try{
            Request request = testUtils.${method.name}_${error.error_class}();
            testUtils.${method.name}RPC().execute(request);
            fail();
        }catch(${macros.toJavaException(error.error_class)} e){/*We expect this error*/}
    }

    </#list>
    </#if>
    public void ${method.name}Test(){
        String method = "${method.name}";
        Request successfulRequest = testUtils.${method.name}_succeeds();
        ResultUnion resultUnion = testUtils.${method.name}RPC().execute(request);
        assertNotNull(resultUnion);
        resultUnion.encode();
    }
</#list>
}