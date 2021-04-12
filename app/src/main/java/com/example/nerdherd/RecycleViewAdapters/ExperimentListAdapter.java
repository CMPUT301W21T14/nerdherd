package com.example.nerdherd.RecycleViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.example.nerdherd.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
        TextView help;
        TextView type;
        TextView title;
        ImageView loc;
        ConstraintLayout background;
        View layout;

        public ViewHolder(View view){
            super(view);
            description = view.findViewById(R.id.experimentTitle);
            owner = view.findViewById(R.id.experimentOwner);
            status = view.findViewById(R.id.experimentStatus);
            layout = view.findViewById(R.id.listItemLayout);
            help = view.findViewById(R.id.tv_needs_trials);
            type = view.findViewById(R.id.tv_exp_list_type_label);
            title = view.findViewById(R.id.tv_exp_title);
            background = view.findViewById(R.id.ll_header_background);
            loc = view.findViewById(R.id.iv_location_req);
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
        int current = ExperimentManager.getInstance().getTrialsExcludeBlacklist(e.getExperimentId()).size();
        int needed = e.getMinimumTrials() - current;
        if(e.getStatus().equals("Ended")) {
            holder.help.setVisibility(View.GONE);
        } else if (e.getMinimumTrials() > e.getTrials().size()) {
            holder.help.setText("Need trials (" + String.valueOf(current) +"/" + String.valueOf(e.getMinimumTrials()) + ")");
            holder.help.setVisibility(View.VISIBLE);
        }
        holder.title.setText(e.getTitle());
        holder.type.setText(e.typeToString());
        String experimentType = e.typeToString();

        if(e.isLocationRequired()) {
            holder.loc.setVisibility(View.VISIBLE);
        } else {
            holder.loc.setVisibility(View.GONE);
        }

        if (experimentType.equals("Binomial") ) {
            holder.layout.setBackgroundColor(0x652ECC71); //Green
            holder.background.setBackgroundColor(0xFF2ECC71);
        }
        else if (experimentType.equals("Count") ) {
            //65E74C3C
            holder.layout.setBackgroundColor(0x65E74C3C); //Red
            holder.background.setBackgroundColor(0xFFE74C3C);
        }
        else if (experimentType.equals("Measurement") ) {
            //65F7DC6F
            holder.layout.setBackgroundColor(0x65F7DC6F); //Yellow
            holder.background.setBackgroundColor(0xFFF7DC6F);
        }
        else if (experimentType.equals("Non-Negative") ) {
            //653498DB
            holder.layout.setBackgroundColor(0x653498DB); //Blue
            holder.background.setBackgroundColor(0xFF3498DB);
        }
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
