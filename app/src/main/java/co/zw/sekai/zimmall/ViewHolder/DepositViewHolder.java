package co.zw.sekai.zimmall.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.zw.sekai.zimmall.R;

public class DepositViewHolder extends RecyclerView.ViewHolder {
    public TextView txtUserName, txtPhone, txtState, txtDateTime;
    public Spinner spinState;
    public Button btnExecuteDeposit, btnView, btnComplete, btnSetState;

    public DepositViewHolder(@NonNull View itemView) {
        super(itemView);

        txtUserName = itemView.findViewById(R.id.textViewUserName);
        txtPhone = itemView.findViewById(R.id.textViewPhone);
        txtState = itemView.findViewById(R.id.textViewState);
        txtDateTime = itemView.findViewById(R.id.textViewDateTime);
        spinState = itemView.findViewById(R.id.spinnerState);
        btnExecuteDeposit = itemView.findViewById(R.id.buttonExecuteDeposit);
        btnView = itemView.findViewById(R.id.buttonView);
        btnComplete = itemView.findViewById(R.id.buttonComplete);
        btnSetState = itemView.findViewById(R.id.buttonSetState);
    }
}
