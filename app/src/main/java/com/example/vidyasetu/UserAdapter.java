package com.example.vidyasetu;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Set<String> selectedUserIds;
    private boolean isSelectionMode;

    public UserAdapter(List<User> userList, Set<String> selectedUserIds, boolean isSelectionMode) {
        this.userList = userList;
        this.selectedUserIds = selectedUserIds;
        this.isSelectionMode = isSelectionMode;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName());

        // Set user initials
        String initials = user.getName().isEmpty() ? "?" : user.getName().substring(0, 1).toUpperCase();
        holder.userInitialTextView.setText(initials);

        // Set colorful background
        int[] colors = {
                Color.parseColor("#FFB6C1"), // Light Pink
                Color.parseColor("#FFD700"), // Gold
                Color.parseColor("#87CEFA"), // Light Sky Blue
                Color.parseColor("#FFA07A"), // Light Salmon
                Color.parseColor("#73C2FB"), // Blue
                Color.parseColor("#8692F7"), // Violet
                Color.parseColor("#C3E0FA"), // Light Blue
                Color.parseColor("#F9F9F9")  // White
        };

        int colorIndex = Math.abs(user.getName().hashCode()) % colors.length;
        GradientDrawable background = (GradientDrawable) holder.userInitialTextView.getBackground();
        background.setColor(colors[colorIndex]);

        if (isSelectionMode) {
            holder.checkBox.setVisibility(View.VISIBLE);

            boolean isSelected = selectedUserIds.contains(user.getId());
            holder.checkBox.setOnCheckedChangeListener(null); // Important to prevent unwanted triggers
            holder.checkBox.setChecked(isSelected);
            holder.itemView.setBackgroundColor(isSelected ? Color.LTGRAY : Color.WHITE);

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                toggleSelection(user, isChecked);
                notifyItemChanged(position);
            });

            holder.itemView.setOnClickListener(v -> {
                boolean currentlySelected = selectedUserIds.contains(user.getId());
                toggleSelection(user, !currentlySelected);
                notifyItemChanged(position);
            });

        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.itemView.setOnClickListener(null); // Remove click listener when not in selection mode
        }
    }

    private void toggleSelection(User user, boolean isSelected) {
        if (isSelected) {
            selectedUserIds.add(user.getId());
        } else {
            selectedUserIds.remove(user.getId());
        }
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, userInitialTextView;
        CheckBox checkBox;

        public UserViewHolder(View itemView) {
            super(itemView);
            userInitialTextView = itemView.findViewById(R.id.userInitial);
            nameTextView = itemView.findViewById(R.id.userName);
            checkBox = itemView.findViewById(R.id.userCheckBox);
        }
    }
}
