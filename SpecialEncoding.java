import java.io.EOFException;

/*
 * @author Tj Wrenn
 * @project SpecialEncoding.java
 */

/**
 * @
 */
public class SpecialEncoding {

    /**
     * Returns length of the binary string that represents the given int
     * Time: O(1)
     * Space: O(1)
     * Parameters: integer whose binary string width is desired
     */
    private static int w(int i){
        return s(i).length();
    }
    /**
     * Takes an integer, finds the binary string, and replaces the first 1 with a zero
     * Space: O(1)
     * Time: O(1)
     * Parameters: integer to convert to a binary string then replace first 1 with zero
     */
    private static String t(int i){
        return s(i).replaceFirst("1","0");
    }
    /**
     * takes an integer and returns binary representation as a string
     * Time: O(1)
     * Space: O(1)
     * Parameters: integer to be represented by binary string
     */
    private static String s(int i){
        return i > 0 ? Integer.toBinaryString(i) : "";
    }
    /**
     * Encode the header by taking the frequencies of the path lengths
     * Time: O(N)
     * Space: 0(1)
     * Parameters: frequency of the codeword lengths and the file to output
     *              encoded data.
     */
     public static void specialEncode(byte [] codewordLenFreqs, BitStream output){
        /* let s(i) denote the binary representation of i (with no leading zeros).
         * For example, s(4) is equal to the binary string 100. For any nonnegative
         * integer i, let w(i) denote the length of the string s(i). For
         * any i > 0, let t(i) denote s(i) with the leading bit (which is a 1)
         * switched to a zero. Forexample, t(4) is equal to 000.*/
        for (int a=0, b=0, i=0; a<256; a+=(codewordLenFreqs[i] & 0xff), b=w(codewordLenFreqs[i] & 0xff), i++){
            if (w(codewordLenFreqs[i] & 0xff)>b){
                //transmit w(codewordLenFreqs[i])-b 1’s
                final int tmplen = w(codewordLenFreqs[i] & 0xff)-b;
                for (int j = 0; j < tmplen; ++j) {
                    //System.out.print("1");
                    output.writeBit(true);
                }
                //followed by t(codewordLenFreqs[i])
                final String strtemp = t(codewordLenFreqs[i] & 0xff);
                for (int j = 0; j < strtemp.length(); ++j) {
                    //System.out.print(strtemp.charAt(j));
                    output.writeBit(strtemp.charAt(j) == '1');
                }
            } else {
                //transmit b-w(codewordLenFreqs[i])+1 0’s
                final int tmplen = b-w(codewordLenFreqs[i] & 0xff)+1;
                for (int j = 0; j < tmplen; ++j) {
                    //System.out.print("0");
                    output.writeBit(false);
                }
                //followed by s(codewordLenFreqs[i]);
                final String strtemp = s(codewordLenFreqs[i] & 0xff);
                for (int j = 0; j < strtemp.length(); ++j) {
                    //System.out.print(strtemp.charAt(j));
                    output.writeBit(strtemp.charAt(j) == '1');
                }
            }

        }
        //System.out.println("----end special code");
    }


    /**
     * Uses the special algorithm to decode the header
     * Time: O(N) where N is the length of the header
     * Space: O(1), 256 bytes
     * accepts the encoded bitStream which it is to decode into the
     * frequency of codeword lengths
     */
    public static byte[] specialDecode(BitStream encoded){
        byte[] decoded  = new byte[256];
        int a=0, b=0, i=0, c=0, count=0, weight=0;
        String temp = "";
            try{
                //
                //111111110111010100000000001110010110
                //                ^ posVal
                while(a<256){
                    boolean posVal = encoded.readBit();                     c++;
                    if(posVal){
                        count=1;
                        while(count + b > 0 && encoded.readBit()){
                            count++;                                        c++;
                        }
                        weight = count+b;
                        //System.out.println("weight = " + weight);
                        if(weight > 0){
                            temp = "1";
                            for (int j = 0; j < weight-1; ++j) {
                                temp += (encoded.readBit() ? "1" : "0");        c++;
                            }
                            //System.out.println("temp = " + temp);
                            decoded[i] = (byte)Integer.parseInt(temp,2);
                            b = weight;
                            a += (int)(decoded[i] & 0xff);
                            //System.out.println("a = " + a);
                        } else {
                            decoded[i] = (byte)0;
                            a += 0;
                            b = 0;
                        }
                        i++;
                    } else {
                        count=1;
                        while(b - count + 1 > 0 && encoded.readBit() == false){
                            count++;                                        c++;
                        }
                        weight = b + 1 - count;
                        //System.out.println("weight else = " + weight);
                        if(weight > 0){
                            temp = "1";
                            for (int j = 0; j < weight-1; ++j) {
                                temp += (encoded.readBit() ? "1" : "0");    c++;
                            }
//                          System.out.println("temp else = " + temp);
                            decoded[i] = (byte)Integer.parseInt(temp, 2);
                            b = weight;
                            a += (int)(decoded[i] & 0xff);
                        } else {
                            decoded[i] = (byte)0;
                            a += 0;
                            b = 0;
                        }
                        i++;
                    }
                    //System.out.println("i = " + i);
                }
            }
            catch(EOFException e){
                System.err.println(e);
            }

//      System.err.println(c);
//      System.out.println();
//      System.out.print("[");
//      for (int j = 0; j < decoded.length; ++j) {
//          System.out.print(((int)decoded[j] & 0xff) + ",");
//      } System.out.println("]");

        return decoded;
    }

}
