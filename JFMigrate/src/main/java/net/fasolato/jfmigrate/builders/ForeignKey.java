package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;
import net.fasolato.jfmigrate.internal.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 21/03/2017.
 */
public class ForeignKey implements Change {
    private String name;
    private String fromTable;
    private List<String> foreignColumns;
    private String toTable;
    private List<String> primaryKeys;
    private boolean onDeleteCascade;
    private boolean onUpdateCascade;

    public ForeignKey(String name) {
        this.name = name;
        foreignColumns = new ArrayList<String>();
        primaryKeys = new ArrayList<String>();
    }

    public List<Pair<String, Object[]>> getSqlCommand(IDialectHelper helper) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnDeleteCascade() {
        return onDeleteCascade;
    }

    public void setOnDeleteCascade(boolean onDeleteCascade) {
        this.onDeleteCascade = onDeleteCascade;
    }

    public boolean isOnUpdateCascade() {
        return onUpdateCascade;
    }

    public void setOnUpdateCascade(boolean onUpdateCascade) {
        this.onUpdateCascade = onUpdateCascade;
    }
}
