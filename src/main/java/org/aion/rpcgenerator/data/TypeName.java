package org.aion.rpcgenerator.data;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.w3c.dom.Node;

enum TypeName {
    TYPE_PRIMITIVE("type-primitive"),
    TYPE_CONSTRAINED("type-constrained"),
    TYPE_ENUM("type-enum"),
    TYPE_COMPOSITE("type-composite"),
    TYPE_PARAMS_WRAPPER("type-params-wrapper");
    private static List<TypeName> typeNames = Arrays.asList(TypeName.values());
    String typeName;

    TypeName(String typeName) {
        this.typeName = typeName;
    }

    public static TypeName fromNode(Node node) {
        for (TypeName typeName : typeNames) {
            if (typeName.typeName.equals(node.getNodeName())) {
                return typeName;
            }
        }
        throw new NoSuchElementException("Could not return an instance for node: " + node
            .getNodeName());
    }
}
