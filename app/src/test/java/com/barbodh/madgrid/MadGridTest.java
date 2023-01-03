package com.barbodh.madgrid;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.Random;

public class MadGridTest {

    //////////////////// Test Cases for Mode Initialization & Accessor ////////////////////

    @Test
    public void test_invalidMode() {
        assertThatThrownBy(() -> {
            MadGrid testGrid = new MadGrid("Invalid", 20);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid Game Mode");
    }

    @Test
    public void test_validMode_1() {
        MadGrid testGrid = new MadGrid("Classic", 20);
        assertThat(testGrid.getMode()).isEqualTo("Classic");
    }

    @Test
    public void test_validMode_2() {
        MadGrid testGrid = new MadGrid("Reverse", 20);
        assertThat(testGrid.getMode()).isEqualTo("Reverse");
    }

    @Test
    public void test_validMode_3() {
        MadGrid testGrid = new MadGrid("Messy", 20);
        assertThat(testGrid.getMode()).isEqualTo("Messy");
    }

    //////////////////// Test Cases for Highest Score Initialization & Accessor ////////////////////

    @Test
    public void test_invalidHighestScore() {
        // highest score < 0
        final int HIGHEST_SCORE = -1 * (new Random()).nextInt(100);
        assertThatThrownBy(() -> {
            MadGrid testGrid = new MadGrid("Classic", HIGHEST_SCORE);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid Highest Score");
    }

    @Test
    public void test_validHighestScore_1() {
        // highest score = 0
        MadGrid testGrid = new MadGrid("Classic", 0);
        assertThat(testGrid.getHighestScore()).isEqualTo(0);
    }

    @Test
    public void test_validHighestScore_2() {
        // highest score > 0
        final int HIGHEST_SCORE = (new Random()).nextInt(100);
        MadGrid testGrid = new MadGrid("Classic", HIGHEST_SCORE);
        assertThat(testGrid.getHighestScore()).isEqualTo(HIGHEST_SCORE);
    }

    //////////////////// Test Cases for incrementKey() & getKey() Accessor ////////////////////

    @Test
    public void test_incrementKeyClassic_1() {
        MadGrid testGrid = new MadGrid("Classic", 0);
        for (int i = 0; i < 100; i++) {
            testGrid.incrementKey();
            assertThat(testGrid.getKey().get(i)).isBetween(1, 4);
        }
    }

    @Test
    public void test_incrementKeyClassic_2() {
        MadGrid testGrid = new MadGrid("Classic", 0);
        final int NUM_INCREMENTS = (new Random()).nextInt(100);
        for (int i = 0; i < NUM_INCREMENTS; i++) {
            testGrid.incrementKey();
        }
        assertThat(testGrid.getKey().size()).isEqualTo(NUM_INCREMENTS);
    }

    @Test
    public void test_incrementKeyReverse_1() {
        MadGrid testGrid = new MadGrid("Reverse", 0);
        for (int i = 0; i < 100; i++) {
            testGrid.incrementKey();
            assertThat(testGrid.getKey().get(i)).isBetween(1, 4);
        }
    }

    @Test
    public void test_incrementKeyReverse_2() {
        MadGrid testGrid = new MadGrid("Reverse", 0);
        final int NUM_INCREMENTS = (new Random()).nextInt(100);
        for (int i = 0; i < NUM_INCREMENTS; i++) {
            testGrid.incrementKey();
        }
        assertThat(testGrid.getKey().size()).isEqualTo(NUM_INCREMENTS);
    }

    @Test
    public void test_incrementKeyMessy_1() {
        MadGrid testGrid = new MadGrid("Messy", 0);
        for (int i = 0; i < 100; i++) {
            testGrid.incrementKey();
            assertThat(testGrid.getKey().get(i)).isBetween(1, 4);
        }
    }

    @Test
    public void test_incrementKeyMessy_2() {
        MadGrid testGrid = new MadGrid("Messy", 0);
        final int NUM_INCREMENTS = (new Random()).nextInt(100);
        for (int i = 0; i < NUM_INCREMENTS; i++) {
            testGrid.incrementKey();
        }
        assertThat(testGrid.getKey().size()).isEqualTo(NUM_INCREMENTS);
    }

    //////////////////// Test Cases for clearKey() & getKey() Accessor ////////////////////

    @Test
    public void test_clearKey_1() {
        MadGrid testGrid = new MadGrid("Classic", 20);
        testGrid.clearKey();
        assertThat(testGrid.getKey().isEmpty()).isEqualTo(true);
    }

    @Test
    public void test_clearKey_2() {
        MadGrid testGrid = new MadGrid("Classic", 20);
        for (int i = 0; i < 10; i++) {
            int NUM_INCREMENTS = (new Random()).nextInt(20);
            for (int j = 0; j < NUM_INCREMENTS; j++) {
                testGrid.incrementKey();
            }
            testGrid.clearKey();
            assertThat(testGrid.getKey().isEmpty()).isEqualTo(true);
        }
    }

    //////////////////// Test Cases for isHighestScore() ////////////////////

    @Test
    public void test_isHighestScore_1() {
        // score < highest score
        final int HIGHEST_SCORE = (new Random()).nextInt(20);
        MadGrid testGrid = new MadGrid("Classic", HIGHEST_SCORE);
        final int SCORE = (new Random()).nextInt(HIGHEST_SCORE);
        for (int i = 0; i < SCORE; i++) {
            testGrid.incrementKey();
        }
        assertThat(testGrid.isHighestScore()).isEqualTo(false);
    }

    @Test
    public void test_isHighestScore_2() {
        // score = highest score
        final int HIGHEST_SCORE = (new Random()).nextInt(20);
        MadGrid testGrid = new MadGrid("Classic", HIGHEST_SCORE);
        for (int i = 0; i < HIGHEST_SCORE; i++) {
            testGrid.incrementKey();
        }
        assertThat(testGrid.isHighestScore()).isEqualTo(false);
    }

    @Test
    public void test_isHighestScore_3() {
        // score > highest score
        final int HIGHEST_SCORE = (new Random()).nextInt(20);
        MadGrid testGrid = new MadGrid("Classic", HIGHEST_SCORE);
        final int SCORE = (new Random()).nextInt(20) + HIGHEST_SCORE;
        for (int i = 0; i < SCORE; i++) {
            testGrid.incrementKey();
        }
        assertThat(testGrid.isHighestScore()).isEqualTo(true);
    }

}
