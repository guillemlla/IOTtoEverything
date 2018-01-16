package pae.iot.processingcpp.ItemActivities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import pae.iot.processingcpp.Item;
import pae.iot.processingcpp.Principal;
import pae.iot.processingcpp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Hores extends Fragment {

    private RecyclerView recyclerView;
    private List<HourItem> items;
    private HourAdapter hourAdapter;
    private Button bttConfirm;
    private boolean isActivated;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hores, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.hores_recycle);
        bttConfirm = (Button) v.findViewById(R.id.BttConfirmHour);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        isActivated = Principal.itemclicked.getAlarm() != "";
        bttConfirm.setVisibility(View.INVISIBLE);
        bttConfirm.setClickable(false);
        bttConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isActivated){
                    Item i = Principal.itemclicked;
                    HourItem h = items.get(0);
                    i.setAlarm(Integer.toString(h.getData().get(Calendar.HOUR_OF_DAY)),Integer.toString(h.getData().get(Calendar.MINUTE)),h.getArrayDies());
                    bttConfirm.setVisibility(View.INVISIBLE);
                    bttConfirm.setClickable(false);
                }else{
                    Principal.itemclicked.setAlarm("");
                    bttConfirm.setVisibility(View.INVISIBLE);
                    bttConfirm.setClickable(false);

                }

            }
        });
        items = new ArrayList<>();

        HourItem hourItem = new HourItem(Calendar.getInstance());
        items.add(hourItem);

        HourAdapter.OnItemClickListener onItemClick = new HourAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(HourItem item, int IdItemClick) {

                switch (IdItemClick){
                    case HourAdapter.TIME_CLICK: changeTime(); break;
                    case HourAdapter.CHANGE_DAY: update();
                    case HourAdapter.ACTIVATE: update();isActivated=true;break;
                    case HourAdapter.DESACTIVATE: {
                        bttConfirm.setVisibility(View.INVISIBLE);
                        bttConfirm.setClickable(false);
                        break;
                    }
                }

            }
        };




        hourAdapter = new HourAdapter(items,onItemClick);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(hourAdapter);

        /*buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickTimeAndCreateHourItem();

            }
        });*/


    }


    public void update(){

        bttConfirm.setVisibility(View.VISIBLE);
        bttConfirm.setClickable(true);

    }


    public void changeTime(){

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Calendar cFinal = c;
                cFinal.set(Calendar.HOUR_OF_DAY,selectedHour);
                cFinal.set(Calendar.MINUTE,selectedMinute);
                cFinal.set(Calendar.SECOND,0);
                HourItem h = items.get(0);
                h.setData(cFinal);
                hourAdapter.notifyDataSetChanged();
                update();

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();



    }



}
