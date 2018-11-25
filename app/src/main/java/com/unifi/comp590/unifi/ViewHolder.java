package com.unifi.comp590.unifi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewHolder extends RecyclerView.ViewHolder   {
    public ViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(Message message){}

}
