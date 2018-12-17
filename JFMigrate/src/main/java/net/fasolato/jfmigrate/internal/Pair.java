package net.fasolato.jfmigrate.internal;

import java.util.Objects;

public class Pair<T, U> {
    private T a;
    private U b;

    public Pair() {
    }

    public Pair(T a, U b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "{\"Pair\":{"
                + "\"a\":" + a
                + ", \"b\":" + b
                + "}}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(a, pair.a) &&
                Objects.equals(b, pair.b);
    }

    @Override
    public int hashCode() {

        return Objects.hash(a, b);
    }

    public T getA() {
        return a;
    }

    public void setA(T a) {
        this.a = a;
    }

    public U getB() {
        return b;
    }

    public void setB(U b) {
        this.b = b;
    }
}
