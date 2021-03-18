package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrialsAdapter extends RecyclerView.Adapter<TrialsAdapter.ViewHolder> {

    ArrayList<Trial> trials;
    TrialsAdapter.onClickListener listener;

    public TrialsAdapter(ArrayList<Trial> trials, TrialsAdapter.onClickListener listener){
        this.trials = trials;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView user_trial;
        TextView trials_executed;
        public ViewHolder(View view){
            super(view);
            user_trial = view.findViewById(R.id.trial_user);
            trials_executed = view.findViewById(R.id.trials_executed);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public TrialsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trial_adapter, parent, false);

        return new TrialsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.user_trial.setText((BinomialTrial)trials.get(position) +"");
    }
    


    @Override
    public int getItemCount() {
        return trials.size();
    }

    public interface onClickListener{
        void onClick(View view, int index);
    }
}