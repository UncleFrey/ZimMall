package co.zw.sekai.zimmall.AdminActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import co.zw.sekai.zimmall.ActivityWelcome;
import co.zw.sekai.zimmall.R;

public class ActivityAdminMenu extends AppCompatActivity implements View.OnClickListener{
    private ImageView imgProducts, imgOrders ,imgDeposits;
    private Button btnLogout, btnAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
        //BInd
        imgProducts = findViewById(R.id.imageViewProducts);
        imgOrders = findViewById(R.id.imageViewOrders);
        imgDeposits = findViewById(R.id.imageViewDeposits);
        btnAdmin = findViewById(R.id.buttonAdminManage);
        btnLogout = findViewById(R.id.buttonLogout);

        //OnClick
        imgProducts.setOnClickListener(this);
        imgOrders.setOnClickListener(this);
        imgDeposits.setOnClickListener(this);
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

        if (v == imgDeposits){
            Intent intent = new Intent(ActivityAdminMenu.this, ActivityAdminDepositsManager.class);
            startActivity(intent);
        }

        if (v == btnAdmin){
            //Nav to Admin Manager
        }

        if (v == btnLogout){
            //Logout
            Intent logOut = new Intent(ActivityAdminMenu.this, ActivityWelcome.class);
            logOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logOut);
            finish();
        }
    }
}
