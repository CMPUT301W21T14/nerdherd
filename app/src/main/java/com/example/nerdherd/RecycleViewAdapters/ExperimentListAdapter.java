package com.example.nerdherd.RecycleViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.example.nerdherd.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Shows the experiment in the main UI of the app
 * @author Zhipeng Z. zhipeng4
 */

public class ExperimentListAdapter extends RecyclerView.Adapter<ExperimentListAdapter.ViewHolder> {

    ArrayList<Experiment> experiments;
    ExperimentListAdapter.onClickListener listener;

    /**
     * Getter/setter/constructor for the app
     * @param experiments add experiment
     * @param listener for the app to wait for user's input
     */

    public ExperimentListAdapter(ArrayList<Experiment> experiments, ExperimentListAdapter.onClickListener listener){
        this.experiments = experiments;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView description;
        TextView owner;
        TextView status;
        View layout;

        public ViewHolder(View view){
            super(view);
            description = view.findViewById(R.id.experimentTitle);
            owner = view.findViewById(R.id.experimentOwner);
            status = view.findViewById(R.id.experimentStatus);
            layout = view.findViewById(R.id.listItemLayout);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_experiment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Experiment e = experiments.get(position);
        holder.description.setText(e.getDescription());
        holder.owner.setText(ProfileManager.getProfile(e.getOwnerId()).getUserName());
        holder.status.setText(e.getStatus());
        String experimentType = e.typeToString();

        if (experimentType.equals("Binomial") )
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("002ECC71",16)); //Green
        else if (experimentType.equals("Count") )
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("00E74C3C",16)); //Red
        else if (experimentType.equals("Measurement") )
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("00F7DC6F",16)); //Yellow
        else if (experimentType.equals("Non-Negative") )
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("003498DB",16)); //Blue
    }

    @Override
    public int getItemCount() {
        return experiments.size();
    }

    /**
     * Listens for click events within the view
     */
    public interface onClickListener{
        void onClick(View view, int index);
    }
}