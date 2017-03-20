package net.fasolato.jfmigrate.builders;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 20/03/2017.
 */
public class Table {
    public Table(String name) {
        this.name = name;
        columns = new ArrayList<Column>();
    }

    private List<Column> columns;

    public Table addColumn(String name, JDBCType type) {
        columns.add(new Column(name, type));
        return this;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
