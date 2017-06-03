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
import org.openjdk.jmh.infra.Blackhole;
import org.seanpatrickmiller.containers.fingertree.Sequence;
import org.seanpatrickmiller.containers.fingertree.SequenceFactory;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations=10)
@State(Scope.Benchmark)
public class BenchmarkPushBack
{
    private ArrayList<Integer> arrayList;
    private Vector<Integer> vector;
    private ConcurrentLinkedDeque<Integer> linkedDeque;
    private Sequence<Integer> sequence;

    @Setup(Level.Iteration)
    public void setup()
    {
        this.arrayList = new ArrayList<>();
        this.vector = new Vector<>();
        this.linkedDeque = new ConcurrentLinkedDeque<>();
        this.sequence = new SequenceFactory().makeEmpty();
    }

    @Benchmark
    public void testArrayList(final Blackhole blackhole)
    {
        this.arrayList.add(1);
        blackhole.consume(this.arrayList);
    }

    @Benchmark
    public void testVector(final Blackhole blackhole)
    {
        this.vector.add(1);
        blackhole.consume(this.vector);
    }

    @Benchmark
    public void testConcurrentLinkedDeque(final Blackhole blackhole)
    {
        this.linkedDeque.add(1);
        blackhole.consume(this.linkedDeque);
    }

    @Benchmark
    public void testSequence(final Blackhole blackhole)
    {
        this.sequence = this.sequence.pushBack(1);
        blackhole.consume(this.sequence);
    }
}
