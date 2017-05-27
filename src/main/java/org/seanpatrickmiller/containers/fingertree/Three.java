package org.seanpatrickmiller.containers.fingertree;

import org.seanpatrickmiller.containers.util.Func;

final class Three<V, A> extends Digit<V, A>
{
    final A a;
    final A b;
    final A c;

    Three(final Measured<V, A> m, final A a, final A b, final A c)
    {
        super(m);

        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    FingerTree<V, A> prepend(
        final A x,
        final FingerTree<V, Node<V, A>> mid,
        final Digit<V, A> right)
    {
        return new Deep<V, A>(
            m,
            new Four<V, A>(m, x, a, b, c),
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
            new Four<V, A>(m, a, b, c, x));
    }

    @Override
    Digit<V, A> reverse(final Func<A, A> f)
    {
        return new Three<V, A>(m, f.call(c), f.call(b), f.call(a));
    }

    @Override
    <B> Digit<V, B> map(final Func<A, B> f, final Measured<V, B> m)
    {
        return new Three<V, B>(m, f.call(a), f.call(b), f.call(c));
    }

    @Override
    Digit<V, A> map(final Func<A, A> f)
    {
        return new Three<V, A>(m, f.call(a), f.call(b), f.call(c));
    }

    @Override
    <B> B foldRight(final Func<A, Func<B, B>> f, final B zero)
    {
        return f.call(a).call(f.call(b).call(f.call(c).call(zero)));
    }

    @Override
    <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero)
    {
        return f.call(f.call(f.call(zero).call(a)).call(b)).call(c);
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
                new Two<V, A>(m, b, c));
        }

        final V vab = m.sum(va, m.measure(b));
        if (pred.call(vab))
        {
            return new Split<Digit<V, A>, A>(
                new One<V, A>(m, a),
                b,
                new One<V, A>(m, c));
        }

        return new Split<Digit<V, A>, A>(
            new Two<V, A>(m, a, b),
            c,
            null);
    }

    @Override
    FingerTree<V, A> toTree()
    {
        return new Deep<V, A>(
            m,
            new Two<V, A>(m, a, b),
            new Empty<V, Node<V, A>>(m.nodeMeasured()),
            new One<V, A>(m, c));
    }

    @Override
    A head()
    {
        return a;
    }

    @Override
    Digit<V, A> tail()
    {
        return new Two<V, A>(m, b, c);
    }

    @Override
    A rhead()
    {
        return c;
    }

    @Override
    Digit<V, A> rtail()
    {
        return new Two<V, A>(m, a, b);
    }

    @Override
    public java.lang.String toString()
    {
        return "Three(" + a + "," + b + "," + c + ")";
    }
}
