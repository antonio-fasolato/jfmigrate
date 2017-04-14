package net.fasolato.jfmigrate.builders;

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
