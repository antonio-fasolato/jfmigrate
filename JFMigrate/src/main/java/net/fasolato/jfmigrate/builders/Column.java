package net.fasolato.jfmigrate.builders;

import java.sql.JDBCType;

/**
 * Created by fasolato on 20/03/2017.
 */
public class Column {
    private String name;
    private JDBCType type;

    public Column(String name, JDBCType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JDBCType getType() {
        return type;
    }

    public void setType(JDBCType type) {
        this.type = type;
    }
}
