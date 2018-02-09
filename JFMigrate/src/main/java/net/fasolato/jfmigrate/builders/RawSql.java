package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;
import net.fasolato.jfmigrate.internal.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 14/04/2017.
 */
public class RawSql implements Change {
    private String rawSql;
    private boolean script;

    public RawSql(String rawSql, boolean script) {
        this.rawSql = rawSql;
        this.script = script;
    }

    public List<Pair<String, Object[]>> getSqlCommand(IDialectHelper helper) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<Pair<String, Object[]>>();
        toReturn.add(new Pair<String, Object[]>(rawSql, null));
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
