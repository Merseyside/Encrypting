package encrypting;

import com.sun.org.apache.xerces.internal.xs.StringList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Admin on 23.11.2016.
 */
public class Graf {
    enum Colors{RED,YELLOW,BLUE}
    Library enc = new Library();
    Cipher ch = new Cipher();
    ArrayList<Node> nodes_fake;
    ArrayList<String> edges;
    private class TwoSecrets{
        public long c1,c2;
    }

    private class ToBob{
        long Z,d,n;

        ToBob(long Z, long d, long n){
            this.Z = Z;
            this.d = d;
            this.n = n;
        }
    }

    private class Node{
        Colors color;
        int number;
        long r;
        Cipher.sub_RSA keys;
        String original_color;

        private void setToZero(int index){
            r &= ~(1 << index);
        }

        private void setToOne(int index){
            r |= (1 << index);
        }

        private void setBits(int second, int first){
            if (second==0) setToZero(1); else setToOne(1);
            if (first==0) setToZero(0); else setToOne(0);
        }

        Node(String color, int number){
            original_color = color;
            this.number = number;
        }

        private long calculateZ(){
            return enc.modPow(r,keys.open_d,keys.open_N);
        }

        private void fillNode(){
            r = enc.random(Library.FIRST_SCOPE,Library.SECOND_SCOPE);
            switch(original_color){
                case "r":
                    this.color = Colors.YELLOW;
                    setBits(0,0);
                    break;
                case "y":
                    this.color = Colors.BLUE;
                    setBits(1,0);
                    break;
                case "b":
                    this.color = Colors.RED;
                    setBits(0,1);
                    break;
            }
            keys = ch.getKeys_RSA();
        }
    }



    Graf(String filename){
        ArrayList<String> list = read(filename);
        String str = list.get(0);
        String[] strs = str.split(" ");
        nodes_fake = new ArrayList<>(Integer.valueOf(strs[0]));
        edges = new ArrayList<>(Integer.valueOf(strs[1]));
        String[] colors = list.get(list.size()-1).split(" ");
        for (int i = 0; i<Integer.valueOf(strs[0]); i++){
            Node node = new Node(colors[i], i);
            nodes_fake.add(node);
        }
        for (int i=1; i<Integer.valueOf(strs[1])+1; i++){
            edges.add(list.get(i));
        }
        ArrayList<ToBob> Z = sendToBob();
        checkByBob(Z);
    }

    private boolean checkByBob(ArrayList<ToBob> Z){
        int rand = (int)enc.random(0,Z.size()-1);
        TwoSecrets secrets = getSecretsOfEdge(rand);
        String[] nodes = edges.get(rand).split(" ");
        long z1,z2;
        z1 = enc.modPow(Z.get(Integer.valueOf(nodes[0])).Z, secrets.c1, Z.get(Integer.valueOf(nodes[0])).n);
        z2 = enc.modPow(Z.get(Integer.valueOf(nodes[1])).Z, secrets.c2, Z.get(Integer.valueOf(nodes[1])).n);
        String bits1 = getBit(z1,1) + getBit(z1,0);
        String bits2 = getBit(z2,1) + getBit(z2,0);
        System.out.println("z1  = " + z1 + "r = " + nodes_fake.get(Integer.valueOf(nodes[0])).r);
        if (bits1.equals(bits2)) System.out.println("Проверка пройдена" + bits2);
        else System.out.println("Проверка не пройдена");
        return true;
    }

    public final static String getBit(long value, int bitPosition)
    {
        if (((value >>> bitPosition) & 1) != 0) return "1";
        return "0";

    }

    private TwoSecrets getSecretsOfEdge(int index){
        String edge = edges.get(index);
        String[] twoEdges = edge.split(" ");
        TwoSecrets sec = new TwoSecrets();
        for (int i = 0; i<nodes_fake.size()-1; i++){
            if (nodes_fake.get(i).number == Integer.valueOf(twoEdges[0])) {
                sec.c1 = nodes_fake.get(i).keys.secret;
                break;
            }
            if (nodes_fake.get(i).number == Integer.valueOf(twoEdges[1])) {
                sec.c2 = nodes_fake.get(i).keys.secret;
                break;
            }
        }
        return sec;
    }




    private ArrayList<ToBob> sendToBob(){
        ArrayList<ToBob> Z = new ArrayList<>();
        for (Node node : nodes_fake){
            node.fillNode();
            ToBob tb = new ToBob(node.calculateZ(), node.keys.open_d, node.keys.open_N);
            Z.add(tb);
        }
        return Z;
    }

    private ArrayList<String> read(String filename){
        ArrayList<String> list = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            for(String line; (line = br.readLine()) != null; ) {
                // process the line.
                list.add(line);
            }
            // line is not visible here.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
