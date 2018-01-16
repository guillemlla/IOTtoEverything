package pae.iot.processingcpp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import pae.iot.processingcpp.CustomStructures.*;

/**
 * Created by guillemllados on 5/10/17.
 */

public class Item {

    private String nom,id;
    private ArrayList<Atributs> atributs;
    private String image;
    private String latitude,longitude;
    private String alarm,futureAlarm; //hh:mm dl-dt-dm-dj-dv-ds-dg
    public ImageButton refreshDevice;

    private String temp,hum;
    private CalendarE c;

    public static final String[] diesSetmama = {"dl","dt","dm","dj","dv","ds","dg"};

    public Item(String nom, String id,String image,String latitude, String longitude) {
        this.nom = nom;
        atributs = new ArrayList<>();
        this.id = id;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        alarm = "";
        futureAlarm = "";

    }

    public boolean hasFutureAlarm(){
        return  this.futureAlarm!= null;
    }

    public void setRefreshVisibility(boolean isVisible){

        if(isVisible){
            refreshDevice.setVisibility(View.VISIBLE);
            refreshDevice.setClickable(true);
        }else{
            refreshDevice.setVisibility(View.INVISIBLE);
            refreshDevice.setClickable(false);
        }


    }

    public String getFutureAlarm() {
        return futureAlarm;
    }

    public void setRefreshDevice(ImageButton refreshDevice) {
        this.refreshDevice = refreshDevice;
    }

    public boolean setAlarm(String hora, String min, int[] diesSetmana){
        if(diesSetmana.length==7){

            String result = hora+":"+min+" ";

            for(int i = 0;i<diesSetmana.length;i++){
                if(diesSetmana[i]==1){
                    result += this.diesSetmama[i]+"-";
                }
            }
            if(result.lastIndexOf("-") == result.length()-1){
                result = result.substring(0,result.length()-1);
            }
            futureAlarm = result;
            return true;

        }
        return false;
    }
    public void setAlarm(String alarm){
        futureAlarm = alarm;
    }


    public void alarmSendToDevice(){
        alarm = futureAlarm;
        futureAlarm = null;
    }

    public ArrayList<Atributs> getAtributs() {
        return atributs;
    }

    public void addAtribute(Atributs a){
        atributs.add(a);
    }

    public void addAtribute(int day,int month, int year, int hour, int min, int sec, String atrib1, String atrib2){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH,day);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.YEAR,year);
        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,min);
        c.set(Calendar.SECOND,sec);
        CalendarE cg = new CalendarE(c);
        atributs.add(new Atributs(cg,atrib1,atrib2));

    }

    public void addAtribute(Calendar c, String atrib1, String atrib2){
        CalendarE cg = new CalendarE(c);
        atributs.add(new Atributs(cg,atrib1,atrib2));
    }
    public void addAtribute(CalendarE cg, String atrib1, String atrib2){

        atributs.add(new Atributs(cg,atrib1,atrib2));
    }

    public void setLastParams(String temp,String hum,CalendarE c){
        this.temp = temp;
        this.hum = hum;
        this.c = c;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNom() {
        return nom;
    }

    public String getLastAtrib1() {
        if(atributs.size()==0){
            return "00";
        }
        if(temp == null){
            return atributs.get(atributs.size()-1).getAtrib1();
        }

        return temp ;
    }

    public String getLastAtrib2() {
        if(atributs.size()==0){
            return "00";
        }
        if(hum == null){
            return atributs.get(atributs.size()-1).getAtrib2();
        }

        return hum;
    }

    public String  getId() {
        return id;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String lastTimeUpdated(){
        CalendarE now  = c;
        if(c == null){
           return "";
        }

        return c.toString();


    }





}



