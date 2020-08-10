import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import integersets.RankSelectPredecessorUpdate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

class RankSelectPredecessorUpdateTest {
  
  private final long seed;
  private final int passes;
  private final List<Long> seeds;
  private final int numKeys;
  
  private ArrayList<TreeSet<Long>> keySetList;

  public RankSelectPredecessorUpdateTest(final long seed, final int passes, final int numKeys) {
    this.seed = seed;
    this.passes = passes;
    this.numKeys = numKeys;
    seeds = new LinkedList<>();

    generateSeeds();
    generateKeys();

  }

  /**
   * Helper method.
   * It is to be called by the constructor to populate the {@code seedSet}.
   * Each seed corresponds to a pass of the test.
   */
  private void generateSeeds() {
    final Random rand = new Random(seed);
    final Set<Long> seedSet = new HashSet<>();

    while (seedSet.size() < passes) {
      seedSet.add(rand.nextLong());
    }

    seeds.addAll(seedSet);
  }

  /**
   * Helper method.
   * It is to be called by the constructor to populate the key sets and store them in
   * {@code keySetList}.
   */
  private void generateKeys() {
    keySetList = new ArrayList<>();

    int pass = 0;
    for (final long seed : seeds) {
      final Random random = new Random(seed);
      final TreeSet<Long> keySet = new TreeSet<Long>(new Comparator<Long>() {
        @Override
        public int compare(final Long o1, final Long o2) {
          return Long.compareUnsigned(o1, o2);
        }
      });

      while (keySet.size() < numKeys) {
        keySet.add(random.nextLong());
      }

      keySetList.add(pass, keySet);
      pass++;
    }
  }

  /**
   * Helper method.
   * Takes a concrete implementation of a {@code RankSelectPredecessorUpdate} and inserts all the
   * keys corresponding to that {@code pass} in the set.
   * 
   * @param testSet the set to insert the keys
   * @param pass the corresponding pass
   */
  private void insertAllKeys(final RankSelectPredecessorUpdate testSet, final int pass) {

    for (final long key : keySetList.get(pass)) {
      testSet.insert(key);
    }

  }

  /**
   * Small test to attest correctness of the insert method.
   * Inserts some keys in {@code testSet} and then calls {@code member} on the inserted keys.
   * 
   * @param testSet the data structure to be tested
   */
  void insertAndMemberSmallTest(final RankSelectPredecessorUpdate testSet) {

    testSet.insert(6917529027641081855L);
    testSet.insert(4611686018427387903L);

    assertFalse(testSet.member(10), "Expected member(10) == false\n");

    assertTrue(testSet.member(6917529027641081855L),
        "Expected member(6917529027641081855L) == true\n");
    
    assertTrue(testSet.member(4611686018427387903L),
        "Expected member(4611686018427387903L) == true\n");

    assertFalse(testSet.member(Long.MAX_VALUE),
        "Expected member(" + Long.MAX_VALUE + ") == false\n");

    testSet.insert(Long.MAX_VALUE);

    assertTrue(testSet.member(Long.MAX_VALUE), "Expected member(" + Long.MAX_VALUE + ") == true\n");
  }

  /**
   * Small correctness test with a defined small set of keys and known answers.
   * 
   * @param testSet the data structure to be tested
   */
  void smallCorrectnessTest(final RankSelectPredecessorUpdate testSet) {
    testSet.insert(10);
    testSet.insert(12);
    testSet.insert(42);
    testSet.insert(-1337);
    if (numKeys > 4) {
      testSet.insert(-42);
      assertEquals(5, testSet.size(), "Expected size() == 5");
    } else {
      assertEquals(4, testSet.size(), "Expected size() == 4");
    }


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
    if (numKeys > 4) {
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

    } else {
      {
        // Testing key -1000
        final long key = -1000;
        final boolean member = false;
        final long rank = 4;
        final Long predecessor = -1337L;
        final Long successor = null;
  
        smallCorrectnessTest(testSet, key, member, rank, predecessor, successor);
      }

      {
        // Testing key -1
        final long key = -1;
        final boolean member = false;
        final long rank = 4;
        final Long predecessor = -1337L;
        final Long successor = null;

        smallCorrectnessTest(testSet, key, member, rank, predecessor, successor);
      }
    }
  }

  private void smallCorrectnessTest(final RankSelectPredecessorUpdate testSet, final long key,
      final boolean member, final long rank, final Long predecessor, final Long successor) {

    // Member
    assertEquals(member, testSet.member(key),
        "Key " + key + " | Test 1/5: expected member(" + key + ") == " + member + "\n");

    // Rank
    assertEquals(rank, testSet.rank(key),
        "Key " + key + " | Test 2/5: expected rank(" + key + ") == " + rank + "\n");

    // Select
    assertEquals(successor, testSet.select(rank),
        "Key " + key + " | Test 3/5: expected select(" + rank + ") == " + successor + "\n");

    // Predecessor
    assertEquals(predecessor, testSet.predecessor(key),
        "Key " + key + " | Test 4/5: expected predecessor(" + key + ") == " + predecessor + "\n");

    // Successor
    assertEquals(successor, testSet.successor(key),
        "Key " + key + " | Test 5/5: expected successor(" + key + ") == " + successor + "\n");

  }

  /**
   * 1. Insert all the pseudorandomly-generated keys in {@code testSet}.
   * 2. Iterate in random order through all the keys and assert that {@code member(key)} is
   * {@code true}.
   * This test is executed in passes.
   * 
   * @param testSet the data structure to be tested
   */
  void insertThenMemberTest(final RankSelectPredecessorUpdate testSet) {

    for (int p = 0; p < passes; p++) {

      insertAllKeys(testSet, p);
      final Random rand = new Random();
      final List<Long> keys = new ArrayList<>(keySetList.get(p));

      long i = 0;
      while (keys.size() > 0) {
        i++;
        final long key = keys.remove(rand.nextInt(keys.size()));
        assertTrue(testSet.member(key),
            "Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys);
      }

      testSet.reset();
    }
  }

  /**
   * 1. The test iterates through all the range and each iteration consists of:
   *    - Asserting that the key {@code i} is not in the set
   *    - Inserting {@code i} in the set
   *    - Asserting that the key {@code i} is in the set
   * 2. Each time the number of keys in the set reaches 10 % of the range, all the keys are removed.
   * 
   * @param testSet the data structure to be tested
   */
  void insertThenDeleteRangeOfKeysTest(final RankSelectPredecessorUpdate testSet) {

    for (long i = 0; i < numKeys; i++) {
      assertFalse(testSet.member(i),
          "Iteration " + (i + 1) + "/" + numKeys + " (before insertion)\n");

      testSet.insert(i);

      assertTrue(testSet.member(i), "Iteration " + (i + 1) + "/" + numKeys
          + " (after insertion)\n");

      if (numKeys > 9 && (i % (numKeys / 10)) == 0) {

        testSet.reset();
        assertTrue(testSet.isEmpty(),
            "Iteration " + (i + 1) + "/" + numKeys
            + " | Expected set to be empty after reseting it.\n");
      }
    }  
  }

  /**
   * 1. Insert all the pseudoramdomly-generated keys in {@code testSet}.
   * 2. Iterate in random order through all the keys and:
   *    - Assert that {@code member(key)} is {@code true}.
   *    - Remove the {@code key}
   *    - Assert that {@code member(key)} is {@code false}.
   * This test is executed in passes.
   * 
   * @param testSet the data structure to be tested
   */
  void insertThenDeleteRandomKeysTest(final RankSelectPredecessorUpdate testSet) {

    for (int p = 0; p < passes; p++) {

      insertAllKeys(testSet, p);
      final Random rand = new Random();
      final List<Long> keys = new ArrayList<>(keySetList.get(p));

      long i = 0;
      while (keys.size() > 0) {
        i++;
        final long key = keys.remove(rand.nextInt(keys.size()));

        assertTrue(testSet.member(key),
            "Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys
            + ": expected member(" + key + ") == true\n");
        
        testSet.delete(key);
        
        assertFalse(testSet.member(key),
            "Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys
            + ": expected member(" + key + ") == false\n");
      }
      assertTrue(testSet.isEmpty(),
          "Pass " + (p + 1) + "/" + passes + " | Seed: " + seeds.get(p)
          + "\nDeleting all keys, expected set to be empty!\n");
    }
  }

  /**
   * Asserts that for all keys in the set, their rank is a monotone increasing function.
   * This test is executed in passes.
   * 
   * @param testSet the data structure to be tested
   */
  void growingRankTest(final RankSelectPredecessorUpdate testSet) {

    for (int p = 0; p < passes; p++) {

      insertAllKeys(testSet, p);

      long i = 0;
      for (final long key : keySetList.get(p)) {
        assertEquals(i, testSet.rank(key),
            "Pass " + (p + 1) + "/" + passes + " | Iteration " + (i + 1) + "/" + numKeys
            + ": expected rank of keys in sorted order to be monotone increasing function.\n");
        i++;
      }

      testSet.reset();
    }
  }

  void deleteTest(final RankSelectPredecessorUpdate testSet) {

    for (int p = 0; p < passes; p++) {

      assertEquals(0, testSet.size());
      insertAllKeys(testSet, p);
      assertEquals(numKeys, testSet.size());
      final Random rand = new Random(seeds.get(p));
      final Set<Long> keys = new HashSet<>(keySetList.get(p));

      long i = 0;
      while (keys.size() > 0) {
        i++;
        long key = rand.nextLong();

        testSet.delete(key);

        StringBuilder errorMessage = new StringBuilder("Pass ").append((p + 1)).append("/")
            .append("" + passes).append(" | Iteration ").append("" + i).append(" | Seed: ")
            .append("" + seeds.get(p)).append(" | Key deleted: ").append("" + key).append("\n");

        StringBuilder member = new StringBuilder(errorMessage.toString())
              .append("After deletion, key should not be member!");

        if (keys.remove(key)) {
          errorMessage.append("Removing existing key returned wrong size().");
        } else {
          errorMessage.append("Removing non-existing key returned wrong size().");
        }

        assertFalse(testSet.member(key), member.toString());
        
        assertEquals(keys.size(), testSet.size(), errorMessage.toString());

      }
      assertTrue(testSet.isEmpty(),
          "Pass " + (p + 1) + "/" + passes + " | Seed: " + seeds.get(p)
          + "\nDeleting all keys, expected set to be empty!\n");
    }
  }

  /**
   * Inserts keys, one by one, to the {@code testSet}, evaluating the {@code size()} at every step.
   * This test is executed in passes.
   * 
   * @param testSet the data structure to be tested
   */
  void sizeTest(final RankSelectPredecessorUpdate testSet) {

    for (int p = 0; p < passes; p++) {

      // add, one by one, all keys to the set
      int keysInS = 0;
      for (final Long key : keySetList.get(p)) {
        testSet.insert(key);
        keysInS++;
        assertEquals(keysInS, testSet.size(),
            "Pass " + (p + 1) + "/" + passes + " | Iteration " + keysInS + "/" + (numKeys * 2)
            + " | Key inserted: " + key + " | Seed: " + seeds.get(p)
            + "\nAfter insertion, expected KeysInS == size()\n");
      }

      final ArrayList<Long> keyList = new ArrayList<>(keySetList.get(p));

      // use generated keys to test the set
      final Random rand = new Random(seeds.get(p));
      long i = keysInS;
      while (keyList.size() > 0) {
        i++;
        final long key = keyList.remove(rand.nextInt(keyList.size()));
        testSet.delete(key);
        keysInS--;

        assertEquals(keysInS, testSet.size(),
            "Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + (numKeys * 2)
            + " | Key deleted: " + key + " | Seed: " + seeds.get(p)
            + "\nAfter deletion, expected KeysInS == size()\n");
      }

      assertTrue(testSet.isEmpty(),
          "Pass " + (p + 1) + "/" + passes + " | Seed: " + seeds.get(p)
          + "\nDeleting all keys, expected set to be empty!\n");
    }
  }

  /**
   * Asserts that {@code select(rank(key)) == key} is {@code true} for all keys in the set.
   * This test is executed in passes.
   * 
   * @param testSet the data structure to be tested
   */
  void selectOfRankTest(final RankSelectPredecessorUpdate testSet) {

    for (int p = 0; p < passes; p++) {

      insertAllKeys(testSet, p);

      long i = 0;
      for (final Long key : keySetList.get(p)) {
        i++;
        assertEquals(key, testSet.select(testSet.rank(key)),
            "Pass " + (p + 1) + "/" + passes + " | Iteration " + i + "/" + numKeys + "\n");
      }

      testSet.reset();
    }
  }

  /**
   * Asserts that {@code rank(select(i)) == i} is {@code true} for every {@code i} ranging from 0 to
   * {@code numKeys} (exclusive).
   * This test is executed in passes.
   * 
   * @param testSet the data structure to be tested
   */
  void rankOfSelectTest(final RankSelectPredecessorUpdate testSet) {

    for (int p = 0; p < passes; p++) {
      insertAllKeys(testSet, p);

      for (long i = 0; i < numKeys; i++) {
        assertEquals(i, testSet.rank(testSet.select(i)),
            "Pass " + (p + 1) + "/" + passes + " | Iteration " + (i + 1) + "/" + numKeys + "\n");
      }

      testSet.reset();
    }
  }
}