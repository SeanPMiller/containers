package org.seanpatrickmiller.containers.fingertree.impl;

import org.seanpatrickmiller.containers.util.Func;

public final class Empty<V, A> extends FingerTree<V, A>
{
    public Empty(final Measured<V, A> m)
    {
        super(m);
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public FingerTree<V, A> pushFront(final A x)
    {
        return new Single<V, A>(m, x);
    }

    @Override
    public FingerTree<V, A> pushBack(final A x)
    {
        return new Single<V, A>(m, x);
    }

    @Override
    public View<V, A> viewLeft()
    {
        return null;
    }

    @Override
    public View<V, A> viewRight()
    {
        return null;
    }

    @Override
    public A head()
    {
        throw new java.lang.UnsupportedOperationException(
            "Empty.head(): cannot call");
    }

    @Override
    public FingerTree<V, A> tail()
    {
        throw new java.lang.UnsupportedOperationException(
            "Empty.tail(): cannot call");
    }

    @Override
    public A rhead()
    {
        throw new java.lang.UnsupportedOperationException(
            "Empty.rhead(): cannot call");
    }

    @Override
    public FingerTree<V, A> rtail()
    {
        throw new java.lang.UnsupportedOperationException(
            "Empty.rtail(): cannot call");
    }

    @Override
    public FingerTree<V, A> reverse(final Func<A, A> f)
    {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <B> FingerTree<V, B> map(final Func<A, B> f, final Measured<V, B> m)
    {
        return (Empty<V, B>)this;
    }

    @Override
    public FingerTree<V, A> map(final Func<A, A> f)
    {
        return this;
    }

    @Override
    public <B> B foldRight(final Func<A, Func<B, B>> f, final B zero)
    {
        return zero;
    }

    @Override
    public <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero)
    {
        return zero;
    }

    @Override
    public FingerTree<V, A> append(final FingerTree<V, A> that)
    {
        return that;
    }

    @Override
    public V measure()
    {
        return m.zero();
    }

    @Override
    Split<FingerTree<V, A>, A> splitHelper(
        final Func<V, Boolean> pred,
        final V i)
    {
        throw new java.lang.UnsupportedOperationException(
            "Empty.splitHelper(): cannot call");
    }

    @Override
    public java.lang.String toString()
    {
        return "Empty";
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj)
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }

        return true;
    }
}
