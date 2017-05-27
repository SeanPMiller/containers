package org.seanpatrickmiller.containers.fingertree;

/**
 * Wrapper for the results of the split operation.
 */
public final class Split<T, A>
{
    public final T left;
    public final A value;
    public final T right;

    Split(final T left, final A value, final T right)
    {
        this.left = left;
        this.value = value;
        this.right = right;
    }
}
