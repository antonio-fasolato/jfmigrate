package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;

/**
 * Created by fasolato on 14/04/2017.
 */
public interface Change {
    String[] getSqlCommand(IDialectHelper helper);
}
