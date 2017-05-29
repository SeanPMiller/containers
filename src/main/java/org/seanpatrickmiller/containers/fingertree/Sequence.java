package org.seanpatrickmiller.containers.fingertree;

import com.google.common.base.Objects;
import java.util.Iterator;
import org.seanpatrickmiller.containers.fingertree.impl.Empty;
import org.seanpatrickmiller.containers.fingertree.impl.FingerTree;
import org.seanpatrickmiller.containers.fingertree.impl.Measured;
import org.seanpatrickmiller.containers.fingertree.impl.Single;
import org.seanpatrickmiller.containers.fingertree.impl.View;
import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Identity;
import org.seanpatrickmiller.containers.util.Monoids;

public final class Sequence<A> implements java.lang.Iterable<A>
{
    // This sequence's actual representation.
    private final FingerTree<java.lang.Integer, A> tree;

    // This sequence's element type's identity function.
    private final Identity<A> identity;

    // Create sequence from tree with identity.
    Sequence(final FingerTree<java.lang.Integer, A> tree,
        final Identity<A> identity)
    {
        this.tree = tree;
        this.identity = identity;
    }

    // Create sequence from tree using state from existing sequence.
    Sequence(final FingerTree<java.lang.Integer, A> tree, final Sequence seq)
    {
        this.tree = tree;
        this.identity = seq.identity;
    }

    // Index function.
    static final class Index
        implements Func<java.lang.Integer, java.lang.Boolean>
    {
        private final Integer index;

        Index(final Integer index)
        {
            this.index = index;
        }

        @Override
        public Boolean call(final Integer measurement)
        {
            return this.index < measurement;
        }
    }

    // Low-level split operation.
    FingerTree.SplitPair<java.lang.Integer, A> split(final int index)
    {
        final Index f = new Index(index);
        return this.tree.split(f);
    }

    public boolean isEmpty()
    {
        return this.tree.isEmpty();
    }

    public A head()
    {
        return this.tree.head();
    }

    public Sequence<A> tail()
    {
        return new Sequence<A>(this.tree.tail(), this);
    }

    public A last()
    {
        return this.tree.rhead();
    }

    public Sequence<A> initial()
    {
        return new Sequence<A>(this.tree.rtail(), this);
    }

    public Iterator<A> iterator()
    {
        return new SequenceIterator<A>(this.tree);
    }

    public Sequence<A> pushFront(final A a)
    {
        return new Sequence<A>(this.tree.pushFront(a), this);
    }

    public Sequence<A> pushBack(final A a)
    {
        return new Sequence<A>(this.tree.pushBack(a), this);
    }

    public Sequence<A> append(final Sequence<A> other)
    {
        return new Sequence<A>(this.tree.append(other.tree), this);
    }

    // TODO: support map : [A] * (A -> B) -> [B]?
    public Sequence<A> map(final Func<A, A> f)
    {
        return new Sequence<A>(this.tree.map(f), this);
    }

    public <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero)
    {
        return this.tree.foldLeft(f, zero);
    }

    public <B> B foldRight(final Func<A, Func<B, B>> f, final B zero)
    {
        return this.tree.foldRight(f, zero);
    }

    public Sequence<A> reverse()
    {
        return new Sequence<A>(this.tree.reverse(this.identity), this);
    }

    public A at(final int index)
    {
        return this.split(index).right.head();
    }

    /**
     * Insert an item before the given index.
     * @param index The index before which to insert.
     * @param a The element to insert.
     */
    public Sequence<A> insert(final int index, final A a)
    {
        FingerTree.SplitPair<java.lang.Integer, A> splat = this.split(index);
        return new Sequence(splat.left.pushBack(a).append(splat.right), this);
    }

    public Sequence<A> delete(final int index)
    {
        FingerTree.SplitPair<java.lang.Integer, A> splat = this.split(index);
        return new Sequence(splat.left.append(splat.right.tail()), this);
    }

    public java.lang.Integer size()
    {
        return this.tree.measure();
    }

    public Sequence<A> take(final int count)
    {
        return new Sequence(this.split(count).left, this);
    }

    public Sequence<A> drop(final int count)
    {
        return new Sequence(this.split(count).right, this);
    }

    @Override
    public boolean equals(final java.lang.Object o)
    {
        if (!(o instanceof Sequence))
        {
            return false;
        }

        final Sequence<A> other = (Sequence<A>) o;

        if (this == o)
        {
            return true;
        }

        if (size() != other.size())
        {
            return false;
        }

        View<java.lang.Integer, A> i = this.tree.viewLeft();
        View<java.lang.Integer, A> j = other.tree.viewLeft();
        while (i != null)
        {
            if (!Objects.equal(i.head, j.head))
            {
                return false;
            }

            i = i.tail.viewLeft();
            j = j.tail.viewLeft();
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = 7;
        for (final A a : this.tree)
        {
            final int c = Objects.hashCode(a);
            result = 37 * result + c;
        }
        return result;
    }

    // TODO: toString()
}
