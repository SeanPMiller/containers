package org.seanpatrickmiller.containers.fingertree;

import com.google.common.base.Objects;
import java.util.Iterator;
import java.util.Spliterator;
import org.seanpatrickmiller.containers.fingertree.impl.Empty;
import org.seanpatrickmiller.containers.fingertree.impl.FingerTree;
import org.seanpatrickmiller.containers.fingertree.impl.Measured;
import org.seanpatrickmiller.containers.fingertree.impl.Single;
import org.seanpatrickmiller.containers.fingertree.impl.View;
import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Identity;
import org.seanpatrickmiller.containers.util.Monoids;

/**
 * Indexed sequence.
 */
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

    /**
     * Is this sequence empty?
     * @return true if this sequence is empty; otherwise, false.
     */
    public boolean isEmpty()
    {
        return this.tree.isEmpty();
    }

    /**
     * Get the first element in this sequence.
     * @return The first element in this sequence.
     */
    public A head()
    {
        return this.tree.head();
    }

    public Sequence<A> tail()
    {
        return new Sequence<A>(this.tree.tail(), this);
    }

    /**
     * Get the last element in this sequence.
     * @return The last element in this sequence.
     */
    public A last()
    {
        return this.tree.rhead();
    }

    public Sequence<A> initial()
    {
        return new Sequence<A>(this.tree.rtail(), this);
    }

    @Override
    public Iterator<A> iterator()
    {
        return this.tree.iterator();
    }

    @Override
    public Spliterator<A> spliterator()
    {
        return new SequenceSpliterator<A>(this.tree);
    }

    /**
     * Push an element onto the front of this sequence.
     * @param a The element to push.
     * @return A new sequence including the given element.
     */
    public Sequence<A> pushFront(final A a)
    {
        return new Sequence<A>(this.tree.pushFront(a), this);
    }

    /**
     * Push an element onto the back of this sequence.
     * @param a The element to push.
     * @return A new sequence including the given element.
     */
    public Sequence<A> pushBack(final A a)
    {
        return new Sequence<A>(this.tree.pushBack(a), this);
    }

    /**
     * Append another sequence to the back of this sequence.
     * @param other The sequence to append to this sequence.
     * @return A new sequence including both sequences.
     */
    public Sequence<A> append(final Sequence<A> other)
    {
        return new Sequence<A>(this.tree.append(other.tree), this);
    }

    // TODO: support map : [A] * (A -> B) -> [B]?
    /**
     * Apply a function to each element in this sequence.
     * @param f The function to apply.
     * @return A new sequence containing the results.
     */
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

    /**
     * Get the element at the given index.
     * @param index
     * @return
     */
    public A at(final int index)
    {
        return this.split(index).right.head();
    }

    /**
     * Insert an item before the element at the given index.
     * @param index The index before which to insert.
     * @param a The element to insert.
     * @return
     */
    public Sequence<A> insert(final int index, final A a)
    {
        FingerTree.SplitPair<java.lang.Integer, A> splat = this.split(index);
        return new Sequence(splat.left.pushBack(a).append(splat.right), this);
    }

    /**
     * Delete the item at the given index.
     * @param index The index of the element to delete.
     * @return
     */
    public Sequence<A> delete(final int index)
    {
        FingerTree.SplitPair<java.lang.Integer, A> splat = this.split(index);
        return new Sequence(splat.left.append(splat.right.tail()), this);
    }

    /**
     * Get this sequence's size.
     * @return This sequence's size.
     */
    public java.lang.Integer size()
    {
        return this.tree.measure();
    }

    /**
     * Get elements from the front of this sequence.
     * @param count How many elements to take.
     * @return
     */
    public Sequence<A> take(final int count)
    {
        return new Sequence(this.split(count).left, this);
    }

    /**
     * Remove elements from the front of this sequence.
     * @param count How many elements to drop.
     * @return
     */
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
