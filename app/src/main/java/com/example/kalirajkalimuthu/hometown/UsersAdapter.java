package com.example.kalirajkalimuthu.hometown;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kalirajkalimuthu.hometown.db.DbContent;
import com.example.kalirajkalimuthu.hometown.model.Chat;
import com.example.kalirajkalimuthu.hometown.utils.OnLoadMoreListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by kalirajkalimuthu on 3/16/17.
 */

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<User> usersList;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private boolean loading=false;
    private int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;





    public UsersAdapter(List<User> users){
          this.usersList = users;
    }

    public UsersAdapter(List<User> users, RecyclerView recyclerView){

        this.usersList = users;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                            }
                        }
                    });
        }
    }

    public List<User> getUsersList(){
        return this.usersList;
    }


    public User getUser(int position) {
        return usersList.get(position);
    }


    @Override
    public int getItemViewType(int position) {
        return usersList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setLoaded() {
       this. loading = false;
    }

    public void setLoading() {
        this. loading = true;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
           View  itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_detail_row, parent, false);
            viewHolder = new myUsersHolder(itemView);

        }
        else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progressbar_item, parent, false);
            viewHolder = new ProgressViewHolder(itemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof myUsersHolder){
            myUsersHolder userHolder = (myUsersHolder)holder;
            User user = usersList.get(position);
            userHolder.nickname.setText(user.getNickname());
            userHolder.locationdetail.setText(user.getCountry() + " | " + user.getState() + " | " + user.getCity() + " | " + user.getYear());
        }
        else
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void add(User user) {
        usersList.add(user);
        notifyItemInserted(usersList.size() - 1);
    }

    public void clear(){
        usersList.clear();
        notifyDataSetChanged();
    }

    public void remove(){
        usersList.remove(usersList.size() - 1);
        notifyItemRemoved(usersList.size());
    }

    public static class myUsersHolder extends RecyclerView.ViewHolder {
        TextView nickname, locationdetail;

        public myUsersHolder(View view){
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname);
            locationdetail = (TextView) view.findViewById(R.id.locationdetail);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressbar_item);
        }
    }
}
