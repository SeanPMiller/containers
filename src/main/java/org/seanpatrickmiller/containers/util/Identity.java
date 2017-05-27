package org.seanpatrickmiller.containers.util;

public class Identity<A> implements Func<A, A>
{
    @Override
    public A call(final A a)
    {
        return a;
    }
}
