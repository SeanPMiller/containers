package org.seanpatrickmiller.containers.fingertree.impl;

import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Lazy;

final class Node2<V, A> extends Node<V, A>
{
    private final A first;
    private final A second;

    Node2(final Measured<V, A> m, final A first, final A second)
    {
        super(m, new Lazy<V>() {
            @Override
            protected V eval()
            {
                return m.sum(m.measure(first), m.measure(second));
            }
        });
        this.first = first;
        this.second = second;
    }

    @Override
    Node<V, A> reverse(final Func<A, A> f)
    {
        return new Node2<V, A>(m, f.call(second), f.call(first));
    }

    @Override
    <B> Node<V, B> map(final Func<A, B> f, final Measured<V, B> m)
    {
        return new Node2<V, B>(m, f.call(first), f.call(second));
    }

    @Override
    Node<V, A> map(final Func<A, A> f)
    {
        return new Node2<V, A>(m, f.call(first), f.call(second));
    }

    @Override
    <B> B foldRight(final Func<A, Func<B, B>> f, final B zero)
    {
        return f.call(first).call(f.call(second).call(zero));
    }

    @Override
    <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero)
    {
        return f.call(f.call(zero).call(first)).call(second);
    }

    @Override
    Split<Digit<V, A>, A> split(final Func<V, Boolean> pred, final V measure)
    {
        final V va = m.sum(measure, m.measure(first));
        if(pred.call(va))
            return new Split<Digit<V, A>, A>(
                null, first, new One<V, A>(m, second));
        else
            return new Split<Digit<V, A>, A>(
                new One<V, A>(m, first), second, null);
    }

    @Override
    Digit<V, A> toDigit()
    {
        return new Two<V, A>(m, first, second);
    }

    @Override
    public java.lang.String toString()
    {
        return "Node2(" + first + "," + second + ")";
    }
}
