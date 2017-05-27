package org.seanpatrickmiller.containers.util;

/**
 * Describes a unary function mapping A to B.
 * @param <A> Domain type.
 * @param <B> Range type.
 */
public interface Func<A, B>
{
    /**
     * The implementation of this function.
     * @param a A value from the domain.
     * @return A value from the range.
     */
    B call(A a);

    /**
     * Swap the parameters of a Curried binary function.
     * @param f A Curried binary function.
     * @return The given function with its parameters reversed.
     */
    public static <A, B, C> Func<B, Func<A, C>> flip(
        final Func<A, Func<B, C>> f)
    {
        return new Func<B, Func<A, C>>() {
            @Override
            public Func<A, C> call(final B b)
            {
                return new Func<A, C>() {
                    @Override
                    public C call(final A a)
                    {
                        return f.call(a).call(b);
                    }
                };
            }
        };
    }
}
