package co.zw.sekai.zimmall.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import co.zw.sekai.zimmall.R;

public class AdminOrderViewHolder extends RecyclerView.ViewHolder {
    public TextView txtUserName, txtDateTime, txtTownCityName, txtOrderValue;
    public Button btnViewOrder;

    public AdminOrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtUserName = itemView.findViewById(R.id.textViewUserName);
        txtDateTime = itemView.findViewById(R.id.textViewDateTime);
        txtTownCityName = itemView.findViewById(R.id.textViewTownCityName);
        txtOrderValue = itemView.findViewById(R.id.textViewOrderTotal);
        btnViewOrder = itemView.findViewById(R.id.buttonViewOrder);
    }
}
