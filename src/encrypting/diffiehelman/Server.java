package encrypting.diffiehelman;

import encrypting.Library;

import static encrypting.diffiehelman.Utils.modPow;
import static encrypting.diffiehelman.Utils.random;

public class Server {

    public long[] calculatePrimeGeneratorValues(){
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
        mass[0] = 2*q+1;
        if (ferma(mass[0]))
        {
            while(true) {
                mass[1] = random(1, mass[0] - 1);
                if (modPow(mass[1], q, mass[0]) != 1) return mass;
            }
        }
        return null;
    }

    private boolean ferma(long number){
        if(number == 2)
            return true;
        for(int i=0;i<100;i++){
            long a = (int)(Math.random()*(number-2)+2);

            Evklid evklid = new Evklid(a, number);
            if (evklid.calculate() != 1)
                return false;
            if(modPow(a, number-1, number) != 1)
                return false;
        }

        return true;
    }
}
