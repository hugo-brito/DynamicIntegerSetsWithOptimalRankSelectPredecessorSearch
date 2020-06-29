import static org.junit.jupiter.api.Assertions.*;

import DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch.MSB;
import org.junit.jupiter.api.Test;

class MSBTest {

  int intLowerBound = -100_000_000;
  int intUpperBound = 100_000_000;
  long longLowerBound = -10_000_000L + Integer.MIN_VALUE;
  long longUpperBound = 10_000_000L + Integer.MAX_VALUE;

  @Test
  void msb32Obvious() {
    for (int i = intLowerBound; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), MSB.msb32Obvious(i));
    }
    assertEquals(-1, MSB.msb32Obvious(0));
    for (int i = 1; i <= intUpperBound; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), MSB.msb32Obvious(i));
    }
  }

  @Test
  void msb64Obvious() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), MSB.msb64Obvious(i));
    }
    assertEquals(-1, MSB.msb64Obvious(0));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), MSB.msb64Obvious(i));
    }
  }

  @Test
  void msb32double() {
    for (int i = intLowerBound; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), MSB.msb32double(i));
    }
    assertEquals(-1, MSB.msb32double(0));
    for (int i = 1; i <= intUpperBound; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), MSB.msb32double(i));
    }
  }

  @Test
  void msb64LookupDistributedOutput() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), MSB.msb64LookupDistributedOutput(i));
    }
    // assertEquals(-1, MSB.msb64LookupDistributedOutput(0));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), MSB.msb64LookupDistributedOutput(i));
    }
  }

  @Test
  void msb64LookupDistributedInput() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), MSB.msb64LookupDistributedInput(i));
    }
    assertEquals(-1, MSB.msb64LookupDistributedInput(0));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), MSB.msb64LookupDistributedInput(i));
    }
  }

  @Test
  void msb32LookupDistributedOutput() {
    for (int i = Integer.MIN_VALUE; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), MSB.msb32LookupDistributedOutput(i));
    }
    assertEquals(-1, MSB.msb32LookupDistributedOutput(0));
    for (int i = 1; i <= Integer.MAX_VALUE - 1; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), MSB.msb32LookupDistributedOutput(i));
    }
  }

  @Test
  void msb32LookupDistributedInput() {
    for (int i = Integer.MIN_VALUE; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), MSB.msb32LookupDistributedInput(i));
    }
    assertEquals(-1, MSB.msb32LookupDistributedInput(0));
    for (int i = 1; i <= Integer.MAX_VALUE - 1; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), MSB.msb32LookupDistributedInput(i));
    }
  }

  @Test
  void splitMerge() {
    for (long i = -100_000_000_000L; i <= 100_000_000_000L; i++) {
      assertEquals(i, MSB.mergeInts(MSB.splitLong(i)));
    }
  }
}