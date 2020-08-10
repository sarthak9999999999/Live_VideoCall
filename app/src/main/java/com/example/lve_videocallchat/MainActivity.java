package com.example.lve_videocallchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navView;
    RecyclerView contact_list;
    ImageView add_friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        contact_list=findViewById(R.id.contact_list);
        add_friends=findViewById(R.id.findpeople);
        contact_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        add_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findpeople=new Intent(MainActivity.this,FindFriends.class);
                startActivity(findpeople);
                finish();
            }
        });

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case(R.id.navigation_home):
                    Intent intent=new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent);
                    break;

                case(R.id.navigation_notifications):
                    Intent notification=new Intent(MainActivity.this,NotificationsActivity.class);
                    startActivity(notification);
                    break;

                case(R.id.navigation_settings):
                    Intent settings=new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(settings);
                    break;

                case(R.id.navigation_logout):
                    FirebaseAuth.getInstance().signOut();
                    Intent signout=new Intent(MainActivity.this,Registration_Activity.class);
                    startActivity(signout);
                    finish();
            }
            return true;
        }
    };
}