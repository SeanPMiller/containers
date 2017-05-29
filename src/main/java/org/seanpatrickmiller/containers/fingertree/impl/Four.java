package org.seanpatrickmiller.containers.fingertree.impl;

import org.seanpatrickmiller.containers.util.Func;

final class Four<V, A> extends Digit<V, A>
{
    final A a;
    final A b;
    final A c;
    final A d;

    Four(final Measured<V, A> m, final A a, final A b, final A c, final A d)
    {
        super(m);

        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
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
            mid.pushFront(new Node3<V, A>(m, b, c, d)),
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
            mid.pushBack(new Node3<V, A>(m, a, b, c)),
            new Two<V, A>(m, d, x));
    }

    @Override
    Digit<V, A> reverse(final Func<A, A> f)
    {
        return new Four<V, A>(m, f.call(d), f.call(c), f.call(b), f.call(a));
    }

    @Override
    <B> Digit<V, B> map(final Func<A, B> f, final Measured<V, B> m)
    {
        return new Four<V, B>(m, f.call(a), f.call(b), f.call(c), f.call(d));
    }

    @Override
    Digit<V, A> map(final Func<A, A> f)
    {
        return new Four<V, A>(m, f.call(a), f.call(b), f.call(c), f.call(d));
    }

    @Override
    <B> B foldRight(final Func<A, Func<B, B>> f, final B zero)
    {
        return f.call(a).call(f.call(b).call(f.call(c).call(f.call(d).call(zero))));
    }

    @Override
    <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero)
    {
        return f.call(f.call(f.call(f.call(zero).call(a)).call(b)).call(c)).call(d);
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
                new Three<V, A>(m, b, c, d));
        }

        final V vab = m.sum(va, m.measure(b));
        if (pred.call(vab))
        {
            return new Split<Digit<V, A>, A>(
                new One<V, A>(m, a),
                b,
                new Two<V, A>(m, c, d));
        }

        final V vabc = m.sum(vab, m.measure(c));
        if (pred.call(vabc))
        {
            return new Split<Digit<V, A>, A>(
                new Two<V, A>(m, a, b),
                c,
                new One<V, A>(m, d));
        }

        return new Split<Digit<V, A>, A>(
            new Three<V, A>(m, a, b, c),
            d,
            null);
    }

    @Override
    FingerTree<V, A> toTree()
    {
        return new Deep<V, A>(
            m,
            new Two<V, A>(m, a, b),
            new Empty<V, Node<V, A>>(m.nodeMeasured()),
            new Two<V, A>(m, c, d));
    }

    @Override
    A head()
    {
        return a;
    }

    @Override
    Digit<V, A> tail()
    {
        return new Three<V, A>(m, b, c, d);
    }

    @Override
    A rhead()
    {
        return d;
    }

    @Override
    Digit<V, A> rtail()
    {
        return new Three<V, A>(m, a, b, c);
    }

    @Override
    public java.lang.String toString()
    {
        return "Four(" + a + "," + b + "," + c + "," + d + ")";
    }
}
