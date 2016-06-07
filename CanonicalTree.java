import java.util.Arrays;

/*
 * Created on Feb 7, 2004
 * @author Tj Wrenn
 * @project CanonicalTree.java
 */

/**
 * @
 */

public class CanonicalTree {
	private static final int MAX_ASCII = 256;
	private String[] paths;
	//activated in build2ndTree, or byte[] constructor
	private HuffmanNode root;

	/**
	 * calculate frequencies of individual characters in file and return them
	 * in array, where index in array corresponds with the ascii ordinal value,
	 * and value at that position corresponds to the number of occurances of
	 * that particular character
	 * CPU:    O(N), where N is the size in bytes of specified input file
	 * Memory: O(1), 256 integers.
	 * @param input the bitstream whose character frequency will be counted
	 */
	public static int[] frequencies(BitStream input) {
		int[] charCount = new int[MAX_ASCII];

		byte[] bitstreamfiledata = input.toBytes();

		for (int i = 0; i < bitstreamfiledata.length; ++i) {
			charCount[(int) bitstreamfiledata[i] & 0xff]++;
		}

		return charCount;
	}
	/**
	 * construct canonical tree using frequencies obtained from static method
	 * CPU:    O(N), where N is the size in bytes of specified input file
	 * Memory: O(1), 256 integers.
	 * @param input the bitstream whose character frequency will be counted
	 * @param element object to insert at position index
	 * @exception IndexOutOfBoundsException thrown if index is out of bounds relative to the deque
	 */
	public CanonicalTree(int[] freq) {
		PriorityQueue myQueue = new PriorityQueue();

		for (int i = 0; i < freq.length; ++i) {
			if (freq[i] > 0)
				myQueue.put(new HuffmanNode((byte) i, freq[i], null, null));
		}

		while (myQueue.size() > 1) {
			HuffmanNode t1 = (HuffmanNode) myQueue.pop();
			HuffmanNode t2 = (HuffmanNode) myQueue.pop();

			myQueue.put(
				new HuffmanNode(
					(byte) Math.min(t1.getData(), t2.getData()),
					t1.getFreq() + t2.getFreq(),
					t1,
					t2));
		}

		paths = new String[MAX_ASCII];

		traverse((HuffmanNode) myQueue.pop(), "");

		build2ndTree(codewordLengths());
	}

	/**
	 * Overloaded constructor
	 * construct canonical tree using DEPTHS of nodes in whatever tree they represent
	 * CPU:    O(N), where N is the size in bytes of specified input file
	 * Memory: O(1), 256 integers.
	 * @param freqs array of byte frequencies corresponding to the length of each path
	 */
	public CanonicalTree(byte[] freqs) {
		build2ndTree(freqs);
	}

	/**
	 * construct canonical tree using DEPTHS of nodes in whatever tree they represent
	 * CPU:    O(1), where ~256*2 iterations are done at max
	 * Memory: O(1), 256*2 Strings.
	 * @param freqs array of byte frequencies corresponding to the length of each path
	 */
	public void build2ndTree(byte[] freqs) {

		java.util.ArrayList nodes = new java.util.ArrayList();
		paths = new String[MAX_ASCII];

		for (int i = 0; i < freqs.length; ++i) {
			if (freqs[i] > 0) {
				nodes.add(new HuffmanNode((byte) i, freqs[i] & 0xff, null, null));
			}
		}

		while (nodes.size() > 1) {
			int i = getMax(nodes);
			HuffmanNode t1 = (HuffmanNode) nodes.get(i);
			nodes.remove(i);
			int oldi = i;

			i = getMax(nodes);
			HuffmanNode t2 = (HuffmanNode) nodes.get(i);
			//nodes.remove(i);
			nodes.add(oldi, new HuffmanNode(t1.getData(), t1.getFreq() - 1, t1, t2));

			nodes.remove(i+1);
		}

		if (nodes.size() > 0) {
			root = (HuffmanNode) nodes.get(0);
			traverse((HuffmanNode) nodes.get(0), "");  //what builds the paths array
		}


	}

	/**
	 * get the max frequency ordered value in the nodes array, maintaining lexograpical
	 * order
	 * CPU:    O(1), where ~256*2 iterations are done at max
	 * Memory: O(1), 256*2 Strings.
	 * @param nodes contains a list of HuffMan used to reconstruct paths from path lengths
	 */
	public static int getMax(java.util.ArrayList nodes) {
		int max = ((HuffmanNode) nodes.get(0)).getFreq();
		int maxIndex = 0;

		for (int i = 0; i < nodes.size(); ++i) {
			if (((HuffmanNode) nodes.get(i)).getFreq() > max) {
				max = ((HuffmanNode) nodes.get(i)).getFreq();
				maxIndex = i;
			}

		}
		return maxIndex;
	}

	/**
	 * construct canonical tree using DEPTHS of nodes in whatever tree they represent
	 * CPU:    O(1), where ~256*2 iterations are done at max
	 * Memory: O(1), 256*2 Strings.
	 * @param freqs array of byte frequencies corresponding to the length of each path
	 */
	private void traverse(HuffmanNode node, String path) {
		if (node != null) {
			traverse(node.left, path + "0");
			if (node.right == null && node.left == null) {
				if (path.length() == 0)
					path = "0";
				paths[(int) (node.getData() & 0xff)] = path;
				path = "";
			}
			traverse(node.right, path + "1");
		}
	}

	/**
	 * Take the input stream and write encoded stream to file
	 * Time: O(N^2) where N is the Size of the file in bytes
	 * Space: O(N) where N is the length of the file in bytes
	 * Parameters: input to encode, and output to write encoded input to
	 */
	public void encode(BitStream input, BitStream output) {

		if (input.count > 0) {
			byte[] bitstreamfiledata = input.toBytes();

			for (int i = 0; i < bitstreamfiledata.length; ++i) {
				String path = paths[(int) bitstreamfiledata[i] & 0xff];
				if (path != null)
					for (int j = 0; j < path.length(); ++j) {
						output.writeBit(path.charAt(j) == '1');
					}
			}
		}

	}

	/**
	 * takes encoded input, interprets it and writes to output for as long as count
	 * Time: O(NlogN) tree traversal
	 * Space: O(1)
	 * parameters: encoded bitStream, stream to output to, how long to read input
	 */
	public void decode(BitStream input, BitStream output, int count) {
		final boolean RIGHT = true;
		final boolean LEFT = false;
		try {
			int i = 0;
			while(!input.isEmpty()) {
				HuffmanNode mario_from_donkykong = root;

				while (mario_from_donkykong.getLeft() != null){
					boolean curDirection = input.readBit();
					if (curDirection == RIGHT) {
						//System.out.println("going right");
						mario_from_donkykong = mario_from_donkykong.getRight();
					} else {
						//System.out.println("going left");
						mario_from_donkykong = mario_from_donkykong.getLeft();
					}
				}
				//System.out.println("hit an end");
				output.writeByte(mario_from_donkykong.getData());
				i++;
				if (i == count)
					break;

			}
		} catch (java.io.EOFException e) {
			System.err.println(e);
		}
	}

	/**
	 * returns byte array of lengths of code words
	 * Time: O(1) simply traverse ascii characters
	 * Space: O(1) 256 bytes
	 */
	public byte[] codewordLengths() {
		byte[] codeLengths = new byte[MAX_ASCII];

		for (int i = 0; i < MAX_ASCII; ++i) {
			if (paths[i] != null) {
				codeLengths[i] = (byte) paths[i].length();
			}
		}
		return codeLengths;
	}

	/** returns frequencies of codeword lengths
	 * Time: O(1), 256 ascii characters
	 * Space: O(1), 256 bytes
	 */
	public byte[] codewordLengthFrequencies() {
		byte[] codewordLenFreq = new byte[MAX_ASCII];
		byte[] codewordLengths = codewordLengths();

		for (int i = 0; i < MAX_ASCII; ++i) {
			codewordLenFreq[(int) codewordLengths[i] & 0xff]++;
		}

		return codewordLenFreq;
	}

	/** Inner Class HuffmanNode **/
	private class HuffmanNode implements Comparable {
		private byte data;
		private int freq;
		private HuffmanNode left;
		private HuffmanNode right;
		private String path;

		/**
		 * returns value of left pointer
		 * Time: O(1)
		 * Space: O(1)
		 */
		public HuffmanNode getLeft() {
			return left;
		}

		/**
		 * returns value of right pointer
		 * Time: O(1)
		 * Space: O(1)
		 */
		public HuffmanNode getRight() {
			return right;
		}

		/**
		 * Creates node
		 * Space: O(1)
		 * Time: O(1)
		 */
		public HuffmanNode(
			byte data,
			int freq,
			HuffmanNode left,
			HuffmanNode right) {
			this.data = data;
			this.freq = freq;
			this.left = left;
			this.right = right;
		}

		/**
		 * returns freq data field
		 * Space: O(1)
		 * Time: O(1)
		 */
		public int getFreq() {
			return this.freq;
		}

		/**
		 * returns data data-field
		 * Space: O(1)
		 * Time: O(1)
		 */
		public byte getData() {
			return this.data;
		}

		/**
		 * compares this to o, if freq is less, object is less
		 * if freq is same, the sorts lexigraphically
		 * Space: O(1)
		 * Time: O(1)
		 * parameters: Object to compare to
		 */
		public int compareTo(Object o) {
			if (!(o instanceof HuffmanNode))
				throw new ClassCastException();

			final HuffmanNode that = (HuffmanNode) o;

			if (that.freq == this.freq)
				if (that.data == this.data)
					return 0;
				else
					return this.data < that.data ? -1 : 1;
			else
				return this.freq < that.freq ? -1 : 1;
		}

		/**
		 * print nodes in a pretty fashion
		 * Time: O(1)
		 * Space: O(1)
		 */
		public String toString() {
			return (char)data + " " + freq + " ["	+ (this.left == null ? "x" : "/")
				+ " "
				+ (this.right == null ? "x" : "\\")
				+ "]";
		}

	}
}
