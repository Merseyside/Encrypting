package encrypting;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Admin on 08.11.2016.
 */
public class DigitalCache {
    Library enc = new Library();
    Cipher cipher = new Cipher();
    Cipher.sub_RSA bankKeys;
    private byte[] hash(long n){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] readBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(n).array();
            return md.digest(readBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private long function(long n){
        byte[] b = hash(n);
        BigInteger bi = new BigInteger(b);
        long hash_long = bi.mod(BigInteger.valueOf(bankKeys.open_N)).longValue();
        return hash_long;
    }

    private long bankSign(long fn){
        return enc.modPow(fn, bankKeys.secret, bankKeys.open_N);
    }

    private Cipher.sub_RSA getBankKeys(){
        return cipher.getKeys_RSA();
    }

    private boolean checkSign(long sf, long fn){
        return (enc.modPow(sf,bankKeys.open_d,bankKeys.open_N) == fn);
    }

    public void signCache(long n){
        bankKeys = getBankKeys();
        long fn = function(n);
        long sf = bankSign(fn);
        if (checkSign(sf,fn)) System.out.println("Банкнота подписана");
        else System.out.println("Банкнота не подписана");
    }
}
