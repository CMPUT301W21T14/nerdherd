package com.example.nerdherd.RecycleViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nerdherd.Model.Reply;
import com.example.nerdherd.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Adapter for displaying replies in a formatted list
 */
public class ReplyListAdapter extends RecyclerView.Adapter<ReplyListAdapter.ViewHolder> {

    ArrayList<Reply> replies;

    public ReplyListAdapter(ArrayList<Reply> replies){
        this.replies = replies;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        View layout;

        public ViewHolder(View view){
            super(view);
            content = view.findViewById(R.id.reply_content);
            layout = view.findViewById(R.id.reply_list_layout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_reply, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Puts different aspects of the Reply into the views that make up holder
     * @param holder Template that holds each element of the list
     * @param position Current position in the Reply array
     */
    @Override
    public void onBindViewHolder(@NonNull ReplyListAdapter.ViewHolder holder, int position) {
        Reply targetReply = replies.get(position);
        holder.content.setText(targetReply.getContent());
        holder.content.setTextColor(0xFF000000 + Integer.parseInt("00212F3C",16));
        if (position % 2 == 0)
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("005DADE2",16));
        else
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("003498DB",16));
    }

    /**
     * Listens for click events within the view
     */
    @Override
    public int getItemCount() {
        return replies.size();
    }
}
