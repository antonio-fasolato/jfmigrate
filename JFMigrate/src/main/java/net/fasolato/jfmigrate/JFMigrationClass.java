package net.fasolato.jfmigrate;

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
}
