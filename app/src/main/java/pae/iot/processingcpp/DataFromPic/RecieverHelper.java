package pae.iot.processingcpp.DataFromPic;

import android.util.Log;

import org.opencv.core.Mat;
import java.util.ArrayList;
import java.util.List;
import pae.iot.processingcpp.CustomStructures.*;

public final class RecieverHelper {
    private FrameListener frameListener = null;
    private RepeatFlashListener flashListener = null;
    private int packetCounter = 0;
    private Packet[] packetArray = new Packet[8];
    private short parityFrame = 0;
    private int flashFailedCounter = 0;
    private int repeatFlashThreshold = 20;

    public static RecieverHelper lightReciever = new RecieverHelper();

    public void processFrame(ArrayList<Integer> frameArray, Packet packet){


        List<Integer> data;
        int startPosition = 0, aux = 0, startSequence = 0xBF, window = 0x80, packetStartAux;
        short[] res = new short[4];
        boolean found = false;

        try{
            for(int i=0;i<frameArray.size();i++){
                for(int j=0;j<8;j++){
                    if(frameArray.get((i+j)%frameArray.size())!=(startSequence&window>>j)>>(7-j)){
                        found = false;
                        break;
                    }else found = true;
                }
                if(found) startPosition = (i+8)%frameArray.size();
            }
            frameArray.toArray();
            if(startPosition>7&&startPosition!=frameArray.size()-1){
                packetStartAux = startPosition-8-(16*4-(frameArray.size()-1-startPosition));
                data = frameArray.subList(startPosition, frameArray.size()-1);

                if(packetStartAux<0) throw new Exception("Fix this error");

                data.addAll(frameArray.subList(packetStartAux, startPosition-8));
                for(int k=0;k<data.size();k++){
                    if(k!=0&&k%16==0){

                        res[k/15-1] = manchesterDemodulate(aux);
                        aux = 0;
                    }
                    aux = aux<<1;
                    aux |= data.get(k);

                }
                res[3] = manchesterDemodulate(aux);
                packet.setBsn((short) (res[0] & 0b00111111));
                //Log.d(Protocol.RECEIVER_TAG,"Bsn: "+packet.getBsn());
                packet.setParityFrame((short)((res[0] & 0b01000000)>>6));
                //Log.d(Protocol.RECEIVER_TAG,"Parity: "+packet.getParityFrame());
                packet.setFirst((char) res[1]);
                packet.setSecond((char) res[2]);
                packet.setChecksum(res[3]);


                if(packet.isValid() && packet.getParityFrame() != parityFrame){

                    flashFailedCounter++;
                    Log.d("Debbug","Flash Pack Counter: "+flashFailedCounter);
                    if(flashFailedCounter>=repeatFlashThreshold){
                        Log.d("Debbug","Repeat Flash Request");
                        flashFailedCounter = 0;
                        flashListener.onRepeatRequest();
                    }
                }else if(!packet.isValid() || packet.getParityFrame() != parityFrame) {
                    packet.setBsn((short) -1);
                    throw new Exception("Checksum Error");
                }else{

                    if(packetArray[packet.getBsn()]==null) {
                        packetArray[packet.getBsn()] = packet;
                        packetCounter++;
                        frameListener.onProgressChanged(packetCounter*100/8);

                        if(packetCounter==8){

                            String result = packet.parseFrame(packetArray);
                            //Log.d(Protocol.RECEIVER_TAG,"Decoded Frame: "+result+" -> Parity: "+packet.getParityFrame());
                            packetCounter=0;
                            packetArray = new Packet[8];
                            frameListener.onFrameDecoded(result);
                        }
                    }
                }

            }else{
                throw new Exception("Invalid Packet");
            }

        }catch(Exception e) {
            //Log.e("Receiver", e.getMessage());
        }
    }

    private short manchesterDemodulate(int manchesterData){
        int window = 0x03;
        short data = 0;
        for(int i=7;i>=0;i--){
            if((manchesterData&(window<<(i*2)))>>i*2==0x02) data = (short) ((data<<1)|1);
            else{
                data = (short) (data<<1);
            }
        }
        return data;
    }

    public ArrayList<Integer> downsampling(short[] a){

        int width, bit=a[0], start=0, mean=0, min1=a.length, min0=a.length;
        int add1=0, add0=0,number1=0, number0=0;
        ArrayList<Integer> stream = new ArrayList<Integer>();
        for(int i=1;i<a.length;i++){
            if(bit!=a[i]){
                width = i-start;
                start = i;
                bit=a[i];
                if((bit+1)%2==1){
                    if(width<=40){
                        add1 += width;
                        number1++;}
                    if(width<min1&&width>10){
                        min1 = width;
                    }
                }else{
                    add0 += width;
                    number0++;
                    if(width<min0&&width>5){
                        min0 = width;
                    }
                }
            }
        }
        bit = a[0];
        start = 0;
        if(number1!=0) min1 = add1/number1-2;
        if(number0!=0) min0 = add0/number0;
        for(int i=0;i<a.length;i++){
            if(bit!=a[i]){
                width = i-start;

                start = i;
                bit=a[i];
                if((bit+1)%2==1){
                    if(width>(4*min1-min1/2)){
                        for (int k = 0; k < 6; k++) {
                            stream.add(1);
                        }
                    }else if(width>(2*min1-min1/2)){
                        for(int k=0;k<2;k++){
                            stream.add(1);
                        }
                    }else{
                        stream.add(1);
                    }
                }else{
                    if(width>(2*min0-min0/2)){
                        for(int k=0;k<2;k++){
                            stream.add(0);
                        }
                    }else{
                        stream.add(0);
                    }
                }
            }
        }
        return stream;
    }

    public void setParityFrame(short parityFrame){
        this.parityFrame = parityFrame;
    }

    public short getParityFrame(){
        return parityFrame;
    }

    public short[] toShort(Mat input){

        int size = input.cols()*input.rows();
        short[] result = new short[size];

        for(int i=0;i<size;i++){
            double[] inside = input.get(i,0);
            result[i] = (short)inside[0];
        }

        return result;
    }

    public void setFrameListener(FrameListener listener) {
        this.frameListener = listener;
    }

    public void setRepeatFlashListener(RepeatFlashListener listener) {
        this.flashListener = listener;
    }

    public interface FrameListener {
        void onFrameDecoded(String frame);
        void onProgressChanged(int progress);
    }

    public interface RepeatFlashListener {
        void onRepeatRequest();
    }

}



