import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch.RankSelectPredecessorUpdate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

class RankSelectPredecessorUpdateTest {

  static final long seed = 42;
  static final int passes = 20;
  static final int numKeys = 100_000;
  
  // static Random rand = new Random(seed);
  static long[] seeds = null;
  static Set<Long> keySet;
  static List<Long> orderedKeyList;

  static void generateSeeds() {
    if (seeds == null) {
      Random rand = new Random(seed);
      seeds = new long[passes];
      for (int p = 0; p < passes; p++) {
        seeds[p] = rand.nextLong();
      }
    }
  }

  static RankSelectPredecessorUpdate generateAndInsertKeys(int pass,
      final RankSelectPredecessorUpdate testSet) {

    testSet.reset(); // reset the data structure, just in case

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
      testSet.insert(key);
    }

    return testSet;
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

  static void insertAndMemberSmallTest(final RankSelectPredecessorUpdate testSet) {
    testSet.insert(6917529027641081855L);
    testSet.insert(4611686018427387903L);
    
    assertFalse("Expected member(10) == false\n", testSet.member(10));
    
    assertTrue("Expected member(6917529027641081855L) == true\n",
        testSet.member(6917529027641081855L));
    assertTrue("Expected member(4611686018427387903L) == true\n",
        testSet.member(4611686018427387903L));
    
    assertFalse("Expected member(" + Long.MAX_VALUE + ") == false\n",
        testSet.member(Long.MAX_VALUE));

    testSet.insert(Long.MAX_VALUE);

    assertTrue("Expected member(" + Long.MAX_VALUE + ") == true\n",
        testSet.member(Long.MAX_VALUE));
  }

  static void smallCorrectnessTest(final RankSelectPredecessorUpdate testSet) {
    testSet.insert(10);
    testSet.insert(12);
    testSet.insert(42);
    testSet.insert(-1337);
    testSet.insert(-42);

    assertEquals("Expected size() == 5", 5, testSet.size());

    {
      // Testing key 10
      final long key = 10;
      final boolean member = true;
      final long rank = 0;
      final Long predecessor = null;
      final Long successor = 10L;

      smallCorrectnessTest(testSet, key, member, rank, predecessor, successor);
    }

    {
      // Testing key 11
      final long key = 11;
      final boolean member = false;
      final long rank = 1;
      final Long predecessor = 10L;
      final Long successor = 12L;

      smallCorrectnessTest(testSet, key, member, rank, predecessor, successor);
    }

    {
      // Testing key 12
      final long key = 12;
      final boolean member = true;
      final long rank = 1;
      final Long predecessor = 10L;
      final Long successor = 12L;

      smallCorrectnessTest(testSet, key, member, rank, predecessor, successor);
    }

    {
      // Testing key 42
      final long key = 42;
      final boolean member = true;
      final long rank = 2;
      final Long predecessor = 12L;
      final Long successor = 42L;

      smallCorrectnessTest(testSet, key, member, rank, predecessor, successor);
    }

    {
      // Testing key -1337
      final long key = -1337;
      final boolean member = true;
      final long rank = 3;
      final Long predecessor = 42L;
      final Long successor = -1337L;

      smallCorrectnessTest(testSet, key, member, rank, predecessor, successor);
    }

    {
      // Testing key -1000
      final long key = -1000;
      final boolean member = false;
      final long rank = 4;
      final Long predecessor = -1337L;
      final Long successor = -42L;

      smallCorrectnessTest(testSet, key, member, rank, predecessor, successor);
    }

    {
      // Testing key -42
      final long key = -42;
      final boolean member = true;
      final long rank = 4;
      final Long predecessor = -1337L;
      final Long successor = -42L;

      smallCorrectnessTest(testSet, key, member, rank, predecessor, successor);
    }

    {
      // Testing key -1
      final long key = -1;
      final boolean member = false;
      final long rank = 5;
      final Long predecessor = -42L;
      final Long successor = null;

      smallCorrectnessTest(testSet, key, member, rank, predecessor, successor);
    }
  }

  private static void smallCorrectnessTest(final RankSelectPredecessorUpdate testSet,
      final long key, final boolean member, final long rank, final Long predecessor,
      final Long successor) {

    // Member
    assertEquals("Key " + key + " | Test 1/5: expected member(" + key + ") == " + member + "\n",
        member, testSet.member(key));

    // Rank
    assertEquals("Key " + key + " | Test 2/5: expected rank(" + key + ") == " + rank + "\n",
        rank, testSet.rank(key));

    // Select
    assertEquals("Key " + key + " | Test 3/5: expected select(" + rank + ") == " + key + "\n",
        successor, testSet.select(rank));
    
    // Predecessor
    assertEquals("Key " + key + " | Test 4/5: expected predecessor(" + key + ") == " + predecessor
        + "\n", predecessor, testSet.predecessor(key));

    // Successor
    assertEquals("Key " + key + " | Test 5/5: expected successor(" + key + ") == " + successor
        + "\n", successor, testSet.successor(key));
    
  }

  static void insertThenMemberTest(RankSelectPredecessorUpdate testSet) {

    generateSeeds();

    for (int p = 0; p < passes; p++) {
      testSet = generateAndInsertKeys(p, testSet);
      Random rand = new Random(seeds[p]);

      long i = 0;
      while (orderedKeyList.size() > 0) {
        i++;
        final long key = orderedKeyList.remove(rand.nextInt(orderedKeyList.size()));
        assertTrue("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys,
            testSet.member(key));
      }
    }
  }

  static void insertThenDeleteRangeOfKeysTest(final RankSelectPredecessorUpdate testSet) {
    for (int p = 0; p < passes; p++) {
      testSet.reset(); // We reset the data structure as there could be keys from the last pass

      final long lowerBound = numKeys * (-1);
      long i = 0;
      for (long j = lowerBound; j <= numKeys; j++) {
        i++;

        assertFalse("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + (numKeys * 2)
            + " (before insertion)\n", testSet.member(j));
        testSet.insert(j);

        assertTrue("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + (numKeys * 2)
            + " (after insertion)\n", testSet.member(j));

        if (j % 1000 == 0) {
          for (long k = j - 1000; k <= j; k++) {
            testSet.delete(k);
          }
        }
      }
    }
  }

  static void insertThenDeleteRandomKeysTest(RankSelectPredecessorUpdate testSet) {

    generateSeeds();

    for (int p = 0; p < passes; p++) {
      testSet = generateAndInsertKeys(p, testSet);
      Random rand = new Random(seeds[p]);

      long i = 0;
  
      // use generated keys to test the set
      while (orderedKeyList.size() > 0) {
        i++;
        final long key = orderedKeyList.remove(rand.nextInt(orderedKeyList.size()));
  
        assertTrue("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys
            + ": expected member(" + key + ") == true\n", testSet.member(key));
  
        testSet.delete(key);
  
        assertFalse("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys
            + ": expected member(" + key + ") == false\n", testSet.member(key));
      }

    }

  }

  static void growingRankTest(RankSelectPredecessorUpdate testSet) {

    generateSeeds();

    for (int p = 0; p < passes; p++) {
      testSet = generateAndInsertKeys(p, testSet);

      for (int i = 0; i < orderedKeyList.size(); i++) {
        assertEquals("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys
            + ": expected rank of keys in sorted order to be monotone increasing function.\n",
            i, testSet.rank(orderedKeyList.get(i)));
      }
    }
  }

  static void selectOfRankTest(RankSelectPredecessorUpdate testSet) {

    generateSeeds();

    for (int p = 0; p < passes; p++) {
      testSet = generateAndInsertKeys(p, testSet);

      long i = 0;
      for (final Long key : keySet) {
        i++;
        assertEquals("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys + "\n",
            key, testSet.select(testSet.rank(key)));
      }
    }
  }

  static void rankOfSelectTest(RankSelectPredecessorUpdate testSet) {

    generateSeeds();

    for (int p = 0; p < passes; p++) {
      testSet = generateAndInsertKeys(p, testSet);

      for (long i = 0; i < numKeys; i++) {
        assertEquals("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys + "\n",
            i, testSet.rank(testSet.select(i)));
      }
    }
  }

  static void sizeTest(final RankSelectPredecessorUpdate testSet) {

    generateSeeds();

    for (int p = 0; p < passes; p++) {

      generateKeys(p);
      long i = 0;

      // add all keys to the set
      int keysInS = 0;
      for (final Long key : keySet) {
        i++;
        testSet.insert(key);
        keysInS++;
        assertEquals("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + (numKeys * 2)
            + ": expected KeysInS == size()\n", keysInS, testSet.size());
      }

      final ArrayList<Long> keyList = new ArrayList<>(keySet);

      // use generated keys to test the set
      Random rand = new Random(seeds[p]);
      while (keyList.size() > 0) {
        i++;
        final long key = keyList.remove(rand.nextInt(keyList.size()));
        testSet.delete(key);
        keysInS--;

        assertEquals("Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + (numKeys * 2)
            + ": expected KeysInS == size()\n", keysInS, testSet.size());
      }
    }
  }
}