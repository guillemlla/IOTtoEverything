package pae.iot.processingcpp.CustomStructures;

/**
 * Created by guillemllados on 18/1/18.
 */

public class Parameters{
    String latitude,longitude,alarm,futureAlarm,id;

    public Parameters(String latitude, String longitude, String alarm, String futureAlarm) {

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
