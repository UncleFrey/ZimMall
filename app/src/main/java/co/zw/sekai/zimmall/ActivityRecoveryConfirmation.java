package co.zw.sekai.zimmall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ActivityRecoveryConfirmation extends AppCompatActivity {
    private String phone;
    private TextView txtMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_confirmation);
        phone = getIntent().getStringExtra("phone");
        txtMsg = findViewById(R.id.textViewMsg);

        txtMsg.setText("A new reset pin will be sent to " + phone + ".\n This message should be" +
                "expected within 30 Mins.");

    }
}
