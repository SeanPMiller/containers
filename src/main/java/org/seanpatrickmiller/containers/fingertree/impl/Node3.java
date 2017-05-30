package org.seanpatrickmiller.containers.fingertree.impl;

import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Lazy;

final class Node3<V, A> extends Node<V, A>
{
    private final A first;
    private final A second;
    private final A third;

    Node3(final Measured<V, A> m, final A first, final A second, final A third)
    {
        super(m, new Lazy<V>() {
            @Override
            protected V eval()
            {
                return m.sum(
                    m.measure(first),
                    m.sum(m.measure(second), m.measure(third)));
            }
        });

        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    Node<V, A> reverse(final Func<A, A> f)
    {
        return new Node3<V, A>(m, f.call(third), f.call(second), f.call(first));
    }

    @Override
    <B> Node<V, B> map(final Func<A, B> f, final Measured<V, B> m)
    {
        return new Node3<V, B>(m, f.call(first), f.call(second), f.call(third));
    }

    @Override
    Node<V, A> map(final Func<A, A> f)
    {
        return new Node3<V, A>(m, f.call(first), f.call(second), f.call(third));
    }

    @Override
    <B> B foldRight(final Func<A, Func<B, B>> f, final B zero)
    {
        return f.call(first).call(f.call(second).call(f.call(third).call(zero)));
    }

    @Override
    <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero)
    {
        return f.call(f.call(f.call(zero).call(first)).call(second)).call(third);
    }

    @Override
    Split<Digit<V, A>, A> split(final Func<V, Boolean> pred, final V measure)
    {
        final V va = m.sum(measure, m.measure(first));
        if(pred.call(va))
            return new Split<Digit<V, A>, A>(
                null, first, new Two<V, A>(m, second, third));

        final V vab = m.sum(va, m.measure(second));
        if(pred.call(vab))
            return new Split<Digit<V, A>, A>(
                new One<V, A>(m, first), second, new One<V, A>(m, third));

        return new Split<Digit<V, A>, A>(
            new Two<V, A>(m, first, second), third, null);
    }

    @Override
    Digit<V, A> toDigit()
    {
        return new Three<V, A>(m, first, second, third);
    }

    @Override
    public java.lang.String toString()
    {
        return "Node3(" + first + "," + second + "," + third + ")";
    }
}
