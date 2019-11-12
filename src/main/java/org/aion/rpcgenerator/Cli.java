package org.aion.rpcgenerator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.aion.rpcgenerator.data.TypeSchema;
import org.aion.rpcgenerator.error.ErrorSchema;
import org.aion.rpcgenerator.rpc.RPCSchema;
import org.aion.rpcgenerator.util.Utils;
import org.aion.rpcgenerator.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

//TODO move the template and spec filenames to an external file/configuration step
@Command(name = "generator", mixinStandardHelpOptions = true)
public class Cli implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Cli.class);
    @Option(names = {"--verbose"}, defaultValue = "false")
    private boolean verbose;//indicates whether the debug level should be used
    @Option(names = {"--templates",
        "-t"}, arity = "1..3", description = "The root directory of each template. The template directories must be named errors, rpc or types.")
    private String[] templates;//the list of templates that should be used
    @Option(names = {"--spec",
        "-s"}, arity = "1", defaultValue = "definitions/spec", description = "The root directory for the xml specification files.")
    private String spec;//the root directory of all the spec files
    @Option(names = {"--out",
        "-o"}, defaultValue = "output", description = "The output output directory for the generated files.")
    private String output;// the output directory

    private Cli() {
    }

    Cli(boolean verbose, String[] templates, String spec, String output) {
        this.verbose = verbose;
        this.templates = templates;
        this.spec = spec;
        this.output = output;
    }

    public static void main(String[] args) {
        CommandLine cli = new CommandLine(new Cli());
        cli.setCaseInsensitiveEnumValuesAllowed(true);
        cli.execute(args);
    }

    /**
     * Runs the code generator with the supplied commands
     */
    @Override
    public void run() {
        setLogLevel();//change the log level
        try {
            checkTemplates();// set the default template location
            checkSpec();//validate the spec path is valid
            logger.info("Starting RPC generator...");
            Configuration configuration = new Configuration();
            logger.debug("Creating templates");
            Path specPath = Paths.get(spec);
            if (specPath.toFile().exists() && specPath.toFile().isDirectory()) {
                logger.debug("Attempting to read spec...");
                File errors = Paths.get(Utils.appendToPath(spec,"errors.xml")).toFile();//get the error spec file
                logger.debug("Reading error template files.");
                //TODO replace this list of errors with a single object
                List<ErrorSchema> errorSchemas = ErrorSchema
                    .fromDocument(XMLUtils.fromFile(errors));// convert the file to a list of errors
                File types = Paths.get(Utils.appendToPath(spec, "types.xml")).toFile();
                TypeSchema typeSchema = new TypeSchema(XMLUtils.fromFile(types));//convert the types to a schema object
                typeSchema.setErrors(errorSchemas); // set the errors in the type schema
                logger.debug("Reading rpc template files.");
                //noinspection ConstantConditions
                //Read the rpc spec into the application
                RPCSchema rpcSchema = new RPCSchema(XMLUtils.fromFile(Utils.appendToPath(spec, "rpc.xml")), typeSchema, errorSchemas);

                //create the output path
                File errorsOutputFile = new File(Utils.appendToPath(output, "errors"));
                File rpcOutputFile = new File(Utils.appendToPath(output, "rpc"));
                File typesOutputFile = new File(Utils.appendToPath(output, "types"));

                for (String template : templates) {// for each defined template file parse the template
                    logger.debug("Processing: {}", template);
                    if (template.contains("errors")) {
                        processAllErrors(errorsOutputFile, errorSchemas, template, configuration);
                    } else if (template.contains("rpc")) {
                        processAllRPCTemplates(rpcOutputFile, rpcSchema, template, configuration);
                    } else if (template.endsWith("types")) {
                        processAllTypes(typesOutputFile, typeSchema, template, configuration);
                    } else {
                        logger.warn("Encountered an unidentified template {}", template);// log any unrecognize file
                    }
                }
                logger.info("Successfully autogenerated files for:\n\t{}", String.join( "\n\t", templates));
            } else {
                logger.warn("Could not open xml specification directory. Check if it exists");
            }
        } catch (RuntimeException e) {
            logger.warn("Failed unexpectedly, check the application arguments.");
            logException(e);
            CommandLine.usage(this, System.out);//print out the help info if we
            // encountered a runtime exception
        } catch (IOException e) {
            logger.warn("Failed due to io.");
            logException(e);
        } catch (SAXException | ParserConfigurationException e) {
            logger.warn("Could not read the xml file.", e);
            logException(e);
        } catch (TemplateException e) {
            logger.warn("Failed to create file from template.", e);
            logException(e);
        }
    }

    private void logException(Exception e) {
        logger.debug("Failed due to exception.", e);
    }


     void processAllErrors(File outputFile, List<ErrorSchema> errorSchemas,
        String templatePath, Configuration configuration)
        throws IOException, TemplateException {
        try {
            createDir(outputFile);// create the output directory that will store the error
            //noinspection ConstantConditions
            List<String> errorTemplates = Arrays
                .stream(Paths.get(templatePath).toFile().listFiles()).filter(
                    p -> p.getName().endsWith("exceptions.ftl") || p.getName()
                        .endsWith("errors.ftl"))
                .map(File::getPath).collect(Collectors.toUnmodifiableList());// get all the error
            // templates which match the expected names
            Map<String, Object> errors = Map.ofEntries(// create the error map
                Map.entry("date", ZonedDateTime.now().toLocalDate()),
                Map.entry("errors", Utils.toListOfMaps(errorSchemas)));

            for (String templateFile : errorTemplates) {
                String outputFileName;
                if (Utils.isJavaTemplate(templateFile)) {// create the output file name
                    //this can be moved to an external error file
                    outputFileName = "RPCExceptions.java";
                } else {
                    outputFileName = "";
                }
                File temp = new File(
                    Utils.appendToPath(outputFile.getAbsolutePath(), outputFileName));
                if (createOutputFile(temp)) {// create the file to write to
                    try(PrintWriter printWriter=new PrintWriter(temp)) {
                        process(configuration, templateFile, printWriter, errors);
                    }
                    logProcessedTemplate(outputFileName, templateFile);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to process error templates");
            throw e;
        }
    }

    /**
     * Logs a message for issue processed file
     * @param outputFile the output file name
     * @param templateFile the template file processed
     */
    private void logProcessedTemplate(String outputFile, String templateFile) {
        logger.info("Wrote {} file for template: {}", outputFile, templateFile);
    }


    //Processes RPC templates
    void processAllRPCTemplates(File outputFile, RPCSchema rpcSchema, String templatePath,
        Configuration configuration)
        throws IOException, TemplateException {
        try {
            createDir(outputFile);
            //noinspection ConstantConditions
            List<String> errorTemplates = Arrays
                .stream(Paths.get(templatePath).toFile().listFiles()).filter(
                    p -> p.getName().endsWith("rpc.ftl") || p.getName().endsWith("rpc_client.ftl")
                        || p.getName().endsWith("testUtils.ftl") || p.getName().endsWith("test.ftl"))
                .map(File::getPath).collect(Collectors.toUnmodifiableList());//get all the supported template files
            for (String templateFile : errorTemplates) {
                String outputFileName;
                if (Utils.isJavaTemplate(templateFile)) {
                    if(templateFile.endsWith("rpc.ftl")) {
                        outputFileName ="RPCServerMethods.java";// server interface
                    } else if (templateFile.endsWith("rpc_client.ftl")){
                        outputFileName="RPCClientMethods.java";// client methods
                    } else if(templateFile.endsWith("test.ftl")){
                        outputFileName="RPCMethodsTest.java";// rpc test cases
                    } else if(templateFile.endsWith("testUtils.ftl")){
                        outputFileName="RPCTestUtilsInterface.java";// rpc test environment
                    } else {
                        logger.warn("Encountered an unexpected file: {}", templateFile);
                        continue;
                    }
                } else {
                    logger.warn("Encountered an unexpected file: {}", templateFile);
                    continue;
                }
                File temp = new File(
                    Utils.appendToPath(outputFile.getAbsolutePath(), outputFileName));
                //create the output file
                if (createOutputFile(temp)) {
                    try (PrintWriter printWriter = new PrintWriter(temp)) {
                        process(configuration, templateFile, printWriter,
                            rpcSchema.toMap());
                    }
                    logProcessedTemplate(outputFileName, templateFile);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to process rpc templates");
            throw e;
        }
    }

    void processAllTypes(File outputFile, TypeSchema typeSchema, String templatePath, Configuration configuration)
        throws IOException, TemplateException {
        try{
            createDir(outputFile);
            List<String> typesTemplates = Arrays.stream(Paths.get(templatePath).toFile().listFiles())
                .filter(file-> file.getName().endsWith("type_converter.ftl") || file.getName().endsWith("types.ftl"))//get all the matching files
                .map(File::getPath).collect(Collectors.toUnmodifiableList());

            for (String templateFile: typesTemplates){
                String outputFileName;
                logger.debug("Processing: {}", templateFile);
                if (Utils.isJavaTemplate(templateFile)){
                    if (templateFile.contains("types.ftl")){// the pojo
                        outputFileName="RPCTypes.java";
                    }else{
                        outputFileName="RPCTypesConverter.java";// codec
                    }
                }
                else {
                    logger.warn("Found unexpected file: {}", templateFile);
                    continue;
                }
                File temp = new File(
                    Utils.appendToPath(outputFile.getAbsolutePath(), outputFileName));
                if (createOutputFile(temp)) {// process the template
                    try (Writer printWriter = new PrintWriter(temp)) {
                        process(configuration, templateFile, printWriter,
                            typeSchema.toMap());
                    }
                    logProcessedTemplate(outputFileName, templateFile);
                }
            }
        }catch (Exception e){
            logger.warn("Failed to process type templates");
            throw e;
        }
    }

    /**
     * Creates an instance of a freemarker template processor
     * @param configuration the freemarker configuration
     * @param ftlFile the freemarker template
     * @param writer the writer to be used to output this file
     * @param map the map to be processed
     */
    void process(Configuration configuration, String ftlFile, Writer writer, Map map)
        throws IOException, TemplateException {
        logger.debug("Processing template with object map: {}", map);
        Template template = configuration.getTemplate(ftlFile);
        template.process(map, writer);
    }

    /**
     * Sets the log level based on whether verbose logging is enabled
     */
    private void setLogLevel() {
        if (verbose) {
            Utils.debug(logger);
        } else {
            Utils.info(logger);
        }
    }

    private boolean createOutputFile(File rpcFile) throws IOException {
        logger.debug("Creating file: {}", rpcFile);
        return rpcFile.exists() || rpcFile.createNewFile();
    }

    private boolean createDir(File dir){
        logger.debug("Creating directory: {}", dir);
        return dir.exists() || dir.mkdirs();
    }

    void checkTemplates() {
        if (templates == null) {
            templates = new String[]{"definitions/templates/errors", "definitions/templates/rpc", "definitions/templates/types"};// default template paths
        }
        boolean validTemplates = true;
        for (String template :
            templates) {// checks that all the template paths are valid. If any have an issue fail
            File file = new File(template);
            if (!file.isDirectory()) {
                validTemplates = false;
                continue;
            }
            //noinspection ConstantConditions
            long fileCount = Arrays.stream(Paths.get(template).toFile().list()).filter(path -> path
                .endsWith(".ftl")).count();// checks that the template path contains templates that can be read
            if (fileCount == 0) {
                logger.warn("Could not find any template files in the path.");
                validTemplates = false;
            }
        }

        if (!validTemplates) {//throw if the templates are not valid
            throw new IllegalStateException("The supplied template folders are invalid");
        }
    }

    void checkSpec() { // chect that the spec files exist
        File file = new File(spec);
        if (!file.isDirectory()) {
            logger.warn("The spec directory is not a folder.");
            throw new IllegalStateException("The spec directory is not a folder");
        } else if (file.list().length ==0) {
            logger.warn("The spec directory is empty.");
            throw new IllegalStateException("The spec directory is empty");
        }
        //noinspection ConstantConditions
        List<String> files = List.of("errors.xml", "types.xml");
        Pattern regex = Pattern.compile("rpc");
        //Check that the required files exist
        boolean containsXMLSpecFiles = Arrays.stream(file.list()).filter(path -> path
            .endsWith(".xml")).allMatch(f-> files.contains(f) || regex.matcher(f).find());
        if (!containsXMLSpecFiles) {
            logger.warn("Could not find any template files in the path.");
            throw new IllegalStateException("The spec directory is not a folder");
        }
    }
}
