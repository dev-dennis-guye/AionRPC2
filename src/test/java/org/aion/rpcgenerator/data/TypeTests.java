package org.aion.rpcgenerator.data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.aion.rpcgenerator.data.EnumType.EnumValues;
import org.aion.rpcgenerator.util.SchemaUtils;
import org.aion.rpcgenerator.util.XMLUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class TypeTests {

    @Test
    public void typesFromNodeList() throws ParserConfigurationException, SAXException, IOException {
        String xml = "<types>"
            + "        <type-composite typeName=\"request\">\n"
            + "            <comment>This is the standard request body for a JSON RPC Request</comment>\n"
            + "            <field fieldName=\"id\" required=\"true\" type=\"int\"/>\n"
            + "            <field fieldName=\"method\" required=\"true\" type=\"string\"/>\n"
            + "            <field fieldName=\"params\" required=\"true\" type=\"string\"/>\n"
            + "            <field fieldName=\"jsonRPC\" required=\"false\" type=\"version_string\"/>\n"
            + "        </type-composite>\n"
            + "        <type-constrained baseType=\"string\" max=\"infinity\"\n"
            + "            min=\"4\" regex=\"^0x([0-9a-fA-F][0-9a-fA-F])+\" typeName=\"data_hex_string\"/>\n"
            + "        <type-constrained baseType=\"string\" max=\"infinity\" min=\"3\" regex=\"^0x[0-9a-fA-F]+\"\n"
            + "            typeName=\"hex_string\"/>\n"
            + "        <type-constrained baseType=\"data_hex_string\" max=\"66\" min=\"66\" typeName=\"address\"/>\n"
            + "        <type-enum typeName=\"version_string\" internalType = \"string\">\n"
            + "            <value name=\"Version2\" var=\"2.0\"/>\n"
            + "        </type-enum>\n"
            + "        <!-- param types specific to each method-->\n"
            + "        <type-params-wrapper typeName=\"ecRecoverParams\">\n"
            + "            <field fieldName=\"dataThatWasSigned\" index=\"0\" required=\"true\" type=\"string\"/>\n"
            + "            <field fieldName=\"signature\" index=\"1\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "        </type-params-wrapper>\n"
            + "        <!-- return type specific to each method-->\n"
            + "        <type-primitive typeName=\"string\"/>\n"
            + "        <type-primitive typeName=\"int\"/>\n"
            + "</types>";

        //Create the type definitions
        Document doc = XMLUtils.fromString(xml);
        Element root = doc.getDocumentElement();
        var types = assertDoesNotThrow(
            () -> XMLUtils.elements(root.getChildNodes()).stream().map(Type::fromNode)
                .collect(Collectors.toUnmodifiableList()));
        //Initialize the nodes
        SchemaUtils.initializeTypes(types);
        //Create well formed nodes
        PrimitiveType intType = new PrimitiveType("int", Collections.emptyList());
        PrimitiveType stringType = new PrimitiveType("string", Collections.emptyList());
        EnumType.EnumValues enumValue = new EnumValues("Version2", "2.0");
        CompositeType.Field compositeField = new CompositeType.Field("id", "int", "true");
        compositeField.setTypeDef(Collections.singletonList(intType));


        //Test that the xml structure was correctly converted to type definitions
        assertTrue(types.stream().anyMatch(m -> walkMapGraph(m.toMap(), intType.toMap())));
        assertTrue(types.stream().anyMatch(m -> walkMapGraph(m.toMap(), stringType.toMap())));
        assertTrue(types.stream().anyMatch(m -> walkMapGraph(m.toMap(), enumValue.toMap())));
        assertTrue(types.stream().anyMatch(m -> walkMapGraph(m.toMap(), compositeField.toMap())));

        //Test that the generated maps contain only the expected types
        assertTrue(types.stream()
            .allMatch(m -> checkTypes(m.toMap(), List.of(List.class, String.class, Map.class))));
    }

    /**
     * iterates through the map structure and finds the expected scheme
     *
     * @param typeSchema
     * @param schema
     * @return
     */
    public boolean walkMapGraph(Map<String, Object> typeSchema, Map schema) {
        if (compareStringMaps(typeSchema, schema)) {
            return true;
        }
        for (var entry : typeSchema.entrySet()) {

            if (entry.getValue() instanceof Map && compareStringMaps(
                (Map<String, Object>) entry.getValue(), schema)) {
                return true;
            } else if (entry.getValue() instanceof Map && walkMapGraph(
                (Map<String, Object>) entry.getValue(), schema)) {
                return true;
            } else if (entry.getValue() instanceof List && walkList((List) entry.getValue(),
                schema)) {
                return true;
            }
        }
        return false;
    }

    /**
     * iterates through the list and compares any maps found
     *
     * @param list
     * @param schema
     * @return
     */
    public boolean walkList(List list, Map schema) {
        for (Object o : list) {
            if (o instanceof Map && compareStringMaps((Map<String, Object>) o, schema)) {
                return true;
            } else if (o instanceof Map) {
                return walkMapGraph((Map<String, Object>) o, schema);
            }
        }
        return false;
    }

    /**
     * checks that the two generated schemes are identical
     *
     * @param map1
     * @param map2
     * @return
     */
    public boolean compareStringMaps(Map<String, Object> map1, Map<String, Object> map2) {
        boolean res0 = true;
        for (Map.Entry entry : map1.entrySet()) {
            if (!res0) {
                break;
            }
            boolean res1 = false;
            for (Map.Entry entry1 : map2.entrySet()) {
                if (res1) {
                    break;
                } else {
                    res1 = entry1.getValue().equals(entry.getValue()) &&
                        entry1.getKey().equals(entry.getKey());
                }
            }
            res0 = res1;
        }
        return res0;
    }

    /**
     * Check that all the maps in the structure have the expected types
     * @param map
     * @param classes
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean checkTypes(Map<String, ?> map, List<Class> classes) {
        boolean res = true;
        for (Object object : map.values()) {
            if (!res) {
                break;
            }
            if (object instanceof Map) {
                //check that any map that is encountered has the expected types
                res = checkTypes((Map<String, ?>) object, classes);
            } else if (object instanceof List) {
                res = checkTypes((List) object, classes);
            }
            if (!res) {
                break;
            }

            res = classes.stream().anyMatch(clazz -> clazz.isAssignableFrom(object.getClass()));

            if (!res) {
                System.out.println(object.getClass().getSimpleName());
            }
        }
        return res;
    }

    /**
     * Check that all elements in the list have the expected types
     * @param list
     * @param classes
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean checkTypes(List list, List<Class> classes) {
        boolean res = true;
        for (Object object :
            list) {
            if (!res) {
                break;
            } else {
                if (object instanceof Map) {
                    res = checkTypes((Map<String, ?>) object, classes);
                } else if (object instanceof List) {
                    res = false;
                }
                if (!res) {
                    break;
                }
                res = classes.stream().anyMatch(clazz -> clazz.isAssignableFrom(object.getClass()));
            }
        }
        return res;
    }
}
