package co.zw.sekai.zimmall.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.zw.sekai.zimmall.Interfaces.ItemClickListener;
import co.zw.sekai.zimmall.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName, txtProductPrice, txtProductQty, txtProductTotal;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.textViewProductName);
        txtProductPrice = itemView.findViewById(R.id.textViewProductPrice);
        txtProductQty = itemView.findViewById(R.id.textViewProductQty);
        txtProductTotal = itemView.findViewById(R.id.textViewProductTotal);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListener(View.OnClickListener itemClickListener) {
        this.itemClickListener = (ItemClickListener) itemClickListener;
    }
}
