package org.aion.rpcgenerator.data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.util.SchemaUtils;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Defines the basic properties of all types
 */
public abstract class Type implements Mappable {

    public final String name;
    private final List<String> comments;

    Type(String name, List<String> comments){
        this.name = name;
        this.comments = comments;
    }

    Type(Element element) {
        name = XMLUtils.valueFromAttribute(element, "typeName");
        comments= SchemaUtils.getComments(element.getChildNodes());
    }

    /**
     * @param node the xml node containing the information to build a TYPE
     * @return
     */
    public static Type fromNode(Element node) {
        TypeName type = TypeName.fromNode(node);
        switch (type) {
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
            case TYPE_ARRAY:
                return new ArrayType(node);
            case TYPE_UNION:
                return new UnionType(node);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Type)) {
            return false;
        }
        Type type = (Type) o;
        return name.equals(type.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public final Map<String, Object> toMap() {
        return Collections.unmodifiableMap(toMutableMap());
    }

    /**
     * Converts the contents of this class into a mutable map
     *
     * @return
     */
    protected Map<String, Object> toMutableMap() {
        // all implementations of this interface have these attributes
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("comments", comments);
        return map;
    }
}
