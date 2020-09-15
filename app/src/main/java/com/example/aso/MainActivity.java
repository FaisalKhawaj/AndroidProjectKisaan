package com.example.aso;

import android.content.Intent;
import android.os.Bundle;

import com.chootdev.recycleclick.RecycleClick;
import com.example.aso.Adapter.AdapterMain;
import com.example.aso.Model.ModelMain;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView mRecyclerView;
    ModelMain modelMain;
    ArrayList<ModelMain> mainArrayList;
    DatabaseReference databaseReference;
    AdapterMain adapterMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainArrayList=new ArrayList<>();
        mRecyclerView =findViewById(R.id.mRecyclerView);
        String selection=getIntent().getStringExtra("selection");

        databaseReference= FirebaseDatabase.getInstance().getReference().child("DSM").child("SavedCategories").child(selection);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mainArrayList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    modelMain=new ModelMain();
                    modelMain.setImage(ds.child("image").getValue().toString());
                    modelMain.setTitle(ds.child("title").getValue().toString());
                    mainArrayList.add(modelMain);
                }
                adapterMain=new AdapterMain(MainActivity.this,mainArrayList);
                adapterMain.notifyDataSetChanged();
                mRecyclerView.setAdapter(adapterMain);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        setData();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        RecycleClick.addTo(mRecyclerView).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent i=new Intent(MainActivity.this,AdsActivity.class);
                i.putExtra("cat",mainArrayList.get(position).getTitle());
                i.putExtra("filter","no");
               // Toast.makeText(MainActivity.this,"item clicked",Toast.LENGTH_SHORT).show();

                startActivity(i);
            }
        });
    }

    private void setData() {







        }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // post an ad

            startActivity(new Intent(MainActivity.this,AdSelectionActivity.class));
        } else if (id == R.id.nav_profile) {

            //  user profile button clicked

            startActivity(new Intent(MainActivity.this,ProfileActivity.class));


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
