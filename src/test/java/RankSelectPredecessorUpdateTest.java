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

  static long seed = 1337;
  static int numKeys = 1_000_000;
  static Random rand;
  static Set<Long> keySet;
  static List<Long> orderedKeyList;

  static RankSelectPredecessorUpdate generateAndInsertKeys(RankSelectPredecessorUpdate S) {
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
      S.insert(key);
    }

    return S;
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

  static void insertAndMemberSmallTest(final RankSelectPredecessorUpdate S) {
    S.insert(6917529027641081855L);
    S.insert(4611686018427387903L);
    assert (!S.member(10));
    assert (S.member(6917529027641081855L));
    assert (S.member(4611686018427387903L));
    assert (!S.member(Long.MAX_VALUE));
    S.insert(Long.MAX_VALUE);
    assert (S.member(Long.MAX_VALUE));
  }

  static void insertThenMemberTest(RankSelectPredecessorUpdate S) {
    S = generateAndInsertKeys(S);
    while (orderedKeyList.size() > 0) {
      final long key = orderedKeyList.remove(rand.nextInt(orderedKeyList.size()));
      boolean failed = !S.member(key);
      if (failed) {
        System.err.println("insertThenMemberTest iteration "
            + (numKeys - orderedKeyList.size()) + "/" + numKeys + "\n"
            + key + " should be member, but it's not.");
      }
      assert (!failed);
    }
  }

  static void insertThenDeleteRangeOfKeysTest(final RankSelectPredecessorUpdate S) {
    long lowerBound = numKeys * (-1);
    for (long i = lowerBound; i <= numKeys; i++) {
      assert (!S.member(i));
      S.insert(i);
      assert (S.member(i));
      if (i % 1000 == 0) {
        for (long j = i - 1000; j <= i; j++) {
          S.delete(j);
        }
      }
    }
  }

  static void insertThenDeleteRandomKeysTest(RankSelectPredecessorUpdate S) {
    S = generateAndInsertKeys(S);

    // use generated keys to test the set
    while (orderedKeyList.size() > 0) {
      final long key = orderedKeyList.remove(rand.nextInt(orderedKeyList.size()));
      assert (S.member(key));
      S.delete(key);
      assert (!S.member(key));
    }
  }

  static void growingRankTest(RankSelectPredecessorUpdate S) {
    S = generateAndInsertKeys(S);

    for (int i = 0; i < orderedKeyList.size(); i++) {
      assertEquals(i+1, S.rank(orderedKeyList.get(i)));
    }
  }

  static void rankSelect(RankSelectPredecessorUpdate S) {

    S = generateAndInsertKeys(S);

    long i = 1;
    for (Long key: keySet) {
      boolean failed = key != S.select(S.rank((key)));
      if (failed) {
        System.err.println("Iteration " + i + ": expected " + key + " but got " + S.select(S.rank(key)));
      }
      assert (!failed);
      i++;
    }
  }

  static void selectRank(RankSelectPredecessorUpdate S) {

    S = generateAndInsertKeys(S);

    for (long i = 1; i <= numKeys; i++) {
      assertEquals(i, S.rank(S.select(i)));
    }
  }

  static void sizeTest(RankSelectPredecessorUpdate S) {

    generateKeys();

    // add all keys to the set
    int keysInS = 0;
    for (Long key: keySet) {
      S.insert(key);
      keysInS++;
      assertEquals(keysInS, S.size());
    }

    ArrayList<Long> keyList = new ArrayList<>(keySet);

    // use generated keys to test the set
    while (keyList.size() > 0) {
      long key = keyList.remove(rand.nextInt(keyList.size()));
      S.delete(key);
      keysInS--;
      assertEquals(keysInS, S.size());
    }

  }

}