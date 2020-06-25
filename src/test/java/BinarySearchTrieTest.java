import DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch.BinarySearchTrie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
  void insertAndMemberSmallTest() {
    RankSelectPredecessorUpdateTest.insertAndMemberSmallTest(t);
  }

  @Test
  void insertThenDeleteRangeOfKeysTest() {
    RankSelectPredecessorUpdateTest.insertThenDeleteRangeOfKeysTest(t);
  }

  @Test
  void insertThenDeleteRandomKeysTest() {
    RankSelectPredecessorUpdateTest.insertThenDeleteRandomKeysTest(t);
  }

  @Test
  void growingRankTest() {
    RankSelectPredecessorUpdateTest.growingRankTest(t);
  }

  @Test
  void sizeTest() {
    RankSelectPredecessorUpdateTest.sizeTest(t);
  }

  @Test
  void rankSelectTest() {
    RankSelectPredecessorUpdateTest.rankSelect(t);
  }

  @Test
  void selectRankTest() {
    RankSelectPredecessorUpdateTest.selectRank(t);
  }
}