package pae.iot.processingcpp.DataFromInternet;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pae.iot.processingcpp.CustomStructures.Atributs;
import pae.iot.processingcpp.CustomStructures.CalendarE;
import pae.iot.processingcpp.Principal;

import  pae.iot.processingcpp.CustomStructures.*;

/**
 * Created by guillemllados on 16/11/17.
 */

public class AsyncGetLastInfoAllDevices extends AsyncTask<String, String, String> {

    private static final String IP= Protocol.IP_SERVER;
    private HashMap<String,Atributs> atributs;
    private AsyncGetLastInfoAllDevices.onNewDataListener onNewDataListener;

    public AsyncGetLastInfoAllDevices(AsyncGetLastInfoAllDevices.onNewDataListener onNewDataListener) {

        this.onNewDataListener = onNewDataListener;
    }

    public interface onNewDataListener{
        public void onNewData(HashMap<String,Atributs> llista);
    }

    @Override
    protected String doInBackground(String[] strings) {

        URL url;
        HttpURLConnection connection = null;
        int i = 1;
        i++;
        try {
            //http://localhost/php/getDataLastUpdateAll.php?deviceId=1,2
            String Surl = "http://"+ IP+"/php/getDataLastUpdateAll.php/?deviceId="+strings[0];

            url = new URL(Surl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Get Response
            atributs = readJsonStream(connection.getInputStream());


            return strings[0];

        } catch (Exception e) {

            e.printStackTrace();


        } finally {

            if(connection != null) {
                connection.disconnect();
            }

        }
        return  "-1";

    }



    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(!s.equals("-1")){

            this.onNewDataListener.onNewData(atributs);
        }



    }

    public HashMap<String,Atributs> getAtributs() {
        return atributs;
    }

    public HashMap<String,Atributs> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public HashMap<String,Atributs> readMessagesArray(JsonReader reader) throws IOException {
        HashMap<String,Atributs> messages = new HashMap<>();

        reader.beginArray();
        while (reader.hasNext()) {
            Estructura e = readMessage(reader);
            messages.put(e.getId(),e.getAtribut());
        }
        reader.endArray();
        return messages;
    }

    public Estructura readMessage(JsonReader reader) throws IOException {

        String temperature = null;
        String humidity = null;
        CalendarE c = null;
        String cString = null;
        String id = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("Temperature")) {
                temperature = reader.nextString();
            }else if (name.equals("Humidity")) {
                humidity = reader.nextString();
            }else if (name.equals("Calendar")) {
                cString = reader.nextString();
            }else if (name.equals("Device_ID")){
                id = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        c = new CalendarE();
        int[] calendarDateTime = stringCalendatToInt(cString);
        c.setDate(calendarDateTime[2],calendarDateTime[1]-1,calendarDateTime[0]);
        c.setTime(calendarDateTime[3],calendarDateTime[4],calendarDateTime[5]);
        String s = "hour" +c.getHour()+"MiN"+c.getMin()+"SEC"+c.getSec()+"Dia"+c.getDay()+"Month"+c.getMonth()+"Year"+c.getYear();
        int i = 1;

        return new Estructura(id, new Atributs(c,temperature,humidity));

    }

    public int[] stringCalendatToInt(String calendar){

        String[] strings = calendar.split(" ");
        String[] date = strings[0].split("-");
        String[] time = strings[1].split(":");

        int[] result = {Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]),Integer.parseInt(time[0]),Integer.parseInt(time[1]),Integer.parseInt(time[2])};
        return result;

    }


    private class Estructura{
        Atributs a;
        String id;

        public Estructura( String id,Atributs a) {
            this.a = a;
            this.id = id;
        }

        public Atributs getAtribut() {
            return a;
        }

        public String getId() {
            return id;
        }
    }
}