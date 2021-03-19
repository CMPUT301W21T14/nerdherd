package com.example.nerdherd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The current logged in user's experiment information is stored for the app to access
 * @author Zhipeng Z. zhipeng4
 * @author Ogooluwa S. osameul
 */

public class MyExperimentAdapter extends RecyclerView.Adapter<MyExperimentAdapter.ViewHolder> {

    ArrayList<Experiment> experiments;
    MyExperimentAdapter.onClickListener listener;

    /**
     *Getter/setter/constructor of the app
     * @param experiments this is the current experiment in focus
     * @param listener for the app to listen to user's input
     */

    public MyExperimentAdapter(ArrayList<Experiment> experiments, MyExperimentAdapter.onClickListener listener){
        this.experiments = experiments;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView status;
        public ViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.expTitle);
            status = view.findViewById(R.id.expStatus);
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
                .inflate(R.layout.row_item_profile_experiment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(experiments.get(position).getTitle());
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
