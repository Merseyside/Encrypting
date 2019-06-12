package encrypting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Admin on 02.09.2016.
 */
public class Tests {

    public static void main(String[] args) {
        Library enc = new Library();
        System.out.println(enc.modPow(22344548,568346,7));
        System.out.println("НОД = " + enc.evklid(1071,462));
        System.out.println("Первый коэффициент = " + enc.firstKoef);
        System.out.println("Второй коэффициент = " + enc.secondKoef);

                long[] p_and_g = enc.get_p_and_g();
                    System.out.println("Ключ для обоих абонентов = " + enc.DiffiHelman(p_and_g[1], p_and_g[0]));

        System.out.println("Шаг великана = " + enc.giantStep(2,61,45));
        Cipher ch = new Cipher();
        String path = "D://Ucheba//4 курс//Защита информации//lab1//stuff//";
        String filename = "icon.png";
        Cipher.sub_shamir[] keys_shamir;
        try {
            keys_shamir = ch.Shamir_encrypt(path, filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Decode?");
            String s = br.readLine();
            ch.Shamir_decrypt(path, filename, keys_shamir);
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*Cipher.sub_algamal keys_algamal[];
        try {
            keys_algamal = ch.Al_Gamal_encrypt(path, filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Decode?");
            String s = br.readLine();
            ch.Al_Gamal_decrypt(path, filename, keys_algamal);
        } catch (IOException e) {
            e.printStackTrace();
        }*/



        /*try {
            Cipher.sub_RSA keys_RSA = ch.RSA_encrypt(path, filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Decode?");
            String s = br.readLine();
            ch.RSA_decrypt(path, filename, keys_RSA);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*try {
            byte[] key = ch.Vernam_encrypt(path, filename);
            ch.Vernam_decrypt(path, filename, key);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //DigitalSign sign = new DigitalSign();

        /*Cipher.sub_RSA keys = sign.RSA_sign(path, filename);
        boolean flag = sign.RSA_sign_check(path,filename,keys);
        if (flag) System.out.println("Файл подленный");
        else System.out.println("Файл не подленный");*/

        /*DigitalSign.sub_algamal keys = sign.Al_Gamal(path, filename);
        boolean flag = sign.Al_Gamal_check(path,filename,keys);
        if (flag) System.out.println("Файл подленный");
        else System.out.println("Файл не подленный");*/

        /*DigitalSign.gost gost = sign.GOST(path, filename);
        boolean flag = sign.check_gost(path, filename, gost);
        if (flag) System.out.println("Файл подленный");
        else System.out.println("Файл не подленный");*/

        /*DigitalCache dc = new DigitalCache();
        dc.signCache(500);

        long p = 17, q = 7, secret = 77;
        long Fi = (p-1)*(q-1);
        long open_d;
        while(true)
        {
            if (enc.evklid(secret, Fi) == 1) {
                open_d = enc.Evklid2(Fi, secret);
                break;
            }
        }
        System.out.println(open_d);*/
        String path = "D://Ucheba//4 курс//Защита информации//lab1//stuff//";
        String filename = "graf.txt";
        Graf graf = new Graf(path + filename);


    }
}
