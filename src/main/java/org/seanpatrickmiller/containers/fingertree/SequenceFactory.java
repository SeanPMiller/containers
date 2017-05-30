package org.seanpatrickmiller.containers.fingertree;

import org.seanpatrickmiller.containers.fingertree.impl.Empty;
import org.seanpatrickmiller.containers.fingertree.impl.FingerTree;
import org.seanpatrickmiller.containers.fingertree.impl.Measured;
import org.seanpatrickmiller.containers.fingertree.impl.Single;
import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Identity;
import org.seanpatrickmiller.containers.util.Monoids;

/**
 * Factory for creating indexed sequences backed by
 * {@link org.seanpatrickmiller.containers.fingertree.impl.FingerTree}.
 */
public final class SequenceFactory<A>
{
    // Constant-valued function that always returns one.
    private final Func<A, java.lang.Integer> returnOne;

    // Sequence measurement algorithm.
    private final Measured<java.lang.Integer, A> measured;

    // The identity function.
    private final Identity<A> identity;

    // The empty sequence.
    private final Sequence<A> nil;

    /**
     * Create a new factory instance.
     */
    public SequenceFactory()
    {
        this.returnOne = new Func<A, java.lang.Integer>() {
            @Override
            public java.lang.Integer call(final A ignored) {
                return 1;
            }
        };

        this.measured = new Measured<>(Monoids.SUM, this.returnOne);

        this.identity = new Identity<>();

        this.nil = new Sequence<>(new Empty<>(this.measured), this.identity);
    }

    /**
     * Create an empty sequence.
     * @return A new sequence with zero elements.
     */
    public Sequence makeEmpty()
    {
        return this.nil;
    }

    /**
     * Create a single-element sequence.
     * @param a Some element.
     * @return A new sequence containing the given element.
     */
    public Sequence makeSingleton(final A a)
    {
        return new Sequence(new Single<>(this.measured, a), this.identity);
    }

    /**
     * Create a single-element sequence.
     * @param first Some element.
     * @param rest More elements.
     * @return A new sequence containing the given elements.
     */
    public Sequence make(final A first, final A... rest)
    {
        FingerTree<java.lang.Integer, A> temp = new Single<>(
            this.measured, first);

        for (final A value : rest)
        {
            temp = temp.pushBack(value);
        }

        return new Sequence(temp, this.identity);
    }
}
