package com.example.kalirajkalimuthu.hometown;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.kalirajkalimuthu.hometown.db.DbUserDataHelper;
import com.example.kalirajkalimuthu.hometown.model.ChatUser;
import com.example.kalirajkalimuthu.hometown.ui.ChatActivity;
import com.example.kalirajkalimuthu.hometown.utils.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalirajkalimuthu on 3/18/17.
 */

public class DisplayMapFragment extends Fragment implements OnMapReadyCallback {

    private String url = "http://bismarck.sdsu.edu/hometown/users";

    private  boolean pickerMode = false;
    private  Double latitude, longitude;
    private List<User> usersList = new ArrayList<User>();
    private String reverse="reverse=true";
    private String appliedFilter = null;
    private String query_url;
    private DbUserDataHelper dbUserDataHelper;





    private GoogleMap mMap;
    private Button mLoadMore;
    public GoogleMap getMap() {
        return mMap;
    }

    public void setMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public boolean isPickerMode() {
        return pickerMode;
    }

    public void setPickerMode(boolean pickerMode) {
        this.pickerMode = pickerMode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        dbUserDataHelper = new DbUserDataHelper(getActivity());
        MapView map = (MapView) view.findViewById(R.id.map);
        mLoadMore = (Button) view.findViewById(R.id.map_load_more);
        map.onCreate(bundle);
        map.onResume();
        map.getMapAsync(this);
        if(!pickerMode) {
            mLoadMore.setVisibility(View.VISIBLE);
           mLoadMore.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  displayUsers(appliedFilter,true);
               }
           });

        }

       getActivity().setTitle("Users Map View");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.display_map_fragment, container, false);
        return view;
    }


    @Override
        public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

           mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

               @Override
               public void onMapClick(LatLng point) {
                   if(pickerMode)
                   addMarker(point);
               }
           });

        if(!pickerMode)
           initDB();

           mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
               @Override
               public void onInfoWindowClick(Marker marker) {
                   callChatActivity(marker.getTitle());
               }
           });



           if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                   PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                   android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               // TODO: Consider calling
               //    ActivityCompat#requestPermissions
               // here to request the missing permissions, and then overriding
               //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
               //                                          int[] grantResults)
               // to handle the case where the user grants the permission. See the documentation
               // for ActivityCompat#requestPermissions for more details.
               return;
           }
           mMap.setMyLocationEnabled(true);
           Location myLocation = mMap.getMyLocation();
           LatLng myLatLng = new LatLng(myLocation.getLatitude(),
                   myLocation.getLongitude());
           CameraPosition myPosition = new CameraPosition.Builder()
                   .target(myLatLng).build();
           googleMap.animateCamera(
                   CameraUpdateFactory.newCameraPosition(myPosition));
        }

    public void addMarker(LatLng point){
        mMap.clear();
        latitude = point.latitude;
        longitude = point.longitude;
        MarkerOptions marker = new MarkerOptions() .position(point);
        mMap.addMarker(marker);
        CameraUpdate newLocation = CameraUpdateFactory.newLatLngZoom(point, 6);
        mMap.moveCamera(newLocation);
      /*  CameraPosition myPosition = new CameraPosition.Builder()
                .target(point).build();
        mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(myPosition)); */
    }

    public void initDB() {
        int value =  dbUserDataHelper.getMaxId();
        if(value > 0){
            String  new_url = url+"?"+"reverse=true&afterid="+value;
            new MyAsyncMapTask().execute(new_url);
        }
        mMap.clear();
        usersList.clear();
        displayUsers(null, false);
    }


    public void callChatActivity(String nickname){
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(nickname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ChatUser user = dataSnapshot.getValue(ChatUser.class);
                        goToChatActivity(user);
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
    public void goToChatActivity(ChatUser user){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, user.nickname);
        intent.putExtra(Constants.ARG_RECEIVER_MAIL,user.email);
        intent.putExtra(Constants.ARG_RECEIVER_UID, user.uid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, user.firebaseToken);
        startActivity(intent);
    }

    public void markUsers(List<User> users, boolean loadMore){

        if(!loadMore)
         mMap.clear();

        LatLng  point = null;
        for(User user: users){
           if(user.getLatitude()== 0 && user.getLongitude() == 0)
               point = getUserLocation(user.getCity()+", "+user.getState()+", "+user.getCountry(), 3);
            else
                point = new LatLng(user.getLatitude(), user.getLongitude());

            MarkerOptions marker = new MarkerOptions().position(point);
            mMap.addMarker(marker.title(user.getNickname()));
        }

        if(point != null){
            CameraPosition myPosition = new CameraPosition.Builder()
                    .target(point).build();
            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(myPosition));
        }

       ((UserView)getActivity()).progressDailog.dismiss();
    }

    public LatLng getUserLocation(String address, int args){

        LatLng  location=new LatLng(0,0);
        Double latitude=null, longitude=null;

        Geocoder locator = new Geocoder(getActivity().getApplicationContext());
        try {
            List<Address> positions =
                    locator.getFromLocationName(address, args);
            if (positions.size() > 0)  {
                Address position = positions.get(0);
                if (position.hasLatitude())
                    latitude =position.getLatitude();
                if (position.hasLongitude())
                    longitude = position.getLongitude();
                location = new LatLng(latitude,longitude);
            }

        } catch (Exception error) {
            Log.e("rew", "Address lookup Error", error);
        }
        return location;
    }

    public void displayUsers(String filter,boolean isLoadMore) {

        if(!isLoadMore) {
            usersList.clear();
            mMap.clear();
        }

        ((UserView)getActivity()).progressDailog.show();
        if(!isLoadMore) usersList.clear();
        appliedFilter = filter;
        Integer beforeId = usersList.isEmpty() ? null : Integer.valueOf(usersList.get(usersList.size() - 1).getUserId());
        List<User> usersFromDb = dbUserDataHelper.getUsers(filter, beforeId);
        usersList.addAll(usersFromDb);

        if (usersFromDb.size() >= 25) {
            markUsers(usersList, isLoadMore);
        } else {
            int pageSize = 0;
            if (usersFromDb.size() > 0) {
                pageSize = 25-(usersFromDb.size()%25);
                beforeId = Integer.valueOf(usersFromDb.get(usersFromDb.size() - 1).getUserId());
            }
                query_url = url +"?"+(filter !=null ? filter:"");
                query_url = query_url + (filter != null ? "&"+"page=0":"page=0");
                query_url = query_url + (pageSize > 0 ? "&pagesize="+pageSize:"");
                query_url = query_url +"&" +reverse;
                query_url = query_url + (beforeId != null?"&beforeid="+beforeId:"");
            new MyAsyncMapTask().execute(query_url);
        }
    }

    public void getUsers(String filter_url){
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(filter_url,
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
                        usersList.addAll(usersFromServer);
                        markUsers(usersList,true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Some", "Error: " + error.getMessage());
            }
        });
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayReq, "do");

    }

    private class MyAsyncMapTask extends AsyncTask<String, Integer, Integer> {
        protected void onPreExecute() {

            ((UserView)getActivity()).progressDailog.show();
        }

        protected Integer doInBackground(String... strings) {
            getUsers(strings[0]);
            return 1;
        }

        protected void onProgressUpdate(Integer... values) {

        }

        protected void onPostExecute(Integer result) {

        }
    }
}
