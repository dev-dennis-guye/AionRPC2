package org.aion.rpcgenerator.util;

import ch.qos.logback.classic.Level;
import java.io.File;
import org.slf4j.Logger;

public class Utils {

    public static void debug(Logger logger){
        ((ch.qos.logback.classic.Logger)logger).setLevel(Level.DEBUG);
    }

    public static void info(Logger logger){
        ((ch.qos.logback.classic.Logger)logger).setLevel(Level.INFO);
    }

    public static String appendToPath(String output, String types) {
        return output + File.separator + types;
    }
}
