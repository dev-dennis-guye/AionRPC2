package org.aion.rpcgenerator;

import java.util.Map;

public interface Mappable {

    /**
     * Converts this java object into a map
     * @return a map representing the java object
     */
    Map<String,Object> toMap();
}
