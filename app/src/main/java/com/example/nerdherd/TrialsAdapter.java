package com.example.nerdherd;

import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nerdherd.Model.TrialT;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ProfileManager;

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

    ArrayList<TrialT> trials;
    TrialsAdapter.onClickListener listener;

    public TrialsAdapter(ArrayList<TrialT> trials, TrialsAdapter.onClickListener listener){
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
        ProfileManager pMgr = ProfileManager.getInstance();
        TrialT t = trials.get(position);
        UserProfile up = pMgr.getProfile(t.getExperimenterId());
        if(up == null) {
            Log.d("TrialAdapter", "up=NULL");
        }
        holder.trials_executed.setText(String.valueOf(t.getOutcome()));
        holder.user_trial.setText(up.getUserName());
    }



    @Override
    public int getItemCount() {
        return trials.size();
    }

    public interface onClickListener{
        void onClick(View view, int index);
    }
}