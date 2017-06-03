package com.seanpatrickmiller;

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
public class BenchmarkPushFront
{
    private ConcurrentLinkedDeque<Integer> linkedDeque;
    private Sequence<Integer> sequence;

    @Setup(Level.Iteration)
    public void setup()
    {
        this.linkedDeque = new ConcurrentLinkedDeque<>();
        this.sequence = new SequenceFactory().makeEmpty();
    }

    @Benchmark
    public void testConcurrentLinkedDeque(final Blackhole blackhole)
    {
        this.linkedDeque.addFirst(1);
        blackhole.consume(this.linkedDeque);
    }

    @Benchmark
    public void testSequence(final Blackhole blackhole)
    {
        this.sequence = this.sequence.pushFront(1);
        blackhole.consume(this.sequence);
    }
}
