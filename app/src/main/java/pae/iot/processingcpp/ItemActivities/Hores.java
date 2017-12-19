package pae.iot.processingcpp.ItemActivities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import pae.iot.processingcpp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Hores extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton buttonAdd;
    private List<HourItem> items;
    private HourAdapter hourAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hores, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.hores_recycle);
        //buttonAdd = (FloatingActionButton) v.findViewById(R.id.hores_button);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        items = new ArrayList<>();

        HourItem hourItem = new HourItem(Calendar.getInstance());
        items.add(hourItem);

        HourAdapter.OnItemClickListener onItemClick = new HourAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(HourItem item, int IdItemClick) {
                if(IdItemClick ==HourAdapter.TIME_CLICK){
                    pickTimeAndCreateHourItem();
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


    public void pickTimeAndCreateHourItem(){

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
                HourItem newHourItem = new HourItem(cFinal);
                items.add(newHourItem);
                hourAdapter.notifyItemInserted(items.size()-1);



            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();



    }



}
