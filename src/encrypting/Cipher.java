package encrypting;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * Created by Admin on 27.09.2016.
 */
public class Cipher {

    final int FIRST = 10000;
    final int SECOND = 25000;
    class sub_shamir{

        public long c, d, p;
        sub_shamir(long c, long d, long  p) {
            this.p = p;
            this.c = c;
            this.d = d;
        }
    }

    class sub_algamal{
        public long open, secret, p;
    }

    class sub_RSA{
        public long open_d, open_N, secret;
        long[] sign;
    }

    class sub_poker{
        long p, c, d;
        sub_poker(long p, long c, long d){
            this.p = p;
            this.c = c;
            this.d = d;
        }
    }

    private Library enc = new Library();

    static boolean[] toBooleanArray(byte[] bytes) {
        BitSet bits = BitSet.valueOf(bytes);
        boolean[] bools = new boolean[bytes.length * 8];
        for (int i = bits.nextSetBit(0); i != -1; i = bits.nextSetBit(i+1)) {
            bools[i] = true;
        }
        return bools;
    }

    public byte[] Vernam_encrypt(String file_path, String file_name) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] file = read(file_path + file_name);
        byte[] key = new byte[file.length];
        boolean[] bool = toBooleanArray(file);
        for (int i = 0; i < bool.length; i++)
        {
            if (i%8 == 0) System.out.print(" ");
            if (bool[i]) System.out.print("1");
            else  System.out.print("0");

        }
        bool = null;
        for (int i = 0; i < file.length; i++)
        {
            long rand =enc.random(1,255);
            byte[] temp = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(rand).array();
            key[i] = temp[temp.length-1];

            int key_i = key[i] & 0xFF;
            int file_i = file[i] & 0xFF;

            System.out.print("Ключ = " + key_i + "  Файл = " + file_i);
            int code = key_i ^ file_i;
            System.out.println("Код = " + code);
            temp = null;
            temp = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(code).array();
            outputStream.write(temp[temp.length-1]);
            System.out.println("темп = " + temp);
        }

        byte[] end = outputStream.toByteArray();
        write(end, file_path + file_name + ".crypt");
        return key;
    }

    public void Vernam_decrypt(String file_path, String file_name, byte[] key)
    {
        System.out.println("Декодирование...");
        byte[] file = read(file_path + file_name+".crypt");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 0; i < file.length; i++)
        {
            int key_i = key[i] & 0xFF;
            int file_i = file[i] & 0xFF;
            int code = key_i ^ file_i;
            System.out.print("Ключ = " + key_i + "  Файл = " + file_i);
            System.out.println("Код = " + code);

            byte[] temp = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(code).array();
            outputStream.write(temp[temp.length-1]);
        }
        byte[] end = outputStream.toByteArray();
        write(end, file_path + "decrypt_" + file_name);

    }

    public sub_RSA RSA_encrypt(String file_path, String file_name) throws IOException {
        sub_RSA alice, bob;
        long message;
        bob = getKeys_RSA();
        byte[] file = read(file_path + file_name);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < file.length; i++) {
            message = file[i] & 0xFF;
            System.out.println("перед шифрованием = " + message);
            long e = enc.modPow(message, bob.open_d, bob.open_N);
            System.out.println("e = " + e);
            long message2 = enc.modPow(e, bob.secret, bob.open_N);
            System.out.println("после " + message2);

            byte[] readBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(e).array();
            message = 0;
            outputStream.write(readBytes);
        }
        byte[] end = outputStream.toByteArray();
        write(end, file_path + file_name + ".crypt");
        outputStream.reset();
        return bob;
    }

    public void RSA_decrypt(String file_path, String file_name,sub_RSA keys)
    {
        byte[] file = read(file_path + file_name+".crypt");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for(int i = 0; i<file.length;i+=8) {

            byte[] array_temp = new byte[8];

            for (int j = 0; j < 8; j++) {
                array_temp[j] = file[i + j];
            }
            ByteBuffer bb = ByteBuffer.wrap(array_temp);
            long temp = bb.getLong();
            temp &= 0xFFFFFFFF;
            long message = enc.modPow(temp, keys.secret, keys.open_N);

            byte[] readBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(message).array();
            outputStream.write(readBytes[readBytes.length-1]);
        }
        byte[] end = outputStream.toByteArray();
        write(end, file_path + "decrypt_" + file_name);
    }

    public long RSA_encrypt(long message, sub_RSA bob) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //System.out.println("перед шифрованием = " + message);
        long e = enc.modPow(message, bob.open_d, bob.open_N);
        //System.out.println("e = " + e);
        return e;
    }

    public long RSA_decrypt(long temp, sub_RSA keys)
    {
        return enc.modPow(temp, keys.secret, keys.open_N);
    }

    public sub_RSA getKeys_RSA()
    {
        sub_RSA sub = new sub_RSA();
        long p = enc.get_random_prime(FIRST, SECOND);
        long q = enc.get_random_prime(FIRST, SECOND);
        System.out.println("Два числа " +  p +  " " + q);
        sub.open_N = p*q;
        System.out.println("N = " + sub.open_N);
        long Fi = (p-1)*(q-1);
        System.out.println(Fi);
        sub.open_d = 3;
        while(true)
        {
            if (enc.evklid(sub.open_d, Fi) == 1) {
                sub.secret = enc.Evklid2(Fi, sub.open_d);
                if (((sub.open_d * sub.secret) % Fi) == 1) {
                    System.out.println("Найдено " + sub.secret);
                    break;
                }
                System.out.println("все плохо");
            }
            sub.open_d++;
            System.out.println(sub.open_d);
        }
        return sub;
    }

    public sub_algamal[] Al_Gamal_encrypt(String file_path, String file_name) throws IOException {
        long[] p_and_g = enc.get_p_and_g();
        long temp = 0, message = 0;
        final long p = p_and_g[0];
        final long g = p_and_g[1];

        int size_p = Long.toBinaryString((long) p).length();


        sub_algamal alice, bob;
        alice = new sub_algamal();
        bob = new sub_algamal();
        alice.secret = enc.random(1, p-1);
        bob.secret = enc.random(1, p-1);
        alice.open = enc.modPow(g,alice.secret,p);
        bob.open = enc.modPow(g,bob.secret,p);
        bob.p = p;
        alice.p = p;

        byte[] file = read(file_path + file_name);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 0; i < file.length; i++) {

            message = file[i] & 0xFF;
            System.out.println("Кодируемое сообщение = " + message);
            long k = enc.random(1,p-2);
            long r = enc.modPow(g,k,p);
            long e = (message * (enc.modPow(bob.open, k, p))) % p;

            byte[] readBytes_r = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(r).array();
            byte[] readBytes_e = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(e).array();

            for (int j = 0; j < 4; j++)
            {
                readBytes_e[j] = readBytes_r[4+j];
            }

            outputStream.write(readBytes_e);
            message = 0;
        }
        byte[] end = outputStream.toByteArray();
        write(end, file_path + file_name + ".crypt");
        outputStream.reset();
        sub_algamal[] mas;
        mas = new sub_algamal[]{alice, bob};
        return mas;
    }

    public void Al_Gamal_decrypt(String file_path, String file_name, sub_algamal[] keys)
    {
        sub_algamal alice, bob;
        alice = keys[0];
        bob = keys[1];
        byte[] file = read(file_path+file_name+".crypt");
        final long p = alice.p;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] end;
        for(int i = 0; i<file.length;i+=8)
        {
            byte[] readBytes_r = new byte[8];
            byte[] readBytes_e = new byte[8];
            for (int j = 0; j<8; j++)
            {
                if (j < 4)
                    readBytes_r[4+j] = file[i+j];
                else readBytes_e[j] = file[i+j];
            }

            ByteBuffer bb = ByteBuffer.wrap(readBytes_r);
            long r = bb.getLong();
            bb = ByteBuffer.wrap(readBytes_e);
            long e =  bb.getLong();
            long message = (e * (enc.modPow(r,p-1-bob.secret,p))) % p;
            System.out.println("Декодированное сообщение = " + message);
            byte[] readBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(message).array();
            outputStream.write(readBytes[readBytes.length-1]);
        }
        end = outputStream.toByteArray();
        write(end, file_path + "decrypt_" + file_name);
    }

    public sub_shamir[] Shamir_encrypt(String file_path, String file_name) throws IOException {
        sub_shamir alice, bob;
        long x1;
        long p;
        while (true) {
            p = enc.random(Library.FIRST_SCOPE, Library.SECOND_SCOPE);
            if (enc.ferma(p)) break;
        }
        alice = getKeys(p);
        bob = getKeys(p);
        long temp = 0;
        byte[] file = read(file_path + file_name);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < file.length; i++) {
            temp = file[i] & 0xFF;
            System.out.println("Кодируемое сообщение= " + temp);
            x1 = enc.modPow(temp, alice.c, p);
            System.out.println("Закодированное сообщение= " + x1);
            //System.out.println("Закодированный temp = " + x1);
            byte[] readBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(x1).array();
            outputStream.write(readBytes);
            temp = 0;
        }
        byte[] end = outputStream.toByteArray();
        write(end, file_path + file_name + ".crypt");
        outputStream.reset();

        sub_shamir[] mas;
        mas = new sub_shamir[]{alice, bob};
        return mas;
    }


    public void Shamir_decrypt(String file_path, String file_name, sub_shamir[] keys)  {
        sub_shamir alice, bob;
        alice = keys[0];
        bob = keys[1];
        byte[] file = read(file_path+file_name+".crypt");
        long x1, x2, x3, x4, temp, p;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] end;
        p = alice.p;

        System.out.println();
        System.out.println();
        System.out.println("Декодирование...");
        int step = 1;
        for(int i = 0; i<file.length;i+=8)
        {
            byte[] array_temp = new byte[8];
            for (int j = 0; j<8; j++)
            {
                array_temp[j] = file[i+j];
                System.out.print(array_temp[j] + " ");
            }
            ByteBuffer bb = ByteBuffer.wrap(array_temp);
            x1 = bb.getLong();
            x1 &= 0xFFFFFFFF;
            System.out.println("принятое сообщение = " + x1);
            x2 = enc.modPow(x1, bob.c, p);
            x3 = enc.modPow(x2,alice.d, p);
            x4 = enc.modPow(x3, bob.d, p);
            System.out.println("Декодированное сообщение = " + x4);
            byte[] readBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(x4).array();
            outputStream.write(readBytes[readBytes.length-1]);
            step++;
        }
        end = outputStream.toByteArray();
        write(end, file_path + "decrypt_" + file_name);
    }

    private sub_shamir getKeys(long p){
        long C, D;
        while(true) {
            C = enc.random(1, Library.FIRST_SCOPE);
            if ((C<p)&&(enc.evklid(p-1,C) == 1)){
                D = enc.Evklid2(p-1, C);
                if (((C*D) % (p-1)) == 1) break;
            }
        }
        return new sub_shamir(C,D,p);
    }

    static byte[] read(String aInputFileName){
        //log("Reading in binary file named : " + aInputFileName);
        File file = new File(aInputFileName);
        //log("File size: " + file.length());
        byte[] result = new byte[(int)file.length()];
        try {
            InputStream input = null;
            try {
                int totalBytesRead = 0;
                input = new BufferedInputStream(new FileInputStream(file));
                while(totalBytesRead < result.length){
                    int bytesRemaining = result.length - totalBytesRead;
                    //input.read() returns -1, 0, or more :
                    int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0){
                        totalBytesRead = totalBytesRead + bytesRead;
                    }
                }
                //log("Num bytes read: " + totalBytesRead);
            }
            finally {
                //log("Closing input stream.");
                input.close();
            }
        }
        catch (FileNotFoundException ex) {
            //log("File not found.");
        }
        catch (IOException ex) {
            //log(ex);
        }
        return result;
    }

    static void write(byte[] aInput, String aOutputFileName){
        //log("Writing binary file...");
        try {
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(new FileOutputStream(aOutputFileName));
                output.write(aInput);
            }
            finally {
                output.close();
            }
        }
        catch(FileNotFoundException ex){
        }
        catch(IOException ex){
            //log(ex);
        }
    }

    public sub_poker getMentalPokerKeys(long p)
    {
        long c;
        do {
            c = enc.get_random_prime();
        }while(enc.evklid(c,p-1)!=1);
        long d = enc.Evklid2(p-1,c);
        return new sub_poker(p,c,d);
    }

    public long Mental_Poker_encrypt(long card, sub_poker keys)
    {
        return enc.modPow(card, keys.c, keys.p);
    }
}
