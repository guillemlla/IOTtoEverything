package pae.iot.processingcpp.ItemActivities;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by guillemllados on 19/10/17.
 */

public class HourItem {

    private Calendar calendar;
    private boolean bDl,bDm,bDt,bDv,bDj,bDs,bDg;
    private boolean isActive;


    public HourItem(Calendar calendar) {
        this.calendar = calendar;

        bDl=false;
        bDt=false;
        bDm=false;
        bDj=false;
        bDv=false;
        bDs=false;
        bDg=false;
        isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setData(Calendar calendar) {
        this.calendar = calendar;
    }

    public Calendar getData() {
        return calendar;
    }

    public void activeDies(String dies){ //"dl,dm,dj,dv"
        String[] separado = dies.split(",");
        for (String s: separado) {
            if(s.equals("dl")){
                bDl = true;
            }else if(s.equals("dt")){
                bDt = true;
            }else if(s.equals("dm")){
                bDm = true;
            }else if(s.equals("dj")){
                bDj = true;
            }else if(s.equals("dv")){
                bDv = true;
            }else if(s.equals("ds")){
                bDs = true;
            }else if(s.equals("dg")){
                bDg = true;
            }
        }
    }

    public void desactiveDies(String dies){ //"dl,dm,dj,dv"
        String[] separado = dies.split(",");
        for (String s: separado) {
            if(s.equals("dl")){
                bDl = false;
            }else if(s.equals("dt")){
                bDt = false;
            }else if(s.equals("dm")){
                bDm = false;
            }else if(s.equals("dj")){
                bDj = false;
            }else if(s.equals("dv")){
                bDv = false;
            }else if(s.equals("ds")){
                bDs = false;
            }else if(s.equals("dg")){
                bDg = false;
            }
        }
    }

    public String getDies(){
        String s = "";

        if(bDl == true) {
            s += "dl,";
        }
        if(bDt == true) {
            s += "dt,";
        }
        if(bDm == true) {
            s += "dm,";
        }
        if(bDj == true) {
            s += "dj,";
        }
        if(bDv == true) {
            s += "dv,";
        }
        if(bDs == true) {
            s += "ds,";
        }
        if(bDg == true){
            s+="dg,";
        }
        return s;
    }

}
