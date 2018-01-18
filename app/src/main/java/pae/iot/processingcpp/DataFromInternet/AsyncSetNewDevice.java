package pae.iot.processingcpp.DataFromInternet;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import  pae.iot.processingcpp.CustomStructures.*;

/**
 * Created by guillemllados on 19/12/17.
 */

public class AsyncSetNewDevice extends AsyncTask<String, String, String> {

    private static final String IP = Protocol.IP_SERVER;
    private String result;
    private AsyncSetNewDevice.onNewDataListener onNewDataListener;

    public AsyncSetNewDevice(AsyncSetNewDevice.onNewDataListener onNewDataListener) {

        this.onNewDataListener = onNewDataListener;
    }

    public interface onNewDataListener {
        public void onNewData(String id);
    }

    @Override
    protected String doInBackground(String[] strings) {

        URL url;
        HttpURLConnection connection = null;
        try {
            //http://localhost/php/newDevice.php?deviceId=10
            String Surl = "https://" + IP + "/newDevice.php?deviceId="+strings[0];

            url = new URL(Surl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            result =  response.toString();


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

        if (!s.equals("-1")) {


            this.onNewDataListener.onNewData(result);
        }


    }
}
