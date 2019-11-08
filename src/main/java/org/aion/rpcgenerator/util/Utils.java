package org.aion.rpcgenerator.util;

import ch.qos.logback.classic.Level;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.Mappable;
import org.slf4j.Logger;

public class Utils {

    public static void debug(Logger logger){
        ((ch.qos.logback.classic.Logger)logger).setLevel(Level.DEBUG);
    }

    public static void info(Logger logger){
        ((ch.qos.logback.classic.Logger)logger).setLevel(Level.INFO);
    }

    public static String appendToPath(String path, String fileName) {
        return path + File.separator + fileName;
    }

    private static final Pattern JAVA_TEMPLATE_FILE_PATTERN = Pattern.compile("java_([a-zA-Z]_?)+\\.ftl$");
    public static boolean isJavaTemplate(String path){
        return JAVA_TEMPLATE_FILE_PATTERN.matcher(path).find();
    }

    private static final Pattern RUST_TEMPLATE_FILE_PATTERN = Pattern.compile("rust_([a-zA-Z]_?)+\\.ftl$");

    public static boolean isRustTemplate(String path){
        return RUST_TEMPLATE_FILE_PATTERN.matcher(path).find();
    }

    public static List<Map<String, Object>> toListOfMaps(List<? extends Mappable> mappables){
        return mappables.stream().map(Mappable::toMap).collect(Collectors.toUnmodifiableList());
    }
}
