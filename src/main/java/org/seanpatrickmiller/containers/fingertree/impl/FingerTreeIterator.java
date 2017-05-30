package org.seanpatrickmiller.containers.fingertree.impl;

import java.util.Iterator;

class FingerTreeIterator<V, A> implements Iterator<A>
{
    private FingerTree<V, A> tree;

    FingerTreeIterator(final FingerTree<V, A> tree)
    {
        this.tree = tree;
    }

    @Override
    public boolean hasNext()
    {
        return !this.tree.isEmpty();
    }

    @Override
    public A next()
    {
        final View<V, A> lview = this.tree.viewLeft();
        this.tree = lview.tail;
        return lview.head;
    }
}
