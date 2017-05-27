package org.seanpatrickmiller.containers.util;

public class Monoids
{
    /**
     * Summation.
     */
    public static final Monoid<java.lang.Integer> SUM =
        new Monoid<>(Functions.ADD, 0);
}
