package com.example.akira.customcamera;

import android.widget.ImageView;


/**
 * Created by Akira on 2017/10/22.
 */

class SingleImage {
    private ImageView image;
    private String text;

    public ImageView getImage() {
        return this.image;
    }

    public String getText() {
        return  this.text;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public void setText(String text) {
        this.text = text;
    }

}
