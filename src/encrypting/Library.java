package encrypting;

/**
 * Created by Admin on 02.09.2016.
 */
public class Library{

    static final long FIRST_SCOPE= 1000000000L;
    static final long SECOND_SCOPE= 2000000000L;
    long firstKoef;
    long secondKoef;
    int index[];

    class Evklid {
        long first;

        long second;

        Evklid(long first, long second) {
            this.first = first;
            this.second = second;
        }
    }

    public long getUnsignedLong(long l)
    {
        String l1Str = Long.toUnsignedString(l);
        return Long.parseUnsignedLong(l1Str);
    }

    public long get_random_prime() {
        long r;
        while(true)
        {
            if (ferma(r = random(FIRST_SCOPE, SECOND_SCOPE))) return r;
        }
    }

    public long get_random_prime(long first, long second) {
        long r;
        while(true)
        {
            if (ferma(r = random(first, second))) return r;
        }
    }

    long modPow(long base, long exponent, long modulus) {
        long Result = 1;
        while(exponent > 0)
        {
            base = base % modulus;
            if ((exponent & 1) == 1)
            {
                Result= (Result*base) % modulus;
            }
            base*=base;
             exponent>>=1;
        }
        return Result;
    }

/*Нахождение НОД, проверка на простоту*/
    long evklid(long a, long b) {
        if (a == 0) {
            firstKoef = 0; secondKoef = 1;
            return b;
        }
        long d = evklid(b%a, a);
        long x1=firstKoef, y1=secondKoef;
        firstKoef = y1 - (b / a) * x1;
        secondKoef = x1;
        return d;
    }
//
//    public long Evklid2(long modulus, long base)
//    {
//        evklid u = new evklid(modulus, 0);
//        evklid v = new evklid(base, 1);
//        evklid t = new evklid();
//        evklid prelast = new evklid();
//        long q = 0;
//        while(v.first !=0)
//        {
//            q = u.first / v.first;
//            t.first = u.first%v.first;
//            t.second = u.second - q*v.second;
//            //System.out.println("t1 = " + t.first + " t2 = " + t.second + " q = " +  q);
//            u.first = v.first;
//            u.second = v.second;
//            prelast.first = v.first;
//            prelast.second = v.second;
//            v.first = t.first;
//            v.second = t.second;
//        }
//        if (prelast.second<0) prelast.second+=modulus;
//        return prelast.second;
//    }
/*Диффи-Хелман*/
    long DiffiHelman(long g, long p)
    {
        long a = random(FIRST_SCOPE, SECOND_SCOPE);
        long b = random(FIRST_SCOPE, SECOND_SCOPE);

        long A = modPow(g,a,p);
        long B = modPow(g,b,p);

        long K1 = modPow(B,a,p);
        long K2 = modPow(A,b,p);

        return K1 == K2 ? K1 : -1;
    }

    public long random(long a, long b)
    {
        long result=(long)(Math.random()*(b-a)+a);
        return result;
    }
/*Теорема Ферма о простоте числа*/
    boolean ferma(long number){
        if(number == 2)
            return true;
        for(int i=0;i<100;i++){
		long a = (int)(Math.random()*(number-2)+2);
            if (evklid(a, number) != 1)
                return false;
            if(modPow(a, number-1, number) != 1)
                return false;
        }
        return true;
    }
/*Находим числа g и р*/

    public long[] get_p_and_g()
    {
        while(true) {
            long q = random(Library.FIRST_SCOPE, Library.SECOND_SCOPE);
            if (ferma(q)) {
                    long[] p_and_g = calculateValues(q);
                    if (p_and_g != null) return p_and_g;
            }
        }
    }

    private long[] calculateValues(long q)
    {
        long[] mass = new long[2];
        mass[0] = 2*q + 1;
        if (ferma(mass[0]))
        {
            while(true) {
                mass[1] = random(1, mass[0] - 1);
                if (modPow(mass[1], q, mass[0]) != 1) return mass;
            }
        }
        return null;
    }
/*Шаг младенца, шаг великана*/
    public long giantStep(long a, long p, long y)
    {
        int m, k;
        int i, j = 0;
        m = (int) Math.sqrt(p);
        k = m;
        if ((Math.sqrt(p) % 1)!=0) {
            m++;
            k++;
        }
        int ryad1[] = ryad1(a,y, p,m);
        int ryad2[] = ryad2(a, p, m,k);
        index = new int[m];
        for(i=0; i<m;i++)
            index[i]=i;

        QuickSort(ryad2, 0, ryad2.length-1);

        for (i = 0; i < m; i++) {
            j = BSearch_1(ryad2, ryad2.length, ryad1[i]);
            if (j!=-1) break;
        }
        j++; //потому что начинаем с 1
        long x = m*j - i;

        if (modPow(a,x,p)==y)
        {
            System.out.println("Проверка пройдена");
            return x;
        }
        else
        {
            System.out.println("Проверка не пройдена!!!");
            System.out.println("Значение = " + modPow(a,x,p) + " X = " + x);
            return -1;
        }
    }

    private int[] ryad1(long a, long y, long p, int m)
    {
        int[] mass = new int[m];
        for (int i=0; i<m;i++)
        {
            mass[i] = (int)((Math.pow(a,i) * y)%p);
            System.out.print(mass[i] + " ");
        }
        System.out.println();
        return mass;
    }

    private int[] ryad2(long a, long p, int m, int k)
    {
        int[] mass = new int[k];
        for (int i=1; i<=k;i++)
        {
            mass[i-1] = (int)(Math.pow(a,i*m)%p);
            System.out.print(mass[i-1] + " ");
        }
        System.out.println();
        return mass;
    }

    private int BSearch_1 (int A[] , int n, int x){
        int L, R, m ;
        L = 0;
        R = (n - 1);
        while (L <= R){
            m = ((L + R) / 2);
            if ( A[index[m]] == x){
                return index[m];
            }
            if (A[index[m]] < x){
                L = (m + 1);
            }
            else{
                R = (m - 1);
            }
        }
        return -1;
    }

    private void QuickSort(int A[], int l, int r)
    {
        int x = A[index[(l + r) / 2]];
        int i = l, j = r,t;


        do {
            while (A[index[i]]<x) {
                i++;
            }
            while (A[index[j]]>x) {
                j--;
            }
            if (i <= j)
            {
                if (i < j) {
                    t = index[i];
                    index[i] = index[j];
                    index[j] = t;
                }
                i++;
                j--;
            }

        } while (i<=j);
        if (l < j)
            QuickSort(A, l, j);
        if (i < r)
            QuickSort(A, i, r);
    }
}
