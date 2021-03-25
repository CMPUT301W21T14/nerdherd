package com.example.nerdherd;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter class for Trial method class
 * This makes the trial visible on the app for the user/owner to choose/pick
 * @author Ogooluwa S. osamuel
 * @author Zhipeng Z. zhipeng4
 */

public class TrialsAdapter extends RecyclerView.Adapter<TrialsAdapter.ViewHolder> {

    ArrayList<Trial> trials;
    TrialsAdapter.onClickListener listener;
    private String trialtype;

    /**
     * Getter/setter of the class
     * @param trials depending on which one the owner chooses
     * @param listener to wait for the user/owner input
     * @param trialType 1/4 options
     */

    public TrialsAdapter(ArrayList<Trial> trials, TrialsAdapter.onClickListener listener, String trialType){
        this.trials = trials;
        this.listener = listener;
        this.trialtype = trialType;
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
        if (trialtype.equals("Binomial Trial")){
            holder.user_trial.setText(((BinomialTrial)trials.get(position)).getSuccess()+"");
            holder.trials_executed.setText(((BinomialTrial)trials.get(position)).getFailure()+"");
        }
        if(trialtype.equals("Count")){
            holder.user_trial.setText(((CountTrial)trials.get(position)).totaltrialCount()+"");
            holder.trials_executed.setVisibility(View.INVISIBLE);
        }
        if(trialtype.equals("Measurement")){
            int maxLength = 22;
            holder.user_trial.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
            holder.user_trial.setText(((MeasurementTrial)trials.get(position)).getMeasurements()+"");
            holder.trials_executed.setVisibility(View.INVISIBLE);
        }
        if(trialtype.equals("Non-Negative Integer Count")){
            holder.user_trial.setText(((NonnegativeTrial)trials.get(position)).totaltrialCount()+"");
            holder.trials_executed.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    public int getItemCount() {
        return trials.size();
    }

    public interface onClickListener{
        void onClick(View view, int index);
    }
}