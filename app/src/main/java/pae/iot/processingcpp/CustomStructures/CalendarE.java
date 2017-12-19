package pae.iot.processingcpp.CustomStructures;

import java.util.Calendar;

/**
 * Created by guillemllados on 31/10/17.
 */

public class CalendarE {

    private Calendar c;

    public CalendarE(){
        c = Calendar.getInstance();
    }

    public CalendarE(Calendar c){
        this.c = c;
    }

    public CalendarE(String calendar){

        c = Calendar.getInstance();
        setDateTimeFromString(calendar);


    }

    public int getHour(){
        return c.get(Calendar.HOUR_OF_DAY);
    }
    public int getMin(){
        return c.get(Calendar.MINUTE);
    }
    public int getSec(){
        return c.get(Calendar.SECOND);
    }
    public int getMonth(){
        return c.get(Calendar.MONTH)+1;
    }
    public int getYear(){
        return c.get(Calendar.YEAR);
    }
    public int getDay(){
        return c.get(Calendar.DAY_OF_MONTH);
    }
    public int getDayOfWeek(){
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public void setDate(int day,int month ,int year){
        c.set(Calendar.DAY_OF_MONTH,day);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.YEAR,year);
    }

    public void setTime(int hour,int min ,int sec){
        c.set(Calendar.MINUTE,min);
        c.set(Calendar.SECOND,sec);
        c.set(Calendar.HOUR_OF_DAY,hour);
    }

    public void setDateTimeFromString(String dateTime){

        String[] calensplit = dateTime.split(" ");
        String[] data = calensplit[0].split("-");
        String[] time = calensplit[1].split(":");

        this.setDate(Integer.parseInt(data[2]),Integer.parseInt(data[1]),Integer.parseInt(data[0]));
        this.setTime(Integer.parseInt(time[0]),Integer.parseInt(time[1]),Integer.parseInt(time[2]));

    }

    public String toString(){

        return Integer.toString(getYear())+"-"+Integer.toString(getMonth())+"-"+Integer.toString(getDay())+" "+Integer.toString(getHour())+":"+Integer.toString(getMin())+":"+Integer.toString(getSec());
    }
    public String toStringSQL() {

        String month, day, hour, min, sec;

        if (getMonth() < 10) {
            month = "0" + getMonth();
        } else {
            month = Integer.toString(getMonth());
        }
        if (getDay() < 10) {
            day = "0" + getDay();
        } else {
            day = Integer.toString(getDay());
        }
        if (getHour() < 10) {
            hour = "0" + getHour();
        } else {
            hour = Integer.toString(getHour());
        }
        if (getMin() < 10) {
            min = "0" + getMin();
        } else {
            min = Integer.toString(getMin());
        }
        if (getSec() < 10) {
            sec = "0" + getSec();
        } else {
            sec = Integer.toString(getSec());
        }

        return "'"+Integer.toString(getYear()) + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec+"'";
    }
}
