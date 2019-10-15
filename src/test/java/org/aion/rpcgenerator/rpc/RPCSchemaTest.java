package org.aion.rpcgenerator.rpc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.aion.rpcgenerator.data.CompositeType;
import org.aion.rpcgenerator.data.ConstrainedType;
import org.aion.rpcgenerator.data.EnumType;
import org.aion.rpcgenerator.data.EnumType.EnumValues;
import org.aion.rpcgenerator.data.ParamType;
import org.aion.rpcgenerator.data.PrimitiveType;
import org.aion.rpcgenerator.data.TypeSchema;
import org.aion.rpcgenerator.error.ErrorSchema;
import org.aion.rpcgenerator.util.XMLUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class RPCSchemaTest {

    @Test
    void testMethodFromDoc() throws ParserConfigurationException, SAXException, IOException {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
            + "<rpc name=\"personal\">\n"
            + "    <errors>\n"
            + "        <error error_class=\"InvalidRequest\"/>\n"
            + "        <error error_class=\"ParseError\"/>\n"
            + "        <error error_class=\"MethodNotFound\"/>\n"
            + "        <error error_class=\"InvalidParams\"/>\n"
            + "        <error error_class=\"InternalError\"/>\n"
            + "    </errors>\n"
            + "    <types>\n"
            + "        <type-primitive typeName=\"string\"/>\n"
            + "        <type-primitive typeName=\"long\"/>\n"
            + "        <type-constrained typeName=\"data_hex_string\"/>\n"
            + "        <type-constrained typeName=\"hex_string\"/>\n"
            + "        <type-constrained typeName=\"address\"/>\n"
            + "        <type-enum typeName=\"version_string\"/>\n"
            + "        <type-composite typeName=\"request\"/>\n"
            + "        <type-params-wrapper typeName=\"ecRecoverParams\"/>\n"
            + "    </types>\n"
            + "    <methods>\n"
            + "        <method name=\"personal_ecRecover\" returnType=\"data_hex_string\" param=\"ecRecoverParams\">\n"
            + "            <comment>Returns the key used to sign an input string.</comment>\n"
            + "        </method>\n"
            + "    </methods>\n"
            + "    <comments>\n"
            + "        <comment>Allows you to interact with accounts on the aion network and provides a handful of crypto utilities</comment>\n"
            + "    </comments>\n"
            + "</rpc>";
        String xmlError = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
            + "<errors>\n"
            + "    <error error_class=\"InvalidRequest\" code=\"-32600\" message=\"Invalid Request\"/>\n"
            + "    <error error_class=\"ParseError\" code=\"-32700\" message=\"Parse error\">\n"
            + "        <comment>Occurs if a user submits a malformed json payload</comment>\n"
            + "    </error>\n"
            + "    <error error_class=\"MethodNotFound\" code=\"-32601\" message=\"Method not found\"/>\n"
            + "    <error error_class=\"InvalidParams\" code=\"-32602\" message=\"Invalid params\">\n"
            + "        <comment>Occurs if a user fails to supply the correct parameters for a method</comment>\n"
            + "    </error>\n"
            + "    <error error_class=\"InternalError\" code=\"-32603\" message=\"Internal error\">\n"
            + "        <comment>Occurs if the server failed to complete the request</comment>\n"
            + "    </error>\n"
            + "</errors>";

        String typeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
            + "<types>\n"
            + "    <encode-error error_class=\"ParseError\"/>\n"
            + "    <decode-error error_class=\"ParseError\"/>\n"
            + "    <composite>\n"
            + "        <type-composite typeName=\"request\">\n"
            + "            <comment>This is the standard request body for a JSON RPC Request</comment>\n"
            + "            <field fieldName=\"id\" required=\"true\" type=\"long\"/>\n"
            + "            <field fieldName=\"method\" required=\"true\" type=\"string\"/>\n"
            + "            <field fieldName=\"params\" required=\"true\" type=\"string\"/>\n"
            + "            <field fieldName=\"jsonRPC\" required=\"false\" type=\"version_string\"/>\n"
            + "        </type-composite>\n"
            + "    </composite>\n"
            + "    <constrained>\n"
            + "        <type-constrained baseType=\"string\" max=\"infinity\"\n"
            + "            min=\"4\" regex=\"^0x([0-9a-fA-F][0-9a-fA-F])+\" typeName=\"data_hex_string\"/>\n"
            + "        <type-constrained baseType=\"long\" max=\"infinity\" min=\"3\" regex=\"^0x[0-9a-fA-F]+\"\n"
            + "            typeName=\"hex_string\"/>\n"
            + "        <type-constrained baseType=\"data_hex_string\" max=\"66\" min=\"66\" typeName=\"address\"/>\n"
            + "    </constrained>\n"
            + "    <enum>\n"
            + "        <type-enum typeName=\"version_string\" internalType = \"string\">\n"
            + "            <value name=\"Version2\" var=\"2.0\"/>\n"
            + "        </type-enum>\n"
            + "    </enum>\n"
            + "    <param>\n"
            + "        <!-- param types specific to each method-->\n"
            + "        <type-params-wrapper typeName=\"ecRecoverParams\">\n"
            + "            <field fieldName=\"dataThatWasSigned\" index=\"0\" required=\"true\" type=\"string\"/>\n"
            + "            <field fieldName=\"signature\" index=\"1\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "        </type-params-wrapper>\n"
            + "        <!-- return type specific to each method-->\n"
            + "    </param>\n"
            + "    <primitives>\n"
            + "        <type-primitive typeName=\"string\"/>\n"
            + "        <type-primitive typeName=\"long\"/>\n"
            + "    </primitives>\n"
            + "</types>";

        Document doc = XMLUtils.fromString(xmlError);
        List<ErrorSchema> errors = assertDoesNotThrow(() -> ErrorSchema.fromDocument(doc));
        TypeSchema typeSchema = new TypeSchema(XMLUtils.fromString(typeXml));
        typeSchema.setErrors(errors);
        RPCSchema schema = new RPCSchema(XMLUtils.fromString(xml), errors, typeSchema);

        PrimitiveType intType = new PrimitiveType("int", Collections.emptyList());
        PrimitiveType stringType = new PrimitiveType("string", Collections.emptyList());
        EnumType.EnumValues enumValue = new EnumValues("Version2",  "2.0");
        CompositeType.Field compositeField = new CompositeType.Field("id", "int", "true");
        compositeField.setTypeDef(Collections.singletonList(intType));
        ConstrainedType hexType = new ConstrainedType("data_hex_string", Collections.emptyList(),
            "^0x([0-9a-fA-F][0-9a-fA-F])+", 4, Integer.MAX_VALUE, "string");

        hexType.setBaseTypeDef(List.of(stringType));

        ParamType paramTypeEcRecover = new ParamType("ecRecoverParams", Collections.emptyList(),
            List.of(
                new ParamType.Field(0, "dataThatWasSigned", "string", "true"),
                new ParamType.Field(1, "signature", "data_hex_string", "true")
            ));
        paramTypeEcRecover.setFieldTypeDef(List.of(hexType, stringType));

        assertTrue(walkMapGraph(schema.toMap(), stringType.toMap()));
        assertTrue(walkMapGraph(schema.toMap(), intType.toMap()));
        assertTrue(walkMapGraph(schema.toMap(), enumValue.toMap()));
        assertTrue(walkMapGraph(schema.toMap(), compositeField.toMap()));

        MethodSchema methodSchema = new MethodSchema("personal_ecRecover", "ecRecoverParams",
            "data_hex_string", List.of(
            "Allows you to interact with accounts on the aion network and provides a handful of crypto utilities"));
        methodSchema.setReturnType(List.of(hexType));
        methodSchema.setParamType(List.of(paramTypeEcRecover));
        ErrorSchema errorSchema = new ErrorSchema("InvalidRequest", -32600, "Invalid Request",
            Collections.emptyList());
        assertEquals("personal", schema.toMap().get("rpc"));

        assertTrue(walkMapGraph(schema.toMap(), methodSchema.toMap()));
        assertTrue(walkMapGraph(schema.toMap(), errorSchema.toMap()));
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

        for (Map.Entry entry : map1.entrySet()) {
            for (Map.Entry entry1 : map2.entrySet()) {
                if (entry1.getValue().equals(entry.getValue()) &&
                    entry1.getKey().equals(entry.getKey())) {
                    return true;
                } else {
                    if (entry.getValue() instanceof List && entry1.getValue() instanceof List) {
                        boolean res = ((List) entry.getValue()).containsAll(
                            (Collection<?>) entry1.getValue()) && entry1.getValue()
                            .equals(entry.getValue());
                        if (res) {
                            return true;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check that all the maps in the structure have the expected types
     *
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
     *
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
