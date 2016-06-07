import java.lang.Math ;
import java.io.* ;

class BitStream
{

    byte [] buf ;
    int cur_byte = -1 ;
    int cur_bit = -1 ;
    int count = 0 ;

    BitStream()
    {
        buf = new byte[1000]; // random
        for(int i = 0 ; i < buf.length ; i++)
            buf[i] = 0 ;
    }

    BitStream(byte [] b)
    {
        buf = b ;
        cur_byte = 0 ;
        cur_bit = 7 ;
        count = 8 * b.length ;
    }


    BitStream(String inputFile, boolean type) throws IOException
    {
        if(type)
            fromByteFile(inputFile);
        else
            fromBitFile(inputFile);


    }

    byte [] toBytes()
    {
        byte [] tmp = new byte[count/8] ;
        System.arraycopy(buf,0,tmp,0,tmp.length);
        return tmp ;
    }

    int fromBitFile(String filename)
    {

        try
        {
            File file = new File(filename);
            int size = (int)file.length();
            FileInputStream f = new FileInputStream(filename);
            BufferedInputStream b = new BufferedInputStream(f);

            buf = new byte[size];
            int c = 0, ctr = -1 ;
            if((c = b.read()) == -1)
                return 0;
            count = c ;
            while((c = b.read()) != -1)
            {
                buf[++ctr] = (byte)c;
            }
            if(ctr >= 0)
            {
                cur_byte = 0 ;
                cur_bit = 7 ;
                count += ctr*8 ;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0 ;
        }
        return 1 ;

    }

    int fromByteFile(String filename)
    {
        try
        {
            File file = new File(filename);
            int size = (int)file.length();
            FileInputStream f = new FileInputStream(filename);
            BufferedInputStream b = new BufferedInputStream(f);

            buf = new byte[size+1];
            int c = 0, ctr = -1 ;
            while((c = b.read()) != -1)
            {
                buf[++ctr] = (byte)c;
            }
            if(ctr >= 0)
            {
                cur_byte = 0 ;
                cur_bit = 7 ;
                count = (ctr+1)*8 ;
            }
        }
        catch(Exception e)
        {

            e.printStackTrace();
            return 0;
        }
        return 1 ;
    }




    int toByteFile(String filename)
    {

        try
        {
        FileOutputStream f = new FileOutputStream(filename);
        BufferedOutputStream b = new BufferedOutputStream(f);

        b.write(buf,0,count/8);
        b.flush();
        f.close();
        } catch(Exception ie)
        {
            ie.printStackTrace();
            return 0 ;
        }
        return 1 ;

    }

    int toBitFile(String filename)
    {

        try
        {
        FileOutputStream f = new FileOutputStream(filename);
        BufferedOutputStream b = new BufferedOutputStream(f);

        int used = count % 8 ;
        if(used == 0)
            used = 8 ;
        b.write(used);
        b.write(buf,0,(int)Math.ceil(count/8.0));
        b.flush();
        f.close();
        } catch(Exception ie)
        {
            ie.printStackTrace();
            return 0 ;
        }
        return 1 ;

    }

    byte readByte() throws EOFException, IOException
    {
        if(count < 8)
            throw new EOFException("EOF");

        if(count % 8 != 0)
            throw new IOException("Mixing calls to a bit and byte operations");

        count -= 8 ;
        return buf[cur_byte++];
    }

    boolean readBit() throws EOFException
    {
        boolean ret ;

        if(count <= 0)
            throw new EOFException("EOF");


        int intval = (int) buf[cur_byte] & 0x0ff ;
        int testval = (int) Math.pow(2,cur_bit);
        if((intval & testval) == 0)
            ret = false ;
        else
            ret = true ;


        count -- ;
        cur_bit -- ;
        if(cur_bit == -1)
        {
            cur_byte ++ ;
            cur_bit = 7 ;
        }
        return ret ;
    }

    void expand()
    {
        if(cur_byte < buf.length - 1)
            return;

        //p("Expanding to ... "+2*buf.length+"\n");
        byte [] tmp = new byte[2*buf.length];
        System.arraycopy(buf,0,tmp,0,buf.length);
        for(int i = buf.length ; i < tmp.length ; i++)
            tmp[i] = 0 ;
        buf = tmp ;

    }


    int writeByte(byte b)
    {
        if(count % 8 != 0)
        {
            System.out.println("Mixing calls to bit and byte operations");
            return 0 ;
        }

        expand();

        buf[++cur_byte] = b ;
        count += 8 ;


       return 1 ;
    }


   int writeBit(boolean bit)
   {

      expand();

      if(cur_bit <= 0)
      {
          cur_byte ++ ;
          cur_bit = 7 ;
      }
      else
          cur_bit -- ;

      int intval = (int) buf[cur_byte] & 0xff ;
      if(bit)
      {
          int testval = (int)Math.pow(2,cur_bit);
          //p(" test "+bp(intval)+" "+bp(testval));
          intval  |= testval ;
          buf[cur_byte] = (byte)intval ;
          //p(" and now "+bp(intval));
      }
      count ++ ;

      return 1 ;
   }

   boolean isEmpty()
   {
       return (count == 0) ;
   }

   static String bp(int i)
   {
       return Integer.toString(i,2) ;
   }
   static void p(String s)
   {
       System.out.print(s);
   }


}

