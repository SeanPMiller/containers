package org.seanpatrickmiller.containers.fingertree;

import java.util.Iterator;

class SequenceIterator<A> implements Iterator<A>
{
    private FingerTree<java.lang.Integer, A> tree;

    SequenceIterator(final FingerTree<java.lang.Integer, A> tree)
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
        final View<java.lang.Integer, A> lview = this.tree.viewLeft();
        this.tree = lview.tail;
        return lview.head;
    }
}
