package org.seanpatrickmiller.containers.fingertree.impl;

import org.seanpatrickmiller.containers.util.Func;

final class Two<V, A> extends Digit<V, A>
{
    final A a;
    final A b;

    Two(final Measured<V, A> m, final A a, final A b)
    {
        super(m);

        this.a = a;
        this.b = b;
    }

    @Override
    FingerTree<V, A> prepend(
        final A x,
        final FingerTree<V, Node<V, A>> mid,
        final Digit<V, A> right)
    {
        return new Deep<V, A>(
            m,
            new Three<V, A>(m, x, a, b),
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
            new Three<V, A>(m, a, b, x));
    }

    @Override
    Digit<V, A> reverse(final Func<A, A> f)
    {
        return new Two<V, A>(m, f.call(b), f.call(a));
    }

    @Override
    <B> Digit<V, B> map(final Func<A, B> f, final Measured<V, B> m)
    {
        return new Two<V, B>(m, f.call(a), f.call(b));
    }

    @Override
    Digit<V, A> map(final Func<A,A> f)
    {
        return new Two<V, A>(m, f.call(a), f.call(b));
    }

    @Override
    <B> B foldRight(final Func<A, Func<B, B>> f, final B zero)
    {
        return f.call(a).call(f.call(b).call(zero));
    }

    @Override
    <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero)
    {
        return f.call(f.call(zero).call(a)).call(b);
    }

    @Override
    Split<Digit<V, A>, A> split(final Func<V, Boolean> pred, final V measure)
    {
        final V va = m.sum(measure, m.measure(a));
        if (pred.call(va))
        {
            return new Split<Digit<V, A>, A>(
                null,
                a,
                new One<V, A>(m, b));
        }

        return new Split<Digit<V, A>, A>(
            new One<V, A>(m, a),
            b,
            null);
    }

    @Override
    FingerTree<V, A> toTree()
    {
        return new Deep<V, A>(
            m,
            new One<V, A>(m, a),
            new Empty<V, Node<V, A>>(m.nodeMeasured()),
            new One<V, A>(m, b));
    }

    @Override
    A head()
    {
        return a;
    }

    @Override
    Digit<V, A> tail()
    {
        return new One<V, A>(m, b);
    }

    @Override
    A rhead()
    {
        return b;
    }

    @Override
    Digit<V, A> rtail()
    {
        return new One<V, A>(m, a);
    }

    @Override
    public java.lang.String toString()
    {
        return "Two(" + a + "," + b + ")";
    }
}
