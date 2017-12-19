package pae.iot.processingcpp.ItemActivities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import pae.iot.processingcpp.R;
import java.util.Calendar;
import java.util.List;

/**
 * Created by guillemllados on 19/10/17.
 */

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.ViewHolder>{

    private List<HourItem> items;
    public static final int TIME_CLICK = 0;

    public interface OnItemClickListener{
        void onItemClick(HourItem item,int IdItemClick);

    }

    private HourAdapter.OnItemClickListener onItemClickListener;


    public HourAdapter(List<HourItem> items,HourAdapter.OnItemClickListener listener) {
        this.items = items;
        this.onItemClickListener = listener;

    }

    public void addItem(HourItem i){
        items.add(i);
    }

    public void getItem(int pos){
        items.get(pos);
    }

    @Override public HourAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_hores_item, parent, false);
        return new HourAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final HourAdapter.ViewHolder holder, int position) {
        final HourItem item = items.get(position);

        setButtonOnClick(holder,item);
        int hora = item.getData().get(Calendar.HOUR_OF_DAY);
        String shora;
        if(hora<10){
            shora = "0"+Integer.toString(hora);
        }else{
            shora = Integer.toString(hora);
        }
        int min = item.getData().get(Calendar.MINUTE);
        String smin;
        if(min<10){
            smin = "0"+Integer.toString(min);
        }else{
            smin = Integer.toString(min);
        }
        holder.hora.setText(shora+":"+smin);

        holder.timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.setActive(b);
            }
        });

    }

    public void setButtonOnClick(final HourAdapter.ViewHolder holder,final HourItem item){

        holder.hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onItemClickListener.onItemClick(item,TIME_CLICK);

            }
        });

        holder.dl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getDies().contains("dl")){
                    item.desactiveDies("dl");
                    holder.dl.setBackgroundColor(holder.view.getResources().getColor(R.color.transparent));
                    holder.dl.setTextColor(holder.view.getResources().getColor(R.color.black));
                }else{
                    item.activeDies("dl");
                    holder.dl.setBackgroundColor(holder.view.getResources().getColor(R.color.colorPrimaryDark));
                    holder.dl.setTextColor(holder.view.getResources().getColor(R.color.white));
                }
            }
        });

        holder.dm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getDies().contains("dm")){
                    item.desactiveDies("dm");
                    holder.dm.setBackgroundColor(holder.view.getResources().getColor(R.color.transparent));
                    holder.dm.setTextColor(holder.view.getResources().getColor(R.color.black));
                }else{
                    item.activeDies("dm");
                    holder.dm.setBackgroundColor(holder.view.getResources().getColor(R.color.colorPrimaryDark));
                    holder.dm.setTextColor(holder.view.getResources().getColor(R.color.white));
                }
            }
        });

        holder.dt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getDies().contains("dt")){
                    item.desactiveDies("dt");
                    holder.dt.setBackgroundColor(holder.view.getResources().getColor(R.color.transparent));
                    holder.dt.setTextColor(holder.view.getResources().getColor(R.color.black));
                }else{
                    item.activeDies("dt");
                    holder.dt.setBackgroundColor(holder.view.getResources().getColor(R.color.colorPrimaryDark));
                    holder.dt.setTextColor(holder.view.getResources().getColor(R.color.white));
                }
            }
        });

        holder.dj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getDies().contains("dj")){
                    item.desactiveDies("dj");
                    holder.dj.setBackgroundColor(holder.view.getResources().getColor(R.color.transparent));
                    holder.dj.setTextColor(holder.view.getResources().getColor(R.color.black));
                }else{
                    item.activeDies("dj");
                    holder.dj.setBackgroundColor(holder.view.getResources().getColor(R.color.colorPrimaryDark));
                    holder.dj.setTextColor(holder.view.getResources().getColor(R.color.white));
                }
            }
        });

        holder.dv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getDies().contains("dv")){
                    item.desactiveDies("dv");
                    holder.dv.setBackgroundColor(holder.view.getResources().getColor(R.color.transparent));
                    holder.dv.setTextColor(holder.view.getResources().getColor(R.color.black));
                }else{
                    item.activeDies("dv");
                    holder.dv.setBackgroundColor(holder.view.getResources().getColor(R.color.colorPrimaryDark));
                    holder.dv.setTextColor(holder.view.getResources().getColor(R.color.white));
                }
            }
        });

        holder.ds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getDies().contains("ds")){
                    item.desactiveDies("ds");
                    holder.ds.setBackgroundColor(holder.view.getResources().getColor(R.color.transparent));
                    holder.ds.setTextColor(holder.view.getResources().getColor(R.color.black));
                }else{
                    item.activeDies("ds");
                    holder.ds.setBackgroundColor(holder.view.getResources().getColor(R.color.colorPrimaryDark));
                    holder.ds.setTextColor(holder.view.getResources().getColor(R.color.white));
                }
            }
        });

        holder.dg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getDies().contains("dg")){
                    item.desactiveDies("dg");
                    holder.dg.setBackgroundColor(holder.view.getResources().getColor(R.color.transparent));
                    holder.dg.setTextColor(holder.view.getResources().getColor(R.color.black));
                }else{
                    item.activeDies("dg");
                    holder.dg.setBackgroundColor(holder.view.getResources().getColor(R.color.colorPrimaryDark));
                    holder.dg.setTextColor(holder.view.getResources().getColor(R.color.white));
                }
            }
        });




    }



    @Override public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Switch timeSwitch;
        private Button dl,dt,dm,dj,dv,ds,dg;
        private TextView hora;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            timeSwitch = (Switch) itemView.findViewById(R.id.itemHora_switch);
            hora = (TextView) itemView.findViewById(R.id.itemHora_tvTime);

            dl = (Button) itemView.findViewById(R.id.bttDl);
            dt = (Button) itemView.findViewById(R.id.bttDt);
            dm = (Button) itemView.findViewById(R.id.bttDm);
            dj = (Button) itemView.findViewById(R.id.bttDj);
            dv = (Button) itemView.findViewById(R.id.bttDv);
            ds = (Button) itemView.findViewById(R.id.bttDs);
            dg = (Button) itemView.findViewById(R.id.bttDg);


        }


    }
}
