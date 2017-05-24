package com.example.kalirajkalimuthu.hometown;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import com.example.kalirajkalimuthu.hometown.model.ChatUser;
import com.example.kalirajkalimuthu.hometown.model.ChatUsers;
import com.example.kalirajkalimuthu.hometown.utils.Constants;
import com.example.kalirajkalimuthu.hometown.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SignupActivity extends AppCompatActivity {
    private String url = "http://bismarck.sdsu.edu/hometown/adduser";
    private String user_url =  "http://bismarck.sdsu.edu/hometown/nicknameexists?name=";
    private String country_url = "http://bismarck.sdsu.edu/hometown/countries";
    private String states_url = "http://bismarck.sdsu.edu/hometown/states?country=";

    private List<String> countries = new ArrayList<String>(){{
        add("Select Country(None)");
    }};
    private  List<String> states = new ArrayList<String>(){{
        add("Select State(None)");
    }};

    EditText mNickName;
    EditText mPassword;
    EditText mEmail;
    EditText mStartYear;
    Spinner mCountry;
    Spinner mState;
    EditText mLong_Lat;
    EditText mCity;
    Button mSubmitButton;
    TextView mLocationLink;
    private FirebaseAuth auth;
    public ProgressDialog progressDailog;

    private String country;
    private String state;
    private Double longitude;
    private Double latitude;

    boolean isFinished = false;
    boolean isValidNickname= true;
    boolean firbaseStatus = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Register Here");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNickName = (EditText) findViewById(R.id.input_nickname);
        mPassword = (EditText) findViewById(R.id.input_password);
        mStartYear = (EditText) findViewById(R.id.input_year);
        mCountry = (Spinner) findViewById(R.id.input_country);
        mState = (Spinner)  findViewById(R.id.input_state);
        mEmail = (EditText) findViewById(R.id.input_email);
        mLong_Lat = (EditText) findViewById(R.id.input_location);
        mCity  = (EditText) findViewById(R.id.input_city);
        mLocationLink = (TextView) findViewById(R.id.link_location);
        mSubmitButton = (Button) findViewById(R.id.btn_signup);

        progressDailog = new ProgressDialog(this);
        progressDailog.setMessage("Loading");
        progressDailog.getWindow().
                setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDailog.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postUserDetails();
            }
        });
        mLocationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
            }
        });

        mCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                country = (String)adapterView.getAdapter().getItem(position);
                state = null;
                populateStateDetails();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        mState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                state = (String)adapterView.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        populateCountryData();

    }

    public void postUserDetails(){

        if(isEmptyNickName())
            mNickName.setError("Nickname is required!");
        else if(!isValidNickName()) {
            Log.i("error", "duplicate nickname");
            isValidNickname= true;
        }
        else if(!isValidPassword())
            mPassword.setError("Must enter password of minimum 6 character");
        else if(!isValidEmail())
            mEmail.setError("Must enter a valid email");
        else if(!isValidYear())
            mStartYear.setError("Must enter an year between 1970 and 2017");
        else if(!isValidCountry()) {
            TextView errorText = (TextView)mCountry.getSelectedView();
            errorText.setError("Country is Required");
            errorText.setTextColor(Color.RED);
        }
        else if(!isValidState()){
            TextView errorText = (TextView)mState.getSelectedView();
            errorText.setError("State is Required");
            errorText.setTextColor(Color.RED);
        }
        else if(!isValidCity())
            mCity.setError("City is required!");
        else {
               submitUserDetails();
            }

    }


    public void addUserToFirebase(FirebaseUser firebaseuser){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String nickname = mNickName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        ChatUser user = new ChatUser(firebaseuser.getUid(),nickname,
                email,
                new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN));
        database.child(Constants.ARG_USERS)
                .child(nickname)
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDailog.dismiss();
                            goToHomePage();

                        } else {
                            progressDailog.dismiss();
                            Toast.makeText(getApplicationContext(), "Adding user details to firebase failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void submitUserDetails(){



        JSONObject userDetailsObject = new JSONObject();
        try {
            userDetailsObject.put("nickname", mNickName.getText().toString());
            userDetailsObject.put("password", mPassword.getText().toString());
            userDetailsObject.put("year", Integer.valueOf(mStartYear.getText().toString()));
            userDetailsObject.put("country", country);
            userDetailsObject.put("state", state);
            if (mCity.getText().toString().length() > 0)
                userDetailsObject.put("city", mCity.getText().toString());

            if (latitude != null && longitude != null) {
                userDetailsObject.put("longitude", longitude);
                userDetailsObject.put("latitude", latitude);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDailog.show();
        JsonObjectRequest jsonPostReq = new JsonObjectRequest(url, userDetailsObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        signupUserwithFireBase();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDailog.dismiss();
                Toast.makeText(getApplicationContext(), "Registration Failed",
                        Toast.LENGTH_LONG).show();
                VolleyLog.d("Some", "Error: " + error.getMessage());
            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonPostReq, "do");

    }

    public boolean isValidEmail(){
        String email = mEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern) && email.length() > 0;

    }

    public void goToHomePage(){
        Intent userView = new Intent(this, UserView.class);
        startActivity(userView);
        finish();
    }

    public boolean signupUserwithFireBase(){

         String email = mEmail.getText().toString().trim();
         String password = mPassword.getText().toString().trim();

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUserToFirebase(task.getResult().getUser());

                            Log.i("User Details:", "Username and password are added to firbase");
                            //finish();
                        } else {
                            progressDailog.dismiss();
                            Toast.makeText(SignupActivity.this, "Creation User Account Failed" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            firbaseStatus = false;
                            Log.w("Authentication Failed", "User Details are not added to firbase:  "+ task.getException().getMessage());
                        }
                    }
                });
        return firbaseStatus;
    }

    public boolean isValidCity(){
        return mCity.getText().toString().length() > 0 ;
    }

    public boolean isEmptyNickName(){
        return this.mNickName.getText().toString().length()==0;
    }
    public boolean isValidNickName(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, user_url+mNickName.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Boolean result = Boolean.valueOf(response);
                        if(result) {
                            mNickName.setError("Nickname already exists");
                        }
                        isFinished = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isFinished = true;
            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, "do");

        return isValidNickname;
    }

    public boolean isValidPassword(){
        return mPassword.getText().toString().length() >=6;
    }

    public boolean isValidCountry(){
        return !(mCountry.getSelectedItem().toString().contains("none"));
    }

    public boolean isValidState(){
        return !(mState.getSelectedItem().toString().contains("none"));
    }


    public boolean isValidYear(){
        return mStartYear.getText().toString().length()>0 && (Integer.valueOf(mStartYear.getText().toString()) >= 1970 &&
                Integer.valueOf(mStartYear.getText().toString()) <= 2017);

    }

    public void setLocation(){
        Intent datePicker = new Intent(this, PickLocation.class);
        startActivityForResult(datePicker,2);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("rew", "Back to Home - " + requestCode+ " " +resultCode);

        if (requestCode == 2 && resultCode == RESULT_OK){
            latitude = Double.valueOf(data.getStringExtra("latitude"));
            longitude =  Double.valueOf(data.getStringExtra("longitude"));
            mLong_Lat.setText(String.format( "%.2f", latitude ) +"/" +String.format( "%.2f", longitude ));
        }
    }


    public void populateCountryData(){
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(country_url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){

                        for(int i=0 ; i<response.length(); i++){
                            try {
                                countries.add(response.getString(i));
                            }
                            catch(JSONException e){

                            }
                            ArrayAdapter<String> countryAdapter=new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.spinner_item, countries);
                            countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mCountry.setAdapter(countryAdapter);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Some", "Error: " + error.getMessage());
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayReq, "do");
    }

    public void populateStateDetails(){
        states.clear();
        states.add("Select State(None)");
        if(country == null){
            ArrayAdapter<String> statesAdapter=new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.spinner_item, states);
            statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mState.setAdapter(statesAdapter);
            return;
        }

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(states_url+country,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){

                        for(int i=0 ; i<response.length(); i++){
                            try {
                                states.add(response.getString(i));
                            }
                            catch(JSONException e){

                            }
                            ArrayAdapter<String> statesAdapter=new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.spinner_item, states);
                            statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mState.setAdapter(statesAdapter);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Some", "Error: " + error.getMessage());
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayReq, "do");
    }

    private class MyAsyncSignupListTask extends AsyncTask<String, Integer, Integer> {

        protected void onPreExecute() {

        }

        protected Integer doInBackground(String... strings) {
            signupUserwithFireBase();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
