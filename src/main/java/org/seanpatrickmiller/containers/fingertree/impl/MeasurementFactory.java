package org.seanpatrickmiller.containers.fingertree.impl;

import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Monoids;

public final class MeasurementFactory
{
    public Measured<java.lang.Integer, java.lang.Integer> makeSequentialMeasurement()
    {
        // Constant function (always returns same value).
        final Func<java.lang.Integer, java.lang.Integer> one =
            new Func<java.lang.Integer, java.lang.Integer>() {
                public java.lang.Integer call(final java.lang.Integer i) {
                    return 1;
                }
            };

        // Measurement algorithm.
        return new Measured<>(Monoids.SUM, one);
    }
}
