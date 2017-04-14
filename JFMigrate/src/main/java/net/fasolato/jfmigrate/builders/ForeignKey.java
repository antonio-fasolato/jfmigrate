package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 21/03/2017.
 */
public class ForeignKey implements Change {
    private String fromTable;
    private List<String> foreignColumns;
    private String toTable;
    private List<String> primaryKeys;

    public ForeignKey() {
        foreignColumns = new ArrayList<String>();
        primaryKeys = new ArrayList<String>();
    }

    public String[] getSqlCommand(IDialectHelper helper) {
        throw new NotImplementedException();
    }

    public String getFromTable() {
        return fromTable;
    }

    public void setFromTable(String fromTable) {
        this.fromTable = fromTable;
    }

    public List<String> getForeignColumns() {
        return foreignColumns;
    }

    public void setForeignColumns(List<String> foreignColumns) {
        this.foreignColumns = foreignColumns;
    }

    public String getToTable() {
        return toTable;
    }

    public void setToTable(String toTable) {
        this.toTable = toTable;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }
}
