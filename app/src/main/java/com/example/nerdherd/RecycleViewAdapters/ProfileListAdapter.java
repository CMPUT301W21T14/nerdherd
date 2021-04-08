package com.example.nerdherd.RecycleViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.Profile;
import com.example.nerdherd.R;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ViewHolder> {

    ArrayList<UserProfile> profiles;
    ProfileListAdapter.onClickListener listener;
    private final Integer[] imageList= {R.drawable.zelda, R.drawable.link, R.drawable.mipha, R.drawable.urbosa, R.drawable.riju, R.drawable.revali, R.drawable.daruk, R.drawable.impa, R.drawable.purah, R.drawable.purah_6_years_old, R.drawable.yunobo, R.drawable.king_rhoam, R.drawable.sidon};
    private final ArrayList<Integer> imageArray = new ArrayList(Arrays.asList(imageList));

    /**
     * Getter/setter/constructor for the class
     * @param profiles of the user
     * @param listener of the user
     */

    public ProfileListAdapter(ArrayList<UserProfile> profiles, ProfileListAdapter.onClickListener listener){
        this.profiles = profiles;
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
    public ProfileListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_user, parent, false);

        return new ProfileListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileListAdapter.ViewHolder holder, int position) {
        holder.name.setText(profiles.get(position).getUserName());
        holder.email.setText(profiles.get(position).getContactInfo());
        holder.avatar.setImageResource(imageArray.get(profiles.get(position).getAvatarId()));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public interface onClickListener{
        void onClick(View view, int index);
    }
}

/*


 */