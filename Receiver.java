class Receiver /* v1.0 */
{
    String inputFile, outputFile ;

    Receiver(String inputf, String outputf)
    {
        inputFile = inputf ; outputFile = outputf ;
    }

    void receive() throws Exception
    {
        BitStream encoded = new BitStream(inputFile,false) ;
        BitStream decoded = new BitStream() ;
        //Receive the message! simulated by reading from disk.

        //Decode the second level header first. This method needs to stop when
        //it decodes 256 bytes otherwise it will 'eat' into the rest of the
        //stream.
        byte [] codewordLenFreqs = SpecialEncoding.specialDecode(encoded);

        // To int ...
        int [] codewordLenFreqsInt = new int[256] ;
        for(int i = 0 ; i < 256 ; i++)
        {
            codewordLenFreqsInt[i] = (int) codewordLenFreqs[i] & 0xff ;
           /* Java has these problems with unsigned data */
        }

        //Now we can build the second-level canonical tree that we use to
        //decode the codeword lengths.
        CanonicalTree secondLevel = new CanonicalTree(codewordLenFreqsInt);
        BitStream codewordLens = new BitStream();
        secondLevel.decode(encoded, codewordLens, 256);
        byte [] codewordLensBytes = codewordLens.toBytes() ;
        // ... and then decode the lengths. The decoding has to stop after 256
        // symbols otherwise we will 'eat' into the rest of the stream.

        //Now construct the main canonical tree and ...
        CanonicalTree topLevel = new CanonicalTree(codewordLensBytes);
        topLevel.decode(encoded,decoded,-1);
        // .. use it to decode till EOF.

        //Write the decoded ASCII symbols to a file.
        decoded.toByteFile(outputFile);
    }

    public static void main(String args []) throws Exception
    {
        if(args.length < 2 )
        {
            System.out.println("Insufficient parameters\n");
            System.exit(-1);
        }

        Receiver r = new Receiver(args[0], args[1]);
        r.receive();
    }
}
