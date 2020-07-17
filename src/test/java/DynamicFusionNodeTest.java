import DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch.DynamicFusionNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamicFusionNodeTest {

  static final long seed = 4545;
  static final int passes = 200_000;
  static final int numKeys = 16;

  private DynamicFusionNode set;
  private static RankSelectPredecessorUpdateTest test;

  // @BeforeAll
  // static void generateTests() {
  //   test = new RankSelectPredecessorUpdateTest(seed, passes, numKeys);
  // }

  @BeforeEach
  void setUp() {
    set = new DynamicFusionNode();
  }

  @AfterEach
  void tearDown() {
    set = null;
  }

  @AfterAll
  static void clear() {
    test = null;
  }

  @Test
  void insertAndMemberSmallTest() {
    test.insertAndMemberSmallTest(set);
  }

  @Test
  void smallCorrectnessTest() {
    test.smallCorrectnessTest(set);
  }

  @Test
  void insertThenMemberTest() {
    test.insertThenMemberTest(set);
  }

  @Test
  void insertThenDeleteRandomKeysTest() {
    test.insertThenDeleteRandomKeysTest(set);
  }

  @Test
  void insertThenDeleteRangeOfKeysTest() {
    test.insertThenDeleteRangeOfKeysTest(set);
  }

  @Test
  void growingRankTest() {
    test.growingRankTest(set);
  }

  @Test
  void selectOfRankTest() {
    test.selectOfRankTest(set);
  }

  @Test
  void rankOfSelectTest() {
    test.rankOfSelectTest(set);
  }

  @Test
  void sizeTest() {
    test.sizeTest(set);
  }

  @Test
  void setAndGetIndexTest() {
    Random rand = new Random(seed);
    final HashSet<Long> seeds = new HashSet<>();
    while (seeds.size() < passes) {
      seeds.add(rand.nextLong());
    }
    
    int p = 0;
    for (final long seed : seeds) {
      p++;
      rand = new Random(seed);
      final HashMap<Integer, Integer> rankSlot = new HashMap<>();

      int i = 0;
      while (rankSlot.size() < numKeys) {

        i++;
        final int rank = rand.nextInt(numKeys);
        final int slot = rand.nextInt(numKeys);

        assertEquals(0, set.getIndex(rank),
            "Pass " + p + "/" + passes + " | Iteration " + i + "/" + numKeys + " | Seed: " + seed
            + "\nBefore setting, index at position " + rank + " was set to " + set.getIndex(rank)
            + " but it should have been 0!");

        rankSlot.put(rank, slot);

        set.setIndex(rank, slot);

        assertEquals(slot, set.getIndex(rank),
            "Pass " + p + "/" + passes + " | Iteration " + i + "/" + numKeys + " | Seed: "
            + seed + "\nAfter setting, Index at position " + rank + " was set to "
            + set.getIndex(rank) + " but it should have been " + slot + "!");
      }

      for (i = 0; i < numKeys; i++) {

        assertEquals((int) rankSlot.get(i), set.getIndex((long) i),
            "Pass " + p + "/" + passes + " | Iteration " + i + "/" + numKeys + " | Seed: "
            + seed + "\nExpected {" + i + "=" + rankSlot.get(i) + "} but got {" + i + "="
            + set.getIndex(i) + "}\nMap: " + rankSlot.toString());
      }

      set.reset();
    }
  }


}