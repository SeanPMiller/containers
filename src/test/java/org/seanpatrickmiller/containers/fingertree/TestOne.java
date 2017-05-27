package org.seanpatrickmiller.containers.fingertree;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TestOne
{
    @Test
    public void testToTree()
    {
        final One<Integer, Integer> one = makeOne(41);
        assertEquals(one.toTree().getClass(), Single.class);
        assertEquals(one.toTree().head().intValue(), 41);
    }

    @Test
    public void testHead()
    {
        assertEquals(makeOne(17).head().intValue(), 17);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testTail()
    {
        makeOne(0).tail();
    }

    @Test
    public void testRhead()
    {
        assertEquals(makeOne(-8).rhead().intValue(), -8);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testRtail()
    {
        makeOne(0).rtail();
    }

    private One<Integer, Integer> makeOne(final int i)
    {
        return new One<Integer, Integer>(
            new MeasurementFactory().makeSequentialMeasurement(),
            i);
    }
}
