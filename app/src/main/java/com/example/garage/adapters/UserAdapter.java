package com.example.garage.adapters;

import static com.example.garage.functions.ImageUtils.getImageFromFirestore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.garage.R;
import com.example.garage.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private final Context context;
    private List<User> userList;
    private final OnUserClickListener listener;

    public UserAdapter(Context context, List<User> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    public void setUserList(List<User> list) {
        this.userList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.username.setText(user.getUsername());
        holder.name.setText(user.getName());

        if (user.getImage() != null && !user.getImage().isEmpty()) {
            getImageFromFirestore(user.getImage()).addOnSuccessListener(bitmap -> {
                holder.userImage.setImageBitmap(bitmap);
            });
        }

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, name;
        ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_username);
            name = itemView.findViewById(R.id.user_name);
            userImage = itemView.findViewById(R.id.user_image);
        }
    }
}
