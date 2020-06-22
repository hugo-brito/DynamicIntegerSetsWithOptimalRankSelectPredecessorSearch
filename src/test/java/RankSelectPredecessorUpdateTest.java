import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

class RankSelectPredecessorUpdateTest {

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
    for (long i = Long.MIN_VALUE; i == Long.MAX_VALUE; i++) {
      assert (!S.member(i));
      S.insert(i);
      assert (S.member(i));
    }
  }

  static void RandomKeys(RankSelectPredecessorUpdate S) {

    // generate 500_000 keys
    Random rand = new Random(42);
    HashSet<Long> keySet = new HashSet<>();
    while (keySet.size() < 500_000) {
      keySet.add(rand.nextLong());
    }

    // add all keys to the set
    for (Long key: keySet) {
      S.insert(key);
    }

    // put the generated keys
    ArrayList<Long> keyList = new ArrayList<>(keySet);

    // use generated keys to test the set
    while (keyList.size() > 0) {
      long key = keyList.remove(rand.nextInt(keyList.size()));
      assert (S.member(key));
      S.delete(key);
      assert (!S.member(key));
    }
  }
}