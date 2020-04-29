class chapter2 {

	/**
	 * Often we view words as divided into ﬁelds of some length f. We then use x(i)_f to denote the ith ﬁeld,
	 * starting from the right with x(0)_f the right most ﬁeld. Thus x represents the integer E^(w−1)_(i=0) 2^i x(i)1.
	 * Note that ﬁelds can easily be masked out using regular instructions, e.g 
	 */

	static class FieldsOfWords {

		private final int F; // the number of bits in a field.

		public FieldsOfWords(int f){
			F = f;
		}

		/**
		 * Assign y to field number i in the word x
		 * @param x the word, where field y will be assigned to
		 * @param i	the position of the field. Field 0 is the right most field in the word x. Field 1 the second most right field of x
		 * @param y the field to be assigned in x
		 * @return returns the word x after the operation
		 */
		public int fieldAssignment(int x, int i, int y){
			return (x & (~(m(i))) | (y << (i * F) & m(i)));
		}

		/**
		 * Function defined in the paper. Used for field assignments within words.
		 * @param i
		 * @return
		 */
		public int m(int i){
			return ((1 << F) - 1) << (i * F);
		}

		/**
		 * Leave fields from i to j intact and everything else is made 0 (and shifted for convenience)
		 * @param x the word where the fields should be extracted from
		 * @param i the smallest field (inclusive), the right most field to be included
		 * @param j the largest field (inclusive), the left most field to be included
		 * @return the field interval, shifted all the way to the right
		 */
		public int getFields(int x, int i, int j){
			return (x >>> (i * F)) & ((1 << ((j - i) * F)) - 1);
		}

		/**
		 * Shift field i to the right most position (and all the larger fields). All the smaller fields are filled with 0.
		 * @param x the word where to operate
		 * @param i the first field to keep
		 * @return the remaining fields after the operation
		 */
		public int getFieldsUntilTheEndOfTheWord(int x, int i){
			return (x >>> (i * F));
		}
		
		
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

	static class Indexing {
		private final int W; // 32 or 64 bits
		private int[] key;
		private int[] index;

		public Indexing(int k){
			W = 32;
			int log2k = (int) Math.ceil(Math.log(k)/Math.log(2)); // log2k, for the Index array
			key = new int[k];
			index = new int[log2k];

		}
	}


}