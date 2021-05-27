package com.vickyjha.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vickyjha.chatter.fragments.CallFragment;
import com.vickyjha.chatter.fragments.ContactsFragment;
import com.vickyjha.chatter.fragments.MessageFragment;
import com.vickyjha.chatter.fragments.VideoCallFragment;

import org.jetbrains.annotations.NotNull;

public class HomepageActivity extends AppCompatActivity {

    BottomNavigationView bNavigation;
    static Fragment  mFragment;
    public static Context context ;

    @Override
    public void onBackPressed() {
        if(mFragment instanceof MessageFragment){
            super.onBackPressed();
        }
        else{
            mFragment = new MessageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,mFragment).commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);
        getPermissions();
        bNavigation = findViewById(R.id.navigation);
        context = HomepageActivity.this;

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MessageFragment()).commit();
        }
        bNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_call:{
                        mFragment = new CallFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,mFragment).commit();
                        break;

                    }
                    case R.id.action_contacts:{
                        mFragment = new ContactsFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,mFragment).commit();
                        break;


                    }
                    case R.id.action_message:{
                        mFragment = new MessageFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,mFragment).commit();
                        break;


                    }
                    case R.id.action_videoCall:{
                        mFragment = new VideoCallFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,mFragment).commit();
                        break;


                    }
                }
                return true;
            }
        });



    }
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }
}

