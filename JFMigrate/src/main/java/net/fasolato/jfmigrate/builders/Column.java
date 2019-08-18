package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent all the changes to a column (creation, update...)
 */
public class Column implements Change {
    private String name;
    private String tableName;
    private String newName;
    private OperationType operationType;
    private boolean primaryKey;
    private boolean unique;
    private boolean nullableChanged;
    private boolean nullable;
    private boolean identity;
    private boolean defaultValueSet;
    private Object defaultValue;
    private boolean typeChanged;
    private JDBCType type;
    private Integer precision;
    private Integer scale;
    private boolean autoIncrementChanged;
    private boolean autoIncrement;
    private long autoIncrementStartWith;
    private int autoIncrementStep;

    /**
     * Constructor
     * @param name The column name
     * @param operationType The operation we are performing
     */
    public Column(String name, OperationType operationType) {
        this.name = name;
        this.operationType = operationType;
    }

    /**
     * Returns the dialct specific command to perform the operation on the column
     * @param helper The database dialect helper class
     * @return The list of queries (left side of the Pair) with their optional parameters (null if not needed). The parameters are object and passed as they are to jdbc setObject() method
     */
    public List<Pair<String, Object[]>> getSqlCommand(IDialectHelper helper) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<Pair<String, Object[]>>();
        switch (operationType) {
            case delete:
                for (String s : helper.getColumnDropCommand(this)) {
                    toReturn.add(new ImmutablePair<>(s, null));
                }
                break;
            case rename:
                for (String s : helper.getColumnRenameCommand(this)) {
                    toReturn.add(new ImmutablePair<String, Object[]>(s, null));
                }
                break;
        }

        return toReturn;
    }

    /* Type definitions */

    /**
     * Helper method to set the column type to integer (no parameters)
     * @return this object to continue the fluent inteface
     */
    public Column asInteger() {
        return asInteger(null);
    }

    /**
     * Helper method to set the column type to integer (no parameters)
     * @param precision The integer precision
     * @return this object to continue the fluent inteface
     */
    public Column asInteger(Integer precision) {
        setType(JDBCType.INTEGER);
        setPrecision(precision);
        typeChanged = true;

        return this;
    }

    /**
     * Helper method to set the column type to String (Varchar)
     * @return this object to continue the fluent inteface
     */
    public Column asString() {
        return asString(null);
    }

    /**
     * Helper method to set the column type to String (Varchar)
     * @param precision The column size
     * @return this object to continue the fluent inteface
     */
    public Column asString(Integer precision) {
        setType(JDBCType.VARCHAR);
        setPrecision(precision);
        typeChanged = true;

        return this;
    }

    /**
     * Helper method to set the column type to Decimal (with default values). This method is wildly dependent on the specific database dialect
     * @return this object to continue the fluent inteface
     */
    public Column asDecimal() {
        return asDecimal(null, null);
    }

    /**
     * Helper method to set the column type to Decimal (with default values). This method is wildly dependent on the specific database dialect
     * @param precision The decimal precision
     * @return this object to continue the fluent inteface
     */
    public Column asDecimal(Integer precision) {
        return asDecimal(precision, null);
    }

    /**
     * Helper method to set the column type to Decimal (with default values). This method is wildly dependent on the specific database dialect
     * @param precision The decimal precision
     * @param scale The decimal scale
     * @return this object to continue the fluent inteface
     */
    public Column asDecimal(Integer precision, Integer scale) {
        setType(JDBCType.DECIMAL);
        setPrecision(precision);
        setScale(scale);
        typeChanged = true;

        return this;
    }

    /**
     * Sets the column type to a generic jdbc type
     * @param t The column type
     * @return this object to continue the fluent inteface
     */
    public Column as(JDBCType t) {
        return as(t, null, null);
    }

    /**
     * Sets the column type to a generic jdbc type with a precision
     * @param t The column type
     * @param precision The column precision. This could lead to a wrong SQL code if the type does not support a precision
     * @return this object to continue the fluent inteface
     */
    public Column as(JDBCType t, Integer precision) {
        return as(t, precision, null);
    }

    /**
     * Sets the column type to a generic jdbc type with a precision
     * @param t The column type
     * @param precision The column precision. This could lead to a wrong SQL code if the type does not support a precision
     * @param scale The column scale. This could lead to a wrong SQL code if the type does not support a precision
     * @return this object to continue the fluent inteface
     */
    public Column as(JDBCType t, Integer precision, Integer scale) {
        setType(t);
        setPrecision(precision);
        setScale(scale);
        typeChanged = true;

        return this;
    }

    /**
     * Sets the column as an autoincrement. Pay attention that this feature is not available on alla database dialects.
     * @param startWith Starting value of the sequence (if the dialect supports it)
     * @param step Sequence increment (if the dialect supports it)
     * @return
     */
    public Column autoIncrement(long startWith, int step) {
        autoIncrement = true;
        typeChanged = true;
        autoIncrementChanged = true;
        autoIncrementStartWith = startWith;
        autoIncrementStep = step;
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

    public boolean isNullableChanged() {
        return nullableChanged;
    }

    public void setNullableChanged(boolean nullableChanged) {
        this.nullableChanged = nullableChanged;
    }

    public boolean isTypeChanged() {
        return typeChanged;
    }

    public void setTypeChanged(boolean typeChanged) {
        this.typeChanged = typeChanged;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isAutoIncrementChanged() {
        return autoIncrementChanged;
    }

    public void setAutoIncrementChanged(boolean autoIncrementChanged) {
        this.autoIncrementChanged = autoIncrementChanged;
    }

    public long getAutoIncrementStartWith() {
        return autoIncrementStartWith;
    }

    public void setAutoIncrementStartWith(long autoIncrementStartWith) {
        this.autoIncrementStartWith = autoIncrementStartWith;
    }

    public int getAutoIncrementStep() {
        return autoIncrementStep;
    }

    public void setAutoIncrementStep(int autoIncrementStep) {
        this.autoIncrementStep = autoIncrementStep;
    }
}
