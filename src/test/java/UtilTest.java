import static org.junit.jupiter.api.Assertions.assertEquals;

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
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msbObvious(i));
    }
    assertEquals(-1, Util.msbObvious(0));
    for (int i = 1; i <= intUpperBound; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msbObvious(i));
    }
  }

  @Test
  void msb64Obvious() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbObvious(i));
    }
    assertEquals(-1, Util.msbObvious(0));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbObvious(i));
    }
  }

  @Test
  void msb64LookupDistributedOutput() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbLookupDistributedOutput(i));
    }
    assertEquals(-1, Util.msbLookupDistributedOutput(0L));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbLookupDistributedOutput(i));
    }
  }

  @Test
  void msb64LookupDistributedInput() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbLookupDistributedInput(i));
    }
    assertEquals(-1, Util.msbLookupDistributedInput(0L));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbLookupDistributedInput(i));
    }
  }

  @Test
  void msb32LookupDistributedOutput() {
    for (int i = Integer.MIN_VALUE; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msbLookupDistributedOutput(i));
    }
    assertEquals(-1, Util.msbLookupDistributedOutput(0));
    for (int i = 1; i <= Integer.MAX_VALUE - 1; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msbLookupDistributedOutput(i));
    }
  }

  @Test
  void msb32LookupDistributedInput() {
    for (int i = Integer.MIN_VALUE; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msbLookupDistributedInput(i));
    }
    assertEquals(-1, Util.msbLookupDistributedInput(0));
    for (int i = 1; i <= Integer.MAX_VALUE - 1; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msbLookupDistributedInput(i));
    }
  }

  @Test
  void msb64Nelson() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbNelsonShort(i),
          "Failed for " + i);
    }
    assertEquals(-1, Util.msbObvious(0));
    for (long i = 1; i <= longUpperBound; i = i + 2) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbConstant(i),
          "Failed for " + i);
    }
  }

  @Test
  void splitMerge() {
    for (long i = -100_000_000_000L; i <= 100_000_000_000L; i++) {
      assertEquals(i, Util.mergeInts(Util.splitLong(i)));
    }
  }
}