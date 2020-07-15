package co.zw.sekai.zimmall.AdminActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import co.zw.sekai.zimmall.ActivityWelcome;
import co.zw.sekai.zimmall.GodView.ActivityGodMenu;
import co.zw.sekai.zimmall.R;

public class ActivityAdminMenu extends AppCompatActivity implements View.OnClickListener{
    private ImageView imgProducts, imgOrders;
    private Button btnLogout, btnAdmin;

    Dialog dialogGodPin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
        //BInd
        imgProducts = findViewById(R.id.imageViewProducts);
        imgOrders = findViewById(R.id.imageViewOrders);
        btnAdmin = findViewById(R.id.buttonAdminManage);
        btnLogout = findViewById(R.id.buttonLogout);
        dialogGodPin = new Dialog(this);

        //OnClick
        imgProducts.setOnClickListener(this);
        imgOrders.setOnClickListener(this);
        btnAdmin.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgProducts){
            //Goto Products
            Intent intent = new Intent(ActivityAdminMenu.this, ActivityAdminCategory.class);
            startActivity(intent);

        }

        if (v == imgOrders){
            //GotoOrders
            Intent intent = new Intent(ActivityAdminMenu.this, ActivityAdminNewOrders.class);
            startActivity(intent);
        }

        if (v == btnAdmin){
            //Nav to Admin Manager
            gotoGodView();
        }

        if (v == btnLogout){
            //Logout
            Intent logOut = new Intent(ActivityAdminMenu.this, ActivityWelcome.class);
            logOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logOut);
            finish();
        }
    }

    private void gotoGodView() {
        dialogGodPin.setContentView(R.layout.popup_god_pin);
        final TextView editPin = dialogGodPin.findViewById(R.id.editTextGodPin);
        Button btnGo = dialogGodPin.findViewById(R.id.buttonGo);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPin.getText().toString().trim().equals("3227010775305558")){
                    Intent intent = new Intent(ActivityAdminMenu.this, ActivityGodMenu.class);
                    startActivity(intent);
                    dialogGodPin.dismiss();
                }else {
                    editPin.setError("You are not worthy.");
                    editPin.requestFocus();
                }
            }
        });

        dialogGodPin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogGodPin.show();

    }


}
