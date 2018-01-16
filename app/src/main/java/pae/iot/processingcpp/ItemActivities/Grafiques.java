package pae.iot.processingcpp.ItemActivities;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;

import pae.iot.processingcpp.CustomStructures.*;
import pae.iot.processingcpp.DataFromInternet.AsyncGetAtributesSingleDevice;
import pae.iot.processingcpp.R;
import pae.iot.processingcpp.*;


import java.util.ArrayList;
import java.util.List;




public class Grafiques extends Fragment implements DatePickerDialog.OnDateSetListener {

    CombinedChart combinedChart;
    private TextView datetext;
    private DatePickerDialog datePickerDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_grafiques, container, false);
        combinedChart = (CombinedChart) v.findViewById(R.id.chart);
        datetext = (TextView) v.findViewById(R.id.textViewDate);
        return v;

    }

    @Override
    public void onStart() {
        super.onStart();

        CalendarE calendarE = new CalendarE();
        datetext.setText(calendarE.getDay()+"-"+calendarE.getMonth()+"-"+calendarE.getYear());
        datePickerDialog = new DatePickerDialog(getContext(), Grafiques.this, calendarE.getYear(), calendarE.getMonth()-1, calendarE.getDay());

        AsyncGetAtributesSingleDevice asyncGetAtributesSingleDevice = new AsyncGetAtributesSingleDevice(new AsyncGetAtributesSingleDevice.onNewDataListener() {
            @Override
            public void onNewData(List<Atributs> llista) {
                actualitzaGrafiques(llista);
            }
        });
        String[] strings= {Principal.itemclicked.getId(),calendarE.toStringSQL()};
        asyncGetAtributesSingleDevice.execute(strings);



        datetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();

            }
        });


        combinedChart.setData(setDummyData());

        //estil
        combinedChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        combinedChart.getXAxis().setDrawGridLines(false);
        combinedChart.getAxisLeft().setDrawGridLines(false);
        combinedChart.getAxisRight().setDrawGridLines(false);

        combinedChart.getAxisRight().setAxisMinValue((float)-15);
        combinedChart.getAxisLeft().setAxisMinValue((float)0);
        combinedChart.getAxisLeft().setAxisMaxValue((float)100);

        combinedChart.getXAxis().setAxisLineWidth((float)2);
        combinedChart.getAxisLeft().setAxisLineWidth((float)2);
        combinedChart.getAxisRight().setAxisLineWidth((float)2);

        combinedChart.setDescription("");
        combinedChart.setDrawGridBackground(true);
        combinedChart.getAxisRight().setTextColor(Color.rgb(0, 155, 0));

    }


    public void actualitzaGrafiques(List<Atributs> list){

        ArrayList<String> dateTime = new ArrayList<>();
        ArrayList<Entry> lineEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        String data = datetext.getText().toString();
        int i = 0;

        for(Atributs a: list){

            if((a.getDate().getDay()+"-"+a.getDate().getMonth()+"-"+a.getDate().getYear()).equals(data)){
                dateTime.add(a.getDate().getHour()+":"+a.getDate().getMin());
                lineEntries.add(new Entry(Float.parseFloat(a.getAtrib2()),i));
                barEntries.add(new BarEntry(Float.parseFloat(a.getAtrib1()),i));
                i++;
            }

        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Temperatura (ºC)");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet.setColor(Color.rgb(0, 155, 0));
        lineDataSet.setCircleColor(Color.rgb(0, 155, 0));
        lineDataSet.setCircleSize(2);
        lineDataSet.setCircleColorHole(Color.rgb(155, 155, 155));

        LineData lineData = new LineData(dateTime, lineDataSet);
        lineData.setDrawValues(false);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Humitat (%)");
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData barData = new BarData(dateTime, barDataSet);
        barData.setDrawValues(false);

        CombinedData dataFinal = new CombinedData(dateTime);

        dataFinal.setData(lineData);
        dataFinal.setData(barData);

        combinedChart.setData(dataFinal);
        combinedChart.refreshDrawableState();
        combinedChart.buildDrawingCache();
        combinedChart.buildLayer();

    }

    public CombinedData setDummyData(){

        ArrayList<String> dateTime = new ArrayList<>();
        dateTime.add("00:00");dateTime.add("01:00");dateTime.add("02:00");dateTime.add("03:00");
        dateTime.add("04:00");dateTime.add("05:00");dateTime.add("06:00");dateTime.add("07:00");
        dateTime.add("08:00");dateTime.add("09:00");dateTime.add("10:00");dateTime.add("11:00");
        dateTime.add("12:00");dateTime.add("13:00");dateTime.add("14:00");dateTime.add("15:00");
        dateTime.add("16:00");dateTime.add("17:00");dateTime.add("18:00");dateTime.add("19:00");
        dateTime.add("20:00");dateTime.add("21:00");dateTime.add("22:00");dateTime.add("23:00");

        ArrayList<Entry> lineEntries = new ArrayList<>();
        for(int i = 0; i < 24; i++) {
            lineEntries.add(new Entry(0,i));
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i < 24; i++) {
            barEntries.add(new BarEntry(0,i));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Temperatura (ºC)");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet.setColor(Color.rgb(0, 155, 0));
        lineDataSet.setCircleColor(Color.rgb(0, 155, 0));
        lineDataSet.setCircleSize(2);
        lineDataSet.setCircleColorHole(Color.rgb(155, 155, 155));

        LineData lineData = new LineData(dateTime, lineDataSet);
        lineData.setDrawValues(false);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Humitat (%)");
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData barData = new BarData(dateTime, barDataSet);
        barData.setDrawValues(false);

        CombinedData data = new CombinedData(dateTime);

        data.setData(lineData);
        data.setData(barData);

        return data;



    }
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        datetext.setText(Integer.toString(i2)+"-"+Integer.toString(i1+1)+"-"+Integer.toString(i));
        CalendarE c = new CalendarE(i+"-"+i1+"-"+i2+" 00:00:00");
        String[] strings= {Principal.itemclicked.getId(),c.toStringSQL()};
        AsyncGetAtributesSingleDevice asyncGetAtributesSingleDevice = new AsyncGetAtributesSingleDevice(new AsyncGetAtributesSingleDevice.onNewDataListener() {
            @Override
            public void onNewData(List<Atributs> llista) {
                actualitzaGrafiques(llista);
            }
        });
        asyncGetAtributesSingleDevice.execute(strings);
    }
}
