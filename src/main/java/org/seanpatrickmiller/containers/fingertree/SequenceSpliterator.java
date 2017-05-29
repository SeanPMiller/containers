package org.seanpatrickmiller.containers.fingertree;

import java.util.Spliterator;
import java.util.function.Consumer;
import org.seanpatrickmiller.containers.fingertree.impl.FingerTree;
import org.seanpatrickmiller.containers.fingertree.impl.View;

final class SequenceSpliterator<A> implements Spliterator<A>
{
    private FingerTree<java.lang.Integer, A> tree;

    public SequenceSpliterator(final FingerTree<java.lang.Integer, A> tree)
    {
        this.tree = tree;
    }

    @Override
    public void forEachRemaining(final Consumer<? super A> action)
    {
        for (final A a : this.tree)
        {
            action.accept(a);
        }
    }

    @Override
    public boolean tryAdvance(final Consumer<? super A> action)
    {
        if (this.tree.isEmpty())
        {
            return false;
        }

        action.accept(this.tree.head());
        this.tree = this.tree.tail();
        return true;
    }

    @Override
    public Spliterator<A> trySplit()
    {
        final int sz = this.tree.measure();
        final int half = sz / 2;
        if (0 == sz)
        {
            return null;
        }

        final Sequence.Index index = new Sequence.Index(half);
        final FingerTree.SplitPair<java.lang.Integer, A> splat =
            this.tree.split(index);
        this.tree = splat.right;
        return new SequenceSpliterator<A>(splat.left);
    }

    @Override
    public long estimateSize()
    {
        return this.tree.measure();
    }

    @Override
    public int characteristics()
    {
        return ORDERED | IMMUTABLE | SIZED | SUBSIZED;
    }
}
