package org.seanpatrickmiller.containers.fingertree;

import com.google.common.testing.EqualsTester;
import org.seanpatrickmiller.containers.fingertree.FingerTree;
import org.seanpatrickmiller.containers.util.Identity;
import org.seanpatrickmiller.containers.util.Functions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestSequenceFactory
{
    private SequenceFactory<Integer> factory;
    private Sequence<Integer> seq;

    @BeforeClass
    public void setup()
    {
        // Reuse factory across test cases.
        factory = new SequenceFactory<>();
    }

    @BeforeMethod
    public void before()
    {
        // Instantiate new seq for each test case.
        seq = factory.makeEmpty();
    }

    @Test
    public void testHead()
    {
        seq = seq.pushBack(1).pushBack(2);
        assertEquals(seq.head(), Integer.valueOf(1));
    }

    @Test
    public void testTail()
    {
        seq = seq.pushBack(1).pushBack(2);
        seq = seq.tail();
        assertEquals(seq.head(), Integer.valueOf(2));
    }

    @Test
    public void testLast()
    {
        seq = seq.pushBack(1).pushBack(2);
        assertEquals(seq.last(), Integer.valueOf(2));
    }

    @Test
    public void testInitial()
    {
        seq = seq.pushBack(1).pushBack(2);
        seq = seq.initial();
        assertEquals(seq.last(), Integer.valueOf(1));
    }

    @Test
    public void testPushBackIntegerSequence()
    {
        // [1,2,...,63]
        for (int i = 1; i < 64; ++i)
        {
            seq = seq.pushBack(i);
        }

        int expected = 1;
        while (!seq.isEmpty())
        {
            final Integer actual = seq.head();
            assertEquals(actual.intValue(), expected++);
            seq = seq.tail();
        }
    }

    @Test
    public void testPushFrontIntegerSequence()
    {
        // [63,62,...,1]
        for (int i = 1; i < 64; ++i)
        {
            seq = seq.pushFront(i);
        }

        int expected = 64;
        while (!seq.isEmpty())
        {
            final Integer actual = seq.head();
            assertEquals(actual.intValue(), --expected);
            seq = seq.tail();
        }
    }

    @Test
    public void testAppend()
    {
        // [1,2,...,5]
        for (int i = 1; i < 6; ++i)
        {
            seq = seq.pushBack(i);
        }

        Sequence<Integer> other = factory.makeEmpty();
        // [6,7,...,10]
        for (int i = 6; i < 11; ++i)
        {
            other = other.pushBack(i);
        }

        seq = seq.append(other);

        int expected = 1;
        while (!seq.isEmpty())
        {
            final Integer actual = seq.head();
            assertEquals(actual.intValue(), expected++);
            seq = seq.tail();
        }

        assertEquals(expected, 11);
    }

    @Test
    public void testMap()
    {
        // [4,3,2,1]
        for (int i = 4; i > 0; --i)
        {
            seq = seq.pushBack(i);
        }

        // Do not change sequence.
        Sequence<Integer> result = seq.map(new Identity<Integer>());
        int expected = 4;
        while (!result.isEmpty())
        {
            final Integer actual = result.head();
            assertEquals(actual.intValue(), expected--);
            result = result.tail();
        }

        // Add two to each element.
        result = seq.map(Functions.ADD.call(2));
        expected = 6;
        while (!result.isEmpty())
        {
            final Integer actual = result.head();
            assertEquals(actual.intValue(), expected--);
            result = result.tail();
        }
    }

    @Test
    public void testFold()
    {
        // [4,3,2,1]
        for (int i = 4; i > 0; --i)
        {
            seq = seq.pushBack(i);
        }

        assertEquals(seq.foldRight(Functions.ADD, 0), Integer.valueOf(10));
        assertEquals(seq.foldRight(Functions.ADD, 2), Integer.valueOf(12));
        assertEquals(seq.foldLeft(Functions.SUB, 0), Integer.valueOf(-10));
        assertEquals(seq.foldLeft(Functions.SUB, 2), Integer.valueOf(-8));
    }

    @Test
    public void testReverse()
    {
        // [4,3,2,1]
        for (int i = 4; i > 0; --i)
        {
            seq = seq.pushBack(i);
        }

        seq = seq.reverse();

        int expected = 1;
        while (!seq.isEmpty())
        {
            final Integer actual = seq.head();
            assertEquals(actual.intValue(), expected++);
            seq = seq.tail();
        }
    }

    @Test
    public void testAt()
    {
        // [1,2,...,63]
        for (int i = 1; i < 64; ++i)
        {
            seq = seq.pushBack(i);
        }

        assertEquals(seq.at(0), Integer.valueOf(1));
        assertEquals(seq.at(32), Integer.valueOf(33));
    }

    @Test
    public void testInsert()
    {
        // [1,2]
        seq = seq.pushBack(1).pushBack(2);

        seq = seq.insert(1, 3);

        assertEquals(seq.head(), Integer.valueOf(1));
        seq = seq.tail();
        assertEquals(seq.head(), Integer.valueOf(3));
        seq = seq.tail();
        assertEquals(seq.head(), Integer.valueOf(2));
        seq = seq.tail();
        assertTrue(seq.isEmpty());
    }

    @Test
    public void testDelete()
    {
        // [1,2,3]
        seq = seq.pushBack(1).pushBack(2).pushBack(3);

        seq = seq.delete(1);

        assertEquals(seq.head(), Integer.valueOf(1));
        seq = seq.tail();
        assertEquals(seq.head(), Integer.valueOf(3));
        seq = seq.tail();
        assertTrue(seq.isEmpty());
    }

    @Test
    public void testLength()
    {
        // [1,2,...,37]
        for (int i = 1; i < 38; ++i)
        {
            seq = seq.pushBack(i);
        }

        assertEquals(seq.size(), Integer.valueOf(37));
    }

    @Test
    public void testTake()
    {
        // [1,2,...,63]
        for (int i = 1; i < 64; ++i)
        {
            seq = seq.pushBack(i);
        }

        seq = seq.take(16);

        int expected = 1;
        while (!seq.isEmpty())
        {
            final Integer actual = seq.head();
            assertEquals(actual.intValue(), expected++);
            seq = seq.tail();
        }
        assertEquals(expected, 17);
    }

    @Test
    public void testDrop()
    {
        // [1,2,...,63]
        for (int i = 1; i < 64; ++i)
        {
            seq = seq.pushBack(i);
        }

        seq = seq.drop(16);

        int expected = 17;
        while (!seq.isEmpty())
        {
            final Integer actual = seq.head();
            assertEquals(actual.intValue(), expected++);
            seq = seq.tail();
        }
        assertEquals(expected, 64);
    }

    @Test
    public void testEquals()
    {
        // TODO
    }
}
