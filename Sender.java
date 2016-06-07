class Sender
{

    String inputFile ;
    String outputFile ;

    Sender(String inpf, String outf)
    {
        inputFile = inpf ;
        outputFile = outf ;
    }

    void send( ) throws Exception
    {
        BitStream input = new BitStream(inputFile,true) ;
        // Read in the input data into a bit stream.
        BitStream output = new BitStream() ;

        //Make a pass through the data and determine the frequencies of the
        //ASCII characters in the input data.
        int [] freqs = CanonicalTree.frequencies(input);

        //Construct a CanonicalTree from these frequncies.
        CanonicalTree topLevel = new CanonicalTree(freqs);
        byte [] codelengths = topLevel.codewordLengths();
        byte [] codewordLenFreqs = topLevel.codewordLengthFrequencies();

        //Transmit the second-level header i.e. the frequencies of the codeword
        //lengths. This uses the special encoding mentioned in the
        //specification.
        SpecialEncoding.specialEncode(codewordLenFreqs, output);

        //Need to convert these frequncies to int from byte.
        int [] codewordLenFreqsInt = new int[256];
        for(int i = 0 ; i < 256 ; i++)
        {
            codewordLenFreqsInt[i] = (int) codewordLenFreqs[i] & 0xff ;
           /* Java has these problems with unsigned data */
        }

       //Construct the second-level canonical tree for encoding the codeword
       //lengths.
       CanonicalTree secondLevel = new CanonicalTree(codewordLenFreqsInt) ;

       //Convert the codeword lengths to a bitstream and encode them using the
       //second level tree.
       BitStream codelens = new BitStream(codelengths) ;
       secondLevel.encode(codelens,output);

       //Now that we have finished with the header, we can encode the actual
       //input data using the main canonical tree.

       input = new BitStream(inputFile, true);
       topLevel.encode(input,output); // get a new bitstream here.

       //Transmission to sender is simulated by writing the file to disk.
       output.toBitFile(outputFile);
    }

    public static void main(String args[]) throws Exception
    {
        if(args.length < 2)
        {
            System.out.println("Insufficient arguments\n");
            System.exit(-1);
        }

        Sender s = new Sender(args[0], args[1]); // check.
        s.send();
    }
}
