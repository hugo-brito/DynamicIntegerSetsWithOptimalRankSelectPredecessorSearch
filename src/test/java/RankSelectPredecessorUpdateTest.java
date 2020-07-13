import static org.junit.Assert.assertEquals;

import DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch.RankSelectPredecessorUpdate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

class RankSelectPredecessorUpdateTest {

  static long seed = 42;
  static int numKeys = 1_000_000;
  static Random rand;
  static Set<Long> keySet;
  static List<Long> orderedKeyList;

  static RankSelectPredecessorUpdate generateAndInsertKeys(
      final RankSelectPredecessorUpdate testSet) {
    rand = new Random(seed);
    keySet = new HashSet<>();
    while (keySet.size() < numKeys) {
      keySet.add(rand.nextLong());
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

  static void generateKeys() {
    rand = new Random(seed);
    keySet = new HashSet<>();
    while (keySet.size() < numKeys) {
      keySet.add(rand.nextLong());
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
    assert (!testSet.member(10));
    assert (testSet.member(6917529027641081855L));
    assert (testSet.member(4611686018427387903L));
    assert (!testSet.member(Long.MAX_VALUE));
    testSet.insert(Long.MAX_VALUE);
    assert (testSet.member(Long.MAX_VALUE));
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
    assertEquals("Expected member(" + key + ") == " + member + "\n", member, testSet.member(key));

    // Rank
    assertEquals("Expected rank(" + key + ") == " + rank + "\n", rank, testSet.rank(key));

    // Select
    assertEquals("Expected select(" + rank + ") == " + key + "\n", successor, testSet.select(rank));
    
    // Predecessor
    assertEquals("Expected predecessor(" + key + ") == " + predecessor + "\n",
        predecessor, testSet.predecessor(key));

    // Successor
    assertEquals("Expected successor(" + key + ") == " + successor + "\n",
        successor, testSet.successor(key));
    
  }

  static void insertThenMemberTest(RankSelectPredecessorUpdate testSet) {
    testSet = generateAndInsertKeys(testSet);
    while (orderedKeyList.size() > 0) {
      final long key = orderedKeyList.remove(rand.nextInt(orderedKeyList.size()));
      final boolean failed = !testSet.member(key);
      if (failed) {
        System.err.println("insertThenMemberTest iteration "
              + (numKeys - orderedKeyList.size()) + "/" + numKeys + "\n"
              + key + " should be member, but it's not.");
      }
      assert (!failed);
    }
  }

  static void insertThenDeleteRangeOfKeysTest(final RankSelectPredecessorUpdate testSet) {
    final long lowerBound = numKeys * (-1);
    int iteration = 0;
    for (long i = lowerBound; i <= numKeys; i++) {
      iteration++;
      boolean failed = false;
      if (testSet.member(i)) {
        failed = true;
        System.err.println("insertThenDeleteRangeOfKeysTest iteration " + iteration + "/" + (2 * numKeys)
            + "\nExpected " + i + " not to be member, but it was!");
      }
      testSet.insert(i);

      if (!testSet.member(i)) {
        failed = true;
        System.err.println("insertThenDeleteRangeOfKeysTest iteration " + iteration + "/" + (2 * numKeys)
            + "\nExpected " + i + " to be member, but it wasn't!");
      }

      assert (!failed);

      if (i % 1000 == 0) {
        for (long j = i - 1000; j <= i; j++) {
          testSet.delete(j);
        }
      }
    }
  }

  static void insertThenDeleteRandomKeysTest(RankSelectPredecessorUpdate testSet) {
    testSet = generateAndInsertKeys(testSet);

    long i = 0;

    // use generated keys to test the set
    while (orderedKeyList.size() > 0) {
      i++;
      boolean failed = false;

      final long key = orderedKeyList.remove(rand.nextInt(orderedKeyList.size()));

      if (!testSet.member(key)) {
        failed = true;
        System.err.println("insertThenDeleteRandomKeysTest iteration " + i + "/" + numKeys
            + "\nExpected " + key + " to be member, but it wasn't!");
      }

      testSet.delete(key);

      if (testSet.member(key)) {
        failed = true;
        System.err.println("insertThenDeleteRandomKeysTest iteration " + i + "/" + numKeys
            + "\nExpected " + key + " not to be member, but it was!");
      }

      assert (!failed);
    }
  }

  static void growingRankTest(RankSelectPredecessorUpdate testSet) {
    testSet = generateAndInsertKeys(testSet);

    for (int i = 0; i < orderedKeyList.size(); i++) {
      boolean failed = false;
      final long res = testSet.rank(orderedKeyList.get(i));

      if (i != res) {
        failed = true;
        System.err.println("selectOfRankTest iteration " + (i + 1) + "/" + numKeys
            + "\nExpected rank of keys in sorted order"
            + "to be monotone increasing function but it wasn't!");
      }
      assert (!failed);
    }
  }

  static void selectOfRankTest(RankSelectPredecessorUpdate testSet) {

    testSet = generateAndInsertKeys(testSet);

    long i = 0;
    for (final Long key : keySet) {
      i++;
      boolean failed = false;
      final long res = testSet.select(testSet.rank(key));

      if (key != res) {
        failed = true;
        System.err.println("selectOfRankTest iteration " + i + "/" + numKeys
            + "\nExpected select(rank(" + key + ")) == " + key + " but it was " + res + "!");
      }
      assert (!failed);
    }
  }

  static void rankOfSelectTest(RankSelectPredecessorUpdate testSet) {

    testSet = generateAndInsertKeys(testSet);

    for (long i = 0; i < numKeys; i++) {
      boolean failed = false;
      final long res = testSet.rank(testSet.select(i));

      if (i != res) {
        failed = true;
        System.err.println("rankOfSelectTest iteration " + i + "/" + numKeys
            + "\nExpected rank(select(" + i + ")) == " + i + " but it was " + res + "!");
      }
      
      assert (!failed);
    }
  }

  static void sizeTest(final RankSelectPredecessorUpdate testSet) {

    generateKeys();

    // add all keys to the set
    int keysInS = 0;
    for (final Long key : keySet) {
      testSet.insert(key);
      keysInS++;
      assertEquals(keysInS, testSet.size());
    }

    final ArrayList<Long> keyList = new ArrayList<>(keySet);

    long i = 1;

    // use generated keys to test the set
    while (keyList.size() > 0) {
      final long key = keyList.remove(rand.nextInt(keyList.size()));
      testSet.delete(key);
      keysInS--;
      boolean failed = false;
      if (keysInS != testSet.size()) {
        failed = true;
        System.err.println("sizeTest iteration " + i + "/" + numKeys
            + "\nExpected " + keysInS + " in the set but there were " + testSet.size());
      }
      assert (!failed);
      i++;
    }

  }

}