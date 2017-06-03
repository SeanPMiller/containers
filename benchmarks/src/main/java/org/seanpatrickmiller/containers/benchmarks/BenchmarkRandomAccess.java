package com.seanpatrickmiller;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.function.IntUnaryOperator;
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
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations=10)
@State(Scope.Benchmark)
public class BenchmarkRandomAccess
{
    private final Random rand;
    private final int[] indices;
    private int index;

    private ArrayList<Integer> arrayList;
    private Vector<Integer> vector;
    private Sequence<Integer> sequence;

    public BenchmarkRandomAccess()
    {
        this.rand = new Random(System.currentTimeMillis());
        this.indices = new int[256];
        this.index = 0;
    }

    @Setup(Level.Trial)
    public void setupTrial()
    {
        this.arrayList = new ArrayList<>();
        this.vector = new Vector<>();
        this.sequence = new SequenceFactory().makeEmpty();
        for (int i = 0; i < 1024; ++i)
        {
            this.arrayList.add(i);
            this.vector.add(i);
            this.sequence = sequence.pushBack(i);
        }
    }

    @Setup(Level.Iteration)
    public void setupIteration()
    {
        // Use different indices for each iteration.
        Arrays.setAll(this.indices, new IntUnaryOperator() {
            @Override
            public int applyAsInt(final int operand)
            {
                return rand.nextInt(1024);
            }
        });
    }

    @Benchmark
    public Integer testArrayList()
    {
        final Integer value = this.arrayList.get(this.index);
        incrementIndex();
        return value;
    }

    @Benchmark
    public Integer testVector()
    {
        final Integer value = this.vector.get(this.index);
        incrementIndex();
        return value;
    }

    @Benchmark
    public Integer testSequence()
    {
        final Integer value = this.sequence.at(this.index);
        incrementIndex();
        return value;
    }

    private void incrementIndex()
    {
        this.index = (this.index + 1) % this.indices.length;
    }
}
