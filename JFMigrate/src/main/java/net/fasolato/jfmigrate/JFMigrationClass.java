package net.fasolato.jfmigrate;

import java.lang.annotation.Annotation;

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
}
