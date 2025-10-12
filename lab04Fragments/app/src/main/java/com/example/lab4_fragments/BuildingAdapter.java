package com.example.lab4_fragments;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder> {

    private List<Building> buildingList;
    private OnBuildingClickListener onBuildingClickListener;

    public interface OnBuildingClickListener {
        void onBuildingClick(int position);
    }

    public BuildingAdapter(List<Building> buildingList, OnBuildingClickListener onBuildingClickListener) {
        this.buildingList = buildingList;
        this.onBuildingClickListener = onBuildingClickListener;
    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_building, parent, false);
        return new BuildingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder holder, int position) {
        Building building = buildingList.get(position);
        holder.title.setText(building.getTitle());
        holder.category.setText(building.getCategory()); // Establecer categoría
        holder.description.setText(building.getDescription());
        holder.image.setImageResource(building.getImageResId());

        // Manejar el clic en el elemento
        holder.itemView.setOnClickListener(v -> {
            if (onBuildingClickListener != null) {
                onBuildingClickListener.onBuildingClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buildingList.size();
    }

    public static class BuildingViewHolder extends RecyclerView.ViewHolder {
        TextView title, category, description; // Añadido category
        ImageView image;

        public BuildingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.building_title);
            category = itemView.findViewById(R.id.building_category); // Inicializar categoría
            description = itemView.findViewById(R.id.building_description);
            image = itemView.findViewById(R.id.building_image);
        }
    }
}
