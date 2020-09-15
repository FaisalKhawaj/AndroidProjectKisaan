package com.example.aso.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aso.R;

public class HolderAds extends RecyclerView.ViewHolder {

    public ImageView Image;
    public TextView PriceTV,DateTV,TitleTV;

    public HolderAds(@NonNull View itemView) {
        super(itemView);
        Image=itemView.findViewById(R.id.Image_Ads);
        PriceTV=itemView.findViewById(R.id.ad_price_tv);
        DateTV=itemView.findViewById(R.id.time_TV);
        TitleTV=itemView.findViewById(R.id.image_title_ads);


    }
}
