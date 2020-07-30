import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import integersets.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;


class UtilTest {

  int intLowerBound = -100_000_000;
  int intUpperBound = 100_000_000;
  long longLowerBound = -10_000_000L + Integer.MIN_VALUE;
  long longUpperBound = 10_000_000L + Integer.MAX_VALUE;
  int passes = 10_000;
  long seed = 42;
  int loB = 3;
  int hiB = 10;

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
  void msbConstant64() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbConstant(i),
          "Failed for " + i);
    }
    assertEquals(-1, Util.msbConstant(0));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbConstant(i),
          "Failed for " + i);
    }
  }

  @Test
  void msbConstant32() {
    for (int i = intLowerBound; i < 0; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msbConstant(i),
          "Failed for " + i);
    }
    assertEquals(-1, Util.msbConstant(0));
    for (int i = 1; i <= intUpperBound; i++) {
      assertEquals(Integer.numberOfLeadingZeros(i), Util.msbConstant(i),
          "Failed for " + i);
    }
  }


  @Test
  void msbConstantCommented() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbConstantCommented(i),
          "Failed for " + i);
    }
    assertEquals(-1, Util.msbConstantCommented(0));
    for (long i = 1; i <= longUpperBound; i = i + 2) {
      assertEquals(Long.numberOfLeadingZeros(i), Util.msbConstantCommented(i),
          "Failed for " + i);
    }
  }

  @Test
  void splitMerge() {
    for (long i = -100_000_000_000L; i <= 100_000_000_000L; i++) {
      assertEquals(i, Util.mergeInts(Util.splitLong(i)));
    }
  }

  @Test
  void rankLemma1Test() {
    Random random = new Random(seed);
    for (int p = 0; p < passes; p++) {
      for (int b = loB; b < hiB + 1; b++) { // we vary the number of bits per key
        // generate m distinct keys of b size + 1 key
        int m = (int) Math.min(Math.pow(2, b), Long.SIZE / b);
        Random passRand = new Random(random.nextLong());
        
        Set<Integer> keys = new HashSet<>();

        while (keys.size() < m) {
          keys.add(passRand.nextInt((int) Math.pow(2, b)));
        }

        int x = passRand.nextInt((int) Math.pow(2, b));

        // put them in an array or list
        ArrayList<Integer> keyList = new ArrayList<>(keys);
        keyList.sort(new Comparator<Integer>() {
          @Override
          public int compare(Integer o1, Integer o2) {
            if (o1 > o2) {
              return -1;
            } else if (o1 == o2) {
              return 0;
            }
            return 1;
          }
        });

        for (int i = 0; i < keyList.size() - 1; i++) {
          assertTrue(keyList.get(i) > keyList.get(i + 1),
              "Pass " + (p + 1) + "/" + passes
              + " | List of keys is not sorted in descending order!\nList: " + keyList.toString());
        }

        // produce A and x from such array
        long A = 0L;
        int rankX = 0;
        boolean foundRankX = false;
        for (int i = 0; i < keyList.size(); i++) {
          A <<= b;
          A |= keyList.get(i);
          if (!foundRankX && keyList.get(i) < x) {
            foundRankX = true;
            rankX = keyList.size() - i;
          }
        }

        // use the function from Util to test it.
        assertEquals(rankX, Util.rank_lemma_1_2(x, A, m, b), "Pass " + (p + 1) + "/" + passes
            + "\nA = " + Util.bin(A, b) + "\nx = " + Util.bin(x, b));
      }
    }
  }

  @Test
  void rankLemma1_3Test() {
    Random random = new Random(seed);
    Set<Long> seeds = new HashSet<>();
    while (seeds.size() < passes) {
      seeds.add(random.nextLong());
    }

    ArrayList<Long> seedList = new ArrayList<>(seeds);
    for (int p = 0; p < passes; p++) {
      for (int b = loB; b < hiB + 1; b++) { // we vary the number of bits per key
        // generate m distinct keys of b size + 1 key
        int m = (int) Math.min(Math.pow(2, b), Long.SIZE / b);
        Random passRand = new Random(seedList.get(0));
        
        Set<Integer> keys = new HashSet<>();

        while (keys.size() < m) {
          keys.add(passRand.nextInt((int) Math.pow(2, b)));
        }

        int x = passRand.nextInt((int) Math.pow(2, b));

        // put them in an array or list
        ArrayList<Integer> keyList = new ArrayList<>(keys);
        keyList.sort(new Comparator<Integer>() {
          @Override
          public int compare(Integer o1, Integer o2) {
            if (o1 > o2) {
              return -1;
            } else if (o1 == o2) {
              return 0;
            }
            return 1;
          }
        });

        for (int i = 0; i < keyList.size() - 1; i++) {
          assertTrue(keyList.get(i) > keyList.get(i + 1),
              "Pass " + (p + 1) + "/" + passes
              + " | List of keys is not sorted in descending order!\nList: " + keyList.toString());
        }

        // produce A and x from such array
        long A = 0L;
        int rankX = 0;
        boolean foundRankX = false;
        for (int i = 0; i < keyList.size(); i++) {
          A <<= b;
          A |= keyList.get(i);
          if (!foundRankX && keyList.get(i) < x) {
            foundRankX = true;
            rankX = keyList.size() - i;
          }
        }

        // use the function from Util to test it.
        assertEquals(rankX, Util.rank_lemma_1_2_3(x, A, m, b), "Pass " + (p + 1) + "/" + passes
            + "\nA = " + Util.bin(A, b) + "\nx = " + Util.bin(x, b));
      }
    }
  }
}