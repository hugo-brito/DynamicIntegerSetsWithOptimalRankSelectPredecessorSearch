import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTrieTest {

  BinarySearchTrie t;

  @BeforeEach
  void setUp() {
    t = new BinarySearchTrie();
  }

  @AfterEach
  void tearDown() {
    t = null;
  }

  @Test
  void insertAndMemberTest() {
    RankSelectPredecessorUpdateTest.insertAndMemberTest(t);
  }

  @Test
  void allRange() {
    RankSelectPredecessorUpdateTest.allRange(t);
  }

  @Test
  void randomKeys() {
    RankSelectPredecessorUpdateTest.randomKeys(t);
  }

  @Test
  void rankTest() {
    RankSelectPredecessorUpdateTest.rankTest(t);
  }

  @Test
  void sizeTest() {

    long seed = 42;
    int numKeys = 1_000_000;

    Random rand = new Random(seed);
    HashSet<Long> keySet = new HashSet<>();
    while (keySet.size() < numKeys) {
      keySet.add(rand.nextLong());
    }

    // add all keys to the set
    int keysInTheTree = 0;
    for (Long key: keySet) {
      t.insert(key);
      keysInTheTree++;
      assertEquals(keysInTheTree, t.size());
    }

    ArrayList<Long> keyList = new ArrayList<>(keySet);

    // use generated keys to test the set
    while (keyList.size() > 0) {
      long key = keyList.remove(rand.nextInt(keyList.size()));
      t.delete(key);
      keysInTheTree--;
      assertEquals(keysInTheTree, t.size());
    }

  }
}