package org.seanpatrickmiller.containers.fingertree;

import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Monoid;

/**
 * Represents the measurement algorithm used to annotate nodes.
 * A simple use case might be the monoid (Z, +, 0) combined with the measurement
 * function f(x) = 1. This would annotate each node of a finger tree with the
 * total number of nodes beneath it, thereby yielding an indexed sequence with
 * a constant-time size operation.
 */
public final class Measured<V, A>
{
    private final Monoid<V> monoid;
    private final Func<A, V> measure;

    /**
     * Constructs a Measured instance.
     * @param monoid The monoid to use when summing measurements for annotation.
     * @param measure A function to process elements and yield measurements.
     */
    public Measured(final Monoid<V> monoid, final Func<A, V> measure)
    {
        this.monoid = monoid;
        this.measure = measure;
    }

    V measure(final A a)
    {
        return measure.call(a);
    }

    V sum(final V a, final V b)
    {
        return monoid.sum(a, b);
    }

    V zero()
    {
        return monoid.zero();
    }

    Measured<V, Node<V, A>> nodeMeasured() {
        return new Measured<V, Node<V, A>>(monoid, new Func<Node<V, A>, V>() {
            @Override
            public V call(final Node<V, A> node)
            {
                return node.measure();
            }
        });
    }

    Measured<V, Digit<V, A>> digitMeasured() {
        return new Measured<V, Digit<V, A>>(monoid, new Func<Digit<V, A>, V>() {
            @Override
            public V call(final Digit<V, A> d)
            {
                return d.measure();
            }
        });
    }
}
