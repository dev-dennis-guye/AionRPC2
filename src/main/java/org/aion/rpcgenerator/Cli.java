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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
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

    Cli() {
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
                File errors = Paths.get(spec + "/errors.xml").toFile();
                logger.debug("Reading error template files.");
                List<ErrorSchema> errorSchemas = ErrorSchema
                    .fromDocument(XMLUtils.fromFile(errors));
                logger.debug("Reading rpc template files.");
                //noinspection ConstantConditions
                List<File> rpcSpecFiles = Arrays.stream(specPath.toFile().listFiles())
                    .filter(s -> s.getName().endsWith("-rpc.xml"))
                    .peek(file -> logger.debug("Found spec file: {}", file.getName()))
                    .collect(Collectors.toUnmodifiableList());

                File errorsOutputFile = new File(Utils.appendToPath(output, "errors"));
                File rpcOutputFile = new File(Utils.appendToPath(output, "rpc"));

                for (String template : templates) {
                    if (template.endsWith("errors")) {
                        processAllErrors(errorsOutputFile, errorSchemas, template, configuration);
                    } else if (template.endsWith("rpc")) {
                        for (var rpcSpec : rpcSpecFiles) {
                            processAllTemplates(rpcOutputFile,
                                new RPCSchema(XMLUtils.fromFile(rpcSpec), errorSchemas), template,
                                configuration);
                        }
                    } else if (template.endsWith("types")) {
                        processAllTypes();
                    } else {
                        logger.warn("Encountered an unidentified template {}", template);
                    }
                }
            } else {
                logger.warn("Could not open the directory. Check if it exists");
            }
        } catch (RuntimeException e) {
            logger.warn("Failed unexpectedly, check the application arguments.");
            logException(e);
            CommandLine.usage(this, System.out);
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


    private void processAllErrors(File outputFile, List<ErrorSchema> errorSchemas,
        String templatePath, Configuration configuration)
        throws IOException, TemplateException {
        try {
            //noinspection ConstantConditions
            List<String> errorTemplates = Arrays
                .stream(Paths.get(templatePath).toFile().listFiles()).filter(
                    p -> p.getName().endsWith("exceptions.ftl") || p.getName()
                        .endsWith("errors.ftl"))
                .map(File::getAbsolutePath).collect(Collectors.toUnmodifiableList());
            Map<String, Object> errors = Map.ofEntries(Map.entry("errors",
                errorSchemas.stream().map(ErrorSchema::toMap)
                    .collect(Collectors.toUnmodifiableList())));

            for (String templateFile : errorTemplates) {
                String outputFileName;
                if (templateFile.startsWith("java")) {
                    outputFileName = "RPCExceptions.java";
                } else {
                    outputFileName = "";
                }
                File temp = new File(
                    Utils.appendToPath(outputFile.getAbsolutePath(), outputFileName));
                if (createOutputFile(temp)) {
                    process(configuration, templateFile, new PrintWriter(temp), errors);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to process error templates");
            throw e;
        }
    }


    private void processAllTemplates(File outputFile, RPCSchema rpcSchema, String templatePath,
        Configuration configuration)
        throws IOException, TemplateException {
        try {
            //noinspection ConstantConditions
            List<String> errorTemplates = Arrays
                .stream(Paths.get(templatePath).toFile().listFiles()).filter(
                    p -> p.toString().endsWith("exceptions.ftl") || p.toString()
                        .endsWith("errors.ftl"))
                .map(File::getAbsolutePath).collect(Collectors.toUnmodifiableList());
            for (String templateFile : errorTemplates) {
                String outputFileName;
                if (templateFile.startsWith("java")) {
                    outputFileName =
                        rpcSchema.getRpc().substring(0, 1).toUpperCase() + rpcSchema.getRpc()
                            .substring(1) + ".java";
                } else {
                    outputFileName = "";
                }
                File temp = new File(
                    Utils.appendToPath(outputFile.getAbsolutePath(), outputFileName));
                if (createOutputFile(temp)) {
                    try (PrintWriter printWriter = new PrintWriter(temp)) {
                        process(configuration, templateFile, printWriter,
                            rpcSchema.toMap());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to process error templates");
            throw e;
        }
    }

    private void processAllTypes() {
        logger.warn("Not supported");
    }

    void process(Configuration configuration, String ftlFile, Writer writer, Map map)
        throws IOException, TemplateException {
        Template template = configuration.getTemplate(ftlFile);
        template.process(map, writer);
    }

    private void setLogLevel() {
        if (verbose) {
            Utils.debug(logger);
        } else {
            Utils.info(logger);
        }
    }

    private boolean createOutputFile(File rpcFile) throws IOException {
        return rpcFile.exists() || ((rpcFile.exists() || rpcFile.getParentFile().mkdirs())
            && rpcFile.createNewFile());
    }

    void checkTemplates() {
        if (templates == null) {
            templates = new String[]{"definitions/templates/errors", "definitions/templates/rpc"};
        }
        boolean validTemplates = true;
        for (String template :
            templates) {
            File file = new File(template);
            if (!file.isDirectory()) {
                validTemplates = false;
                continue;
            }
            //noinspection ConstantConditions
            long fileCount = Arrays.stream(Paths.get(template).toFile().list()).filter(path -> path
                .endsWith(".ftl")).count();
            if (fileCount == 0) {
                logger.warn("Could not find any template files in the path.");
                validTemplates = false;
            }
        }

        if (!validTemplates) {
            throw new IllegalStateException("The supplied template folders are invalid");
        }
    }

    void checkSpec() {
        File file = new File(spec);
        if (!file.isDirectory()) {
            logger.warn("The spec directory is not a folder.");
            throw new IllegalStateException("The spec directory is not a folder");
        }
        //noinspection ConstantConditions
        long fileCount = Arrays.stream(file.list()).filter(path -> path
            .endsWith(".xml")).count();
        if (fileCount == 0) {
            logger.warn("Could not find any template files in the path.");
            throw new IllegalStateException("The spec directory is not a folder");
        }
    }
}
