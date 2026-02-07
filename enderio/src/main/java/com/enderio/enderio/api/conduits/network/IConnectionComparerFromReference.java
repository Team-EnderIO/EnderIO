package com.enderio.enderio.api.conduits.network;

// TODO: Temporary - once we can represent a path with a distance we can use a normal comparer.
public interface IConnectionComparerFromReference {
    /**
     * Compare {@code connectionA} and {@code connectionB} to determine their sorting order with respect to {@code refConnection}.
     * By default, this will compare the distances between the two connection's blocks to the reference node's connected block.
     * @param refConnection the reference node's connection to compare against.
     * @param connectionA  the first connection to compare.
     * @param connectionB  the second connection to compare.
     * @return Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    int compare(ConduitBlockConnection refConnection, ConduitBlockConnection connectionA,
        ConduitBlockConnection connectionB);
}
