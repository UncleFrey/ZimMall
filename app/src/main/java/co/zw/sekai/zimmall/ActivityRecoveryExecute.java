package co.zw.sekai.zimmall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityRecoveryExecute extends AppCompatActivity implements View.OnClickListener{
    //Dec Views
    private TextView txtQst;
    private EditText editAns;
    private Button btnProceed;

    //Dec Firebase
    private DatabaseReference recoveryRef;

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_execute);

        phone = getIntent().getStringExtra("phone");

        recoveryRef = FirebaseDatabase.getInstance().getReference().child("Recovery Options")
                .child(phone);
        
        //Bind views
        txtQst = findViewById(R.id.textViewQst);
        editAns = findViewById(R.id.editTextAns);
        btnProceed = findViewById(R.id.buttonProceed);

        btnProceed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnProceed){

        }
    }
}
