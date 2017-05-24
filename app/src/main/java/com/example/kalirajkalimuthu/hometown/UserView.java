package com.example.kalirajkalimuthu.hometown;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kalirajkalimuthu.hometown.db.DatabaseWrapper;
import com.example.kalirajkalimuthu.hometown.db.DbUserDataHelper;
import com.example.kalirajkalimuthu.hometown.ui.ChatUsersListFragment;
import com.google.firebase.auth.FirebaseAuth;


public class UserView extends AppCompatActivity implements FilterFragment.FilterFragmentInterface {

    private FilterFragment filter;
    private UsersPostFragment usersPost;
    private DisplayMapFragment displayMap;
    private BottomNavigationView mBottomNav;
    public ProgressDialog progressDailog;
    private ChatUsersListFragment chatFragment;
   // public DbUserDataHelper dbUserDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);
        FragmentManager fragments = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        filter = new FilterFragment();
        usersPost = new UsersPostFragment();
        fragmentTransaction.add(R.id.filterFragment,filter);
        fragmentTransaction.add(R.id.detailFragment,usersPost);
        fragmentTransaction.commit();
        progressDailog = new ProgressDialog(this);
        progressDailog.setMessage("Loading");
        progressDailog.getWindow().
                setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDailog.setCancelable(false);

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                handleMenuSelection(item);
                return true;
            }
        });


    }


    public void handleMenuSelection(MenuItem item){
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.detailFragment);
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.menu_list:
                if(!(fragment instanceof UsersPostFragment)) {
                    filter.reset();
                    usersPost = new UsersPostFragment();
                    fragmentTransaction.replace(R.id.detailFragment, usersPost);
                }
                break;
            case R.id.menu_map:
                filter.reset();
                if(!(fragment instanceof DisplayMapFragment)) {
                    displayMap = new DisplayMapFragment();
                    fragmentTransaction.replace(R.id.detailFragment, displayMap);
                }
                break;
            case R.id.menu_chat:
                filter.reset();
                if(!(fragment instanceof ChatUsersListFragment)) {
                    chatFragment = new ChatUsersListFragment();
                    fragmentTransaction.replace(R.id.detailFragment, chatFragment);
                }
                break;
        }
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.log_out:
                signout();
                break;
        }

        return true;
    }

    @Override
    public void applyFilter(){
        Fragment fragment = getFragmentManager().findFragmentById(R.id.detailFragment);
        if(fragment instanceof UsersPostFragment){
            ((UsersPostFragment) fragment).displayUsers(filter.getFilter(),false);
        }
        else if (fragment instanceof DisplayMapFragment){
            ((DisplayMapFragment) fragment).displayUsers(filter.getFilter(),false);
        }
    }

    @Override
    public void clearFilter() {
        filter.reset();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.detailFragment);
        if(fragment instanceof UsersPostFragment){
            ((UsersPostFragment) fragment).displayUsers(filter.getFilter(),false);
        }
        else if (fragment instanceof DisplayMapFragment){
            ((DisplayMapFragment) fragment).displayUsers(filter.getFilter(),false);
        }
    }

    @Override
    public void onBackPressed(){
        signout();

    }

    public void signout(){
        startActivity(new Intent(this, LoginActivity.class)); //Go back to home page
        finish();
        Toast.makeText(this, "Logged Out Successfully" , Toast.LENGTH_LONG).show();
    }
}
