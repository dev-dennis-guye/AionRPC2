package org.aion.rpcgenerator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.aion.rpcgenerator.data.TypeSchema;
import org.aion.rpcgenerator.error.ErrorSchema;
import org.aion.rpcgenerator.rpc.RPCSchema;
import org.aion.rpcgenerator.util.XMLUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class CliTest {

    private static String badDirectoryLocation = "defitions/badlocation";
    private static String nonExistentDirectory = "definitons/doesNotExist";
    private Cli cli;
    private Configuration configuration;
    private String errorsXML;
    private String rpcXML;
    private String typeXML;
    private List<ErrorSchema> errors;
    private TypeSchema typeSchema;
    private RPCSchema rpcSchema;
    private PrintWriter printWriter;

    @BeforeAll
    static void beforeAll() {
        new File(badDirectoryLocation).delete();
        assertTrue(new File(badDirectoryLocation).mkdirs());
    }

    @AfterAll
    static void afterAll() {
        assertTrue(new File(badDirectoryLocation).delete());
    }

    @BeforeEach
    void setup() throws ParserConfigurationException, SAXException, IOException {
        cli = new Cli(false,
            new String[]{"definitions/templates/rpc", "definitions/templates/errors"},
            "definitions/spec", "out");
        configuration = new Configuration();
        errorsXML = "definitions/spec/errors.xml";
        rpcXML = "definitions/spec/personal-rpc.xml";
        typeXML = "definitions/spec/types.xml";
        errors = ErrorSchema.fromDocument(XMLUtils.fromFile(errorsXML));
        typeSchema = new TypeSchema(XMLUtils.fromFile(typeXML));
        typeSchema.setErrors(errors);
        rpcSchema = new RPCSchema(XMLUtils.fromFile(rpcXML), errors, typeSchema);
        printWriter = new PrintWriter(System.out);
    }

    @Test
    void testValidateSpec(){
        Cli cli = new Cli(false, new String[]{}, "definitions/spec", "out" );
        Assertions.assertDoesNotThrow(cli::checkSpec);
        cli = new Cli(false, new String[]{}, "definitions/spec/types.xml", "out" );//a single file
        Assertions.assertThrows(IllegalStateException.class, cli::checkSpec);
        cli = new Cli(false, new String[]{}, badDirectoryLocation, "out" );//empty directory
        Assertions.assertThrows(IllegalStateException.class, cli::checkSpec);
        cli = new Cli(false, new String[]{}, nonExistentDirectory, "out" );//none existent dir
        Assertions.assertThrows(IllegalStateException.class, cli::checkSpec);
    }

    @Test
    void testValidateTemplates(){
        Cli cli = new Cli(false, new String[]{"definitions/templates/rpc","definitions/templates/errors"}, "definitions/spec", "out");
        Assertions.assertDoesNotThrow(cli::checkTemplates);
        cli = new Cli(false, new String[]{badDirectoryLocation}, "definitions/spec", "out");//empty directory
        Assertions.assertThrows(IllegalStateException.class, cli::checkTemplates);
        cli = new Cli(false, new String[]{"definitions/templates/rpc/java_macros.ftl"}, "definitions/spec", "out");//a single file
        Assertions.assertThrows(IllegalStateException.class,cli::checkTemplates);
        cli = new Cli(false, new String[]{nonExistentDirectory}, "definitions/spec", "out");//none existent dir
        Assertions.assertThrows(IllegalStateException.class,cli::checkTemplates);
    }

    @Test
    void testProcessExceptions() {

        assertDoesNotThrow(() -> cli
            .process(configuration, "definitions/templates/errors/java_exceptions.ftl", printWriter,
                Map.ofEntries(Map.entry("errors",
                    errors.stream().map(ErrorSchema::toMap)
                        .collect(Collectors.toUnmodifiableList()))))
        );
    }

    @Test
    void testProcessRPCSchema(){
        assertDoesNotThrow(() -> cli
            .process(configuration, "definitions/templates/rpc/java_rpc.ftl", printWriter,
                rpcSchema.toMap())
        );
    }

    @Test
    void testProcessTypesTemplate(){
        assertDoesNotThrow(() -> cli
            .process(configuration, "definitions/templates/types/java_types.ftl", printWriter,
                typeSchema.toMap()));
    }

    @Test
    void testProcessTypesConverterTemplate(){
        assertDoesNotThrow(() -> cli
            .process(configuration, "definitions/templates/types/java_type_converter.ftl",
                printWriter, typeSchema.toMap()));
    }
}
