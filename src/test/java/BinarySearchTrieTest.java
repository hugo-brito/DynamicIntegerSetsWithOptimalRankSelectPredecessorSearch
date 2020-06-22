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
  void RandomKeys() {
    RankSelectPredecessorUpdateTest.RandomKeys(t);
  }
}