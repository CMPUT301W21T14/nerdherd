package com.example.nerdherd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User visible on the app
 * Adapter class for user to opt their desired parameters
 * @author Zhipeng Z. zhipeng4
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<Profile> profiles;
    ArrayList<Integer> images;
    UserAdapter.onClickListener listener;

    /**
     * Getter/setter/constructor for the class
     * @param profiles of the user
     * @param images of the user
     * @param listener of the user
     */

    public UserAdapter(ArrayList<Profile> profiles, ArrayList<Integer> images, UserAdapter.onClickListener listener){
        this.profiles = profiles;
        this.images = images;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        TextView email;
        ImageView avatar;
        public ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.user_name);
            email = view.findViewById(R.id.user_email);
            avatar = view.findViewById(R.id.user_avatar);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_user, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        holder.name.setText(profiles.get(position).getName());
        holder.email.setText(profiles.get(position).getEmail());
        holder.avatar.setImageResource(images.get(profiles.get(position).getAvatar()));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public interface onClickListener{
        void onClick(View view, int index);
    }
}
