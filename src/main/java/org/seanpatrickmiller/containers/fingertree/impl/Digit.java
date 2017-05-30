package org.seanpatrickmiller.containers.fingertree.impl;

import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Lazy;

abstract class Digit<V, A>
{
    protected final Measured<V, A> m;
    private final Lazy<V> v;

    Digit(final Measured<V, A> m)
    {
        this.m = m;
        this.v = new Lazy<V>() {
            @Override
            protected V eval()
            {
                return foldLeft(new Func<V, Func<A, V>>() {
                    @Override
                    public Func<A, V> call(final V v) {
                        return new Func<A, V>() {
                            @Override
                            public V call(final A a) {
                                return m.sum(v, m.measure(a));
                            }
                        };
                    }
                }, m.zero());
            }
        };
    }

    abstract FingerTree<V, A> prepend(
        final A x,
        final FingerTree<V, Node<V, A>> mid,
        final Digit<V, A> right);

    abstract FingerTree<V, A> append(
        final A x,
        final FingerTree<V, Node<V, A>> mid,
        final Digit<V, A> left);

    abstract Digit<V, A> reverse(final Func<A, A> f);

    abstract <B> Digit<V, B> map(final Func<A, B> f, final Measured<V, B> m);

    abstract Digit<V, A> map(final Func<A, A> f);

    abstract <B> B foldRight(final Func<A, Func<B, B>> f, final B zero);

    abstract <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero);

    abstract Split<Digit<V, A>, A> split(
        final Func<V, Boolean> pred,
        final V measure);

    abstract FingerTree<V, A> toTree();

    abstract A head();

    abstract Digit<V, A> tail();

    abstract A rhead();

    abstract Digit<V, A> rtail();

    V measure()
    {
        return v.getValue();
    }
}
