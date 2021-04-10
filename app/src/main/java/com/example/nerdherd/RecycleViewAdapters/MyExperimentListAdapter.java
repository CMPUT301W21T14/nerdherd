package com.example.nerdherd.RecycleViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The current logged in user's experiment information is stored for the app to access
 * @author Zhipeng Z. zhipeng4
 * @author Ogooluwa S. osameul
 */

public class MyExperimentListAdapter extends RecyclerView.Adapter<MyExperimentListAdapter.ViewHolder> {

    ArrayList<Experiment> experiments;
    MyExperimentListAdapter.onClickListener listener;

    /**
     *Getter/setter/constructor of the app
     * @param experiments this is the current experiment in focus
     * @param listener for the app to listen to user's input
     */

    public MyExperimentListAdapter(ArrayList<Experiment> experiments, MyExperimentListAdapter.onClickListener listener){
        this.experiments = experiments;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView status;
        View layout;

        public ViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.expTitle);
            status = view.findViewById(R.id.expStatus);
            layout = view.findViewById(R.id.profileExperimentLayout);
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
        Experiment e = experiments.get(position);
        holder.title.setText(experiments.get(position).getTitle());
        holder.status.setText(experiments.get(position).getStatus());
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
