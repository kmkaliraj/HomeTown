package com.example.kalirajkalimuthu.hometown;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class FilterFragment extends Fragment {

    private String country_url = "http://bismarck.sdsu.edu/hometown/countries";
    private String states_url = "http://bismarck.sdsu.edu/hometown/states?country=";

    private List<String> years = new ArrayList<String>(){{
        add("Select(None)");
    }};
    private  List<String> countries = new ArrayList<String>(){{
        add("Select(None)");
    }};

    private List<String>  states = new ArrayList<String>(){{
        add("Select(None)");
    }};
    private Spinner mCountrySpinner;
    private Spinner mStateSpinner;
    private Spinner mYearSpinner;
    private Button mApplyFilter;
    private Button mClearFilter;

    private String country, state, year;


    public static interface FilterFragmentInterface{
       void  applyFilter();
       void clearFilter();
    }

    public void reset(){
        countries.clear();
        states.clear();
        years.clear();
        countries.add("Select(None)");
        states.add("Select(None)");
        years.add("Select(None)");

        populateCountryData();
        populateYearDetails();

        ArrayAdapter<String> statesAdapter=new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, states);
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStateSpinner.setAdapter(statesAdapter);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        runFilter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void  initializeFilter(){
        new MyAsyncFilterTask().execute();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view  = inflater.inflate(R.layout.fragment_filter, container, false);

        mCountrySpinner = (Spinner) view.findViewById(R.id.CountrySpinner);
        mStateSpinner = (Spinner) view.findViewById(R.id.StateSpinner);
        mYearSpinner = (Spinner) view.findViewById(R.id.YearSpinner);
        mApplyFilter = (Button) view.findViewById(R.id.apply_filter);
        mClearFilter = (Button) view.findViewById(R.id.clear_filter);



        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        mStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                state = (String)adapterView.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        mYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                year= (String)adapterView.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        mApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apply();
            }
        });
        mClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               clear();
            }
        });


        return view;
    }

    public void apply(){
        ((FilterFragment.FilterFragmentInterface)getActivity()).applyFilter();
    }

    public void clear(){
        clearFilter();
        ((FilterFragment.FilterFragmentInterface)getActivity()).clearFilter();
    }

    public void prepareFilters(){

        populateCountryData();
        populateYearDetails();

        ArrayAdapter<String> stateAdapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, states);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStateSpinner.setAdapter(stateAdapter);
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
                            ArrayAdapter<String> countryAdapter=new ArrayAdapter<String>(getActivity(),
                                    R.layout.spinner_item, countries);
                            countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mCountrySpinner.setAdapter(countryAdapter);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Some", "Error: " + error.getMessage());
            }
        });
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayReq, "do");
    }

    public void populateStateDetails(){
        states.clear();
        states.add("Select(None)");
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
                            ArrayAdapter<String> statesAdapter=new ArrayAdapter<String>(getActivity(),
                                    R.layout.spinner_item, states);
                            statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mStateSpinner.setAdapter(statesAdapter);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Some", "Error: " + error.getMessage());
            }
        });
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayReq, "do");
    }

    public void populateYearDetails(){
        for(int i=2017; i >= 1970; i--)
            years.add(String.valueOf(i));

        ArrayAdapter<String> yearAdapter=new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mYearSpinner.setAdapter(yearAdapter);

    }

    public String getFilter(){
        String filter = "";
        if(country != null && !country.contains("None"))
            filter = "country="+country;
        if(state != null && !state.contains("None"))
            filter =  filter.length() > 0 ?filter+"&state="+state : filter+"state="+state;
        if(year != null && !year.contains("None"))
            filter = filter.length() > 0 ? filter+"&year="+year : filter+"year="+year;
        return filter.length() > 0 ? filter: null;
    }

    public void clearFilter(){
        country = null;
        state = null;
        year = null;
    }

    public void runFilter(){
        new MyAsyncFilterTask().execute();
    }

    private class MyAsyncFilterTask extends AsyncTask<String, Integer, Integer> {
        protected void onPreExecute() {
        }

        protected Integer doInBackground(String... strings) {
            prepareFilters();
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
}
