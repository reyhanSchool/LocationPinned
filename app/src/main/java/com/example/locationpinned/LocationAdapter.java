package com.example.locationpinned;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private List<Location> locationList; // A list of locations

    public LocationAdapter(List<Location> locations) {
        this.locationList = locations;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for a single location item and create a view holder.
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        return new LocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        // Populate views in the view holder with data from the location at the given position.
        Location location = locationList.get(position);

        // Set the latitude and longitude data
        holder.latitudeTextView.setText(String.valueOf(location.getLatitude()));
        holder.longitudeTextView.setText(String.valueOf(location.getLongitude()));

        // Set the address data
        holder.addressTextView.setText(location.getAddress());

        // Set a click listener for the item view (the whole item)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trigger the onItemClick method when an item is clicked
                int position = holder.getAdapterPosition();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView latitudeTextView;
        TextView longitudeTextView;
        TextView addressTextView;

        public LocationViewHolder(View itemView) {
            super(itemView);
            latitudeTextView = itemView.findViewById(R.id.latitudeValueView);
            longitudeTextView = itemView.findViewById(R.id.longitudeValueView);
            addressTextView = itemView.findViewById(R.id.addressValueView);
        }
    }

    private OnItemClickListener onItemClickListener;

    // Interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Setter for the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}

