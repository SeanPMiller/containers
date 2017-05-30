package org.seanpatrickmiller.containers.fingertree.impl;

import com.google.common.base.Objects;
import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Lazy;

public final class Single<V, A> extends FingerTree<V, A>
{
    final A val;
    final Lazy<V> v;

    public Single(final Measured<V, A> m, final A val)
    {
        super(m);

        this.val = val;
        v = new Lazy<V>() {
            @Override
            protected V eval()
            {
                return m.measure(val);
            }
        };
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public FingerTree<V, A> pushFront(final A x)
    {
        return new Deep<V, A>(
            m,
            new One<V, A>(m, x),
            new Empty<V, Node<V, A>>(m.nodeMeasured()),
            new One<V, A>(m, val));
    }

    @Override
    public FingerTree<V, A> pushBack(final A x)
    {
        return new Deep<V, A>(
            m,
            new One<V, A>(m, val),
            new Empty<V, Node<V, A>>(m.nodeMeasured()),
            new One<V, A>(m, x));
    }

    @Override
    public View<V, A> viewLeft()
    {
        return new View<V, A>(val, new Empty<V, A>(m));
    }

    @Override
    public View<V, A> viewRight()
    {
        return new View<V, A>(val, new Empty<V, A>(m));
    }

    @Override
    public A head()
    {
        return val;
    }

    @Override
    public FingerTree<V, A> tail()
    {
        return new Empty<V, A>(m);
    }

    @Override
    public A rhead()
    {
        return val;
    }

    @Override
    public FingerTree<V, A> rtail()
    {
        return new Empty<V, A>(m);
    }

    @Override
    public FingerTree<V, A> reverse(final Func<A, A> f)
    {
        return new Single<V, A>(m, f.call(val));
    }

    @Override
    public <B> FingerTree<V, B> map(final Func<A, B> f, final Measured<V, B> m)
    {
        return new Single<V, B>(m, f.call(val));
    }

    @Override
    public FingerTree<V, A> map(final Func<A, A> f)
    {
        return new Single<V, A>(m, f.call(val));
    }

    @Override
    public <B> B foldRight(final Func<A, Func<B, B>> f, final B zero)
    {
        return f.call(val).call(zero);
    }

    @Override
    public <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero)
    {
        return f.call(zero).call(val);
    }

    @Override
    public FingerTree<V, A> append(final FingerTree<V, A> that)
    {
        return that.pushFront(val);
    }

    @Override
    public V measure()
    {
        return v.getValue();
    }

    @Override
    Split<FingerTree<V, A>, A> splitHelper(final Func<V, Boolean> pred, final V i)
    {
        return new Split<FingerTree<V, A>, A>(
            new Empty<V, A>(m),
            val,
            new Empty<V, A>(m));
    }

    @Override
    public java.lang.String toString()
    {
        return "Single(" + val + ")";
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

        final Single<V, A> other = (Single<V, A>) obj;
        return Objects.equal(val, other.val) &&
            Objects.equal(m, other.m);
    }
}
