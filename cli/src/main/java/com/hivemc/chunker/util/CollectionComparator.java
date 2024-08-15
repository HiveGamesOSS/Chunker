package com.hivemc.chunker.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Comparator class that sorts collections by the size of the elements then the elements themselves in ascending order.
 *
 * @param <T> the type used for the collection.
 * @param <U> the type used for the elements which are comparable.
 */
public class CollectionComparator<T extends Collection<U>, U extends Comparable<? super U>> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        // Sort by size first
        int sizeComparison = Integer.compare(o1.size(), o2.size());
        if (sizeComparison != 0) return sizeComparison;

        // Now we iterate through the comparable components
        Iterator<U> iterator1 = o1.iterator();
        Iterator<U> iterator2 = o2.iterator();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            U c1 = iterator1.next();
            U c2 = iterator2.next();

            // Compare this element and do the next if it's equal
            int comparison = c1.compareTo(c2);
            if (comparison != 0) return comparison;

        }

        return 0; // Equal
    }
}
