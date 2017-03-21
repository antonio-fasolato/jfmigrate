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

    public Table addColumn(String name, JDBCType type, Integer precision, Integer scale, boolean nullable, boolean primaryKey, boolean unique, ForeignKey foreignKey) {
        columns.add(new Column(name, type, precision, scale, nullable, primaryKey, unique, foreignKey));
        return this;
    }

    public Table addColumn(String name, JDBCType type) {
        columns.add(new Column(name, type, null, null, true, false, false, null));
        return this;
    }

    public Table addColumn(String name, JDBCType type, boolean primaryKey) {
        columns.add(new Column(name, type, null, null, true, primaryKey, false, null));
        return this;
    }

    public Table addColumn(String name, JDBCType type, Integer precision, Integer scale) {
        columns.add(new Column(name, type, precision, scale, true, false, false, null));
        return this;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
