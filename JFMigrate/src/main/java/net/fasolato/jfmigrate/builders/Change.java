package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;
import net.fasolato.jfmigrate.internal.Pair;

import java.util.List;

/**
 * Created by fasolato on 14/04/2017.
 */
public interface Change {
    List<Pair<String, Object[]>> getSqlCommand(IDialectHelper helper);
}
