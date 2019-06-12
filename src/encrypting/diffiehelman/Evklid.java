package encrypting.diffiehelman;

public class Evklid {

    private long a, b;

    long firstKoef = 0, secondKoef = 0;

    Evklid(long a, long b) {
        this.a = a;
        this.b = b;
    }

    public long calculate() {
        return evklid(a, b);
    }

    synchronized private long evklid(long a, long b) {
        if (a == 0) {
            firstKoef = 0; secondKoef = 1;
            return b;
        }
        long d = evklid(b % a, a);
        long x1 = firstKoef, y1 = secondKoef;
        firstKoef = y1 - (b / a) * x1;
        secondKoef = x1;
        return d;
    }
}
