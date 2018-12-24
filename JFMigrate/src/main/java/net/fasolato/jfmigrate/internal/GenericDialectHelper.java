package net.fasolato.jfmigrate.internal;

public abstract class GenericDialectHelper implements IDialectHelper {
    protected String getQueryValueFromObject(Object o) {
        if(o == null) {
            return null;
        }

        if(o instanceof Integer || o instanceof Double || o instanceof Float) {
            return String.format("%s", o);
        } else if(o instanceof String) {
            return String.format("'%s'", o);
        }

        return String.format("'%s'", o);
    }
}
