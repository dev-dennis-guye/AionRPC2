package org.aion.rpcgenerator.error;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.aion.rpcgenerator.util.XMLUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ErrorSchemaTests {

    @Test
    public void errorsFromDocument()
        throws ParserConfigurationException, SAXException, IOException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
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

        Document doc = XMLUtils.fromString(xml);
        List<ErrorSchema> errors = assertDoesNotThrow(() -> ErrorSchema.fromDocument(doc));
        List<String> errorClasses = errors.stream().map(ErrorSchema::getErrorClass)
            .collect(Collectors.toUnmodifiableList());
        List<String> codes = errors.stream().mapToInt(ErrorSchema::getCode).boxed()
            .map(Object::toString).collect(Collectors.toUnmodifiableList());
        List<String> errorMessages = errors.stream().map(ErrorSchema::getMessage)
            .collect(Collectors.toUnmodifiableList());
        //Validate that all attributes were read
        assertTrue(List.of("InvalidRequest", "ParseError", "MethodNotFound", "InvalidParams",
            "InternalError")
                .containsAll(errorClasses),
            "[" + String.join(",", errorClasses) + "]"
        );

        assertTrue(List.of("-32600", "-32700", "-32601", "-32602", "-32603").containsAll(codes),
            "[" + String.join(",", codes) + "]");

        assertTrue(List.of("Invalid Request", "Parse error", "Method not found", "Invalid params",
            "Internal error").containsAll(errorMessages),
            "[" + String.join(",", errorMessages) + "]");

        //Ensure that we can create the maps
        List<Map<String, Object>> mapList = assertDoesNotThrow(
            () -> errors.stream().map(ErrorSchema::toMap).collect(
                Collectors.toUnmodifiableList()));

        //Validate that one object was correctly constructed

        Map<String, Object> schema = mapList.stream()
            .filter(e -> e.get("error_class").equals("InternalError")).findFirst().orElseThrow();

        assertTrue(
            List.of("error_class", "comments", "code", "message").containsAll(schema.keySet()));
        //Validate the data of an internal error
        assertEquals("Internal error", schema.get("message"));
        assertEquals("-32603", schema.get("code"));
        assertEquals(List.of("Occurs if the server failed to complete the request"),
            schema.get("comments"));

    }
}
