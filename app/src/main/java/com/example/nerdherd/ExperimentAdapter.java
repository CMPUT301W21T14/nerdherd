package com.example.nerdherd;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Shows the experiment in the main UI of the app
 * @author Zhipeng Z. zhipeng4
 */

public class ExperimentAdapter extends RecyclerView.Adapter<ExperimentAdapter.ViewHolder> {

    ArrayList<Experiment> experiments;
    ExperimentAdapter.onClickListener listener;

    /**
     * Getter/setter/constructor for the app
     * @param experiments add experiment
     * @param listener for the app to wait for user's input
     */

    public ExperimentAdapter(ArrayList<Experiment> experiments, ExperimentAdapter.onClickListener listener){
        this.experiments = experiments;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView owner;
        TextView status;
        View layout;

        public ViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.experimentTitle);
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
        holder.title.setText(experiments.get(position).getTitle());
        holder.owner.setText(experiments.get(position).getOwnerProfile().getName());
        holder.status.setText(experiments.get(position).getStatus());
        String experimentType = experiments.get(position).getType();
        if (experimentType.compareTo("Binomial Trial") == 0)
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("002ECC71",16)); //Green
        else if (experimentType.compareTo("Count") == 0)
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("00E74C3C",16)); //Red
        else if (experimentType.compareTo("Measurement") == 0)
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("00F7DC6F",16)); //Yellow
        else if (experimentType.compareTo("Non-Negative Integer Count") == 0)
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("003498DB",16)); //Blue
    }

    @Override
    public int getItemCount() {
        return experiments.size();
    }

    public interface onClickListener{
        void onClick(View view, int index);
    }
}
