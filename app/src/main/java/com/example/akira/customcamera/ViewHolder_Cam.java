package com.example.akira.customcamera;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Akira on 2017/10/25.
 */

public class ViewHolder_Cam extends RecyclerView.ViewHolder {
    public TextView titleText;
    public ImageView imageView;
    public  ViewHolder_Cam(View itemView) {
        super(itemView);
        titleText = (TextView)itemView.findViewById(R.id.image);
        imageView = (ImageView)itemView.findViewById(R.id.imageView);
    }
}
