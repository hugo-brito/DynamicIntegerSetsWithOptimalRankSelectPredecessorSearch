import DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch.NonRecursivePatriciaTrie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NonRecursivePatriciaTrieTest {

  NonRecursivePatriciaTrie t;

  @BeforeEach
  void setUp() {
    t = new NonRecursivePatriciaTrie();
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
  void insertThenMemberTest() {
    RankSelectPredecessorUpdateTest.insertThenMemberTest(t);
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