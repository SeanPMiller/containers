package com.seanpatrickmiller;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.seanpatrickmiller.containers.fingertree.Sequence;
import org.seanpatrickmiller.containers.fingertree.SequenceFactory;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations=10)
@State(Scope.Benchmark)
public class BenchmarkIteration
{
    private final Random rand;

    private ArrayList<Integer> arrayList;
    private Vector<Integer> vector;
    private Sequence<Integer> sequence;

    public BenchmarkIteration()
    {
        this.rand = new Random(System.currentTimeMillis());
    }

    @Setup(Level.Trial)
    public void setupTrial()
    {
        this.arrayList = new ArrayList<>();
        this.vector = new Vector<>();
        this.sequence = new SequenceFactory().makeEmpty();
        for (int i = 0; i < 16384; ++i)
        {
            this.arrayList.add(i);
            this.vector.add(i);
            this.sequence = sequence.pushBack(i);
        }
    }

    @Benchmark
    public int testArrayList()
    {
        int sum = 0;
        for (final Integer i : this.arrayList)
        {
            sum += i;
        }
        return sum;
    }

    @Benchmark
    public int testVector()
    {
        int sum = 0;
        for (final Integer i : this.vector)
        {
            sum += i;
        }
        return sum;
    }

    @Benchmark
    public int testSequence()
    {
        int sum = 0;
        for (final Integer i : this.sequence)
        {
            sum += i;
        }
        return sum;
    }
}
