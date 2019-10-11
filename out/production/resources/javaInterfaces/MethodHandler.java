package org.aion.api.server.rpc3.handler;

import java.util.Map;

public interface MethodHandler{
    JsonObject call(RequestObject rpcRequest);
}