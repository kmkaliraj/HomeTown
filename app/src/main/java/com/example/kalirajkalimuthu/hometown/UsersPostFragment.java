package com.example.kalirajkalimuthu.hometown;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.kalirajkalimuthu.hometown.db.DbUserDataHelper;
import com.example.kalirajkalimuthu.hometown.model.ChatUser;
import com.example.kalirajkalimuthu.hometown.ui.ChatActivity;
import com.example.kalirajkalimuthu.hometown.utils.Constants;
import com.example.kalirajkalimuthu.hometown.utils.ItemClickSupport;
import com.example.kalirajkalimuthu.hometown.utils.OnLoadMoreListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by kalirajkalimuthu on 4/6/17.
 */

public class UsersPostFragment extends Fragment implements ItemClickSupport.OnItemClickListener {

    private String url = "http://bismarck.sdsu.edu/hometown/users";
    private List<User> usersList = new ArrayList<User>();
    private LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private UsersAdapter mAdapter;
    private String last_query;
    int pageCount = 0;

    private String reverse = "reverse=true";
    private String appliedFilter = null;
    private String query_url;
    private DbUserDataHelper dbUserDataHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void increasePageCount() {
        pageCount++;
    }

    public void resetCount() {
        pageCount = 0;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbUserDataHelper = new DbUserDataHelper(getActivity());
       // displayUsers(null, false);
        getActivity().setTitle("Users List View");
        initDB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_post_fragment, container, false);
        initRecycleView(view);
        return view;
    }

    public void initDB() {
       int value =  dbUserDataHelper.getMaxId();
        if(value > 0){
           String  new_url = url+"?"+"reverse=true&afterid="+value;
            new MyAsyncListTask().execute(new_url);
        }
        mAdapter.clear();
        displayUsers(null, false);
    }



    public void getUsers(String url) {
        //mAdapter.add(null);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    List<User> usersFromServer = new ArrayList<>();

                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject userObject = response.getJSONObject(i);
                                User user = new User(userObject.getInt("id"),
                                        userObject.getString("nickname"),
                                        userObject.getString("country"),
                                        userObject.getString("state"),
                                        userObject.getString("city"),
                                        userObject.getString("year"),
                                        Double.valueOf(userObject.getString("latitude")),
                                        Double.valueOf(userObject.getString("longitude")));
                                usersFromServer.add(user);
                            } catch (JSONException e) {
                            }
                        }

                        dbUserDataHelper.insertUsers(usersFromServer);
                      //  mAdapter.remove();
                        for (User user : usersFromServer)
                            mAdapter.add(user);
                        mAdapter.setLoaded();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Some", "Error: " + error.getMessage());
            }
        });
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayReq, "do");

    }

    public void displayUsers(String filter, Boolean isLoadMore) {
        mAdapter.setLoading();
        if (!isLoadMore) {
            mAdapter.clear();
        }
        appliedFilter = filter;
        Integer beforeId = usersList.isEmpty() ? null : Integer.valueOf(usersList.get(usersList.size() - 1).getUserId());
        List<User> usersFromDb = dbUserDataHelper.getUsers(filter, beforeId);
        if (usersFromDb.size() >= 25) {
             for(User user:usersFromDb)
                 mAdapter.add(user);
                 mAdapter.setLoaded();
        } else {
            int pageSize = 0;
            if (usersFromDb.size() > 0) {
                pageSize = 25 - (usersFromDb.size() % 25);
                beforeId = Integer.valueOf(usersFromDb.get(usersFromDb.size() - 1).getUserId());
            }
            query_url = url + "?" + (filter != null ? filter : "");
            query_url = query_url + (filter != null ? "&" + "page=0" : "page=0");
            query_url = query_url + (pageSize > 0 ? "&pagesize=" + pageSize : "");
            query_url = query_url + "&" + reverse;
            query_url = query_url + (beforeId != null ? "&beforeid=" + beforeId : "");
            new MyAsyncListTask().execute(query_url);
        }
    }

    private class MyAsyncListTask extends AsyncTask<String, Integer, Integer> {
        protected void onPreExecute() {

        }

        protected Integer doInBackground(String... strings) {
            getUsers(strings[0]);
            return 1;
        }

        protected void onProgressUpdate(Integer... values) {
            // Executes whenever publishProgress is called from doInBackground
            // Used to update the progress indicator
            // mProgressBar.setProgress(values[0]);
        }

        protected void onPostExecute(Integer result) {

        }
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

        String nickname = ((UsersAdapter) recyclerView.getAdapter()).getUser(position).getNickname();
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(nickname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ChatUser user = dataSnapshot.getValue(ChatUser.class);
                        goToChatActivity(user);

                } else
                    Toast.makeText(getActivity(), "user not exist in firebase", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "failed to bring the data", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void goToChatActivity(ChatUser user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, user.nickname);
        intent.putExtra(Constants.ARG_RECEIVER_MAIL, user.email);
        intent.putExtra(Constants.ARG_RECEIVER_UID, user.uid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, user.firebaseToken);
        startActivity(intent);
    }


    public void initRecycleView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        // mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new UsersAdapter(usersList, recyclerView);
        recyclerView.setAdapter(mAdapter);
        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(this);

        // preparePosts(url);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                displayUsers(appliedFilter, true);
            }
        });

    }




}
