SPM's Containers Library
========================

This library contains odds and ends that I have found interesting over time.

## 2-3 Finger Tree

My implementation, based on the [2006 Hinze and Paterson paper](http://www.staff.city.ac.uk/~ross/papers/FingerTree.html),
provides a fully-persistent data structure suitable for implementing other data
structures such as indexable sequences, catenable deques, and priority queues.
The Haskell programming language uses the 2-3 finger tree as the underlying
implementation of [Data.Sequence](https://downloads.haskell.org/~ghc/7.6.2/docs/html/libraries/containers-0.5.0.0/Data-Sequence.html).

Generally, 2-3 finger trees provide good worst-case asymptotic behavior for all
operations at the cost of high constant factors.

Implementing a data structure atop a 2-3 finger tree entails providing a monoid
and a measurement function to suit your use case. For example, an indexable
sequence of elements of type A requires the monoid (**Z**, +) (*i.e.*, the set
of integers and the addition operation) and the measurement function f(x) = 1,
where f : A -> **Z** (*i.e.*, a unary function that always returns one).
