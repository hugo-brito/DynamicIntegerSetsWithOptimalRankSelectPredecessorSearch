import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class RankSelectPredecessorUpdateTest {

  static long seed = 42;
  static int numKeys = 500_000;
  static Random rand;
  static Set<Long> keys;

  static void generateKeys() {
    rand = new Random(seed);
    HashSet<Long> keySet = new HashSet<>();
    while (keySet.size() < numKeys) {
      keySet.add(rand.nextLong());
    }
  }


  static void insertAndMemberTest(RankSelectPredecessorUpdate S) {
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

  static void allRange(RankSelectPredecessorUpdate S) {
    for (long i = Long.MIN_VALUE; i <= Long.MAX_VALUE; i++) {
      assert (!S.member(i));
      S.insert(i);
      assert (S.member(i));
      if (i % 1000 == 0) {
        for (long j = i-1000; j <= i; j++) {
          S.delete(j);
        }
      }
    }
  }



  static void randomKeys(RankSelectPredecessorUpdate S) {

    generateKeys();

    // add all keys to the set
    for (Long key: keys) {
      S.insert(key);
    }

    // put the generated keys
    ArrayList<Long> keyList = new ArrayList<>(keys);

    // use generated keys to test the set
    while (keyList.size() > 0) {
      long key = keyList.remove(rand.nextInt(keyList.size()));
      assert (S.member(key));
      S.delete(key);
      assert (!S.member(key));
    }
  }
}