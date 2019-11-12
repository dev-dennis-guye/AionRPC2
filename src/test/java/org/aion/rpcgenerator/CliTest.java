package org.aion.rpcgenerator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.ByteArrayOutputStream;
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
import org.aion.rpcgenerator.util.Utils;
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
    private ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
    private static File output = new File("output");
    private static File errorsOutputFile = new File(Utils.appendToPath(output.getPath(), "errors"));
    private static File rpcOutputFile = new File(Utils.appendToPath(output.getPath(), "rpc"));
    private static File typesOutputFile = new File(Utils.appendToPath(output.getPath(), "types"));
    @BeforeAll
    static void beforeAll() {
        new File(badDirectoryLocation).delete();
        assertTrue(new File(badDirectoryLocation).mkdirs());
        assertTrue(!output.exists() || recursivelyDelete(output));
    }

    /**
     *
     * @param folder the folder that needs to be deleted
     * @return true if the operation succeeded
     */
    private static boolean recursivelyDelete(File folder){
        if (folder.isDirectory()){
            File[] files = folder.listFiles();
            if (files == null || files.length==0){
                return folder.delete();
            }
            else {
                boolean res=true;
                for (File file : files) {
                    res&=recursivelyDelete(file);
                }
                return res;
            }
        }
        else return folder.delete();
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
        rpcSchema = new RPCSchema(XMLUtils.fromFile(rpcXML), typeSchema, errors);
        System.out.println(new String(byteArrayOutputStream.toByteArray()));
        byteArrayOutputStream.reset();
        printWriter = new PrintWriter(byteArrayOutputStream);
    }

    @Test
    void testProcessAllErrors() throws IOException, TemplateException {
        cli.processAllErrors(errorsOutputFile, errors, "definitions/templates/errors", configuration);
        assertTrue(errorsOutputFile.exists());
        assertNotNull(errorsOutputFile.listFiles());
        //noinspection ConstantConditions
        assertTrue(errorsOutputFile.listFiles().length>0);
    }

    @Test
    void testProcessAllRPC() throws IOException, TemplateException {
        cli.processAllRPCTemplates(rpcOutputFile, rpcSchema, "definitions/templates/rpc", configuration);
        assertTrue(rpcOutputFile.exists());
        assertNotNull(rpcOutputFile.listFiles());
        //noinspection ConstantConditions
        assertTrue(rpcOutputFile.listFiles().length>0);
    }

    @Test
    void testProcessAllTypes() throws IOException, TemplateException {
        cli.processAllTypes(typesOutputFile, typeSchema, "definitions/templates/types", configuration);
        assertTrue(typesOutputFile.exists());
        assertNotNull(typesOutputFile.listFiles());
        //noinspection ConstantConditions
        assertTrue(typesOutputFile.listFiles().length>0);
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
        assertTrue(byteArrayOutputStream.size()>0);
    }

    @Test
    void testProcessRPCSchema(){
        assertDoesNotThrow(() -> cli
            .process(configuration, "definitions/templates/rpc/java_rpc.ftl", printWriter,
                rpcSchema.toMap())
        );
        assertTrue(byteArrayOutputStream.size()>0);
    }

    @Test
    void testProcessTypesTemplate(){
        assertDoesNotThrow(() -> cli
            .process(configuration, "definitions/templates/types/java_types.ftl", printWriter,
                typeSchema.toMap()));
        assertTrue(byteArrayOutputStream.size()>0);
    }

    @Test
    void testProcessTypesConverterTemplate(){
        assertDoesNotThrow(() -> cli
            .process(configuration, "definitions/templates/types/java_type_converter.ftl",
                printWriter, typeSchema.toMap()));
        assertTrue(byteArrayOutputStream.size()>0);
    }
}
