# x86_diagnostic
Virtual machine for x86 written in Java for computing memory distributions (used in oram)

Approximates the access pattern in memory using a first-order markov transition matrix on the set of all instructions issued at least once.

The purpose of this VM is to compute access patterns to verify that an oblivious ram compiler works.
