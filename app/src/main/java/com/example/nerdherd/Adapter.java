package com.example.nerdherd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The class allows it to be visible for the user to pick and choose
 * The user gets to pick an image for their register
 * The class contains some motivation from a tutorial found here <a href="https://developer.android.com/"></a>
 * @author Zhipeng Z. zhipeng4
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    ArrayList<Integer> images;
    onClickListener listener;

    /**
     * Stores user's decision
     * Getter/setter/constructor for the class
     * @param images the image itself
     * @param listener to take user's input
     */

    public Adapter(ArrayList<Integer> images, onClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        public ViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.imageView);
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
                .inflate(R.layout.row_item_avatar, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface onClickListener{
        void onClick(View view, int index);
    }
}
