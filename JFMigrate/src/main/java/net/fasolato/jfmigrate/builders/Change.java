package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;
import net.fasolato.jfmigrate.internal.Pair;

import java.util.List;

/**
 * Generic class to represent a DB change
 */
public interface Change {
    List<Pair<String, Object[]>> getSqlCommand(IDialectHelper helper);
}
