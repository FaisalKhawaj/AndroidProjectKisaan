package com.example.aso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class AdSelectionActivity extends AppCompatActivity {

    ImageView Vegetables,Fruits,Grains,Dairy,Poultry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_selection);
        Vegetables=findViewById(R.id.vegetables);
        Fruits=findViewById(R.id.fruit);
        Grains=findViewById(R.id.grains);
        Dairy=findViewById(R.id.dairy);
        Poultry=findViewById(R.id.poltry);

        Picasso.get().load("gs://discover-pakistan-fa761.appspot.com/DSMImages/vegetable.jpg").into(Vegetables);
        Picasso.get().load("gs://discover-pakistan-fa761.appspot.com/DSMImages/fruit.jpg").into(Fruits);
        Picasso.get().load("gs://discover-pakistan-fa761.appspot.com/DSMImages/grains.jpg").into(Grains);
        Picasso.get().load("gs://discover-pakistan-fa761.appspot.com/DSMImages/dairy.jpg").into(Dairy);
        Picasso.get().load("gs://discover-pakistan-fa761.appspot.com/DSMImages/poultry.jpg").into(Poultry);
        Poultry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdSelectionActivity.this,PostAdActivity.class);
                intent.putExtra("selection","poultry");
                startActivity(intent);
            }
        });
        Dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdSelectionActivity.this,PostAdActivity.class);
                intent.putExtra("selection","dairy");
                startActivity(intent);
            }
        });

        Vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdSelectionActivity.this,PostAdActivity.class);
                intent.putExtra("selection","vegetables");
                startActivity(intent);
            }
        });


        Fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdSelectionActivity.this,PostAdActivity.class);
                intent.putExtra("selection","fruits");
                //   Toast.makeText(SelectionActivity.this,"fruits")
                startActivity(intent);
            }
        });

        Grains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdSelectionActivity.this,PostAdActivity.class);
                intent.putExtra("selection","grains");
                startActivity(intent);
            }
        });
    }
}
