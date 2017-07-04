package net.fasolato.jfmigrate;

import java.lang.annotation.Annotation;

/**
 * Created by fasolato on 04/04/2017.
 */
public abstract class JFMigrationClass {
    protected JFMigrationFluent migration;

    public JFMigrationClass() {
        migration = new JFMigrationFluent();
    }

    public abstract void up();

    public abstract void down();

    public long getMigrationNumber() {
        Annotation[] annotations = this.getClass().getAnnotations();
        for (Annotation a : annotations) {
            if (Migration.class.isAssignableFrom(a.annotationType())) {
                return ((Migration) a).number();
            }
        }

        return -1;
    }

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
