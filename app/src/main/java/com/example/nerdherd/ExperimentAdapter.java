package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4
/*It just helps showing the experiment in the main UI
* */


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExperimentAdapter extends RecyclerView.Adapter<ExperimentAdapter.ViewHolder> {

    ArrayList<Experiment> experiments;
    ExperimentAdapter.onClickListener listener;

    public ExperimentAdapter(ArrayList<Experiment> experiments, ExperimentAdapter.onClickListener listener){
        this.experiments = experiments;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView owner;
        TextView status;
        public ViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.experimentTitle);
            owner = view.findViewById(R.id.experimentOwner);
            status = view.findViewById(R.id.experimentStatus);
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
    }

    @Override
    public int getItemCount() {
        return experiments.size();
    }

    public interface onClickListener{
        void onClick(View view, int index);
    }
}
