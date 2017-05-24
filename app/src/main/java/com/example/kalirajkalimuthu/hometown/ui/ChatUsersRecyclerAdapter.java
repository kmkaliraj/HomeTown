package com.example.kalirajkalimuthu.hometown.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kalirajkalimuthu.hometown.R;
import com.example.kalirajkalimuthu.hometown.User;
import com.example.kalirajkalimuthu.hometown.UsersAdapter;
import com.example.kalirajkalimuthu.hometown.model.ChatUser;
import com.example.kalirajkalimuthu.hometown.model.ChatUsers;

import java.util.List;

/**
 * Created by kalirajkalimuthu on 4/10/17.
 */

public class ChatUsersRecyclerAdapter extends RecyclerView.Adapter<ChatUsersRecyclerAdapter.myChatUsersHolder> {

    public List<ChatUser> usersList;

    public static class myChatUsersHolder extends RecyclerView.ViewHolder {
        TextView nickname, email;

        public myChatUsersHolder(View view){
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname);
            email = (TextView) view.findViewById(R.id.locationdetail);

        }
    }

    public ChatUser getChatUser(int position) {
        return usersList.get(position);
    }



    public ChatUsersRecyclerAdapter(List<ChatUser> users){
        this.usersList = users;
    }

    @Override
    public ChatUsersRecyclerAdapter.myChatUsersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_detail_row, parent, false);

        return new ChatUsersRecyclerAdapter.myChatUsersHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatUsersRecyclerAdapter.myChatUsersHolder holder, int position) {
        ChatUser user = usersList.get(position);
        holder.nickname.setText(user.nickname);
        holder.email.setText(user.email);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

}
