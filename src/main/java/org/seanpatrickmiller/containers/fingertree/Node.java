package org.seanpatrickmiller.containers.fingertree;

import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Lazy;

abstract class Node<V, A>
{
    protected final Measured<V, A> m;
    protected final Lazy<V> v;

    Node(final Measured<V, A> m, final Lazy<V> v)
    {
        this.m = m;
        this.v = v;
    }

    abstract Node<V, A> reverse(final Func<A, A> f);

    abstract <B> Node<V, B> map(final Func<A, B> f, final Measured<V, B> m);

    abstract Node<V, A> map(final Func<A, A> f);

    abstract <B> B foldRight(final Func<A, Func<B, B>> f, final B zero);

    abstract <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero);

    abstract Split<Digit<V, A>, A> split(final Func<V, Boolean> pred, final V measure);

    abstract Digit<V, A> toDigit();

    V measure()
    {
        return v.getValue();
    }

    Measured<V, A> measured()
    {
        return m;
    }

    static <V, A> Func<Node<V, A>, Node<V, A>> liftReverse(final Func<A, A> f)
    {
        return new Func<Node<V, A>, Node<V, A>>() {
            @Override
            public Node<V, A> call(final Node<V, A> node) {
                return node.reverse(f);
            }
        };
    }

    static <V, A, B> Func<Node<V, A>, Node<V, B>> liftMap(
        final Func<A, B> f,
        final Measured<V, B> m)
    {
        return new Func<Node<V, A>, Node<V, B>>() {
            @Override
            public Node<V, B> call(final Node<V, A> node) {
                return node.map(f, m);
            }
        };
    }

    static <V, A> Func<Node<V, A>, Node<V, A>> liftMap(final Func<A, A> f)
    {
        return new Func<Node<V, A>, Node<V, A>>() {
            @Override
            public Node<V, A> call(final Node<V, A> node) {
                return node.map(f);
            }
        };
    }

    static <V, A, B> Func<B, Func<Node<V, A>, B>> liftFoldLeft(
        final Func<B, Func<A, B>> f)
    {
        return new Func<B, Func<Node<V, A>, B>>() {
            @Override
            public Func<Node<V, A>, B> call(final B b) {
                return new Func<Node<V, A>, B>() {
                    @Override
                    public B call(final Node<V, A> node) {
                        return node.foldLeft(f, b);
                    }
                };
            }
        };
    }

    static <V, A, B> Func<B, Func<Node<V, A>, B>> liftFoldRight(
        final Func<A, Func<B, B>> f)
    {
        return new Func<B, Func<Node<V, A>, B>>() {
            @Override
            public Func<Node<V, A>, B> call(final B b) {
                return new Func<Node<V, A>, B>() {
                    @Override
                    public B call(final Node<V, A> node) {
                        return node.foldRight(f, b);
                    }
                };
            }
        };
    }
}
