package com.example.akira.customcamera;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Akira on 2017/06/21.
 */

public class ImageViewAdapter extends RecyclerView.Adapter<ViewHolder_Cam> {
    private List<SingleImage> list;

    public ImageViewAdapter(List<SingleImage> list) {
        this.list = list;
    }

    @Override
    public ViewHolder_Cam onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_slide,parent,false);
        ViewHolder_Cam vh = new ViewHolder_Cam(inflate);
        return  vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder_Cam holder, int position) {
        holder.imageView.setImageBitmap(list.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
