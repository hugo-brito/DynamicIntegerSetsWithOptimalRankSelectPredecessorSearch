import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import integersets.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class UtilTest {

  static int passes = 1_000_000;
  static long seed = 42;
  static List<Long> seedList;

  int intLowerBound = -100_000_000;
  int intUpperBound = 100_000_000;
  long longLowerBound = -10_000_000L + Integer.MIN_VALUE;
  long longUpperBound = 10_000_000L + Integer.MAX_VALUE;
  int loB = 3;
  int hiB = 10;

  @BeforeAll
  static void generateSeeds() {
    Random random = new Random(seed);
    Set<Long> seeds = new HashSet<>();
    while (seeds.size() < passes) {
      seeds.add(random.nextLong());
    }

    seedList = new ArrayList<>(seeds);
  }

  @Test
  void msb32Obvious() {
    for (int i = intLowerBound; i < 0; i++) {
      assertEquals(Integer.SIZE - 1 - Integer.numberOfLeadingZeros(i), Util.msbObvious(i));
    }
    assertEquals(-1, Util.msbObvious(0));
    for (int i = 1; i <= intUpperBound; i++) {
      assertEquals(Integer.SIZE - 1 - Integer.numberOfLeadingZeros(i), Util.msbObvious(i));
    }
  }

  @Test
  void msb64Obvious() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.SIZE - 1 - Long.numberOfLeadingZeros(i), Util.msbObvious(i));
    }
    assertEquals(-1, Util.msbObvious(0));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.SIZE - 1 - Long.numberOfLeadingZeros(i), Util.msbObvious(i));
    }
  }

  @Test
  void msb64LookupDistributedOutput() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.SIZE - 1 - Long.numberOfLeadingZeros(i),
          Util.msbLookupDistributedOutput(i));
    }
    assertEquals(-1, Util.msbLookupDistributedOutput(0L));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.SIZE - 1 - Long.numberOfLeadingZeros(i),
          Util.msbLookupDistributedOutput(i));
    }
  }

  @Test
  void msb64LookupDistributedInput() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.SIZE - 1 - Long.numberOfLeadingZeros(i), Util.msbLookupDistributedInput(i));
    }
    assertEquals(-1, Util.msbLookupDistributedInput(0L));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.SIZE - 1 - Long.numberOfLeadingZeros(i), Util.msbLookupDistributedInput(i));
    }
  }

  @Test
  void msb32LookupDistributedOutput() {
    for (int i = Integer.MIN_VALUE; i < 0; i++) {
      assertEquals(Integer.SIZE - 1 - Integer.numberOfLeadingZeros(i),
          Util.msbLookupDistributedOutput(i));
    }
    assertEquals(-1, Util.msbLookupDistributedOutput(0));
    for (int i = 1; i <= Integer.MAX_VALUE - 1; i++) {
      assertEquals(Integer.SIZE - 1 - Integer.numberOfLeadingZeros(i),
          Util.msbLookupDistributedOutput(i));
    }
  }

  @Test
  void msb32LookupDistributedInput() {
    for (int i = Integer.MIN_VALUE; i < 0; i++) {
      assertEquals(Integer.SIZE - 1 - Integer.numberOfLeadingZeros(i),
          Util.msbLookupDistributedInput(i));
    }
    assertEquals(-1, Util.msbLookupDistributedInput(0));
    for (int i = 1; i <= Integer.MAX_VALUE - 1; i++) {
      assertEquals(Integer.SIZE - 1 - Integer.numberOfLeadingZeros(i),
          Util.msbLookupDistributedInput(i));
    }
  }

  @Test
  void msb64Constant() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.SIZE - 1 - Long.numberOfLeadingZeros(i), Util.msbConstant(i),
          "Failed for " + i);
    }
    assertEquals(-1, Util.msbConstant(0));
    for (long i = 1; i <= longUpperBound; i++) {
      assertEquals(Long.SIZE - 1 - Long.numberOfLeadingZeros(i), Util.msbConstant(i),
          "Failed for " + i);
    }
  }

  @Test
  void msb32Constant() {
    for (int i = intLowerBound; i < 0; i++) {
      assertEquals(Integer.SIZE - 1 - Integer.numberOfLeadingZeros(i), Util.msbConstant(i),
          "Failed for " + i);
    }
    assertEquals(-1, Util.msbConstant(0));
    for (int i = 1; i <= intUpperBound; i++) {
      assertEquals(Integer.SIZE - 1 - Integer.numberOfLeadingZeros(i), Util.msbConstant(i),
          "Failed for " + i);
    }
  }


  @Test
  void msb64ConstantCommented() {
    for (long i = longLowerBound; i < 0; i++) {
      assertEquals(Long.SIZE - 1 - Long.numberOfLeadingZeros(i), Util.msbConstantCommented(i),
          "Failed for " + i);
    }
    assertEquals(-1, Util.msbConstantCommented(0));
    for (long i = 1; i <= longUpperBound; i = i + 2) {
      assertEquals(Long.SIZE - 1 - Long.numberOfLeadingZeros(i), Util.msbConstantCommented(i),
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
  void setAndGetField32() {
    for (int p = 0; p < passes; p++) {
      Random passRand = new Random(seedList.get(p));
      for (int f = 1; f < Integer.SIZE; f++) {
        int m = Integer.SIZE / f;
        int[] fields = new int[m];
        int A = 0;
        for (int i = 0; i < m; i++) {
          int field = passRand.nextInt((1 << f) - 1);
          fields[i] = field;
          assertEquals(0, (int) Util.getField(A, i, f), "Field " + i + " was not empty.");
          A = Util.setField(A, i, field, f);
          assertEquals(field, (int) Util.getField(A, i, f),
              "Field " + i + " returned wrong value.");
        }
        for (int i = 0; i < m; i++) {
          assertEquals(fields[i], (int) Util.getField(A, i, f),
              "Field " + i + " returned wrong value.");
        }
      }
    }
  }

  @Test
  void setAndGetField64() {
    for (int p = 0; p < passes; p++) {
      Random passRand = new Random(seedList.get(p));
      for (int f = 1; f < Integer.SIZE; f++) {
        int m = Long.SIZE / f;
        int[] fields = new int[m];
        long A = 0;
        for (int i = 0; i < m; i++) {
          int field = passRand.nextInt((1 << f) - 1);
          fields[i] = field;
          assertEquals(0, (long) Util.getField(A, i, f), "Field " + i + " was not empty.");
          A = Util.setField(A, i, field, f);
          assertEquals(field, (long) Util.getField(A, i, f),
              "Field " + i + " returned wrong value.");
        }
        for (int i = 0; i < m; i++) {
          assertEquals((long) fields[i], (long) Util.getField(A, i, f),
              "Field " + i + " returned wrong value.");
        }
      }
    }
  }

  @Test
  void binTest32() {
    for (int p = 0; p < passes; p++) {
      Random passRand = new Random(seedList.get(p));
      int key = passRand.nextInt();
      for (int i = 0; i < Integer.SIZE; i++) {
        String bin = Util.bin(key, i).replace("0b", "").replace("_", "");
        assertEquals(key, Integer.parseUnsignedInt(bin, 2));
      }
    }
  }

  @Test
  void binTest64() {
    for (int p = 0; p < passes; p++) {
      Random passRand = new Random(seedList.get(p));
      long key = passRand.nextLong();
      for (int i = 0; i < Long.SIZE; i++) {
        String bin = Util.bin(key, i).replace("0b", "").replace("_", "").replace("l", "");
        assertEquals(key, Long.parseUnsignedLong(bin, 2));
      }
    }
  }

  @Test
  void rankLemma1Commented() {
    for (int p = 0; p < passes; p++) {
      for (int b = loB; b < hiB + 1; b++) { // we vary the number of bits per key
        // generate m distinct keys of b size + 1 key
        int m = (int) Math.min(Math.pow(2, b), Long.SIZE / b);
        Random passRand = new Random(seedList.get(p));
        
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
        assertEquals(rankX, Util.rankLemma1Commented(x, A, m, b), "Pass " + (p + 1) + "/" + passes
            + " | Seed: " + seedList.get(p) + "\nA = " + Util.bin(A, b)
            + "\nx = " + Util.bin(x, b));
      }
    }
  }

  @Test
  void rankLemma1() {
    for (int p = 0; p < passes; p++) {
      for (int b = loB; b < hiB + 1; b++) { // we vary the number of bits per key
        // generate m distinct keys of b size + 1 key
        int m = (int) Math.min(Math.pow(2, b), Long.SIZE / b);
        Random passRand = new Random(seedList.get(p));
        
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
        assertEquals(rankX, Util.rankLemma1(x, A, m, b), "Pass " + (p + 1) + "/" + passes
            + " | Seed: " + seedList.get(p) + "\nA = " + Util.bin(A, b)
            + "\nx = " + Util.bin(x, b));
      }
    }
  }
}