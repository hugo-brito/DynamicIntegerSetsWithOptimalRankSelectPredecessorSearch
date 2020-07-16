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
  void smallCorrectnessTest() {
    RankSelectPredecessorUpdateTest.smallCorrectnessTest(set);
  }
}