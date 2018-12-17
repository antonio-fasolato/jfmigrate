package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.JFException;
import net.fasolato.jfmigrate.internal.IDialectHelper;
import net.fasolato.jfmigrate.internal.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fasolato on 20/03/2017.
 */
public class Column implements Change {
    private String name;
    private String tableName;
    private String newName;
    private OperationType operationType;
    private boolean primaryKey;
    private boolean unique;
    private boolean nullable;
    private boolean identity;
    private boolean defaultValueSet;
    private Object defaultValue;
    private JDBCType type;
    private Integer precision;
    private Integer scale;

    public Column(String name, OperationType operationType) {
        this.name = name;
        this.operationType = operationType;
    }

    public List<Pair<String, Object[]>> getSqlCommand(IDialectHelper helper) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<Pair<String, Object[]>>();
        switch (operationType) {
            case delete:
                for (String s : helper.getColumnDropCommand(this)) {
                    toReturn.add(new Pair<String, Object[]>(s, null));
                }
                break;
            case rename:
                for (String s : helper.getColumnRenameCommand(this)) {
                    toReturn.add(new Pair<String, Object[]>(s, null));
                }
                break;
        }

        return toReturn;
    }

    /* Type definitions */
    public Column asInteger() {
        return asInteger(null);
    }

    public Column asInteger(Integer precision) {
        setType(JDBCType.INTEGER);
        setPrecision(precision);

        return this;
    }

    public Column asString() {
        return asString(null);
    }

    public Column asString(Integer precision) {
        setType(JDBCType.VARCHAR);
        setPrecision(precision);

        return this;
    }

    public Column asDecimal() {
        return asDecimal(null, null);
    }

    public Column asDecimal(Integer precision) {
        return asDecimal(precision, null);
    }

    public Column asDecimal(Integer precision, Integer scale) {
        setType(JDBCType.DECIMAL);
        setPrecision(precision);
        setScale(scale);

        return this;
    }

    public Column as(JDBCType t) {
        return as(t, null, null);
    }

    public Column as(JDBCType t, Integer precision) {
        return as(t, precision, null);
    }

    public Column as(JDBCType t, Integer precision, Integer scale) {
        setType(t);
        setPrecision(precision);
        setScale(scale);

        return this;
    }
    /* Type definitions */

    public String getName() {
        return name;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isIdentity() {
        return identity;
    }

    public void setIdentity(boolean identity) {
        this.identity = identity;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public JDBCType getType() {
        return type;
    }

    public void setType(JDBCType type) {
        this.type = type;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public boolean isDefaultValueSet() {
        return defaultValueSet;
    }

    public void setDefaultValueSet(boolean defaultValueSet) {
        this.defaultValueSet = defaultValueSet;
    }
}
