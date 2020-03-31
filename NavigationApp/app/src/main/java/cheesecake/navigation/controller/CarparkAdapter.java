package cheesecake.navigation.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import cheesecake.navigation.model.Carpark;
import cheesecake.navigation.model.CarparkData;

public class CarparkAdapter extends RecyclerView.Adapter<CarparkAdapter.CarparkViewHolder> {

    private CarparkData dataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CarparkViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public TextView descriptionView;

        public CarparkViewHolder(View v) {
            super(v);
            titleView = v.findViewById(R.id.text_item_title);
            descriptionView = v.findViewById(R.id.text_item_description);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CarparkAdapter(CarparkData myDataset) {
        dataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CarparkAdapter.CarparkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_item, parent, false);
        CarparkViewHolder vh = new CarparkViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CarparkViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Carpark currentPark = dataset.getDisplayValues().get(position);

        String title = currentPark.getArea() + " - " + currentPark.getDevelopment();
        String description = "Available Parking Lots: \n" + currentPark.getAvailableLots();

        holder.titleView.setText(title);
        holder.descriptionView.setText(description);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.getDisplayValues().size();
    }

}
