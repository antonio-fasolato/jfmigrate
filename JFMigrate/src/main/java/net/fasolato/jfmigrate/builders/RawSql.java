package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to add a generic SQL snippet/script to a migration
 */
public class RawSql implements Change {
    private String rawSql;
    private boolean script;

    /**
     * Constructor
     * @param rawSql The SQL code to execute.
     * @param script If the code shoud be executed as a script
     */
    public RawSql(String rawSql, boolean script) {
        this.rawSql = rawSql;
        this.script = script;
    }

    /**
     * Internal method used in generating the SQL code to be executed
     * @param helper The database dialect helper class
     * @return The list of queries and optional data to execute
     */
    public List<Pair<String, Object[]>> getSqlCommand(IDialectHelper helper) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<Pair<String, Object[]>>();
        toReturn.add(new ImmutablePair<>(rawSql, null));
        return toReturn;
    }


    public String getRawSql() {
        return rawSql;
    }

    public void setRawSql(String rawSql) {
        this.rawSql = rawSql;
    }

    public boolean isScript() {
        return script;
    }

    public void setScript(boolean script) {
        this.script = script;
    }
}
