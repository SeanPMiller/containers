package org.seanpatrickmiller.containers.fingertree;

import com.google.common.base.Objects;
import org.seanpatrickmiller.containers.util.Func;
import org.seanpatrickmiller.containers.util.Lazy;

/**
 * A full-fledged finger tree that is neither empty nor singleton.
 * Abandon all hope, ye who enter here.
 */
final class Deep<V, A> extends FingerTree<V, A>
{
    final Lazy<V> v;
    final Digit<V, A> left;
    final FingerTree<V, Node<V, A>> mid;
    final Digit<V, A> right;

    /**
     * Constructs an instance of Deep.
     * The monoidal sum of child measurements is suspended in an instance of
     * {@link org.seanpatrickmiller.containers.util.Lazy}, which will defer
     * evaluation until the user requests the result of measurement.
     * @param m
     * @param left
     * @param mid
     * @param right
     */
    Deep(final Measured<V, A> m,
         final Digit<V, A> left,
         final FingerTree<V, Node<V, A>> mid,
         final Digit<V, A> right)
    {
        super(m);

        this.v = new Lazy<V>() {
            @Override
            protected V eval()
            {
                return m.sum(
                    left.measure(),
                    m.sum(mid.measure(), right.measure()));
            }
        };
        this.left = left;
        this.mid = mid;
        this.right = right;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public FingerTree<V, A> pushFront(final A x)
    {
        return left.prepend(x, mid, right);
    }

    @Override
    public FingerTree<V, A> pushBack(final A x)
    {
        return right.append(x, mid, left);
    }

    @Override
    public View<V, A> viewLeft()
    {
        if (left instanceof One)
        {
            final One<V, A> one = (One<V, A>)left;
            final View<V, Node<V, A>> vleft = mid.viewLeft();

            return (null == vleft) ?
                new View<V, A>(one.a, right.toTree()) :
                new View<V, A>(
                    one.a,
                    new Deep<V, A>(m, vleft.head.toDigit(), vleft.tail, right));
        }
        else
            return new View<V, A>(
                left.head(),
                new Deep<V, A>(m, left.tail(), mid, right));
    }

    @Override
    public View<V, A> viewRight()
    {
        if (right instanceof One)
        {
            final One<V, A> one = (One<V, A>)right;
            final View<V, Node<V, A>> vright = mid.viewRight();

            return new View<V, A>(
                one.a,
                (null == vright) ?
                    left.toTree() :
                    new Deep<V, A>(m, left, vright.tail, vright.head.toDigit()));
        }
        else
            return new View<V, A>(
                right.rhead(),
                new Deep<V, A>(m, left, mid, right.rtail()));
    }

    @Override
    public A head()
    {
        return left.head();
    }

    @Override
    public FingerTree<V, A> tail()
    {
        return viewLeft().tail;
    }

    @Override
    public A rhead()
    {
        return right.rhead();
    }

    @Override
    public FingerTree<V, A> rtail()
    {
        return viewRight().tail;
    }

    @Override
    public FingerTree<V, A> reverse(final Func<A, A> f)
    {
        return new Deep<V, A>(
            m,
            right.reverse(f),
            mid.reverse(Node.<V,A>liftReverse(f)),
            left.reverse(f));
    }

    @Override
    public <B> FingerTree<V, B> map(final Func<A, B> f, final Measured<V, B> m)
    {
        return new Deep<V, B>(
            m,
            left.map(f, m),
            mid.map(Node.<V,A,B>liftMap(f, m), m.nodeMeasured()),
            right.map(f, m));
    }

    @Override
    public FingerTree<V, A> map(final Func<A, A> f)
    {
        return new Deep<V, A>(
            m,
            left.map(f),
            mid.map(Node.<V,A>liftMap(f)),
            right.map(f));
    }

    @Override
    public <B> B foldRight(final Func<A, Func<B, B>> f, final B zero)
    {
        return left.foldRight(
            f,
            mid.foldRight(Func.flip(Node.<V,A,B>liftFoldRight(f)),
            right.foldRight(f, zero)));
    }

    @Override
    public <B> B foldLeft(final Func<B, Func<A, B>> f, final B zero)
    {
        return right.foldLeft(
            f,
            mid.foldLeft(Node.<V,A,B>liftFoldLeft(f),
            left.foldLeft(f, zero)));
    }

    @Override
    public FingerTree<V, A> append(final FingerTree<V, A> that)
    {
        if (that instanceof Empty)
        {
            // When appending empty finger tree, no-op.
            return this;
        }
        else if (that instanceof Single)
        {
            return pushBack(((Single<V, A>)that).val);
        }
        else
        {
            final Deep<V, A> yss = (Deep<V, A>)that;

            return new Deep<V, A>(
                m,
                left,
                FingerTree.add0(m, mid, right, yss.left, yss.mid),
                yss.right);
        }
    }

    @Override
    public V measure()
    {
        return v.getValue();
    }

    @Override
    Split<FingerTree<V, A>, A> splitHelper(final Func<V, Boolean> pred,
        final V i)
    {
        final V vpr = m.sum(i, left.measure());
        if (pred.call(vpr))
        {
            final Split<Digit<V, A>, A> temp = left.split(pred, i);
            return new Split<FingerTree<V, A>, A>(
                (null == temp.left) ?
                    new Empty<V, A>(m) :
                    temp.left.toTree(),
                temp.value,
                deepL(m, temp.right, mid, right));
        }

        final V vm = m.sum(vpr, mid.measure());
        if (pred.call(vm))
        {
            final Split<FingerTree<V, Node<V, A>>, Node<V, A>> mtemp =
                mid.splitHelper(pred, vpr);
            final Split<Digit<V, A>, A> temp =
                mtemp.value.split(pred, mappendVal(vpr, mtemp.left));

            return new Split<FingerTree<V, A>, A>(
                deepR(m, left, mtemp.left, temp.left),
                temp.value,
                deepL(m, temp.right, mtemp.right, right));
        }

        final Split<Digit<V, A>, A> temp = right.split(pred, vm);
        return new Split<FingerTree<V, A>, A>(
            deepR(m, left, mid, temp.left),
            temp.value,
            (null == temp.right) ?
                new Empty<V, A>(m) :
                temp.right.toTree());
    }

    private static <V, A> V mappendVal(final V v, final FingerTree<V, A> t)
    {
        return (t instanceof Empty) ?
            v :
            t.m.sum(v, t.measure());
    }

    private static <V, A> FingerTree<V, A> deepL(
        final Measured<V, A> m,
        final Digit<V, A> pr,
        final FingerTree<V, Node<V, A>> mid,
        final Digit<V, A> sf)
    {
        if (null == pr)
        {
            final View<V, Node<V, A>> vleft = mid.viewLeft();

            return (null == vleft) ?
                sf.toTree() :
                new Deep<V, A>(m, vleft.head.toDigit(), vleft.tail, sf);
        }
        else
            return new Deep<V, A>(m, pr, mid, sf);
    }

    private static <V, A> FingerTree<V, A> deepR(
        final Measured<V, A> m,
        final Digit<V, A> pr,
        final FingerTree<V, Node<V, A>> mid,
        final Digit<V, A> sf)
    {
        if (null == sf)
        {
            final View<V, Node<V, A>> vr = mid.viewRight();

            return (null == vr) ?
                pr.toTree() :
                new Deep<V, A>(m, pr, vr.tail, vr.head.toDigit());
        }
        else
            return new Deep<V, A>(m, pr, mid, sf);
    }

    @Override
    public java.lang.String toString()
    {
        return "Deep(" + left + "," + mid + "," + right + ")";
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj)
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }

        final Deep<V, A> other = (Deep<V, A>) obj;
        return Objects.equal(left, other.left) &&
            Objects.equal(mid, other.mid) &&
            Objects.equal(right, other.right);
    }
}
