package net.fasolato.jfmigrate.builders;

/**
 * Class to express the default function to be used in a column with the DEFAULT constraint
 */
public class DefaultFunction {
    private String function;

    /**
     * Constructor
     * @param function The SQL function to use in a CREATE/ALTER table/column operation. The syntax is database/specific
     */
    public DefaultFunction(String function) {
        this.function = function;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
