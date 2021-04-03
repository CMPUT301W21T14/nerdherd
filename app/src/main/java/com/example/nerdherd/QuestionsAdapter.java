package com.example.nerdherd;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    ArrayList<Question> questions;
    QuestionsAdapter.onClickListener listener;

    public QuestionsAdapter(ArrayList<Question> questions, QuestionsAdapter.onClickListener listener){
        this.questions = questions;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView content;
        TextView replyCount;
        View layout;

        public ViewHolder(View view){
            super(view);
            content = view.findViewById(R.id.question_content);
            replyCount = view.findViewById(R.id.reply_count);
            layout = view.findViewById(R.id.question_list_layout);
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
                .inflate(R.layout.row_item_questions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question targetQuestion = questions.get(position);
        holder.content.setText(targetQuestion.getContent());
        holder.replyCount.setText(String.valueOf(targetQuestion.getReplies().size()));
        holder.content.setTextColor(0xFF000000 + Integer.parseInt("00212F3C",16));
        if (position % 2 == 0)
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("005DADE2",16));
        else
            holder.layout.setBackgroundColor(0xFF000000 + Integer.parseInt("003498DB",16));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public interface onClickListener{
        void onClick(View view, int index);
    }
}
