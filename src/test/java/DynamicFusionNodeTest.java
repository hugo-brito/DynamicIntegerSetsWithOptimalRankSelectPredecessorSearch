import integersets.DynamicFusionNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamicFusionNodeTest {

  static final long seed = 42;
  static final int passes = 100_000;
  static final int numKeys = 16;

  private DynamicFusionNode set;
  private static RankSelectPredecessorUpdateTest test;

  @BeforeAll
  static void generateTests() {
    test = new RankSelectPredecessorUpdateTest(seed, passes, numKeys);
  }

  @BeforeEach
  void setUp() {
    set = new DynamicFusionNode();
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
  void insertThenDeleteRangeOfKeysTest() {
    test.insertThenDeleteRangeOfKeysTest(set);
  }

  @Test
  void insertThenDeleteRandomKeysTest() {
    test.insertThenDeleteRandomKeysTest(set);
  }

  @Test
  void deleteTest() {
    test.deleteTest(set);
  }

  @Test
  void sizeTest() {
    test.sizeTest(set);
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
}