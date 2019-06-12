package encrypting.diffiehelman;

import static encrypting.diffiehelman.Utils.modPow;
import static encrypting.diffiehelman.Utils.random;

public class DiffieHelmanAlgorithm {

    private long local;
    private long from, to;
    private long prime, generator;
    private long publicKey, secretKey, opponentPublicKey;

    /*Secret key already generated and both opponents have it*/
    DiffieHelmanAlgorithm(long secretKey) {
        this.secretKey = secretKey;
    }

    /*Prepare to create our public key*/
    DiffieHelmanAlgorithm(long prime, long generator, long from, long to) {
        this.from = from;
        this.to = to;

        this.prime = prime;
        this.generator = generator;
    }

    /*Generate public and @return publicKey to send it to opponent*/
    public long generatePublicKey() throws IllegalArgumentException {

        if (from != 0 && to > from && generator != 0 && prime != 0) {
            long local = random(from, to);

            publicKey = modPow(generator, local, prime);
            return publicKey;
        } else {
            throw new IllegalArgumentException("not enough params");
        }
    }

    public void setOpponentPublicKey(long opponentPublicKey) {
        this.opponentPublicKey = opponentPublicKey;

    }

    public long generateSecretKey() throws IllegalArgumentException {
        if (opponentPublicKey != 0 && local != 0 && prime != 0) {
            secretKey = modPow(opponentPublicKey, local, prime);

            return secretKey;
        } else {
            throw new IllegalArgumentException("Not enough params");
        }
    }

   // long DiffieHelman(long g, long p) {
//        long a = random(from, to);
//        //long b = random(FIRST_SCOPE, SECOND_SCOPE);
//
//        long A = modPow(g,a,p);
//        //long B = modPow(g,b,p);
//
//        long K1 = modPow(B,a,p);
//        long K2 = modPow(A,b,p);
//
//        return K1 == K2 ? K1 : -1;
   // }
}
