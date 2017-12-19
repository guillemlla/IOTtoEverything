package pae.iot.processingcpp.CustomStructures;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Packet {
    private short bsn;
    private short counter;
    private short parityFrame;
    private char first, second,decodedFirst,decodedSecond;

    private static HashMap<Short,Character> characterMap;
    private static ArrayList<Short> dictionary;

    public Packet(short header, char first, char second){
        this.bsn = (short) (header>>4);
        this.counter = (short) (header&0x0F);
        this.first = first;
        this.second = second;
        //TODO parityFrame?

    }
    public void setChecksum(short checksum){
        this.counter = checksum;
    }

    public short getCounter(){
        return this.counter;
    }

    public Packet() {
        characterMap = new HashMap<Short,Character>();
        characterMap.put((short)0x00,'0');
        characterMap.put((short)0x01,'1');
        characterMap.put((short)0x05,'2');
        characterMap.put((short)0x15,'3');
        characterMap.put((short)0xFE,'4');
        characterMap.put((short)0xFA,'5');
        characterMap.put((short)0xEA,'6');
        characterMap.put((short)0xFF,'7');
        characterMap.put((short)0x55,'8');
        characterMap.put((short)0x08,'9');
        characterMap.put((short)0xF7,'T');
        characterMap.put((short)0xAF,'M');
        characterMap.put((short)0x50,':');
        characterMap.put((short)0xCC,'-');

        dictionary = new ArrayList<Short>();
        dictionary.add((short)(0x00));
        dictionary.add((short)(0x01));
        dictionary.add((short)(0x05));
        dictionary.add((short)(0x15));
        dictionary.add((short)(0xFE));
        dictionary.add((short)(0xFA));
        dictionary.add((short)(0xEA));
        dictionary.add((short)(0xFF));
        dictionary.add((short)(0x55));
        dictionary.add((short)(0x08));
        dictionary.add((short)(0xF7));
        dictionary.add((short)(0xAF));
        dictionary.add((short)(0x50));
        dictionary.add((short)(0xCC));

        this.bsn = -1;
    }

    public short getBsn() {
        return bsn;
    }

    public void setBsn(short bsn) {
        this.bsn = bsn;
    }

    public short getParityFrame(){ return parityFrame;}

    public void setParityFrame(short parityFrame){ this.parityFrame = parityFrame;}

    public char getFirst() {
        return first;
    }

    public void setFirst(char first) {
        this.first = first;
    }

    public char getSecond() {
        return second;
    }

    public void setSecond(char second) {
        this.second = second;
    }

    public boolean isNull(){
        return bsn == -1;
    }

    public char getDecodedFirst() {
        return decodedFirst;
    }

    public void setDecodedFirst(char decodedFirst) {
        this.decodedFirst = decodedFirst;
    }

    public char getDecodedSecond() {
        return decodedSecond;
    }

    public void setDecodedSecond(char decodedSecond) {
        this.decodedSecond = decodedSecond;
    }

    //if(counter==this.counter)
    //Mirar si els dos bytes de data rebuts estan dins del arraylist, si hi son buscar el valor en el hashmap i retornar true
    public boolean isValid(){
        short counter = 0;
        //Check Dictionary
        if(dictionary.contains((short)first) && dictionary.contains((short)second)){
            //Check Checksum Data1
            for(int i=0;i<8;i++){
                if(((first&0x80>>i)>>(7-i))==1) counter++;
            }
            //Check Checksum Data2
            for(int i=0;i<8;i++){
                if(((second&0x80>>i)>>(7-i))==1) counter++;
            }
            //Check Checksum BSN
            //short header = (short) (bsn | (parityFrame<<6));
            for(int i=0;i<8;i++){
                if(((bsn&0x80>>i)>>(7-i))==1) counter++;
            }

            //Check Checksum Parity
            if(parityFrame==1) counter++;

            if(counter==this.counter){
                decodedFirst = characterMap.get((short)first);
                decodedSecond = characterMap.get((short)second);
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public String parseFrame(Packet[] packetArray){

        String s = "";
        for(int i=0; i<packetArray.length; i++){
            //Log.d("",packetArray[i].decodedFirst);
            s += Character.toString(packetArray[i].getDecodedFirst())+Character.toString(packetArray[i].getDecodedSecond());
        }
        return s;
    }

    public String printPacket(){

        String res = "pkg: ";
        res += bsn;
        res+="|";
        res += decodedFirst;
        res+=":";
        res += decodedSecond;

        return res;
    }
}
