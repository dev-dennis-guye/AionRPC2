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
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class CliTest {

    private static String badDirectoryLocation = "defitions/badlocation";
    private static String nonExistentDirectory ="definitons/doesNotExist";

    @BeforeAll
    static void beforeAll(){
        new File(badDirectoryLocation).delete();
        assertTrue(new File(badDirectoryLocation).mkdirs());
    }

    @AfterAll
    static void afterAll(){
        assertTrue(new File(badDirectoryLocation).delete());
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
    void testProcess()
        throws ParserConfigurationException, SAXException, IOException, TemplateException {
        Cli cli = new Cli(false,
            new String[]{"definitions/templates/rpc", "definitions/templates/errors"},
            "definitions/spec", "out");

        Configuration configuration = new Configuration();
        String errorsXML = "definitions/spec/errors.xml";
        String rpcXML = "definitions/spec/personal-rpc.xml";
        String typeXML = "definitions/spec/types.xml";

        List<ErrorSchema> errors = ErrorSchema.fromDocument(XMLUtils.fromFile(errorsXML));
        TypeSchema typeSchema = new TypeSchema(XMLUtils.fromFile(typeXML));
        typeSchema.setErrors(errors);
        RPCSchema rpcSchema = new RPCSchema(XMLUtils.fromFile(rpcXML), errors, typeSchema);
        PrintWriter printWriter = new PrintWriter(System.out);

        assertDoesNotThrow( () ->cli.process(configuration, "definitions/templates/errors/java_exceptions.ftl", printWriter , Map.ofEntries(Map.entry("errors",
            errors.stream().map(ErrorSchema::toMap)
                .collect(Collectors.toUnmodifiableList()))))
        );

        assertDoesNotThrow( () ->cli.process(configuration, "definitions/templates/rpc/java_rpc.ftl", printWriter , rpcSchema.toMap())
        );

    }
}
