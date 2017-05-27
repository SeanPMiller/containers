package org.seanpatrickmiller.containers.util;

import com.google.common.base.Objects;

/**
 * Representation of operation used to sum measurements during annotation.
 * A monoid is a 3-tuple containing the set over which the monoid is
 * defined, some binary, associative operation, and its identity element. A
 * simple example using this definition would be the set of integers, the
 * addition operation, and the additive identity, which we could write as
 * (Z, +, 0).
 * 
 * @param <A> The type over which this monoid is defined.
 */
public final class Monoid<A>
{
    // This monoid's operator.
    private final Func<A, Func<A, A>> op;

    // This monoid's identity.
    private final A zero;

    /**
     * Constructs a Monoid instance.
     * @param op A Curried, binary function that satisfies associativity.
     * @param zero The identity of the operation.
     */
    public Monoid(final Func<A, Func<A, A>> op, final A zero)
    {
        this.op = op;
        this.zero = zero;
    }

    /**
     * Evaluate fully.
     * @return Fully-evaluated result.
     */
    public A sum(final A a, final A b)
    {
        return op.call(a).call(b);
    }

    /**
     * Evaluate partially.
     * @return Unary function with first parameter bound to given argument.
     */
    public Func<A, A> sum(final A a)
    {
        return op.call(a);
    }

    /**
     * Get unevaluated function.
     * @return The unevaluated function.
     */
    public Func<A, Func<A, A>> sum()
    {
        return op;
    }

    /**
     * This monoid's identity.
     * @return This monoid's identity.
     */
    public A zero()
    {
        return this.zero;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof Monoid))
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        if (this == obj)
        {
            return true;
        }
        
        final Monoid<A> other = (Monoid<A>) obj;
        return Objects.equal(zero, other.zero) &&
            op == other.op;
    }
}
