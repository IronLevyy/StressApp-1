package com.zemnuhov.stressapp.ScanResurce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zemnuhov.stressapp.GlobalValues;
import com.zemnuhov.stressapp.MainResurce.MainFragment;
import com.zemnuhov.stressapp.R;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.DeviceViewHolder> {

    List<Device> devices;

    RecycleViewAdapter(List<Device> devices){
        this.devices=devices;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_card_layout,parent,false);
        DeviceViewHolder deviceViewHolder=new DeviceViewHolder(view);
        return deviceViewHolder;
    }

    @Override
    public void onBindViewHolder( DeviceViewHolder holder, int position) {
        holder.deviceMAC.setText(devices.get(position).MAC);
        holder.nameDevice.setText(devices.get(position).Name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalValues.getFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, MainFragment.newInstance(devices.get(position).MAC)).
                        commit();
                GlobalValues.saveDevice(devices.get(position).MAC);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView nameDevice;
        TextView deviceMAC;

        public DeviceViewHolder( View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.card_view);
            nameDevice=itemView.findViewById(R.id.name_device);
            deviceMAC=itemView.findViewById(R.id.mac_device);
        }
    }

}
