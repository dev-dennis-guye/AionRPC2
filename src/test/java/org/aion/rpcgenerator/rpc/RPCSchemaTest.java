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
            + "    <error error_class=\"InvalidRequest\" code=\"-32600\" message=\"Invalid Request\" modifiable=\"false\"/>\n"
            + "    <error error_class=\"ParseError\" code=\"-32700\" message=\"Parse error\" modifiable=\"false\">\n"
            + "        <comment>Occurs if a user submits a malformed json payload</comment>\n"
            + "    </error>\n"
            + "    <error error_class=\"MethodNotFound\" code=\"-32601\" message=\"Method not found\" modifiable=\"true\"/>\n"
            + "    <error error_class=\"InvalidParams\" code=\"-32602\" message=\"Invalid params\"  modifiable=\"false\">\n"
            + "        <comment>Occurs if a user fails to supply the correct parameters for a method</comment>\n"
            + "    </error>\n"
            + "    <error error_class=\"InternalError\" code=\"-32603\" message=\"Internal error\"  modifiable=\"true\">\n"
            + "        <comment>Occurs if the server failed to complete the request</comment>\n"
            + "    </error>\n"
            + "</errors>";

        String typeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
            + "<types>\n"
            + "    <encode-error error_class=\"ParseError\"/>\n"
            + "    <decode-error error_class=\"ParseError\"/>\n"
            + "    <regexes>\n"
            + "        <regex name=\"hexPattern\" value=\"^0x[0-9a-fA-F]+$\"/>\n"
            + "        <regex name=\"decPattern\" value=\"^-?[0-9]+$\"/>\n"
            + "        <regex name=\"booleanPattern\" value=\"^([Tt]rue|[Ff]alse)$\"/>\n"
            + "        <regex name=\"byteArrayPattern\" value=\"^0x[0-9a-fA-F]*$\"/>\n"
            + "    </regexes>\n"
            + "    <union>\n"
            + "        <!--Unions are to be used anywhere multiple types are valid but only one should be used.-->\n"
            + "        <!--They are included in the RPC spec to ensure that all types are checked on compile time\n"
            + "        and so we avoid using language specific features like overloading or universal types. -->\n"
            + "        <type-union typeName=\"blockSpecifierUnion\" nullable=\"true\">\n"
            + "            <comment>Specifies a block</comment>\n"
            + "            <union-element type=\"byte-array\" name=\"hash\"/>\n"
            + "            <union-element type=\"long\" name=\"blockNumber\"/>\n"
            + "            <union-element type=\"blockEnum\" name=\"blockEnum\"/>\n"
            + "        </type-union>\n"
            + "        <type-union typeName=\"resultUnion\" nullable=\"true\">\n"
            + "            <!--Please use the same string for the name and type-->\n"
            + "            <comment>Ensures that the result is type safe</comment>\n"
            + "            <union-element type=\"blockDetails\" name=\"blockDetails\"/>\n"
            + "            <union-element type=\"address\" name=\"address\"/>\n"
            + "            <union-element type=\"byte-array\" name=\"byteArray\"/>\n"
            + "            <union-element type=\"bool\" name=\"bool\"/>\n"
            + "        </type-union>\n"
            + "        <type-union typeName=\"paramUnion\" nullable=\"false\">\n"
            + "            <comment>Ensures that the request is type safe</comment>\n"
            + "            <union-element type=\"ecRecoverParams\" name=\"ecRecoverParams\"/>\n"
            + "            <union-element type=\"blockSpecifier\" name=\"blockSpecifier\"/>\n"
            + "            <union-element type=\"voidParams\" name=\"voidParams\"/>\n"
            + "            <union-element type=\"submitSeedParams\" name=\"submitSeedParams\"/>\n"
            + "            <union-element type=\"submitSignatureParams\" name=\"submitSignatureParams\"/>\n"
            + "        </type-union>\n"
            + "    </union>\n"
            + "    <composite>\n"
            + "        <!--Composite types are used to define struct like data structures-->\n"
            + "        <type-composite typeName=\"request\">\n"
            + "            <comment>This is the standard request body for a JSON RPC Request</comment>\n"
            + "            <field fieldName=\"id\" required=\"false\" type=\"int\"/>\n"
            + "            <field fieldName=\"method\" required=\"true\" type=\"string\"/>\n"
            + "            <field fieldName=\"params\" required=\"false\" type=\"paramUnion\"/>\n"
            + "            <field fieldName=\"jsonrpc\" required=\"false\" type=\"version_string\"/>\n"
            + "        </type-composite>\n"
            + "        <type-composite typeName=\"response\">\n"
            + "            <comment>This is the standard response body for a JSON RPC Request</comment>\n"
            + "            <field fieldName=\"id\" required=\"false\" type=\"int\"/>\n"
            + "            <field fieldName=\"result\" required=\"false\" type=\"resultUnion\"/>\n"
            + "            <field fieldName=\"error\" required=\"false\" type=\"error\"/>\n"
            + "            <field fieldName=\"jsonrpc\" required=\"true\" type=\"version_string\"/>\n"
            + "        </type-composite>\n"
            + "        <type-composite typeName=\"error\">\n"
            + "            <comment>Contains the error messages for failed JSON RPC Requests</comment>\n"
            + "            <field fieldName=\"code\" required=\"true\" type=\"int\"/>\n"
            + "            <field fieldName=\"message\" required=\"true\" type=\"string\" />\n"
            + "        </type-composite>\n"
            + "        <type-composite typeName=\"txLogDetails\">\n"
            + "            <field fieldName=\"address\" required=\"true\" type=\"address\"/>\n"
            + "            <field fieldName=\"transactionIndex\" required=\"true\" type=\"int\"/>\n"
            + "            <field fieldName=\"data\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"topics\" required=\"true\" type=\"data_hex_string_array\"/>\n"
            + "            <field fieldName=\"blockNumber\" required=\"true\" type=\"long\"/>\n"
            + "        </type-composite>\n"
            + "        <type-composite typeName=\"txDetails\">\n"
            + "            <field fieldName=\"contractAddress\" required=\"false\" type=\"address\"/>\n"
            + "            <field fieldName=\"hash\" required=\"true\" type=\"byte_32_string\"/>\n"
            + "            <field fieldName=\"transactionIndex\" required=\"true\" type=\"int\"/>\n"
            + "            <field fieldName=\"value\" required=\"true\" type=\"big_int_hex_string\"/>\n"
            + "            <field fieldName=\"nrg\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"nrgPrice\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"gas\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"gasPrice\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"nonce\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"from\" required=\"true\" type=\"address\"/>\n"
            + "            <field fieldName=\"to\" required=\"false\" type=\"address\"/>\n"
            + "            <field fieldName=\"timestamp\" required=\"true\" type=\"long\"/>\n"
            + "            <field fieldName=\"input\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"blockNumber\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"blockHash\" required=\"true\" type=\"byte_32_string\"/>\n"
            + "            <field fieldName=\"error\" required=\"true\" type=\"string\"/>\n"
            + "            <field fieldName=\"type\" required=\"true\" type=\"byte_hex_string\"/>\n"
            + "            <field fieldName=\"nrgUsed\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"gasUsed\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"hasInternalTransactions\" required=\"true\" type=\"bool\"/>\n"
            + "            <field fieldName=\"logs\" required=\"true\" type=\"txLogDetails_array\"/>\n"
            + "            <field fieldName=\"beaconHash\" required=\"false\" type=\"byte_32_string\"/>\n"
            + "        </type-composite>\n"
            + "        <type-composite typeName=\"blockDetails\">\n"
            + "            <field fieldName=\"number\" required=\"true\" type=\"long\"/>\n"
            + "            <field fieldName=\"hash\" required=\"true\" type=\"byte_32_string\"/>\n"
            + "            <field fieldName=\"parentHash\" required=\"true\" type=\"byte_32_string\"/>\n"
            + "            <field fieldName=\"logsBloom\" required=\"true\" type=\"byte-array\"/>\n"
            + "            <field fieldName=\"transactionsRoot\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"statesRoot\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"receiptsRoot\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"difficulty\" required=\"true\" type=\"big_int_hex_string\"/>\n"
            + "            <field fieldName=\"totalDifficulty\" required=\"true\" type=\"big_int_hex_string\"/>\n"
            + "            <field fieldName=\"miner\" required=\"true\" type=\"address\" />\n"
            + "            <field fieldName=\"timestamp\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"gasUsed\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"gasLimit\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"nrgUsed\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"nrgLimit\" required=\"true\" type=\"long_hex_string\"/>\n"
            + "            <field fieldName=\"sealType\" required=\"true\" type=\"byte_hex_string\"/>\n"
            + "            <field fieldName=\"mainChain\" required=\"true\" type=\"bool\"/>\n"
            + "            <field fieldName=\"extraData\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"size\" required=\"true\" type=\"int\"/>\n"
            + "            <field fieldName=\"numTransactions\" required=\"true\" type=\"int\"/>\n"
            + "            <field fieldName=\"txTrieRoot\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"blockReward\" required=\"true\" type=\"big_int_hex_string\"/>\n"
            + "            <field fieldName=\"transactions\" required=\"true\" type=\"txDetails_array\"/>\n"
            + "            <field fieldName=\"nonce\" required=\"false\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"solution\" required=\"false\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"seed\" required=\"false\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"signature\" required=\"false\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"publicKey\" required=\"false\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"blockTime\" required=\"false\" type=\"int\"/>\n"
            + "        </type-composite>\n"
            + "    </composite>\n"
            + "    <array>\n"
            + "        <!--Array types are self explanatory. They can nest a composite type, constrained type, primitive type or param type.-->\n"
            + "        <!--TODO explicitly prevent nesting of arrays-->\n"
            + "        <type-list typeName=\"request_array\" nestedType=\"request\"/>\n"
            + "        <type-list typeName=\"response_array\" nestedType=\"response\"/>\n"
            + "        <type-list typeName=\"data_hex_string_array\" nestedType=\"data_hex_string\"/>\n"
            + "        <type-list typeName=\"txLogDetails_array\" nestedType=\"txLogDetails\"/>\n"
            + "        <type-list typeName=\"txDetails_array\" nestedType=\"txDetails\"/>\n"
            + "    </array>\n"
            + "    <constrained>\n"
            + "        <!--Constrained types define a maximum and minimum size, and a regex to be used for validating the type-->\n"
            + "        <!--They all rely on a base type-->\n"
            + "        <type-constrained baseType=\"byte-array\" max=\"infinity\"\n"
            + "            min=\"2\" regex=\"^0x([0-9a-fA-F][0-9a-fA-F])*$\" typeName=\"data_hex_string\"/>\n"
            + "        <type-constrained baseType=\"bigint\" max=\"infinity\" min=\"3\" regex=\"^0x[0-9a-fA-F]+$\"\n"
            + "            typeName=\"big_int_hex_string\"/>\n"
            + "        <type-constrained baseType=\"long\" min=\"3\" max=\"19\" regex=\"^0x[0-9a-fA-F]+$\"\n"
            + "            typeName=\"long_hex_string\"/>\n"
            + "        <type-constrained baseType=\"int\" min=\"3\" max=\"11\" regex=\"^0x[0-9a-fA-F]+$\"\n"
            + "            typeName=\"int_hex_string\"/>\n"
            + "        <type-constrained baseType=\"byte\" min=\"3\" max=\"4\" regex=\"^0x[0-9a-fA-F]+$\"\n"
            + "            typeName=\"byte_hex_string\"/>\n"
            + "        <type-constrained baseType=\"data_hex_string\" typeName=\"byte_32_string\" max=\"66\" min=\"66\" regex=\".*\"/>\n"
            + "        <type-constrained baseType=\"data_hex_string\" typeName=\"byte_64_string\" max=\"130\" min=\"130\" regex=\".*\"/>\n"
            + "    </constrained>\n"
            + "    <enum>\n"
            + "        <!--Enum types are used in the case of a string or value with a restricted set of values-->\n"
            + "        <type-enum typeName=\"version_string\" internalType = \"string\">\n"
            + "            <value name=\"Version2\" var=\"2.0\"/>\n"
            + "        </type-enum>\n"
            + "        <type-enum typeName=\"blockEnum\" internalType=\"string\">\n"
            + "            <value name=\"LATEST\" var=\"latest\"/>\n"
            + "        </type-enum>\n"
            + "    </enum>\n"
            + "    <param>\n"
            + "        <!-- param types specific to each method-->\n"
            + "        <type-params-wrapper typeName=\"ecRecoverParams\">\n"
            + "            <field fieldName=\"dataThatWasSigned\" index=\"0\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "            <field fieldName=\"signature\" index=\"1\" required=\"true\" type=\"data_hex_string\"/>\n"
            + "        </type-params-wrapper>\n"
            + "        <type-params-wrapper typeName=\"blockSpecifier\">\n"
            + "            <field fieldName=\"block\" index=\"0\" required=\"true\" type=\"blockSpecifierUnion\"/>\n"
            + "        </type-params-wrapper>\n"
            + "        <type-params-wrapper typeName=\"submitSeedParams\">\n"
            + "            <field fieldName=\"newSeed\" index=\"0\" required=\"true\" type=\"byte_64_string\"/>\n"
            + "            <field fieldName=\"signingPublicKey\" index=\"1\" required=\"true\" type=\"byte_32_string\"/>\n"
            + "            <field fieldName=\"coinbase\" index=\"2\" required=\"true\" type=\"address\"/>\n"
            + "        </type-params-wrapper>\n"
            + "        <type-params-wrapper typeName=\"submitSignatureParams\">\n"
            + "            <field fieldName=\"signature\" index=\"0\" required=\"true\" type=\"byte_64_string\"/>\n"
            + "            <field fieldName=\"sealHash\" index=\"1\" required=\"true\" type=\"byte_32_string\"/>\n"
            + "        </type-params-wrapper>\n"
            + "        <type-params-wrapper typeName=\"voidParams\"/>\n"
            + "        <!-- return type specific to each method-->\n"
            + "    </param>\n"
            + "    <primitives>\n"
            + "        <!--Base types-->\n"
            + "        <type-primitive typeName=\"any\"/><!--TODO remove-->\n"
            + "        <type-primitive typeName=\"bool\"/>\n"
            + "        <type-primitive typeName=\"string\"/>\n"
            + "        <type-primitive typeName=\"long\"/>\n"
            + "        <type-primitive typeName=\"int\"/>\n"
            + "        <type-primitive typeName=\"byte\"/>\n"
            + "        <type-primitive typeName=\"bigint\"/>\n"
            + "        <type-primitive typeName=\"byte-array\"/>\n"
            + "        <type-primitive typeName=\"address\"/>\n"
            + "    </primitives>\n"
            + "</types>";

        Document doc = XMLUtils.fromString(xmlError);
        List<ErrorSchema> errors = assertDoesNotThrow(() -> ErrorSchema.fromDocument(doc));
        TypeSchema typeSchema = new TypeSchema(XMLUtils.fromString(typeXml));
        typeSchema.setErrors(errors);
        RPCSchema schema = new RPCSchema(XMLUtils.fromString(xml), typeSchema, errors);

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
                new ParamType.Field(0, "dataThatWasSigned", "string", "true", ""),
                new ParamType.Field(1, "signature", "data_hex_string", "true", "")
            ));
        paramTypeEcRecover.setFieldTypeDef(List.of(hexType, stringType));

        assertTrue(walkMapGraph(schema.toMap(), stringType.toMap()));
        assertTrue(walkMapGraph(schema.toMap(), intType.toMap()));
        assertTrue(walkMapGraph(schema.toMap(), enumValue.toMap()));
        assertTrue(walkMapGraph(schema.toMap(), compositeField.toMap()));

        MethodSchema methodSchema = new MethodSchema("personal_ecRecover", "ecRecoverParams",
            "data_hex_string", List.of(
            "Allows you to interact with accounts on the aion network and provides a handful of crypto utilities"),
            Collections.emptyList());
        methodSchema.setReturnType(List.of(hexType));
        methodSchema.setParamType(List.of(paramTypeEcRecover));
        ErrorSchema errorSchema = new ErrorSchema("InvalidRequest", -32600, "Invalid Request",
            Collections.emptyList(), "false");
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
