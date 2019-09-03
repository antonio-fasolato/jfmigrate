package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Generic class to represent a DB change
 */
public interface Change {
    List<Pair<String, Object[]>> getSqlCommand(IDialectHelper helper);
}
