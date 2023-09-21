package de.thm.holdem.utils;

import de.thm.holdem.utils.TurnManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnManagerTest {

    List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

    @Test
    public void Should_GetFirstElement_If_TargetIsLastAndModeIsCircular() {

        Integer nextNumber = TurnManager.getNext(numbers, 5, true);
        assertEquals(1, nextNumber);
    }

    @Test
    public void Should_ReturnNull_If_TargetIsLastAndModeIsNotCircular() {

        Integer nextNumber = TurnManager.getNext(numbers, 5, false);
        assertNull(nextNumber);
    }

    @Test
    void Should_ReturnNull_If_CollectionIsEmpty() {

        Integer nextNumber = TurnManager.getNext(new ArrayList<>(), 1, false);
        assertNull(nextNumber);
    }

    @Test
    void Should_GetNextElement_If_TargetIsNotLast() {

        Integer nextNumber = TurnManager.getNext(numbers, 3, false);
        assertEquals(4, nextNumber);
    }

}