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
    private static final Logger log = LogManager.getLogger(ScriptParser.class);

    public static List<String> parseScript(String script, String delimiter) {

        List<String> commands = new ArrayList<>();
        StringBuilder command = new StringBuilder();

        String[] lines = script.split("\\R");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("--") || trimmedLine.startsWith("//")) {
                log.debug("Empty line or comment");
                command.append(trimmedLine);
                command.append("\n");
            } else if (trimmedLine.endsWith(delimiter)) {
                command.append(trimmedLine, 0, trimmedLine.lastIndexOf(delimiter) + 1);
                commands.add(command.toString());
                command = new StringBuilder();
            } else {
                command.append(trimmedLine);
                command.append("\n");
            }
        }
        if (command.length() != 0) {
            commands.add(command.toString());
        }

        return commands;
    }
}
