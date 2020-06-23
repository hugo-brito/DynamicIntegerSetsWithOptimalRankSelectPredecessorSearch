import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BitsKeyTest {

  long longLowerBound = -1_000_000L + Integer.MIN_VALUE; 
  long longUpperBound = 1_000_000L + Integer.MAX_VALUE;
  Random rand = new Random(42);

  @Test
  void bit() {
    for (int i = 0; i < 64; i++) {
      BitsKey v = new BitsKey(1L << i);
      for (int j = 0; j < i; j++) {
        assertEquals(0, v.bit(63 - j));
      }
      assertEquals(1, v.bit(63 - i));
      for (int j = i + 1; j < 64; j++) {
        assertEquals(0, v.bit(63 - j));
      }
    }
  }

  @Test
  void compareToNaive() {
    for (long i = 0; i <= longUpperBound; i++) {
      BitsKey v = new BitsKey(rand.nextLong());
      BitsKey w = new BitsKey(rand.nextLong());
      assertEquals(Long.compareUnsigned(v.val, w.val), v.compareToNaive(w));
    }
  }

  @Test
  void compareTo() {
    for (long i = 0; i <= longUpperBound; i++) {
      BitsKey v = new BitsKey(rand.nextLong());
      BitsKey w = new BitsKey(rand.nextLong());
      assertEquals(Long.compareUnsigned(v.val, w.val), v.compareTo(w));
    }
  }

  @Test
  void testEquals() {
    for (long i = 0; i <= 1_000_000_000L; i++) {
      long v = rand.nextLong();
      long w = rand.nextLong();
      if (v != w) {
        assertFalse(new BitsKey(v).equals(new BitsKey(w)));
      } else {
        assert (new BitsKey(v).equals(new BitsKey(w)));
      }
    }
  }

  @Test
  void bin() {
    for (long i = -10_000_000L; i <= 10_000_000L; i++) {
      assertEquals(String.format("%64s", Long.toBinaryString(i)).replace(' ', '0'),
      new BitsKey(i).bin());
    }
  }
}