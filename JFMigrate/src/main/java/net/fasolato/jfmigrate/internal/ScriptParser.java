package net.fasolato.jfmigrate.internal;

/*
 * The idea from this script is taken from here: https://github.com/BenoitDuffez/ScriptRunner
 * I modified the original code to get a list of string to be run instead of executing them directly, but the idea is the same
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ScriptParser {
    private static Logger log = LogManager.getLogger(ScriptParser.class);

    public static List<String> parseScript(String script, String delimiter) {
        String delim = delimiter;

        List<String> commands = new ArrayList<>();
        StringBuffer command = new StringBuffer();

        String[] lines = script.split("\\R");
        for(String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.length() < 1) {
                log.debug("Empty line");
            } else if (trimmedLine.startsWith("--") || trimmedLine.startsWith("//")) {
                log.debug("Stripped comment:{}{}", System.lineSeparator(), trimmedLine);
            } else if (trimmedLine.endsWith(delim)) {
                command.append(trimmedLine.substring(0, trimmedLine.lastIndexOf(delim)));
                commands.add(command.toString());
                command = new StringBuffer();
            } else {
                command.append(trimmedLine);
                command.append("\n");
            }
        }
        if(command.length() != 0) {
            commands.add(command.toString());
        }

        return commands;
    }
}
