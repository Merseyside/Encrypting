package encrypting.diffiehelman;

public class Utils {

    static long modPow(long base, long exponent, long modulus) {
        long Result = 1;
        while(exponent > 0) {
            base = base % modulus;
            if ((exponent & 1) == 1) {
                Result= (Result*base) % modulus;
            }

            base*=base;
            exponent>>=1;
        }
        return Result;
    }

    static long random(long a, long b) {
        return (long)(Math.random() * (b-a) + a);
    }
}
