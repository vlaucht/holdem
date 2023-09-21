package de.thm.holdem.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class TurnManager {

    /**
     * Finds the next element in a collection based on the current element.
     *
     * <p>
     *     This method searches for the element that follows the specified 'target'
     *     element in the given 'collection'. The search can be configured to be circular,
     *     meaning it wraps around to the beginning of the collection when the end is reached.
     * </p>
     *
     * @param <T>        The type of elements in the collection.
     * @param collection The collection in which to search for the next element.
     * @param target     The current element for which to find the next element.
     * @param isCircular   A boolean flag indicating whether the search should be circular.
     *                   If 'true', the search wraps around to the beginning of the collection.
     *                   If 'false', the search stops at the end of the collection.
     * @return The next element in the collection after the 'target' element.
     *         If the 'target' element is the last element in the collection and 'circular'
     *         is 'true', the first element will be returned (circular search).
     *         If the 'target' is not found in the collection, 'null' is returned.
     */
    public static <T> T getNext(Collection<T> collection, T target, final boolean isCircular) {
        if (collection == null) {
            return null;
        }

        Iterator<T> iterator = collection.iterator();
        T first = null;
        boolean isFirstIteration = true;

        // Iterate through the collection
        while (iterator.hasNext()) {
            T element = iterator.next();
            // If circular mode is enabled, remember the first element
            if (isCircular && isFirstIteration) {
                first = element;
                isFirstIteration = false;
            }
            // If the target element is found, return the next element.
            if (Objects.equals(element, target)) {
                return iterator.hasNext() ? iterator.next() : isCircular ? first : null;
            }
        }
        return null;
    }

}