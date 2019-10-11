package org.aion.rpcgenerator.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Defines the basic properties of all types
 */
public abstract class Type {

    public final String name;
    private final List<String> comments;

    Type(Node node) {
        name = XMLUtils.valueFromAttribute(node, "name");
        if (node.getChildNodes().getLength()!=0){
            comments = new ArrayList<>();
            NodeList nodeList= node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node childNode = nodeList.item(i);
                if (childNode.getNodeName().equals("comment")){
                    comments.add(childNode.getTextContent());
                }
            }
        }
        else {
            comments = Collections.emptyList();
        }
    }

    /**
     *
     * @param node the xml node containing the information to build a TYPE
     * @return
     */
    public static Type fromNode(Node node){
        TypeName type = TypeName.fromNode(node);
        switch (type){
            case TYPE_PRIMITIVE:
                return new PrimitiveType(node);
            case TYPE_CONSTRAINED:
                return new ConstrainedType(node);
            case TYPE_ENUM:
                return new EnumType(node);
            case TYPE_COMPOSITE:
                return new CompositeType(node);
            case TYPE_PARAMS_WRAPPER:
                return new ParamType(node);
            default:
                return null;//realistically we will never get here.
        }
    }


    public final Map<String, Object> toMap() {
        return Collections.unmodifiableMap(toMutableMap());
    }

    /**
     * Converts the contents of this class into a mutable map
     * @return
     */
    protected Map<String, Object> toMutableMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("comments", comments);
        return map;
    }
}
