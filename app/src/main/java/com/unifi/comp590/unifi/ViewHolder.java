package com.unifi.comp590.unifi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
public abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
    public ViewHolder(View itemView) {
        super(itemView);
    }
}
