package com.seanpatrickmiller;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;
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
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations=10)
@State(Scope.Benchmark)
public class BenchmarkHead
{
    private ArrayList<Integer> arrayList;
    private Vector<Integer> vector;
    private ConcurrentLinkedDeque<Integer> linkedDeque;
    private Sequence<Integer> sequence;

    @Setup(Level.Trial)
    public void setupTrial()
    {
        this.arrayList = new ArrayList<>();
        this.vector = new Vector<>();
        this.linkedDeque = new ConcurrentLinkedDeque<>();
        this.sequence = new SequenceFactory().makeEmpty();
        for (int i = 0; i < 1024; ++i)
        {
            this.arrayList.add(i);
            this.vector.add(i);
            this.linkedDeque.addLast(i);
            this.sequence = sequence.pushBack(i);
        }
    }

    @Benchmark
    public Integer testArrayList()
    {
        return this.arrayList.get(0);
    }

    @Benchmark
    public Integer testVector()
    {
        return this.vector.firstElement();
    }

    @Benchmark
    public Integer testConcurrentLinked()
    {
        return this.linkedDeque.peekFirst();
    }

    @Benchmark
    public Integer testSequence()
    {
        return this.sequence.head();
    }
}
