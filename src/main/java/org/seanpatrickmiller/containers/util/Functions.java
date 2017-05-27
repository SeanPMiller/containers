package org.seanpatrickmiller.containers.util;

public class Functions
{
    /**
     * Addition.
     */
    public static final Func<java.lang.Integer, Func<java.lang.Integer, java.lang.Integer>> ADD =
        new Func<java.lang.Integer, Func<java.lang.Integer, java.lang.Integer>>() {
            @Override
            public Func<java.lang.Integer, java.lang.Integer> call(final java.lang.Integer a) {
                return new Func<java.lang.Integer, java.lang.Integer>() {
                    @Override
                    public java.lang.Integer call(final java.lang.Integer b) {
                        return a + b;
                    }
                };
            }
        };

    public static final Func<java.lang.Integer, Func<java.lang.Integer, java.lang.Integer>> SUB =
        new Func<java.lang.Integer, Func<java.lang.Integer, java.lang.Integer>>() {
            @Override
            public Func<java.lang.Integer, java.lang.Integer> call(final java.lang.Integer a) {
                return new Func<java.lang.Integer, java.lang.Integer>() {
                    @Override
                    public java.lang.Integer call(final java.lang.Integer b) {
                        return a - b;
                    }
                };
            }
        };
}
