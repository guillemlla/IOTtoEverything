package pae.iot.processingcpp.DataFromInternet;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import pae.iot.processingcpp.CustomStructures.Atributs;
import pae.iot.processingcpp.CustomStructures.Parameters;
import pae.iot.processingcpp.CustomStructures.Protocol;

/**
 * Created by guillemllados on 18/1/18.
 */

public class AsyncUpdateDeviceParameters extends AsyncTask<String,String,String> {

    private static final String IP = Protocol.IP_SERVER;
    private List<Atributs> atributs;




    @Override
    protected String doInBackground(String[] strings) {

        URL url;
        HttpURLConnection connection = null;
        OutputStream out = null;
        BufferedReader reader;
        try {
            //Create connection
            String Surl = "https://"+ IP+"/updateDeviceParameters.php";

            url = new URL(Surl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());


            out = new BufferedOutputStream(connection.getOutputStream());

            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

            writer.write("Data="+strings[0]);

            writer.flush();

            int responseCode = connection.getResponseCode();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            String response = sb.toString();


            return strings[0];

        } catch (Exception e) {

            e.printStackTrace();


        } finally {

            if(connection != null) {
                connection.disconnect();
                return strings[0];
            }


        }
        return  "-1";

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(!s.equals("-1")){
            //ALL OKEI
        }

    }

    public void sendParameters(String id, Parameters deviceParameters){

        JSONArray jsonArray = new JSONArray();

        try{

            JSONObject object = new JSONObject();

            object.put("id",id);
            object.put("latitude",deviceParameters.getLatitude());
            object.put("longitude",deviceParameters.getLongitude());
            object.put("alarm",deviceParameters.getAlarm());
            object.put("futureAlarm",deviceParameters.getFutureAlarm());

            jsonArray.put(object);



            JSONObject dataToSendJson = new JSONObject();
            dataToSendJson.put("Parameters", jsonArray);

            this.execute(dataToSendJson.toString());
        }catch(Exception e){
            e.printStackTrace();
        }




    }

}

