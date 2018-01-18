package pae.iot.processingcpp.DataFromInternet;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pae.iot.processingcpp.CustomStructures.Atributs;
import pae.iot.processingcpp.CustomStructures.CalendarE;
import pae.iot.processingcpp.Principal;

import  pae.iot.processingcpp.CustomStructures.*;

/**
 * Created by guillemllados on 19/12/17.
 */

public class AsyncGetDeviceParameters extends AsyncTask<String, String, String> {

    private static final String IP = Protocol.IP_SERVER;
    private List<DeviceParameters> atributs;
    private AsyncGetDeviceParameters.onNewDataListener onNewDataListener;

    public AsyncGetDeviceParameters(AsyncGetDeviceParameters.onNewDataListener onNewDataListener) {

        this.onNewDataListener = onNewDataListener;
    }

    public interface onNewDataListener {
        public void onNewData(List<DeviceParameters> llista);
    }

    @Override
    protected String doInBackground(String[] strings) {

        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            String Surl = "https://" + IP + "/getDeviceParameters.php/?deviceId=" + strings[0];

            url = new URL(Surl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Get Response
            atributs = readJsonStream(connection.getInputStream());
            for(DeviceParameters p : atributs){
                p.setId(strings[0]);
            }


            return strings[0];

        } catch (Exception e) {

            e.printStackTrace();


        } finally {

            if (connection != null) {
                connection.disconnect();
            }

        }
        return "-1";

    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);





            this.onNewDataListener.onNewData(atributs);



    }

    public List<DeviceParameters> getAtributs() {
        return atributs;
    }

    public List<DeviceParameters> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<DeviceParameters> readMessagesArray(JsonReader reader) throws IOException {
        List<DeviceParameters> messages = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    public DeviceParameters readMessage(JsonReader reader) throws IOException {

        String latitude= null;
        String longitude= null ;
        String alarm = null;
        String futureAlarm = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("Longitude")) {
                latitude = reader.nextString();
            } else if (name.equals("Latitude")) {
                longitude = reader.nextString();
            } else if (name.equals("Alarm")) {
                alarm = reader.nextString();
            } else if (name.equals("Future_Alarm")) {
                futureAlarm = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new DeviceParameters(latitude,longitude,alarm,futureAlarm);

    }

    public class DeviceParameters{
        String latitude,longitude,alarm,futureAlarm,id;

        public DeviceParameters(String latitude, String longitude, String alarm, String futureAlarm) {

            this.latitude = latitude;
            this.longitude = longitude;
            this.alarm = alarm;
            this.futureAlarm = futureAlarm;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getAlarm() {
            return alarm;
        }

        public String getFutureAlarm() {
            return futureAlarm;
        }
    }
}
