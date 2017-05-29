package org.seanpatrickmiller.containers.fingertree.impl;

import org.seanpatrickmiller.containers.util.Func;

final class One<V, A> extends Digit<V, A>
{
    final A a;

    One(final Measured<V, A> m, final A a)
    {
        super(m);

        this.a = a;
    }

    @Override
    FingerTree<V, A> prepend(
        final A x,
        final FingerTree<V, Node<V, A>> mid,
        final Digit<V, A> right)
    {
        return new Deep<V, A>(
            m,
            new Two<V, A>(m, x, a),
            mid,
            right);
    }

    @Override
    FingerTree<V, A> append(
        final A x,
        final FingerTree<V, Node<V, A>> mid,
        final Digit<V, A> left)
    {
        return new Deep<V, A>(
            m,
            left,
            mid,
            new Two<V, A>(m, a, x));
    }

    @Override
    Digit<V, A> reverse(final Func<A, A> f)
    {
        return new One<V, A>(m, f.call(a));
    }

    @Override
    <B> Digit<V, B> map(final Func<A, B> f, final Measured<V, B> m)
    {
        return new One<V, B>(m, f.call(a));
    }

    @Override
    Digit<V, A> map(final Func<A, A> f)
    {
        return new One<V, A>(m, f.call(a));
    }

    @Override
    <B> B foldRight(final Func<A, Func<B, B>> f, final B zero)
    {
        return f.call(a).call(zero);
    }

    @Override
    <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero)
    {
        return f.call(zero).call(a);
    }

    @Override
    Split<Digit<V, A>, A> split(final Func<V, Boolean> pred, final V measure)
    {
        return new Split<Digit<V, A>, A>(null, a, null);
    }

    @Override
    FingerTree<V, A> toTree()
    {
        return new Single<V, A>(m, a);
    }

    @Override
    A head()
    {
        return a;
    }

    @Override
    Digit<V, A> tail()
    {
        throw new java.lang.UnsupportedOperationException(
            "Digit.tail(): cannot call on instance of One");
    }

    @Override
    A rhead()
    {
        return a;
    }

    @Override
    Digit<V, A> rtail()
    {
        throw new java.lang.UnsupportedOperationException(
            "Digit.rtail(): cannot call on instance of One");
    }

    @Override
    public java.lang.String toString()
    {
        return "One(" + a + ")";
    }
}
