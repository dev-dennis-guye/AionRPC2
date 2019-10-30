package org.aion.rpcgenerator.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.util.SchemaUtils;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Element;

public class UnionType extends Type {

    private final List<UnionElement> unionElements;
    private final String nullable;
    UnionType(String name, List<String> comments,
        List<UnionElement> elements, String nullable) {
        super(name, comments);
        this.unionElements = elements;
        this.nullable = nullable;
    }

    UnionType(Element element) {
        super(element);
        unionElements = XMLUtils.elements(element.getChildNodes()).stream()
            .filter(e -> e.getNodeName().equals("union-element")).map(UnionElement::new).collect(
                Collectors.toUnmodifiableList());
        if (XMLUtils.hasAttribute(element, "nullable"))
        this.nullable = XMLUtils.valueFromAttribute(element, "nullable");
        else nullable="true";
    }

    @Override
    protected Map<String, Object> toMutableMap() {
        Map<String,Object> mutableMap= super.toMutableMap();
        mutableMap.put("unionElements", unionElements.stream().map(UnionElement::toMap).collect(Collectors.toUnmodifiableList()));
        mutableMap.put("nullable", nullable);
        return Collections.unmodifiableMap(mutableMap);
    }

    public void setTypes(List<Type> types){
        for (UnionElement element : unionElements) {
            element.setType(types);
        }
    }

    public static class UnionElement implements Mappable {

        public final String name;
        public final String typeName;
        public final List<String> comment;
        private Type type;

        public UnionElement(Element element) {
            name = XMLUtils.valueFromAttribute(element, "name");
            typeName = XMLUtils.valueFromAttribute(element, "type");
            comment = SchemaUtils.getComments(element.getChildNodes());
        }

        public void setType(List<Type> types) {
            for (Type typeDef : types) {
                if (typeName.equals(typeDef.name)) {
                    this.type = typeDef;
                    return;
                }
            }
            throw new IllegalStateException();
        }

        @Override
        public Map<String, Object> toMap() {
            return Map.ofEntries(Map.entry("name", name),
                Map.entry("type", type.toMap()),
                Map.entry("comments", comment));
        }
    }
}
