package com.barbodh.madgrid;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class MadGridTest {
    @Test
    public void test01a_initializeInstance() {
        // checks if new key values are within [1, 4] interval (100 trials) for test constructor
        MadGrid testGrid = new MadGrid("Classic", 99, 99, 100);
        for (int index = 0; index < testGrid.getKey().size(); index++) {
            assertThat(testGrid.getKey().get(index)).isBetween(1, 4);
        }
    }

    @Test
    public void test02a_incrementKey() {
        // checks if new key values are within [1, 4] interval (100 trials)
        MadGrid testGrid = new MadGrid("Classic", 10);
        for (int index = 0; index < 100; index++) {
            testGrid.incrementKey();
            assertThat(testGrid.getKey().get(index)).isBetween(1, 4);
        }
    }
}
