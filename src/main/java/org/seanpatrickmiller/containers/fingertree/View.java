package org.seanpatrickmiller.containers.fingertree;

/**
 * Wrapper for the results of the view operations.
 */
public final class View<V, A>
{
    public final A head;
    public final FingerTree<V, A> tail;

    View(final A head, final FingerTree<V, A> tail)
    {
        this.head = head;
        this.tail = tail;
    }
}
