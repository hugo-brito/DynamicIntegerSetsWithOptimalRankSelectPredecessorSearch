import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch.DynamicFusionNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamicFusionNodeTest {

  static final long seed = 42;
  static final int passes = 100;
  static final int numKeys = 16;

  static long[] seeds = null;
  static Set<Long> keySet;
  static List<Long> orderedKeyList;

  DynamicFusionNode set;

  @BeforeEach
  void setUp() {
    set = new DynamicFusionNode();
  }

  @AfterEach
  void tearDown() {
    set = null;
  }

  static void generateSeeds() {
    if (seeds == null) {
      Random rand = new Random(seed);
      seeds = new long[passes];
      for (int p = 0; p < passes; p++) {
        seeds[p] = rand.nextLong();
      }
    }
  }

  static void generateKeys(int pass) {
    Random random = new Random(seeds[pass]);
    keySet = new HashSet<>();
    while (keySet.size() < numKeys) {
      keySet.add(random.nextLong());
    }

    // produce a list of keys in ascending order using the unsigned comparator
    orderedKeyList = new ArrayList<>(keySet);

    // because the msb of a negative number is 1.
    Collections.sort(orderedKeyList, new Comparator<Long>() {
      @Override
      public int compare(final Long o1, final Long o2) {
        return Long.compareUnsigned(o1, o2);
      }
    });
  }

  DynamicFusionNode generateAndInsertKeys(int pass, DynamicFusionNode set) {

    set.reset(); // reset the data structure, just in case

    Random random = new Random(seeds[pass]);
    keySet = new HashSet<>();
    while (keySet.size() < numKeys) {
      keySet.add(random.nextLong());
    }

    // produce a list of keys in ascending order using the unsigned comparator
    orderedKeyList = new ArrayList<>(keySet);

    // because the msb of a negative number is 1.
    Collections.sort(orderedKeyList, new Comparator<Long>() {
      @Override
      public int compare(final Long o1, final Long o2) {
        return Long.compareUnsigned(o1, o2);
      }
    });

    // add all keys to the set
    for (final Long key : keySet) {
      set.insert(key);
    }

    return set;
  }

  @Test
  void smallCorrectnessTest() {
    RankSelectPredecessorUpdateTest.smallCorrectnessTest(set);
  }

  @Test
  void insertAndMemberSmallTest() {
    RankSelectPredecessorUpdateTest.smallCorrectnessTest(set);
  }

  @Test
  void insertThenMemberTest() {

    generateSeeds();

    for (int p = 0; p < passes; p++) {
      set = generateAndInsertKeys(p, set);
      Random rand = new Random(seeds[p]);

      if (p == 23) {
        System.err.println(orderedKeyList.toString());
      }

      long i = 0;
      while (orderedKeyList.size() > 0) {
        i++;
        final long key = orderedKeyList.remove(rand.nextInt(orderedKeyList.size()));
        assertTrue("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys 
            + "\nExpected " + key + " to be member.", set.member(key));
      }
    }
  }

}

