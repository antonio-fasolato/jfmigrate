package net.fasolato.jfmigrate;

/**
 * Created by fasolato on 16/03/2017.
 */
public interface IMigration {
    void up();

    void down();
}
