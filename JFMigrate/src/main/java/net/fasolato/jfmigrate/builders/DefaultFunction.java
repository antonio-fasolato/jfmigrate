package net.fasolato.jfmigrate.builders;

/**
 * Class to express the default function to be used in a column with the DEFAULT constraint
 */
public class DefaultFunction {
    private String function;

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
