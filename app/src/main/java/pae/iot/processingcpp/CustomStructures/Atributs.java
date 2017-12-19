package pae.iot.processingcpp.CustomStructures;

/**
 * Created by guillemllados on 31/10/17.
 */

public class Atributs {

    private CalendarE c;
    private String atrib1,atrib2;

    public Atributs(CalendarE c, String atrib1, String atrib2) {
        this.c = c;
        this.atrib1 = atrib1;
        this.atrib2 = atrib2;
    }

    public Atributs(CalendarE c) {
        this.c = c;
    }

    public String getAtrib1() {
        return atrib1;
    }
    public CalendarE getDate(){return c;}

    public String getAtrib2() {
        return atrib2;
    }
}
