package pae.iot.processingcpp.DataFromInternet;

import android.os.AsyncTask;
import android.util.JsonReader;
import pae.iot.processingcpp.CustomStructures.*;
import pae.iot.processingcpp.*;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import  pae.iot.processingcpp.CustomStructures.*;
/**
 * Created by guillemllados on 31/10/17.
 */

public class AsyncGetDayData extends AsyncTask<String, String, String> {

    private static final String IP= Protocol.IP_SERVER;
    private List<Atributs> atributs;
    private onNewDataListener onNewDataListener;

    public AsyncGetDayData(onNewDataListener onNewDataListener) {

        this.onNewDataListener = onNewDataListener;
    }

    public interface onNewDataListener{
        public void onNewData(List<Atributs> llista);
    }

    @Override
    protected String doInBackground(String[] strings) {

        URL url;
        HttpURLConnection connection = null;
        try {

            String Surl = "https://"+ IP+"/getDayData.php?deviceId="+strings[0]+"&day="+strings[1];

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
            List<Atributs> llista = Principal.items.get(s).getAtributs();

            llista.addAll(atributs);

            this.onNewDataListener.onNewData(llista);
        }



    }

    public List<Atributs> getAtributs() {
        return atributs;
    }

    public List<Atributs> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<Atributs> readMessagesArray(JsonReader reader) throws IOException {
        List<Atributs> messages = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    public Atributs readMessage(JsonReader reader) throws IOException {

        String temperature = null;
        String humidity = null;
        CalendarE c = null;
        String cString = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("Temperature")) {
                temperature = reader.nextString();
            }else if (name.equals("Humidity")) {
                humidity = reader.nextString();
            }else if (name.equals("Calendar")){
                cString = reader.nextString();
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
        return new Atributs(c,temperature,humidity);

    }

    public int[] stringCalendatToInt(String calendar){

        String[] strings = calendar.split(" ");
        String[] date = strings[0].split("-");
        String[] time = strings[1].split(":");

        int[] result = {Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]),Integer.parseInt(time[0]),Integer.parseInt(time[1]),Integer.parseInt(time[2])};
        return result;

    }
}
