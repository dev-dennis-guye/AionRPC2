package org.aion.rpcgenerator.data;

import java.util.List;
import org.w3c.dom.Element;

public class PrimitiveType extends Type {

    protected PrimitiveType(Element node) {
        super(node);
    }
    public PrimitiveType(String name, List<String> comments){
        super(name, comments);
    }
}

