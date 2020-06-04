package com.example.etanker.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.etanker.Interface.ItemClickListener;
import com.example.etanker.R;

public class TankerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tankerNumber;
    public TextView tankerDriverName;
    public TextView Status;

    private ItemClickListener itemClickListener;
    public TankerViewHolder(@NonNull View itemView) {
        super(itemView);
        tankerDriverName=itemView.findViewById(R.id.tankerDriverName1);
        tankerNumber=itemView.findViewById(R.id.tankerNumber);
        Status=itemView.findViewById(R.id.tankerStatus);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

}
