import DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch.NaiveDynamicFusionNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NaiveDynamicFusionNodeTest {

  static final long seed = 42;
  static final int passes = 100_000;
  static final int numKeys = 16;

  private NaiveDynamicFusionNode set;
  private static RankSelectPredecessorUpdateTest test;

  @BeforeAll
  static void generateTests() {
    test = new RankSelectPredecessorUpdateTest(seed, passes, numKeys);
  }

  @BeforeEach
  void setUp() {
    set = new NaiveDynamicFusionNode();
  }

  @AfterEach
  void tearDown() {
    set = null;
  }

  @AfterAll
  static void clear() {
    test = null;
  }

  @Test
  void insertAndMemberSmallTest() {
    test.insertAndMemberSmallTest(set);
  }

  @Test
  void smallCorrectnessTest() {
    test.smallCorrectnessTest(set);
  }

  @Test
  void insertThenMemberTest() {
    test.insertThenMemberTest(set);
  }

  @Test
  void insertThenDeleteRandomKeysTest() {
    test.insertThenDeleteRandomKeysTest(set);
  }

  @Test
  void insertThenDeleteRangeOfKeysTest() {
    test.insertThenDeleteRangeOfKeysTest(set);
  }

  @Test
  void growingRankTest() {
    test.growingRankTest(set);
  }

  @Test
  void selectOfRankTest() {
    test.selectOfRankTest(set);
  }

  @Test
  void rankOfSelectTest() {
    test.rankOfSelectTest(set);
  }

  @Test
  void sizeTest() { // VERY SLOW
    test.sizeTest(set);
  }
}