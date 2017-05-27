package org.seanpatrickmiller.containers.util;

/**
 * Threadsafe representation of a suspended evaluation.
 * This relies on double-checked locking. It will not work unless you use
 * JDK5 or later. If you attempt to use an earlier version of Java, the thunk
 * may appear to work, but it will silently fail. The since tag is my best
 * attempt at preventing you from using it when you shouldn't. For more
 * information, visit the following URL:
 *   http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
 * 
 * @param <T> The type of the value this thunk will yield.
 * @since JDK5
 */
public abstract class Lazy<T>
{
    /**
     * The result of the call to {@link #eval()}.
     * 
     * ################################
     *  HOLY FUCK! A MUTABLE VARIABLE!
     * ################################
     */
    private volatile T value = null;

    /**
     * Empty c-tor.
     */
    protected Lazy()
    {
        // pass
    }

    /**
     * Override this method to calculate and to return some value that will be
     * stored as this thunk's value.
     * @return This thunk's value.
     */
    protected abstract T eval();

    /**
     * Evaluate this thunk, if necessary, and return the result.
     * The first call of this function will invoke {@link #eval()}, the result
     * of which will be cached for future calls to {@link #getValue()}. 
     * @return The result of the first call to {@link #eval()}.
     */
    public final T getValue()
    {
        // To minimize the number of reads and writes on the volatile variable,
        // we use a temporary.
        //
        // Volatile read.
        T temp = value;

        // The first check. If the value has already been set, we skip the rest
        // of this function.
        if (null == temp)
        {
            // Note how I protect the mutable fucking variable to prevent any
            // disastrous thready shit. In most cases, thanks to the first
            // check, synchronization will never happen.
            synchronized (this)
            {
                // Volatile read. With the volatile modifier on the result
                // variable, the JVM will reload its value. Earlier versions of
                // Java would just use the cached value, which could make this
                // fail horribly.
                temp = value;

                // The second check.
                if (null == temp)
                {
                    // Volatile write.
                    value = temp = eval();
                }
            }
        }

        return temp;
    }

    /**
     * Returns this thunk's value as a string.
     * I could throw an exception when the programmer attempts to request the
     * string-value of an unevaluated thunk, but I don't think that's the ideal
     * solution.
     * @return This thunk's value as a string.
     */
    @Override
    public java.lang.String toString()
    {
        return (null == value) ?
            "Thunktacular, man!" :
            value.toString();
    }    
}
