import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class RankSelectPredecessorUpdateTest {

  static long seed = 42;
  static int numKeys = 5_000_000;
  static Random rand;
  static Set<Long> keySet;

  static void generateKeys() {
    rand = new Random(seed);
    keySet = new HashSet<>();
    while (keySet.size() < numKeys) {
      keySet.add(rand.nextLong());
    }
  }

  static void insertAndMemberTest(final RankSelectPredecessorUpdate S) {
    S.insert(6917529027641081855L);
    S.insert(4611686018427387903L);
    assert (!S.member(10));
    assert (S.member(6917529027641081855L));
    assert (S.member(4611686018427387903L));
    assert (!S.member(Long.MAX_VALUE));
    S.insert(Long.MAX_VALUE);
    assert (S.member(Long.MAX_VALUE));
    S.delete(Long.MAX_VALUE);
  }

  static void allRange(final RankSelectPredecessorUpdate S) {
    for (long i = Long.MIN_VALUE; i <= Long.MAX_VALUE; i++) {
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

  static void randomKeys(final RankSelectPredecessorUpdate S) {

    generateKeys();

    // add all keys to the set
    for (final Long key : keySet) {
      S.insert(key);
    }

    // put the generated keys
    final ArrayList<Long> keyList = new ArrayList<>(keySet);

    // use generated keys to test the set
    while (keyList.size() > 0) {
      final long key = keyList.remove(rand.nextInt(keyList.size()));
      assert (S.member(key));
      S.delete(key);
      assert (!S.member(key));
    }
  }

  static void rankTest(final RankSelectPredecessorUpdate S) {
    // for (long i = 0; i < 500_000; i++) {
    // S.insert(i);
    // assertEquals(i, S.rank(i));
    // }

    generateKeys();

    // add all keys to the set
    for (final Long key : keySet) {
      S.insert(key);
    }

    // put the generated keys
    final ArrayList<Long> keyList = new ArrayList<>(keySet);

    // because the msb of a negative number is 1.
    Collections.sort(keyList, new Comparator<Long>() {
      @Override
      public int compare(final Long o1, final Long o2) {
        return Long.compareUnsigned(o1, o2);
      }
    });

    for (int i = 0; i < keyList.size(); i++) {
      // System.err.println(i + "");
      assertEquals(i, S.rank(keyList.get(i)));
    }
  }
}