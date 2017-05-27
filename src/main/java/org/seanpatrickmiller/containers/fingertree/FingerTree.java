package org.seanpatrickmiller.containers.fingertree;

import org.seanpatrickmiller.containers.util.Func;

/**
 * An immutable, fully-persistent 2-3 tree with fixed-size (1-4 elements)
 * digits to provide fast access to both ends.
 *
 * <p>Each "mutation" generates a new version of the finger tree and leaves
 * behind the old version. Creation of this new finger tree is kept cheap by
 * sharing most of the old version's structure with the new. You can reuse
 * older versions of any finger tree whenever you like without conflicting with
 * newer versions, though you do risk running into pessimal behavior in rare
 * circumstances involving tight loops. Together, immutability and persistence
 * buy thread safety, meaning multiple threads can share any instance of a
 * finger tree without copying, without synchronizing, and without fear of
 * concurrent modification exceptions.</p>
 *
 * <p>Although the most obvious use of this data structure would be in the
 * implementation of a catenable deque, the finger tree can also implement
 * indexed sequences, ordered sequences, and priority queues, among other
 * things. All you must is supply a monoid and a measurement to suit your
 * purpose. Regardless of the application, the amortized time-complexity of
 * common operations (where <i>n</i> is the number of elements in the tree and
 * <i>i</i> is an arbitrary position in the tree) is as follows:</p>
 *
 * <table style="margin-left: 2em">
 *   <tr><td>push-front</td><td>O(1)</td></tr>
 *   <tr><td>push-back</td><td>O(1)</td></tr>
 *   <tr><td>pop-front</td><td>O(1)</td></tr>
 *   <tr><td>pop-back</td><td>O(1)</td></tr>
 *   <tr><td>peek-front</td><td>O(1)</td></tr>
 *   <tr><td>peek-back</td><td>O(1)</td></tr>
 *   <tr><td>reverse</td><td>O(<i>n</i>)</td></tr>
 *   <tr><td>map</td><td>O(<i>n</i>)</td></tr>
 *   <tr><td>fold-left</td><td>O(<i>n</i>)</td></tr>
 *   <tr><td>fold-right</td><td>O(<i>n</i>)</td></tr>
 *   <tr><td>append</td><td>O(log<sub>2</sub>(min(<i>n</i><sub>1</sub>,
 *       <i>n</i><sub>2</sub>)))</td></tr>
 *   <tr><td>is-empty</td><td>O(1)</td></tr>
 *   <tr><td>measure</td><td>O(1)</td></tr>
 *   <tr><td>split</td><td>O(log<sub>2</sub>(min(<i>i</i>,
 *       <i>n</i> - <i>i</i>)))</td></tr>
 * </table>
 *
 * <p>Based on the Hinze &amp; Paterson paper
 * (<a href="http://www.soi.city.ac.uk/~ross/papers/FingerTree.pdf">
 * http://www.soi.city.ac.uk/~ross/papers/FingerTree.pdf</a>) and cribs heavily
 * from H&amp;P's official Haskell version
 * (<a href="http://www.soi.city.ac.uk/~ross/software/Data/FingerTree.hs">
 * http://www.soi.city.ac.uk/~ross/software/Data/FingerTree.hs</a>).
 *
 * @author Sean P. Miller
 * @version	0.1.0
 *
 * @param <V> annotation type
 * @param <A> element type
 */
public abstract class FingerTree<V, A>
{
    /**
     * The instance of Measured inherited by deriving classes.
     */
    protected final Measured<V, A> m;

    /**
     * For derived classes to initialize the measurement.
     * @param m
     */
    protected FingerTree(final Measured<V, A> m)
    {
        this.m = m;
    }

    /**
     * Does this tree have zero elements?
     * @return true if this FingerTree has zero elements; otherwise, false.
     */
    public abstract boolean isEmpty();

    /**
     * Push-left.
     * @param x
     * @return A new FingerTree with the given element pushed onto the left-
     * hand end.
     */
    public abstract FingerTree<V, A> pushFront(final A x);

    /**
     * Push-right.
     * @param x
     * @return A new FingerTree with the given element pushed onto the right-
     * hand end.
     */
    public abstract FingerTree<V, A> pushBack(final A x);

    /**
     * Left view of this tree.
     * @return null if called on Empty, else an instance of View
     * @see View
     */
    public abstract View<V, A> viewLeft();

    /**
     * Right view of this tree.
     * @return null if called on Empty, else an instance of View
     * @see View
     */
    public abstract View<V, A> viewRight();

    /**
     * Peek-left.
     * @return the leftmost element in this FingerTree
     */
    public abstract A head();

    /**
     * Pop-left.
     * @return this FingerTree without its leftmost element
     */
    public abstract FingerTree<V, A> tail();

    /**
     * Peek-right.
     * @return the rightmost element in this FingerTree
     */
    public abstract A rhead();

    /**
     * Pop-right.
     * @return this FingerTree without its rightmost element
     */
    public abstract FingerTree<V, A> rtail();

    /**
     * Reverses this tree.
     * @param f
     * @return this FingerTree in reverse order
     */
    public abstract FingerTree<V, A> reverse(final Func<A, A> f);

    /**
     * Applies a function to each element in this tree.
     * @param f The function to apply to each element.
     * @param m The measurement to use when annotating the new tree.
     * @return a new FingerTree containing the results of application of f to each element in this FingerTree
     */
    public abstract <B> FingerTree<V, B> map(final Func<A, B> f, final Measured<V, B> m);

    /**
     * Applies a function to each element in this tree.
     * @param f The function to apply to each element.
     * @return a new FingerTree containing the results of application of f to each element in this FingerTree
     */
    public abstract FingerTree<V, A> map(final Func<A, A> f);

    /**
     * Right-to-left accumulation.
     * @return the right-to-left accumulation of the elements of this FingerTree by function f with base case zero
     */
    public abstract <B> B foldRight(final Func<A, Func<B, B>> f, final B zero);

    /**
     * Left-to-right accumulation.
     * @return the left-to-right accumulation of the elements of this FingerTree by function f with base case zero
     */
    public abstract <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero);

    /**
     * Concatenation.
     * @return the concatenation of this and that
     * @note The code for this operation is horrifyingly tedious. View the
     *       source for a taste.
     */
    public abstract FingerTree<V, A> append(final FingerTree<V, A> that);

    /**
     * Yields measurement cached at root.
     * @return the root annotation of this FingerTree
     */
    public abstract V measure();

    /**
     * Public-facing wrapper for the results of the split operation.
     */
    public static final class SplitPair<V, A>
    {
        public final FingerTree<V, A> left;
        public final FingerTree<V, A> right;

        SplitPair(final FingerTree<V, A> left, final FingerTree<V, A> right)
        {
            this.left = left;
            this.right = right;
        }
    }

    /**
     * Splits the tree at an annotation specified by the given predicate.
     * @param pred Must start true and change to false while accumulating measurements.
     */
    public SplitPair<V, A> split(final Func<V, Boolean> pred)
    {
        if(isEmpty())
            return new SplitPair<V, A>(new Empty<V, A>(m), new Empty<V, A>(m));
        else if(pred.call(measure()))
        {
            final Split<FingerTree<V, A>, A> result = splitHelper(pred, m.zero());
            return new SplitPair<V, A>(result.left, result.right.pushFront(result.value));
        }
        else
            return new SplitPair<V, A>(this, new Empty<V, A>(m));
    }

    /*** HELPER FUNCTIONS ***/

    abstract Split<FingerTree<V, A>, A> splitHelper(final Func<V, Boolean> pred, final V i);

    static <V, A> FingerTree<V, Node<V, A>> add0(final Measured<V, A> m, final FingerTree<V, Node<V, A>> m1, final Digit<V, A> sf1, final Digit<V, A> pr2, final FingerTree<V, Node<V, A>> m2)
    {
        if(sf1 instanceof One) {
            final One<V, A> left = (One<V, A>)sf1;

            if(pr2 instanceof One) {
                final One<V, A> right = (One<V, A>)pr2;
                return append1(m, m1, new Node2<V, A>(m, left.a, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, A> right = (Two<V, A>)pr2;
                return append1(m, m1, new Node3<V, A>(m, left.a, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, A> right = (Three<V, A>)pr2;
                return append2(m, m1, new Node2<V, A>(m, left.a, right.a), new Node2<V, A>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, A> right = (Four<V, A>)pr2;
                return append2(m, m1, new Node3<V, A>(m, left.a, right.a, right.b), new Node2<V, A>(m, right.c, right.d), m2);
            }
        } else if(sf1 instanceof Two) {
            final Two<V, A> left = (Two<V, A>)sf1;

            if(pr2 instanceof One) {
                final One<V, A> right = (One<V, A>)pr2;
                return append1(m, m1, new Node3<V, A>(m, left.a, left.b, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, A> right = (Two<V, A>)pr2;
                return append2(m, m1, new Node2<V, A>(m, left.a, left.b), new Node2<V, A>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, A> right = (Three<V, A>)pr2;
                return append2(m, m1, new Node3<V, A>(m, left.a, left.b, right.a), new Node2<V, A>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, A> right = (Four<V, A>)pr2;
                return append2(m, m1, new Node3<V, A>(m, left.a, left.b, right.a), new Node3<V, A>(m, right.b, right.c, right.d), m2);
            }
        } else if(sf1 instanceof Three) {
            final Three<V, A> left = (Three<V, A>)sf1;

            if(pr2 instanceof One) {
                final One<V, A> right = (One<V, A>)pr2;
                return append2(m, m1, new Node2<V, A>(m, left.a, left.b), new Node2<V, A>(m, left.c, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, A> right = (Two<V, A>)pr2;
                return append2(m, m1, new Node3<V, A>(m, left.a, left.b, left.c), new Node2<V, A>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, A> right = (Three<V, A>)pr2;
                return append2(m, m1, new Node3<V, A>(m, left.a, left.b, left.c), new Node3<V, A>(m, right.a, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, A> right = (Four<V, A>)pr2;
                return append3(m, m1, new Node3<V, A>(m, left.a, left.b, left.c), new Node2<V, A>(m, right.a, right.b), new Node2<V, A>(m, right.c, right.d), m2);
            }
        } else /*if(sf1 instanceof Four)*/ {
            final Four<V, A> left = (Four<V, A>)sf1;

            if(pr2 instanceof One) {
                final One<V, A> right = (One<V, A>)pr2;
                return append2(m, m1, new Node3<V, A>(m, left.a, left.b, left.c), new Node2<V, A>(m, left.d, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, A> right = (Two<V, A>)pr2;
                return append2(m, m1, new Node3<V, A>(m, left.a, left.b, left.c), new Node3<V, A>(m, left.d, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, A> right = (Three<V, A>)pr2;
                return append3(m, m1, new Node3<V, A>(m, left.a, left.b, left.c), new Node2<V, A>(m, left.d, right.a), new Node2<V, A>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, A> right = (Four<V, A>)pr2;
                return append3(m, m1, new Node3<V, A>(m, left.a, left.b, left.c), new Node3<V, A>(m, left.d, right.a, right.b), new Node2<V, A>(m, right.c, right.d), m2);
            }
        }
    }

    static <V, A> FingerTree<V, Node<V, A>> append1(final Measured<V, A> m, final FingerTree<V, Node<V, A>> xs, final Node<V, A> a, final FingerTree<V, Node<V, A>> ys)
    {
        if(xs instanceof Empty) {
            return ys.pushFront(a);
        } else if(ys instanceof Empty) {
            return xs.pushBack(a);
        } else if(xs instanceof Single) {
            return ys.pushFront(a).pushFront(((Single<V, Node<V, A>>)xs).val);
        } else if(ys instanceof Single) {
            return xs.pushBack(a).pushBack(((Single<V, Node<V, A>>)ys).val);
        } else {
            final Deep<V, Node<V, A>> xss = (Deep<V, Node<V, A>>)xs;
            final Deep<V, Node<V, A>> yss = (Deep<V, Node<V, A>>)ys;
            final Measured<V, Node<V, A>> nm = m.nodeMeasured();
            return new Deep<V, Node<V, A>>(nm,
                                           xss.left,
                                           add1(nm, xss.mid, xss.right, a, yss.left, yss.mid),
                                           yss.right);
        }
    }

    static <V, A> FingerTree<V, Node<V, Node<V, A>>> add1(final Measured<V, Node<V, A>> m, final FingerTree<V, Node<V, Node<V, A>>> m1, final Digit<V, Node<V, A>> sf1, final Node<V, A> a, final Digit<V, Node<V, A>> pr2, final FingerTree<V, Node<V, Node<V, A>>> m2)
    {
        if(sf1 instanceof One) {
            final One<V, Node<V, A>> left = (One<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append1(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node2<V, Node<V, A>>(m, left.a, a), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, right.a), new Node3<V, Node<V, A>>(m, right.b, right.c, right.d), m2);
            }
        } else if(sf1 instanceof Two) {
            final Two<V, Node<V, A>> left = (Two<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node2<V, Node<V, A>>(m, left.a, left.b), new Node2<V, Node<V, A>>(m, a, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node3<V, Node<V, A>>(m, right.a, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node2<V, Node<V, A>>(m, right.a, right.b), new Node2<V, Node<V, A>>(m, right.c, right.d), m2);
            }
        } else if(sf1 instanceof Three) {
            final Three<V, Node<V, A>> left = (Three<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node2<V, Node<V, A>>(m, a, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node2<V, Node<V, A>>(m, a, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, right.a, right.b), new Node2<V, Node<V, A>>(m, right.c, right.d), m2);
            }
        } else /*if(sf1 instanceof Four)*/ {
            final Four<V, Node<V, A>> left = (Four<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node2<V, Node<V, A>>(m, left.d, a), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, right.a), new Node3<V, Node<V, A>>(m, right.b, right.c, right.d), m2);
            }
        }
    }

    static <V, A> FingerTree<V, Node<V, A>> append2(final Measured<V, A> m, final FingerTree<V, Node<V, A>> xs, final Node<V, A> a, final Node<V, A> b, final FingerTree<V, Node<V, A>> ys)
    {
        if(xs instanceof Empty) {
            return ys.pushFront(b).pushFront(a);
        } else if(ys instanceof Empty) {
            return xs.pushBack(a).pushBack(b);
        } else if(xs instanceof Single) {
            return ys.pushFront(b).pushFront(a).pushFront(((Single<V, Node<V, A>>)xs).val);
        } else if(ys instanceof Single) {
            return xs.pushBack(a).pushBack(b).pushBack(((Single<V, Node<V, A>>)ys).val);
        } else {
            final Deep<V, Node<V, A>> xss = (Deep<V, Node<V, A>>)xs;
            final Deep<V, Node<V, A>> yss = (Deep<V, Node<V, A>>)ys;
            final Measured<V, Node<V, A>> nm = m.nodeMeasured();
            return new Deep<V, Node<V, A>>(nm,
                                           xss.left,
                                           add2(nm, xss.mid, xss.right, a, b, yss.left, yss.mid),
                                           yss.right);
        }
    }

    static <V, A> FingerTree<V, Node<V, Node<V, A>>> add2(final Measured<V, Node<V, A>> m, final FingerTree<V, Node<V, Node<V, A>>> m1, final Digit<V, Node<V, A>> sf1, final Node<V, A> a, final Node<V, A> b, final Digit<V, Node<V, A>> pr2, final FingerTree<V, Node<V, Node<V, A>>> m2)
    {
        if(sf1 instanceof One) {
            final One<V, Node<V, A>> left = (One<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node2<V, Node<V, A>>(m, left.a, a), new Node2<V, Node<V, A>>(m, b, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node3<V, Node<V, A>>(m, right.a, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node2<V, Node<V, A>>(m, right.a, right.b), new Node2<V, Node<V, A>>(m, right.c, right.d), m2);
            }
        } else if(sf1 instanceof Two) {
            final Two<V, Node<V, A>> left = (Two<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node2<V, Node<V, A>>(m, b, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node3<V, Node<V, A>>(m, b, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node2<V, Node<V, A>>(m, b, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node3<V, Node<V, A>>(m, b, right.a, right.b), new Node2<V, Node<V, A>>(m, right.c, right.d), m2);
            }
        } else if(sf1 instanceof Three) {
            final Three<V, Node<V, A>> left = (Three<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, b, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node2<V, Node<V, A>>(m, a, b), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, b, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, b, right.a), new Node3<V, Node<V, A>>(m, right.b, right.c, right.d), m2);
            }
        } else /*if(sf1 instanceof Four)*/ {
            final Four<V, Node<V, A>> left = (Four<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node2<V, Node<V, A>>(m, left.d, a), new Node2<V, Node<V, A>>(m, b, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node3<V, Node<V, A>>(m, right.a, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append4(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node2<V, Node<V, A>>(m, right.a, right.b), new Node2<V, Node<V, A>>(m, right.c, right.d), m2);
            }
        }
    }

    static <V, A> FingerTree<V, Node<V, A>> append3(final Measured<V, A> m, final FingerTree<V, Node<V, A>> xs, final Node<V, A> a, final Node<V, A> b, final Node<V, A> c, final FingerTree<V, Node<V, A>> ys)
    {
        if(xs instanceof Empty) {
            return ys.pushFront(c).pushFront(b).pushFront(a);
        } else if(ys instanceof Empty) {
            return xs.pushBack(a).pushBack(b).pushBack(c);
        } else if(xs instanceof Single) {
            return ys.pushFront(c).pushFront(b).pushFront(a).pushFront(((Single<V, Node<V, A>>)xs).val);
        } else if(ys instanceof Single) {
            return xs.pushBack(a).pushBack(b).pushBack(c).pushBack(((Single<V, Node<V, A>>)ys).val);
        } else {
            final Deep<V, Node<V, A>> xss = (Deep<V, Node<V, A>>)xs;
            final Deep<V, Node<V, A>> yss = (Deep<V, Node<V, A>>)ys;
            final Measured<V, Node<V, A>> nm = m.nodeMeasured();
            return new Deep<V, Node<V, A>>(nm,
                                           xss.left,
                                           add3(nm, xss.mid, xss.right, a, b, c, yss.left, yss.mid),
                                           yss.right);
        }
    }

    static <V, A> FingerTree<V, Node<V, Node<V, A>>> add3(final Measured<V, Node<V, A>> m, final FingerTree<V, Node<V, Node<V, A>>> m1, final Digit<V, Node<V, A>> sf1, final Node<V, A> a, final Node<V, A> b, final Node<V, A> c, final Digit<V, Node<V, A>> pr2, final FingerTree<V, Node<V, Node<V, A>>> m2)
    {
        if(sf1 instanceof One) {
            final One<V, Node<V, A>> left = (One<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node2<V, Node<V, A>>(m, c, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node3<V, Node<V, A>>(m, c, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node2<V, Node<V, A>>(m, c, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node3<V, Node<V, A>>(m, c, right.a, right.b), new Node2<V, Node<V, A>>(m, right.c, right.d), m2);
            }
        } else if(sf1 instanceof Two) {
            final Two<V, Node<V, A>> left = (Two<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node3<V, Node<V, A>>(m, b, c, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node2<V, Node<V, A>>(m, b, c), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node3<V, Node<V, A>>(m, b, c, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node3<V, Node<V, A>>(m, b, c, right.a), new Node3<V, Node<V, A>>(m, right.b, right.c, right.d), m2);
            }
        } else if(sf1 instanceof Three) {
            final Three<V, Node<V, A>> left = (Three<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node2<V, Node<V, A>>(m, a, b), new Node2<V, Node<V, A>>(m, c, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, b, c), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, b, c), new Node3<V, Node<V, A>>(m, right.a, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append4(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, b, c), new Node2<V, Node<V, A>>(m, right.a, right.b), new Node2<V, Node<V, A>>(m, right.c, right.d), m2);
            }
        } else /*if(sf1 instanceof Four)*/ {
            final Four<V, Node<V, A>> left = (Four<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node2<V, Node<V, A>>(m, c, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node3<V, Node<V, A>>(m, c, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append4(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node2<V, Node<V, A>>(m, c, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append4(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node3<V, Node<V, A>>(m, c, right.a, right.b), new Node2<V, Node<V, A>>(m, right.c, right.d), m2);
            }
        }
    }

    static <V, A> FingerTree<V, Node<V, A>> append4(final Measured<V, A> m, final FingerTree<V, Node<V, A>> xs, final Node<V, A> a, final Node<V, A> b, final Node<V, A> c, final Node<V, A> d, final FingerTree<V, Node<V, A>> ys)
    {
        if(xs instanceof Empty) {
            return ys.pushFront(d).pushFront(c).pushFront(b).pushFront(a);
        } else if(ys instanceof Empty) {
            return xs.pushBack(a).pushBack(b).pushBack(c).pushBack(d);
        } else if(xs instanceof Single) {
            return ys.pushFront(d).pushFront(c).pushFront(b).pushFront(a).pushFront(((Single<V, Node<V, A>>)xs).val);
        } else if(ys instanceof Single) {
            return xs.pushBack(a).pushBack(b).pushBack(c).pushBack(d).pushBack(((Single<V, Node<V, A>>)ys).val);
        } else {
            final Deep<V, Node<V, A>> xss = (Deep<V, Node<V, A>>)xs;
            final Deep<V, Node<V, A>> yss = (Deep<V, Node<V, A>>)ys;
            final Measured<V, Node<V, A>> nm = m.nodeMeasured();
            return new Deep<V, Node<V, A>>(nm,
                                           xss.left,
                                           add4(nm, xss.mid, xss.right, a, b, c, d, yss.left, yss.mid),
                                           yss.right);
        }
    }

    static <V, A> FingerTree<V, Node<V, Node<V, A>>> add4(final Measured<V, Node<V, A>> m, final FingerTree<V, Node<V, Node<V, A>>> m1, final Digit<V, Node<V, A>> sf1, final Node<V, A> a, final Node<V, A> b, final Node<V, A> c, final Node<V, A> d, final Digit<V, Node<V, A>> pr2, final FingerTree<V, Node<V, Node<V, A>>> m2)
    {
        if(sf1 instanceof One) {
            final One<V, Node<V, A>> left = (One<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append2(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node3<V, Node<V, A>>(m, c, d, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node2<V, Node<V, A>>(m, c, d), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node3<V, Node<V, A>>(m, c, d, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, a, b), new Node3<V, Node<V, A>>(m, c, d, right.a), new Node3<V, Node<V, A>>(m, right.b, right.c, right.d), m2);
            }
        } else if(sf1 instanceof Two) {
            final Two<V, Node<V, A>> left = (Two<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node2<V, Node<V, A>>(m, b, c), new Node2<V, Node<V, A>>(m, d, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node3<V, Node<V, A>>(m, b, c, d), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node3<V, Node<V, A>>(m, b, c, d), new Node3<V, Node<V, A>>(m, right.a, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append4(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, a), new Node3<V, Node<V, A>>(m, b, c, d), new Node2<V, Node<V, A>>(m, right.a, right.b), new Node2<V, Node<V, A>>(m, right.c, right.d), m2);
            }
        } else if(sf1 instanceof Three) {
            final Three<V, Node<V, A>> left = (Three<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, b, c), new Node2<V, Node<V, A>>(m, d, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, b, c), new Node3<V, Node<V, A>>(m, d, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append4(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, b, c), new Node2<V, Node<V, A>>(m, d, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append4(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, a, b, c), new Node3<V, Node<V, A>>(m, d, right.a, right.b), new Node2<V, Node<V, A>>(m, right.c, right.d), m2);
            }
        } else /*if(sf1 instanceof Four)*/ {
            final Four<V, Node<V, A>> left = (Four<V, Node<V, A>>)sf1;

            if(pr2 instanceof One) {
                final One<V, Node<V, A>> right = (One<V, Node<V, A>>)pr2;
                return append3(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node3<V, Node<V, A>>(m, c, d, right.a), m2);
            } else if(pr2 instanceof Two) {
                final Two<V, Node<V, A>> right = (Two<V, Node<V, A>>)pr2;
                return append4(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node2<V, Node<V, A>>(m, c, d), new Node2<V, Node<V, A>>(m, right.a, right.b), m2);
            } else if(pr2 instanceof Three) {
                final Three<V, Node<V, A>> right = (Three<V, Node<V, A>>)pr2;
                return append4(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node3<V, Node<V, A>>(m, c, d, right.a), new Node2<V, Node<V, A>>(m, right.b, right.c), m2);
            } else /*if(pr2 instanceof Four)*/ {
                final Four<V, Node<V, A>> right = (Four<V, Node<V, A>>)pr2;
                return append4(m, m1, new Node3<V, Node<V, A>>(m, left.a, left.b, left.c), new Node3<V, Node<V, A>>(m, left.d, a, b), new Node3<V, Node<V, A>>(m, c, d, right.a), new Node3<V, Node<V, A>>(m, right.b, right.c, right.d), m2);
            }
        }
    }
}
