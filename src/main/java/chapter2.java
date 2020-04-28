class chapter2 {

	/**
	 * Often we view words as divided into ﬁelds of some length f. We then use x(i)_f to denote the ith ﬁeld,
	 * starting from the right with x(0)_f the right most ﬁeld. Thus x represents the integer E^(w−1)_(i=0) 2^i x(i)1.
	 * Note that ﬁelds can easily be masked out using regular instructions, e.g 
	 */

	static class FieldsOfWords {
		
		
	}

	static class MsbLsb {
	/**
	 * Finding the most and least significant bits in constant time
	 * 
	 * We have an operation msb(x) that for an integer x computes the index of its most significant
	 * set bit. Fredman and Willard [FW93] showed how to implement this in constant time using
	 * multiplication, but msb can also be implemented very efficiently by assigning x to a floating
	 * point number and extract the exponent. A theoretical advantage to using the conversion to
	 * floating point numbers is that we avoid the universal constants depending on w used in [FW93].
	 */
		public static int msbNaive(int x) {
			if (x == 0) return -1; // because 0 has no 1 bits
			String bin = bin(x);
			for (int i = 0; i < 32; i++) {
				// System.out.println(bin.charAt(i));
				if (bin.charAt(i) == '1') return i;
			}
			return -1;
		}

		public static int msb(int x) {
			if (x == 0) return -1; // because 0 has no 1 bits
			return Integer.numberOfLeadingZeros(x);
		}

		/**
		 * Using msb, we can also easily find the least significant bit of x as lsb(x) = msb((x − 1) ⊕ x).
		 * @param x
		 * @return
		 */
		public static int lsb(int x) {
			if (x == 0) return -1; // because 0 has no 1 bits
			return msb((x-1)^x);
		}

		public static int lsbNaive(int x) {
			if (x == 0) return -1; // because 0 has no 1 bits
			return msbNaive((x-1)^x);
		}

		public static int lsbNaiver(int x) {
			if (x == 0) return -1; // because 0 has no 1 bits
			String bin = bin(x);
			for (int i = 31; i > -1; i--) {
				// System.out.println(bin.charAt(i));
				if (bin.charAt(i) == '1') return i;
			}
			return -1;
		}

		/**
		 * Returns a binary representation of integer x in a String containing leading zeroes.
		 * @param x
		 * @return
		 */
		public static String bin(int x) {
			String aux = Integer.toBinaryString(x);
			if (aux.length() < 32) {
				StringBuilder res = new StringBuilder(32);
				for (int i = 32-aux.length(); i > 0; i--) {
					res.append(0);
				}
				res.append(aux);
				return res.toString();
			}
			
			else return aux;
		}

		public static void main(String[] args) {
			int i = Integer.MAX_VALUE;
			System.out.println("Binary representation: " + bin(i));
			System.out.println("Indices go from 0 .. 31. If it returns -1 then there is no 1-bit");
			System.out.println("MSB Naive: " + msbNaive(i));
			System.out.println("MSB standard library: " + msb(i));
			System.out.println("LSB using the MSB naive: " + lsbNaive(i));
			System.out.println("LSB using the same loop as in MSB naive: " + lsbNaiver(i));
			System.out.println("LSB using MSB standard library: " + lsb(i));
			
		}

	}


}