package com.example.kalirajkalimuthu.hometown.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kalirajkalimuthu.hometown.R;
import com.example.kalirajkalimuthu.hometown.model.ChatUser;
import com.example.kalirajkalimuthu.hometown.utils.Constants;
import com.example.kalirajkalimuthu.hometown.utils.ItemClickSupport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalirajkalimuthu on 4/10/17.
 */

public class ChatUsersListFragment extends Fragment implements ItemClickSupport.OnItemClickListener{

    private RecyclerView chatUsersRecyclerView;
    private ChatUsersRecyclerAdapter mChatUsersRecyclerAdapter;
    private List<ChatUser>  chatUsersList = new ArrayList<>();;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.chat_users_list_fragment, container, false);
        chatUsersRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.chat_users_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        chatUsersRecyclerView.setLayoutManager(mLayoutManager);
        mChatUsersRecyclerAdapter = new ChatUsersRecyclerAdapter(chatUsersList);
        chatUsersRecyclerView.setAdapter(mChatUsersRecyclerAdapter);
        ItemClickSupport.addTo(chatUsersRecyclerView)
                .setOnItemClickListener(this);
        return fragmentView;
    }


    @Override
    public void onViewCreated(View view, Bundle bundle) {
        getActivity().setTitle("Chat Users");
        init();
    }

    public void init() {
        getUsers();
    }

    public void getUsers(){
        chatUsersList.clear();
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot snap: dataSnapshot.getChildren())
                        chatUsersList.add((ChatUser)snap.getValue(ChatUser.class));
                    mChatUsersRecyclerAdapter = new ChatUsersRecyclerAdapter(chatUsersList);
                    chatUsersRecyclerView.setAdapter(mChatUsersRecyclerAdapter);

                }
                else
                    Toast.makeText(getActivity(), "user not exist in firebase" , Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "failed to bring the data" , Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        ChatUser user = ((ChatUsersRecyclerAdapter) recyclerView.getAdapter()).getChatUser(position);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, user.nickname);
        intent.putExtra(Constants.ARG_RECEIVER_MAIL,user.email);
        intent.putExtra(Constants.ARG_RECEIVER_UID, user.uid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, user.firebaseToken);
        startActivity(intent);
    }

}
