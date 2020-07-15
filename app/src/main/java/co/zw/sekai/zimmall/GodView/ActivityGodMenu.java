package co.zw.sekai.zimmall.GodView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import co.zw.sekai.zimmall.AdminActivities.ActivityAdminDepositsManager;
import co.zw.sekai.zimmall.AdminActivities.ActivityAdminMenu;
import co.zw.sekai.zimmall.AdminActivities.ActivityAdminResetPin;
import co.zw.sekai.zimmall.R;

public class ActivityGodMenu extends AppCompatActivity implements View.OnClickListener{
    private TextView txtDeposits, txtPinReset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_god_menu);

        txtDeposits = findViewById(R.id.textViewDepositsManager);
        txtPinReset = findViewById(R.id.textViewPinResets);

        txtPinReset.setOnClickListener(this);
        txtDeposits.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == txtDeposits){
            Intent intent = new Intent(ActivityGodMenu.this, ActivityAdminDepositsManager.class);
            startActivity(intent);
        }

        if (v == txtPinReset){
            Intent intent = new Intent(ActivityGodMenu.this, ActivityAdminResetPin.class);
            startActivity(intent);
        }
    }
}
