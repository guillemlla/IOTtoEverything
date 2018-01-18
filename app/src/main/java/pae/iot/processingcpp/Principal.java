package pae.iot.processingcpp;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import pae.iot.processingcpp.CustomStructures.*;
import pae.iot.processingcpp.DataFromPic.SoundModulator;
import pae.iot.processingcpp.ItemActivities.*;
import pae.iot.processingcpp.DataFromInternet.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Principal extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton add;
    public static HashMap<String,Item> items;
    private ItemAdapter itemAdapter;
    public static Item itemclicked;
    public TextView textView;
    LinearLayoutManager llm;


    //SOUND VARS
    private final byte generatedSnd[] = new byte[246960]; //2*126*NSamples
    Handler handler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.principal);

        textView = (TextView) findViewById(R.id.tVTitol);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        items = new HashMap<>();
        items = inicialitzarItems(items);




        itemAdapter = new ItemAdapter(items, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int whatClick, final Item item) {
                Toast.makeText(getApplicationContext(), "Item +"+item.getNom()+"CLicked", Toast.LENGTH_SHORT).show();
                if(whatClick == itemAdapter.ITEM_CLICK){
                    itemclicked = item;
                    iniciaDispositiu();
                }else if(whatClick == itemAdapter.REFRESH_CLICK){

                    //UDATE DEL DEVICE POR SONIDO
                    Thread alarmThread = new Thread(new Runnable() {
                        public void run() {
                            Log.d(Sound.SOUND_THREAD,"Setting Alarm");
                            String alarma = item.getFutureAlarm();
                            String temps = alarma.split(" ")[0];
                            int hora = Integer.parseInt(temps.split(":")[0]);
                            int min = Integer.parseInt(temps.split(":")[1]);
                            String diaSetmana = alarma.split(" ")[1];
                            int dia =0;
                            for(int i = 0 ; i<Item.diesSetmama.length;i++){
                                if(diaSetmana.equals(Item.diesSetmama[i])){
                                   dia = i;
                                }
                            }

                            int[]  data = new int[60];
                            data = SoundModulator.soundModulator.setAlarm(hora,min,0,dia+1);

                            int [] data_all= new int[105];
                            int [] aux = new int[4];
                            int [] aux2;
                            for(int i=0; i<15; i++){
                                for(int j=0; j<4;j++){
                                    aux[j]=data[i*4+j];
                                }
                                aux2 = SoundModulator.soundModulator.generateData(aux);
                                System.arraycopy(aux2, 0, data_all, i*7, 7);
                            }

                            int [] soundCommand= SoundModulator.soundModulator.generateData(Sound.CONFIG_ALARM);

                            double[] generatedAudio = SoundModulator.soundModulator.dataToNoise(
                                    SoundModulator.soundModulator.mergeAll(data_all,soundCommand)
                            );

                            genTone(generatedAudio);
                            handler.post(new Runnable() {

                                public void run() {
                                    playSound();
                                }
                            });
                            item.alarmSendToDevice();
                            AsyncUpdateDeviceParameters asyncUpdateDeviceParameters = new AsyncUpdateDeviceParameters();
                            Parameters deviceParameters = new Parameters(item.getLatitude(),item.getLongitude(),item.getAlarm(),item.getFutureAlarm());
                            asyncUpdateDeviceParameters.sendParameters(item.getId(), deviceParameters);
                        }
                    });
                    alarmThread.start();



                }
            }
        });
        recyclerView.setAdapter(itemAdapter);


        AsyncGetLastInfoAllDevices asyncGetLastInfoAllDevices = new AsyncGetLastInfoAllDevices(new AsyncGetLastInfoAllDevices.onNewDataListener() {
            @Override
            public void onNewData(HashMap<String,Atributs> llista) {

                for(String s: items.keySet()){
                    if(llista.keySet().contains(s)){
                        items.get(s).setLastParams(llista.get(s).getAtrib1(),llista.get(s).getAtrib2(),llista.get(s).getDate());

                    }

                }

                recyclerView.swapAdapter(itemAdapter,false);
                recyclerView.setLayoutManager(llm);
                itemAdapter.notifyDataSetChanged();


            }
        });

        String s = "";
        for (Item item : items.values()) {
            s = s+item.getId()+",";
        }
        String[] params = {s};
        asyncGetLastInfoAllDevices.execute(params);

        add = (FloatingActionButton) findViewById(R.id.addItem);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sincronizeDialog(true);
            }
        });



    }

    public void iniciaDispositiu(){
        Intent i = new Intent(this,DispositiuPage.class);
        startActivity(i);
    }

    public void sincronizeDialog(final Boolean isNew){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogadd);
        dialog.setTitle("Afegeix DispositiuPage");

        // set the custom dialog components - text, image and button
        Button bttConfigure = (Button) dialog.findViewById(R.id.bttDialogId);

        Button bttFisic = (Button) dialog.findViewById(R.id.bttDiaglogFisic);
        final RelativeLayout dialogOptions = (RelativeLayout) dialog.findViewById(R.id.rlDialogOptions);

        bttConfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AsyncGetNewID asyncGetNewID = new AsyncGetNewID(new AsyncGetNewID.onNewDataListener() {
                    @Override
                    public void onNewData(String id) {
                        configurePic(id);
                    }
                });
                asyncGetNewID.execute();

            }
        });

        bttFisic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Principal.this,CameraActivity.class);
                startActivityForResult(i, Protocol.ACTIVITY_INTENT);
                dialog.cancel();

            }
        });

        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (Protocol.ACTIVITY_INTENT) : {
                if(resultCode != RESULT_CANCELED) {
                    if (resultCode == RESULT_OK) {
                        String finalResult = data.getStringExtra(Protocol.FINAL_STRING_IDENTIFIER);
                        //TODO Parse ID and Data separated

                        String IDBin = finalResult.substring(4,8);
                        Integer id = Integer.parseInt(IDBin,2);
                        String ID = Integer.toString(id);

                        String Data = finalResult.substring(16);
                        Log.d("debbug", finalResult);

                        Log.d("debbug", "ID: " + ID + "DATA: " + Data);


                        String[] tempsTemp = Data.split(",");
                        Item item;
                        String temp="";
                        String humitat = "";

                        if(!items.containsKey(ID)){

                            item = new Item("Item"+ID,ID,"null","0","0");
                            items.put(ID,item);
                            String[] parameters = {ID};
                            AsyncGetDeviceParameters getDeviceParameters = new AsyncGetDeviceParameters(new AsyncGetDeviceParameters.onNewDataListener() {
                                @Override
                                public void onNewData(List<AsyncGetDeviceParameters.DeviceParameters> llista) {
                                    for(AsyncGetDeviceParameters.DeviceParameters d : llista){
                                        items.get(d.getId()).setLatitude(d.getLatitude());
                                        items.get(d.getId()).setLongitude(d.getLongitude());
                                        items.get(d.getId()).setAlarm(d.getAlarm());
                                        items.get(d.getId()).alarmSendToDevice();
                                        items.get(d.getId()).setAlarm(d.getFutureAlarm());

                                    }
                                }
                            });
                            getDeviceParameters.execute(parameters);
                            
                            Atributs a = new Atributs(new CalendarE(),temp,humitat);
                            item.addAtribute(a);

                            recyclerView.destroyDrawingCache();
                            recyclerView.setAdapter(itemAdapter);
                            recyclerView.setLayoutManager(llm);
                            itemAdapter.notifyItemInserted(items.size());
                            itemAdapter.notifyDataSetChanged();


                        }else{
                            item = items.get(ID);
                        }

                        String dia,hora;

                        ArrayList<Atributs> atributs = new ArrayList<>();
                        //Parsing of the data of the device
                        for(String s : tempsTemp){
                            dia = s.substring(0,2);
                            hora = s.substring(2,4);
                            temp = s.substring(4,6);
                            humitat = s.substring(6,8);
                            CalendarE cE = new CalendarE();
                            cE.setDate(Integer.parseInt(dia),1,2018);
                            cE.setTime(Integer.parseInt(hora),12,2);
                            Atributs a = new Atributs(cE,temp,"0");
                            atributs.add(a);
                        }

                        Atributs a = new Atributs(new CalendarE(),temp,humitat);
                        atributs.add(a);
                        if(atributs.size()>0){
                            AsyncUpdateDeviceDB asyncUpdateDeviceDB = new AsyncUpdateDeviceDB();
                            asyncUpdateDeviceDB.sendAtrributes(ID,atributs);
                        }
                        getLastTimeInfo();

                    }
                }
                break;
            }default:{
                Item i = items.get(itemclicked.getId());
                if(i.hasFutureAlarm()){

                    i.setRefreshVisibility(true);
                    recyclerView.swapAdapter(itemAdapter,false);
                    recyclerView.setLayoutManager(llm);
                    itemAdapter.notifyDataSetChanged();

                }

                itemclicked = null;

                break;
            }
        }
    }

    public void getLastTimeInfo(){
        AsyncGetLastInfoAllDevices asyncGetLastInfoAllDevices = new AsyncGetLastInfoAllDevices(new AsyncGetLastInfoAllDevices.onNewDataListener() {
            @Override
            public void onNewData(HashMap<String,Atributs> llista) {

                for(String s: items.keySet()){
                    if(llista.keySet().contains(s)){
                        items.get(s).setLastParams(llista.get(s).getAtrib1(),llista.get(s).getAtrib2(),llista.get(s).getDate());

                    }

                }

                recyclerView.swapAdapter(itemAdapter,false);
                recyclerView.setLayoutManager(llm);
                itemAdapter.notifyDataSetChanged();


            }
        });

        String s = "";
        for (Item item : items.values()) {
            s = s+item.getId()+",";
        }
        String[] params = {s};
        //asyncGetLastInfoAllDevices.execute(params);
    }

    public HashMap<String,Item> inicialitzarItems(HashMap<String,Item> items){

        Item i = new Item("Item1", "1","null",Double.toString(41.423658), Double.toString(2.145692));
        CalendarE c = new CalendarE();
        Atributs atributs = new Atributs(c,"10", "30");
        i.addAtribute(atributs);
        c = new CalendarE();
         atributs = new Atributs(c,"10", "30");

        i.addAtribute(atributs);
        c = new CalendarE();
         atributs = new Atributs(c,"10", "30");

        i.addAtribute(atributs);
        c = new CalendarE();
         atributs = new Atributs(c,"10", "30");

        i.addAtribute(atributs);
        items.put(i.getId(),i);

        i = new Item("Item2", "2" ,"null",Double.toString(41.4013690), Double.toString(2.196325));
        c = new CalendarE();
         atributs = new Atributs(c,"10", "30");

        i.addAtribute(atributs);
        c = new CalendarE();
         atributs = new Atributs(c,"10", "30");

        i.addAtribute(atributs);
        c = new CalendarE();
         atributs = new Atributs(c,"10", "30");

        i.addAtribute(atributs);
        c = new CalendarE();
         atributs = new Atributs(c,"10", "30");

        i.addAtribute(atributs);
        items.put(i.getId(),i);


        return items;

    }

    @Override
    protected void onResume() {
        super.onResume();
        for (Item i: this.items.values()) {
            if(i.getFutureAlarm()!="") {
                i.setRefreshVisibility(true);

            }
        }
        getLastTimeInfo();
        recyclerView.swapAdapter(itemAdapter,false);
        recyclerView.setLayoutManager(llm);
        itemAdapter.notifyItemChanged(0);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Principal.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void genTone(double[] audiodouble) {

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : audiodouble) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }

    void playSound() {
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                44100, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }

    public void configurePic(String deviceId){


        final String finalID = deviceId;
        Thread idThread = new Thread(new Runnable() {
            public void run() {
                //int [][] raw_data_matrix_all= new int[4][6];
                Log.d(Sound.SOUND_THREAD,"Installing Device");
                int[]  data = new int[60];
                int id = Integer.parseInt(finalID);
                CalendarE c = new CalendarE();
                data = SoundModulator.soundModulator.installDevice(id,c.getYear()-2000,c.getMonth()
                        ,c.getDay(),c.getDayOfWeek(),c.getHour(),c.getMin(),c.getSec());
                //data = SoundModulator.soundModulator.installDevice(5,17,12,4,3,1,4,7);
                int [] data_all= new int[105];
                int [] aux = new int[4];
                int [] aux2;
                for(int i=0; i<15; i++){
                    for(int j=0; j<4;j++){
                        aux[j]=data[i*4+j];
                    }
                    aux2 = SoundModulator.soundModulator.generateData(aux);
                    System.arraycopy(aux2, 0, data_all, i*7, 7);
                }

                int [] soundCommand= SoundModulator.soundModulator.generateData(Sound.CONFIG_PIC);

                double[] generatedAudio = SoundModulator.soundModulator.dataToNoise(
                        SoundModulator.soundModulator.mergeAll(data_all,soundCommand)
                );

                genTone(generatedAudio);
                handler.post(new Runnable() {

                    public void run() {
                        playSound();
                    }
                });
            }
        });
        idThread.start();

    }
}
