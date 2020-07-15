import DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch.DynamicFusionNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamicFusionNodeTest {

  DynamicFusionNode set;

  @BeforeEach
  void setUp() {
    set = new DynamicFusionNode();
  }

  @AfterEach
  void tearDown() {
    set = null;
  }

  @Test
  void insertAndMemberSmallTest() {
    RankSelectPredecessorUpdateTest.insertAndMemberSmallTest(set);
  }

  @Test
  void insertThenMemberTest() {
    RankSelectPredecessorUpdateTest.insertThenMemberTest(set);
  }

  @Test
  void smallCorrectnessTest() {
    RankSelectPredecessorUpdateTest.smallCorrectnessTest(set);
  }

  @Test
  void insertThenDeleteRangeOfKeysTest() {
    RankSelectPredecessorUpdateTest.insertThenDeleteRangeOfKeysTest(set);
  }

  @Test
  void insertThenDeleteRandomKeysTest() {
    RankSelectPredecessorUpdateTest.insertThenDeleteRandomKeysTest(set);
  }

  @Test
  void growingRankTest() {
    RankSelectPredecessorUpdateTest.growingRankTest(set);
  }

  @Test
  void sizeTest() {
    RankSelectPredecessorUpdateTest.sizeTest(set);
  }

  @Test
  void selectOfRankTest() {
    RankSelectPredecessorUpdateTest.selectOfRankTest(set);
  }

  @Test
  void rankOfSelectTest() {
    RankSelectPredecessorUpdateTest.rankOfSelectTest(set);
  }
}