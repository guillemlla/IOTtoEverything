package pae.iot.processingcpp.DataFromPic;

import android.util.Log;

import java.util.Random;


import pae.iot.processingcpp.CustomStructures.*;

import static java.lang.Math.max;
import static java.lang.Math.min;


public final class SoundModulator {

    private double[] wave;
    private Random random;
    private int NSamples = 980;
    //private int NSamples = 512;

    public static SoundModulator soundModulator = new SoundModulator();

    public SoundModulator() {

        this.random = new Random();

        wave = new double[126*NSamples];
    }

    public double[] dataToNoise(int[] data){
        Log.d(Sound.SOUND_LOG,"Input data length: "+data.length);

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < NSamples; j++) {
                double soroll = random.nextGaussian();
                wave[i * NSamples + j] = data[i]*((soroll > 0) ? min(soroll,1): max(soroll,-1));
            }
        }
        //Log.d(Sound.SOUND_LOG,"Output data length: "+wave.length);
        return wave;
    }

    public int[] generateData(int[] data_raw){

        int[] data = new int[7];
        for (int i = 0; i < 7; i++) {
            int aux = 0;
            for (int j = 0; j < 4; j++) {
                aux += Sound.MATRIX_G[i][j] * data_raw[j];
            }
            data[i] = aux % 2;
        }
        return data;
    }

    public int[] mergeAll(int[] data, int[] soundCommand){

        int[] data_all= new int[126];
        System.arraycopy(Sound.HEADER, 0, data_all, 0, 4 );
        System.arraycopy(soundCommand, 0, data_all, 4, 7 );
        System.arraycopy(data         ,0, data_all,11, 35);
        System.arraycopy(Sound.HEADER2, 0,data_all, 46, 5 );
        System.arraycopy(data         ,35,data_all,51, 42);
        System.arraycopy(Sound.HEADER2, 0,data_all, 93, 5 );
        System.arraycopy(data         ,77,data_all,98, 28);

        return data_all;
    }

    public void charToBinary(char input,int[] data_array ){
        int aux;
        for(int i= 0;i<8;i++){
            aux = (input&(0b10000000>>i))>>(7-i);
            if(aux==1){
                data_array[7-i] = 1;
            }else{
                data_array[7-i] = 0;
            }
        }
    }

    public int[] intToBinary(int input){
        int[] out = new int[8];
        int aux;
        for(int i= 0;i<8;i++){
            aux = (input&(0b10000000>>i))>>(7-i);
            if(aux==1){
                out[7-i] = 1;
            }else{
                out[7-i] = 0;
            }
        }
        return out;
    }

    //Install
    /*
    * Set New ID to Device (INT) and Set Time
    * - 8 bits for ID
    * 56 bits for Time
    * - 8bits YEAR
    * - 4bits MONTH
    * - 8bits DAY
    * - 8Bits Week Day
    * - 8Bits Hour
    * - 8Bits Minutes
    * - 8Bits Seconds
    * */

    public int[] installDevice(int id, int year, int month, int day, int weekday, int hour, int min, int sec){

        year = year-2000;

        int[] data_out = new int[60];
        System.arraycopy(intToBinary(id),       0, data_out, 0,  8);
        System.arraycopy(intToBinary(year),     0, data_out, 8,  8);
        System.arraycopy(intToBinary(month),    0, data_out, 16, 4);
        System.arraycopy(intToBinary(day),      0, data_out, 20, 8);
        System.arraycopy(intToBinary(weekday),  0, data_out, 28, 8);
        System.arraycopy(intToBinary(hour),     0, data_out, 36, 8);
        System.arraycopy(intToBinary(min),      0, data_out, 44, 8);
        System.arraycopy(intToBinary(sec),      0, data_out, 52, 8);

        return data_out;
    }

    //Set Alarm
    /*
    * - 8bits Hour
    * - 8bits Minutes
    * - 8bits Seconds
    * - 8Bits Week Days
    * - 0 padding
    * */

    public int[] setAlarm(int hour, int min, int sec, int weekday){

        int[] data_out = new int[60];
        System.arraycopy(intToBinary(hour),    0, data_out, 0, 8);
        System.arraycopy(intToBinary(min),     0, data_out, 8, 8);
        System.arraycopy(intToBinary(sec),     0, data_out, 16, 8);
        System.arraycopy(intToBinary(weekday), 0, data_out, 24, 8);

        return data_out;
    }

}
