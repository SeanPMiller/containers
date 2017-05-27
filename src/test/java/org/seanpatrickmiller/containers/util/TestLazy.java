package org.seanpatrickmiller.containers.util;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class TestLazy
{
    private Random rand;

    /**
     * Seed the PRNG.
     */
    @BeforeClass
    public void setup()
    {
        rand = new Random(System.currentTimeMillis());
    }
    
    // Implementation.
    final class TestSubject extends Lazy<Integer>
    {
        private final Integer value;
        private final AtomicInteger count;

        /**
         * @param value The value to return when {@link #eval()} is called.
         */
        TestSubject(final int value)
        {
            this.value = Integer.valueOf(value);
            count = new AtomicInteger(0);
        }

        @Override
        protected Integer eval()
        {
            count.incrementAndGet();
            return value;
        }

        /**
         * @return the number of times {@link #eval()} has been called on this
         * object.
         */
        public int getCount()
        {
            return count.get();
        }
    }

    /**
     * Passing this test does NOT guarantee that the implementation is correct.
     */
    @Test
    public void testThreadSafety() throws InterruptedException
    {
        for (int i = 0; i < 1000; ++i)
        {
            try
            {
                runThreadSafetyTrial(rand.nextInt());
            }
            catch (final AssertionError exn)
            {
                fail("Failed testThreadSafety() on trial #" + i);
            }
        }
    }

    private void runThreadSafetyTrial(final int value)
        throws InterruptedException
    {
        // The testing subject.
        final TestSubject subject = new TestSubject(value);

        // Total lack of ethics.
        doManyInvocations(subject, Runtime.getRuntime().availableProcessors());

        // You monster. What have you done?
        assertEquals(subject.getCount(), 1);
        assertEquals(subject.getValue().intValue(), value);
    }

    // Single run, slamming the subject with the specified number of threads.
    private void doManyInvocations(final TestSubject subject,
        final int threadCount) throws InterruptedException
    {
        // On your marks...
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch stopLatch = new CountDownLatch(threadCount);

        // Get set...
        for (int i = 0; i < threadCount; ++i)
        {
            final Runnable runner = new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        startLatch.await();
                        subject.getValue();
                        stopLatch.countDown();
                    }
                    catch (final InterruptedException exn)
                    {
                        stopLatch.countDown();
                        Thread.currentThread().interrupt();
                    }
                }
            };

            new Thread(runner, "MadScientist#" + i).start();
        }

        // Go!
        startLatch.countDown();
        stopLatch.await();
    }
}
