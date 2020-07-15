package co.zw.sekai.zimmall.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.zw.sekai.zimmall.R;

public class PinResetRequestViewHolder extends RecyclerView.ViewHolder {

    public TextView phone, pin;
    public ImageView imgSMS, imgDEL;


    public PinResetRequestViewHolder(@NonNull View itemView) {
        super(itemView);

        phone = itemView.findViewById(R.id.textViewPhone);
        pin = itemView.findViewById(R.id.textViewNewPin);
        imgSMS = itemView.findViewById(R.id.imageViewMsg);
        imgDEL = itemView.findViewById(R.id.imageViewDel);

    }
}
