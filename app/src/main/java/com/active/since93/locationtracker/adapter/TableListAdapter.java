package com.active.since93.locationtracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.active.since93.locationtracker.R;
import com.active.since93.locationtracker.model.LocationData;
import com.active.since93.locationtracker.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myzupp on 18-03-2017.
 *
 * @author Darshan Parikh (parikhdarshan36@gmail.com)
 */

public class TableListAdapter extends RecyclerView.Adapter<TableListAdapter.ViewHolder> {

    private Context context;
    private List<LocationData> locationDataList = new ArrayList<>();

    public TableListAdapter(Context context, List<LocationData> locationDataList) {
        this.context = context;
        this.locationDataList = locationDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_raw_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationData locationData = locationDataList.get(position);
        holder.txtLatitude.setText(String.valueOf(locationData.getPlace().getLatitude()));
        holder.txtLongitude.setText(String.valueOf(locationData.getPlace().getLongitude()));
        holder.txtTime.setText(CommonUtils.getTimeOnly(context, locationData.getTime()));
    }

    @Override
    public int getItemCount() {
        return locationDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtLatitude, txtLongitude, txtTime;

        public ViewHolder(View itemView) {
            super(itemView);

            txtLatitude = (TextView) itemView.findViewById(R.id.txtLatitude);
            txtLongitude = (TextView) itemView.findViewById(R.id.txtLongitude);
            txtTime= (TextView) itemView.findViewById(R.id.txtTime);
        }
    }
}
