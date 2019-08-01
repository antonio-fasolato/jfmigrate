package net.fasolato.jfmigrate;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for all migrations. The extending classes must implement an up and down method to generate the UP or DOWN migration actions.
 */
public abstract class JFMigrationClass {
    /**
     * Object used to compose a migration
     */
    protected JFMigrationFluent migration;

    public JFMigrationClass() {
        migration = new JFMigrationFluent();
    }

    /**
     * Method to generate the UP actions
     */
    public abstract void up();

    /**
     * Method to generate the DOWN actions
     */
    public abstract void down();

    /**
     * Utility method that returns the migration number defined in the annotation
     *
     * @return The migration number
     */
    public long getMigrationNumber() {
        Annotation[] annotations = this.getClass().getAnnotations();
        for (Annotation a : annotations) {
            if (Migration.class.isAssignableFrom(a.annotationType())) {
                return ((Migration) a).number();
            }
        }

        return -1;
    }

    /**
     * Utility method that returns the migration name defined in the annotation
     *
     * @return The migration name
     */
    public String getMigrationName() {
        Annotation[] annotations = this.getClass().getAnnotations();
        for (Annotation a : annotations) {
            if (Migration.class.isAssignableFrom(a.annotationType())) {
                Migration m = (Migration) a;
                if (!m.description().equals("")) {
                    return m.description();
                }
            }
        }

        return this.getClass().getSimpleName();
    }

    /**
     * Checks if the current dialect is compatible with what is specfied in the annotation
     *
     * @param dialect The current configured dialect
     * @return Whether to execute the migration
     */
    public boolean executeForDialect(SqlDialect dialect) {
        List<SqlDialect> exclude = new ArrayList<>();
        SqlDialect only = SqlDialect.NONE;

        Annotation[] annotations = this.getClass().getAnnotations();
        for (Annotation a : annotations) {
            if (Migration.class.isAssignableFrom(a.annotationType())) {
                Migration m = (Migration) a;
                exclude = Arrays.asList(m.excludeDialect());
                only = m.onlyDialect();
            }
        }

        if (exclude.size() != 0 && only != SqlDialect.NONE) {
            throw new JFException("excludeDialect and onlyDialect @Migration parameters cannot be set at the same time");
        }

        if (exclude.contains(dialect)) {
            return false;
        }
        if (only != SqlDialect.NONE && only != dialect) {
            return false;
        }

        return true;
    }
}
