package com.example.aso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Toast;

import com.chootdev.recycleclick.RecycleClick;
import com.example.aso.Adapter.AdapterAds;
import com.example.aso.Adapter.AdapterMain;
import com.example.aso.Model.ModelAds;
import com.example.aso.Model.ModelMain;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdsActivity extends AppCompatActivity {


    AdapterAds adapterAds;
    RecyclerView RecyclerViewAds;
    ArrayList<ModelAds> modelAdsArrayList;
    ModelAds modelAds;
    ImageView FilterAds;
    LinearLayoutManager linearLayoutManager;
    String ReferenceCategory,Filter;
    Boolean PriceFilter=false;
    int MinPrice=0,MaxPrice=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        ReferenceCategory = getIntent().getStringExtra("cat"); // recieving category as banana for fruit's sub catagory
        Filter = getIntent().getStringExtra("filter");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DSM").child("PostedAds");
        //.child(ReferenceCategory);


        linearLayoutManager = new LinearLayoutManager(this);
        FilterAds = findViewById(R.id.filter_ads);
        FilterAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdsActivity.this, FilterActivity.class);
                i.putExtra("cat", ReferenceCategory);
                startActivity(i);
            }
        });

        Query query = databaseReference.orderByChild("subCategory").equalTo(ReferenceCategory);
        modelAdsArrayList = new ArrayList<>();
        RecyclerViewAds = findViewById(R.id.recyclerviewads);
        RecyclerViewAds.setHasFixedSize(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        RecyclerViewAds.setLayoutManager(linearLayoutManager);


        if (Filter.equalsIgnoreCase("no")) {
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount() != 0) {
                        modelAdsArrayList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            modelAds = new ModelAds();
                            modelAds.setPrice(ds.child("price").getValue().toString());
                            modelAds.setPicture(ds.child("image").getValue().toString());
                            modelAds.setTitle(ds.child("title").getValue().toString());
                            modelAds.setTime(ds.child("time").getValue().toString());
                            modelAds.setUid(ds.child("Uid").getValue().toString());
                            modelAds.setDetails(ds.child("details").getValue().toString());
                            modelAdsArrayList.add(modelAds);
                        }
                    }
                    else
                        {
                        Toast.makeText(AdsActivity.this, "Nothing yet :(", Toast.LENGTH_SHORT).show();
                    }

                    //  perform sorting if needed ..
                    adapterAds = new AdapterAds(AdsActivity.this, modelAdsArrayList);
                    adapterAds.notifyDataSetChanged();
                    RecyclerViewAds.setAdapter(adapterAds);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if (Filter.equalsIgnoreCase("yes")) {

            // filter is yes , it is to be applied .

            final String Province = getIntent().getStringExtra("province");
            final String City = getIntent().getStringExtra("city");
            final String Street = getIntent().getStringExtra("street");

            if (Province.equalsIgnoreCase("Whole Country")) {
                // means no check on province  , i.e. whole country ..

                // so check if there is any check on price or not ..
                String PF=getIntent().getStringExtra("pricefilter");
                Toast.makeText(AdsActivity.this,PF,Toast.LENGTH_SHORT).show();

                if (!PF.equalsIgnoreCase("no")) {
                    // means there are min and max price parameters ..
                    MinPrice = getIntent().getIntExtra("min", 0);
                    MaxPrice = getIntent().getIntExtra("max", 0);


                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getChildrenCount() != 0) {

                                modelAdsArrayList.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    //int price .... check price for prince range from to this ..
                                    modelAds = new ModelAds();
                                    int price = Integer.parseInt(ds.child("price").getValue().toString());
                                    if (price >= MinPrice && price <= MaxPrice) {
                                        modelAds.setPrice(ds.child("price").getValue().toString());
                                        modelAds.setPicture(ds.child("image").getValue().toString());
                                        modelAds.setTitle(ds.child("title").getValue().toString());
                                        modelAds.setTime(ds.child("time").getValue().toString());
                                        modelAds.setUid(ds.child("Uid").getValue().toString());
                                        modelAds.setDetails(ds.child("details").getValue().toString());
                                        modelAdsArrayList.add(modelAds);
                                    }
                                }

                            } else {
                                Toast.makeText(AdsActivity.this, "Nothing yet :(", Toast.LENGTH_SHORT).show();
                            }

                            //  perform sorting if needed ..
                            adapterAds = new AdapterAds(AdsActivity.this, modelAdsArrayList);
                            adapterAds.notifyDataSetChanged();
                            RecyclerViewAds.setAdapter(adapterAds);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    // no price filter ..
                    //so display ads from whole country ..
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getChildrenCount() != 0) {


                                modelAdsArrayList.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    modelAds = new ModelAds();
                                    modelAds.setPrice(ds.child("price").getValue().toString());
                                    modelAds.setPicture(ds.child("image").getValue().toString());
                                    modelAds.setTitle(ds.child("title").getValue().toString());
                                    modelAds.setTime(ds.child("time").getValue().toString());
                                    modelAds.setUid(ds.child("Uid").getValue().toString());
                                    modelAds.setDetails(ds.child("details").getValue().toString());
                                    modelAdsArrayList.add(modelAds);
                                }
                            } else {
                                Toast.makeText(AdsActivity.this, "Nothing yet :(", Toast.LENGTH_SHORT).show();
                            }

                            //  perform sorting if needed ..
                            adapterAds = new AdapterAds(AdsActivity.this, modelAdsArrayList);
                            adapterAds.notifyDataSetChanged();
                            RecyclerViewAds.setAdapter(adapterAds);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
            else {

                // means some province is selected ..
                if (City.equalsIgnoreCase("Whole Province")) {
                    //  means no check on city ..
                    String PF=getIntent().getStringExtra("pricefilter");
                    if (!PF.equalsIgnoreCase("no")) {
                        PriceFilter = true;
                        // means there are min and max price parameters ..
                        MinPrice = getIntent().getIntExtra("min", 0);
                        MaxPrice = getIntent().getIntExtra("max", 0);


                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getChildrenCount() != 0) {

                                    modelAdsArrayList.clear();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        //int price .... check price for prince range from to this ..
                                        modelAds = new ModelAds();
                                        int price = Integer.parseInt(ds.child("price").getValue().toString());

                                        if ((price >= MinPrice && price <= MaxPrice) && ds.child("province").equals(Province)) {
                                            modelAds.setPrice(ds.child("price").getValue().toString());
                                            modelAds.setPicture(ds.child("image").getValue().toString());
                                            modelAds.setTitle(ds.child("title").getValue().toString());
                                            modelAds.setTime(ds.child("time").getValue().toString());
                                            modelAds.setUid(ds.child("Uid").getValue().toString());
                                            modelAds.setDetails(ds.child("details").getValue().toString());
                                            modelAdsArrayList.add(modelAds);
                                        }
                                    }
                                } else {
                                    Toast.makeText(AdsActivity.this, "Nothing yet :(", Toast.LENGTH_SHORT).show();
                                }

                                //  perform sorting if needed ..
                                adapterAds = new AdapterAds(AdsActivity.this, modelAdsArrayList);
                                adapterAds.notifyDataSetChanged();
                                RecyclerViewAds.setAdapter(adapterAds);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        // no price filter ..
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getChildrenCount() != 0)
                                {


                                    modelAdsArrayList.clear();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        modelAds = new ModelAds();
                                        if ((ds.child("province").equals(Province))) {
                                            modelAds.setPrice(ds.child("price").getValue().toString());
                                            modelAds.setPicture(ds.child("image").getValue().toString());
                                            modelAds.setTitle(ds.child("title").getValue().toString());
                                            modelAds.setTime(ds.child("time").getValue().toString());
                                            modelAds.setUid(ds.child("Uid").getValue().toString());
                                            modelAds.setDetails(ds.child("details").getValue().toString());
                                            modelAdsArrayList.add(modelAds);
                                        }
                                    }
                                } else {
                                    Toast.makeText(AdsActivity.this, "Nothing yet :(", Toast.LENGTH_SHORT).show();
                                }

                                //  perform sorting if needed ..
                                adapterAds = new AdapterAds(AdsActivity.this, modelAdsArrayList);
                                adapterAds.notifyDataSetChanged();
                                RecyclerViewAds.setAdapter(adapterAds);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }


                else {
                    // means some city is also selected ..
                    if (Street.equalsIgnoreCase("Whole City"))
                    {
                        // means no street is specified ..
                        String PF=getIntent().getStringExtra("pricefilter");
                        if (!PF.equalsIgnoreCase("no")) {
                            // means there are min and max price parameters ..
                            MinPrice = getIntent().getIntExtra("min", 0);
                            MaxPrice = getIntent().getIntExtra("max", 0);


                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.getChildrenCount() != 0) {

                                        modelAdsArrayList.clear();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            //int price .... check price for prince range from to this ..
                                            modelAds = new ModelAds();
                                            int price = Integer.parseInt(ds.child("price").getValue().toString());

                                            if ((price >= MinPrice && price <= MaxPrice) && (ds.child("city").equals(City)) && (ds.child("province").equals(Province))) {
                                                modelAds.setPrice(ds.child("price").getValue().toString());
                                                modelAds.setPicture(ds.child("image").getValue().toString());
                                                modelAds.setTitle(ds.child("title").getValue().toString());
                                                modelAds.setTime(ds.child("time").getValue().toString());
                                                modelAds.setUid(ds.child("Uid").getValue().toString());
                                                modelAds.setDetails(ds.child("details").getValue().toString());
                                                modelAdsArrayList.add(modelAds);
                                            }
                                        }
                                    } else {
                                        Toast.makeText(AdsActivity.this, "Nothing yet :(", Toast.LENGTH_SHORT).show();
                                    }

                                    //  perform sorting if needed ..
                                    adapterAds = new AdapterAds(AdsActivity.this, modelAdsArrayList);
                                    adapterAds.notifyDataSetChanged();
                                    RecyclerViewAds.setAdapter(adapterAds);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // no price filter ..

                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.getChildrenCount() != 0) {


                                        modelAdsArrayList.clear();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            modelAds = new ModelAds();
                                            if ((ds.child("city").equals(City)) && (ds.child("province").equals(Province))) {
                                                modelAds.setPrice(ds.child("price").getValue().toString());
                                                modelAds.setPicture(ds.child("image").getValue().toString());
                                                modelAds.setTitle(ds.child("title").getValue().toString());
                                                modelAds.setTime(ds.child("time").getValue().toString());
                                                modelAds.setUid(ds.child("Uid").getValue().toString());
                                                modelAds.setDetails(ds.child("details").getValue().toString());
                                                modelAdsArrayList.add(modelAds);

                                            }
                                        }
                                    } else {
                                        Toast.makeText(AdsActivity.this, "Nothing yet :(", Toast.LENGTH_SHORT).show();
                                    }

                                    //  perform sorting if needed ..
                                    adapterAds = new AdapterAds(AdsActivity.this, modelAdsArrayList);
                                    adapterAds.notifyDataSetChanged();
                                    RecyclerViewAds.setAdapter(adapterAds);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                    else
                        {
                        // means some street is also selected ..
                            String PF=getIntent().getStringExtra("pricefilter");
                            if (!PF.equalsIgnoreCase("no")) {
                                // means there are min and max price parameters ..
                                MinPrice = getIntent().getIntExtra("min", 0);
                                MaxPrice = getIntent().getIntExtra("max", 0);


                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.getChildrenCount() != 0) {

                                            modelAdsArrayList.clear();
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                //int price .... check price for prince range from to this ..
                                                modelAds = new ModelAds();
                                                int price = Integer.parseInt(ds.child("price").getValue().toString());

                                                if ((price >= MinPrice && price <= MaxPrice)
                                                        && (ds.child("city").equals(City))
                                                        && (ds.child("province").equals(Province))
                                                        && (ds.child("street").equals(Street)))
                                                {
                                                    modelAds.setPrice(ds.child("price").getValue().toString());
                                                    modelAds.setPicture(ds.child("image").getValue().toString());
                                                    modelAds.setTitle(ds.child("title").getValue().toString());
                                                    modelAds.setTime(ds.child("time").getValue().toString());
                                                    modelAds.setUid(ds.child("Uid").getValue().toString());
                                                    modelAds.setDetails(ds.child("details").getValue().toString());
                                                    modelAdsArrayList.add(modelAds);
                                                }
                                            }
                                        } else {
                                            Toast.makeText(AdsActivity.this, "Nothing yet :(", Toast.LENGTH_SHORT).show();
                                        }

                                        //  perform sorting if needed ..
                                        adapterAds = new AdapterAds(AdsActivity.this, modelAdsArrayList);
                                        adapterAds.notifyDataSetChanged();
                                        RecyclerViewAds.setAdapter(adapterAds);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else
                                {
                                // no price filter ..

                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.getChildrenCount() != 0) {


                                            modelAdsArrayList.clear();
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                modelAds = new ModelAds();
                                                if ((ds.child("city").equals(City))
                                                        && (ds.child("province").equals(Province))
                                                        && (ds.child("street").equals(Street)))
                                                {
                                                    modelAds.setPrice(ds.child("price").getValue().toString());
                                                    modelAds.setPicture(ds.child("image").getValue().toString());
                                                    modelAds.setTitle(ds.child("title").getValue().toString());
                                                    modelAds.setTime(ds.child("time").getValue().toString());
                                                    modelAds.setUid(ds.child("Uid").getValue().toString());
                                                    modelAds.setDetails(ds.child("details").getValue().toString());
                                                    modelAdsArrayList.add(modelAds);

                                                }
                                            }
                                        } else {
                                            Toast.makeText(AdsActivity.this, "Nothing yet :(", Toast.LENGTH_SHORT).show();
                                        }

                                        //  perform sorting if needed ..
                                        adapterAds = new AdapterAds(AdsActivity.this, modelAdsArrayList);
                                        adapterAds.notifyDataSetChanged();
                                        RecyclerViewAds.setAdapter(adapterAds);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }


                }

            }




            RecycleClick.addTo(RecyclerViewAds).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    Intent i = new Intent(AdsActivity.this, AdDetails.class);
                    i.putExtra("uid", modelAdsArrayList.get(position).getUid());
                    i.putExtra("price", modelAdsArrayList.get(position).getPrice());
                    i.putExtra("title", modelAdsArrayList.get(position).getTitle());
                    i.putExtra("time", modelAdsArrayList.get(position).getTime());
                    i.putExtra("image", modelAdsArrayList.get(position).getPicture());
                    i.putExtra("details", modelAdsArrayList.get(position).getDetails());

                    // Toast.makeText(AdsActivity.this,modelAdsArrayList.get(position).getUid(),Toast.LENGTH_SHORT).show();

                    startActivity(i);
                }
            });
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            showSortDialogue();
            return true;
        }
        else if (id==R.id.action_filter)
        {
            Intent i=new Intent(AdsActivity.this,FilterActivity.class);
            i.putExtra("cat", ReferenceCategory);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void showSortDialogue()
    {
        String[] optionsItems = {"Newest","Oldest"};
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(AdsActivity.this);
        builder.setTitle("Sort By :").setItems(optionsItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0) {
                    // newest post first ..
                    linearLayoutManager.setReverseLayout(false);
                    linearLayoutManager.setStackFromEnd(false);
                    //recreate();
                }
                else if(which==1)
                {
                    // oldest post first ..

                    linearLayoutManager.setReverseLayout(true);
                    linearLayoutManager.setStackFromEnd(true);
                   // recreate();
                }
                }


            }
            );
        builder.create().show();
    }

}