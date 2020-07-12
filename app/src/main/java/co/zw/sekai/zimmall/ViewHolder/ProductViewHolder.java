package co.zw.sekai.zimmall.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.zw.sekai.zimmall.Interfaces.ItemClickListener;
import co.zw.sekai.zimmall.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, txtProductPrice, txtPostDate;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = (TextView) itemView.findViewById(R.id.textViewProductName);
        txtProductDescription = (TextView) itemView.findViewById(R.id.textViewProductDescription);
        txtProductPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
        txtPostDate = (TextView)itemView.findViewById(R.id.textViewPostDate);
        imageView = (ImageView) itemView.findViewById(R.id.imageViewProductImage);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;

    }
    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
