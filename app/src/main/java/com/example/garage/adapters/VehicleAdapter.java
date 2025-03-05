package com.example.garage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.R;
import com.example.garage.models.Vehicle;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {
    private final Context context;
    private final List<Vehicle> vehicleList;

    public VehicleAdapter(Context context, List<Vehicle> vehicleList) {
        this.context = context;
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vehicle_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.vehicleMakeModel.setText(vehicle.getMake() + " " + vehicle.getModel());
        holder.vehicleYear.setText(String.valueOf(vehicle.getYear()));
        holder.vehicleType.setText(String.valueOf(vehicle.getType()));
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView vehicleMakeModel, vehicleYear, vehicleType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleMakeModel = itemView.findViewById(R.id.vehicleMakeModel);
            vehicleYear = itemView.findViewById(R.id.vehicleYear);
            vehicleType = itemView.findViewById(R.id.vehicleType);
        }
    }
}

