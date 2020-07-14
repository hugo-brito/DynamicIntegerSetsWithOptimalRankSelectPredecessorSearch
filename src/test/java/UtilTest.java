import static org.junit.Assert.assertEquals;

import DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch.Util;
import org.junit.jupiter.api.Test;

class UtilTest {

  int intLowerBound = -100_000_000;
  int intUpperBound = 100_000_000;
  long longLowerBound = -10_000_000L + Integer.MIN_VALUE;
  long longUpperBound = 10_000_000L + Integer.MAX_VALUE;

  @Test
  void msb32Obvious() {
    for (int i = intLowerBound; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msb32Obvious(i));
    }
    assertEquals(-1, Util.msb32Obvious(0));
    for (int i = 1; i <= intUpperBound; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msb32Obvious(i));
    }
  }

  @Test
  void msb64Obvious() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msb64Obvious(i));
    }
    assertEquals(-1, Util.msb64Obvious(0));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msb64Obvious(i));
    }
  }

  @Test
  void msb32double() {
    for (int i = intLowerBound; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msb32double(i));
    }
    assertEquals(-1, Util.msb32double(0));
    for (int i = 1; i <= intUpperBound; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msb32double(i));
    }
  }

  @Test
  void msb64LookupDistributedOutput() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msb64LookupDistributedOutput(i));
    }
    // assertEquals(-1, Util.msb64LookupDistributedOutput(0));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msb64LookupDistributedOutput(i));
    }
  }

  @Test
  void msb64LookupDistributedInput() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msb64LookupDistributedInput(i));
    }
    assertEquals(-1, Util.msb64LookupDistributedInput(0));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msb64LookupDistributedInput(i));
    }
  }

  @Test
  void msb32LookupDistributedOutput() {
    for (int i = Integer.MIN_VALUE; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msb32LookupDistributedOutput(i));
    }
    assertEquals(-1, Util.msb32LookupDistributedOutput(0));
    for (int i = 1; i <= Integer.MAX_VALUE - 1; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msb32LookupDistributedOutput(i));
    }
  }

  @Test
  void msb32LookupDistributedInput() {
    for (int i = Integer.MIN_VALUE; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msb32LookupDistributedInput(i));
    }
    assertEquals(-1, Util.msb32LookupDistributedInput(0));
    for (int i = 1; i <= Integer.MAX_VALUE - 1; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msb32LookupDistributedInput(i));
    }
  }

  @Test
  void splitMerge() {
    for (long i = -100_000_000_000L; i <= 100_000_000_000L; i++) {
      assertEquals(i, Util.mergeInts(Util.splitLong(i)));
    }
  }
}