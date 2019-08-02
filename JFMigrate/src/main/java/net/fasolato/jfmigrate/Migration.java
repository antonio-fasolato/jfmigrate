package net.fasolato.jfmigrate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Migration annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Migration {
    /**
     * Migration number. Must be a positive integer
     * @return The migration number
     */
    long number() default -1;

    /**
     * Optional migration description
     * @return The description
     */
    String description() default "";

    /**
     * Optionally skips the migration if the database dialect is the one specified
     * @return The dialect to skip
     */
    SqlDialect[] excludeDialect() default {};

    /**
     * Optionally execute the migration only if the database dialect is the one specified
     * @return The dialect to consider
     */
    SqlDialect onlyDialect() default SqlDialect.NONE;
}
