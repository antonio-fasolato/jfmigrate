package net.fasolato.jfmigrate.builders;

import java.sql.JDBCType;

/**
 * Created by fasolato on 20/03/2017.
 */
public class Column {
    private String name;
    private JDBCType type;
    private Integer precision;
    private Integer scale;
    private boolean nullable;
    private boolean primaryKey;
    private boolean unique;
    private ForeignKey foreignKey;

    public Column(String name, JDBCType type, Integer precision, Integer scale, boolean nullable, boolean primaryKey, boolean unique, ForeignKey foreignKey) {
        this.name = name;
        this.type = type;
        this.precision = precision;
        this.scale = scale;
        this.nullable = nullable;
        this.primaryKey = primaryKey;
        this.unique = unique;
        this.foreignKey = foreignKey;

    }

    public String getName() {
        return name;
    }

    public JDBCType getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public Integer getPrecision() {
        return precision;
    }

    public Integer getScale() {
        return scale;
    }

    public boolean isUnique() {
        return unique;
    }

    public ForeignKey getForeignKey() {
        return foreignKey;
    }
}
