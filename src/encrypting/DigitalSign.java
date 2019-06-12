package encrypting;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Admin on 17.10.2016.
 */
public class DigitalSign {

    class sub_algamal{
        public long r,s[],y,p,g;
    }

    class gost{
        long r,s,y,p,q,a;
        gost(long r, long s, long y, long p, long q, long a){
            this.r = r;
            this.s = s;
            this.y = y;
            this.p = p;
            this.q = q;
            this.a = a;
        }
    }

    class p_and_q
    {
        long p,q;

        p_and_q(long p, long q)
        {
            this.p = p;
            this.q = q;
        }

    }

    Library enc = new Library();
    Cipher ch = new Cipher();

    Cipher.sub_RSA RSA_sign(String file_path, String file_name) {
        Cipher.sub_RSA alice = ch.getKeys_RSA();
        byte[] file = Cipher.read(file_path + file_name);
        byte[] hash = (hash_code(file));
        alice.sign = new long[hash.length];
        for (int i = 0; i<hash.length; i++){
            System.out.println(hash[i]);
            alice.sign[i] = enc.modPow(hash[i],alice.secret, alice.open_N);
        }
        byte[] end = longToByte(alice.sign);
        Cipher.write(end, file_path+file_name+".sign");
        return alice;
    }

    boolean RSA_sign_check(String file_path, String file_name, Cipher.sub_RSA alice){
        System.out.println("Проверка\n\n\n");
        byte[] file = Cipher.read(file_path + file_name);
        byte[] hash = hash_code(file);
        byte[] signes = Cipher.read(file_path + file_name + ".sign");
        byte[] temp_array = new byte[8];
        for (int i = 0, j1=0; i<signes.length; i+=8, j1++) {
            for (int j = 0; j<8; j++)
            {
                temp_array[j] = signes[i+j];
            }
            ByteBuffer bb = ByteBuffer.wrap(temp_array);
            long sign = bb.getLong();
            sign &= 0xFFFFFFFF;
            long w = enc.modPow(sign, alice.open_d, alice.open_N);
            if (!(w == (hash[j1]))) return false;
        }
        return true;
    }

    byte[] longToByte(long[] longs)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(longs.length * 8);
        LongBuffer longBuffer = byteBuffer.asLongBuffer();
        longBuffer.put(longs);
        return byteBuffer.array();
    }

    sub_algamal Al_Gamal(String file_path, String file_name){
        sub_algamal alice = new sub_algamal();
        long[] p_and_g = enc.get_p_and_g();
        long temp = 0, message = 0;
        alice.p = p_and_g[0];
        alice.g = p_and_g[1];
        long x = enc.random(1,alice.p-1);
        alice.y = enc.modPow(alice.g,x,alice.p);
        byte[] file = Cipher.read(file_path + file_name);
        byte[] hash = hash_code(file);
        alice.s = new long[hash.length];
        long k;
        while(true)
        {
            k = enc.random(1, alice.p-1);
            if ((enc.evklid(alice.p-1,k)) == 1) break;
        }
        alice.r = enc.modPow(alice.g,k,alice.p);
        long k1 = enc.Evklid2(alice.p - 1, k);
        for (int i = 0; i<hash.length;i++) {

            long u = ((hash[i]&0xF) - (x * alice.r)) % (alice.p - 1);
            if (u < 0) u += alice.p - 1;
            alice.s[i] = (k1 * u) % (alice.p - 1);
            System.out.println("sign = " + alice.s[i]);
        }
        Cipher.write(longToByte(alice.s), file_path + file_name + ".sign");
        return alice;
    }

    boolean Al_Gamal_check(String file_path, String file_name, sub_algamal alice){
        System.out.println("Проверка...");
        byte[] file = Cipher.read(file_path + file_name);
        byte[] hash = hash_code(file);
        byte[] signes = Cipher.read(file_path + file_name + ".sign");
        byte[] temp_array = new byte[8];
        for (int i = 0, j1=0; i<signes.length; i+=8, j1++) {
            for (int j = 0; j<8; j++)
            {
                temp_array[j] = signes[i+j];
            }
            ByteBuffer bb = ByteBuffer.wrap(temp_array);
            long sign = bb.getLong();
            sign &= 0xFFFFFFFF;
            System.out.println("sign = " + sign);
            long left_1 = enc.modPow(alice.y,alice.r,alice.p);
            long left_2 = enc.modPow(alice.r,sign,alice.p);
            long left_side = (left_1*left_2) % alice.p;
            long right_side = enc.modPow(alice.g,(hash[j1]&0xF),alice.p);
            if (!(left_side == right_side)) return false;
            System.out.println("все ок");
        }
        return true;
    }

    gost GOST(String file_path, String file_name){
        p_and_q p_and_q = p_and_q();
        long p = p_and_q.p;
        long q = p_and_q.q;
        long a = getA(p,q);
        long x = enc.random(0,q);
        long y = enc.modPow(a,x,p);
        byte[] file = Cipher.read(file_path + file_name);
        byte[] hash = hash_code(file);
        BigInteger bi = new BigInteger(hash);
        long hash_long = bi.mod(BigInteger.valueOf(q)).longValue();
        System.out.println("hash = " + hash_long);
        long k,r,s;
        while(true) {
            k = enc.random(0, q);
            r = (enc.modPow(a, k, p)) % q;
            s = (k*hash_long + x*r) % q;
            if ((s!=0)&&(r!=0)) break;
        }
        return new gost(r,s,y,p,q,a);
    }

    boolean check_gost(String file_path, String file_name, gost goost){
        byte[] file = Cipher.read(file_path + file_name);
        byte[] hash = hash_code(file);
        BigInteger bi = new BigInteger(hash);
        long hash_long = bi.mod(BigInteger.valueOf(goost.q)).longValue();
        System.out.println("Hash = " + hash_long);
        if (!(((goost.r>0)&&(goost.r<goost.q))&&((goost.s>0)&&(goost.s<goost.q)))) return false;
        else{
            System.out.println("Внутри");
            long h1 = enc.Evklid2(goost.q,hash_long);
            System.out.println("h1 = " + h1);
            long u1 = (goost.s*h1) % goost.q;
            long u2 = (-1*goost.r * h1) % goost.q;
            if (u2<0) u2+=goost.q;
            long v = ((enc.modPow(goost.a, u1,goost.p)*enc.modPow(goost.y,u2,goost.p)) % goost.p) % goost.q;
            if (v != goost.r) return false;
        }
        return true;
    }


    byte[] hash_code(byte[] file)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(file);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    p_and_q p_and_q(){
        long p = enc.get_random_prime(2147483648L, 4294967295L);
        long b = 32768;
        long q;
        while(true)
        {
            q = p/b;
            System.out.println("тут");
            if ((enc.ferma(q))&&(p == b*q + 1)) break;
            b++;
            if (p/b < 32768){
                p = enc.get_random_prime();
                b = 32768;
            }
        }
        return new p_and_q(p,q);
    }

    long getA(long p, long q)
    {
        long a;
        while(true)
        {
            long g = enc.random(Library.FIRST_SCOPE, Library.SECOND_SCOPE);
            a = enc.modPow(g,(p-1)/q, p);
            if (a>1) return a;
        }
    }

}
