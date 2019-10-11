package org.aion.rpcgenerator.util;

import org.w3c.dom.Node;

public class XMLUtils {
    private XMLUtils(){
        throw new UnsupportedOperationException("Cannot instantiate "+ getClass().getSimpleName());
    }
    public static String valueFromAttribute(Node node, String attributeName){
        return  node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    public static boolean hasAttribute(Node node, String attributeName){
        return node.getAttributes().getNamedItem(attributeName) != null;
    }
}
