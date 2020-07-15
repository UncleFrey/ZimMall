package co.zw.sekai.zimmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import co.zw.sekai.zimmall.Models.RecoveryOptions;

public class ActivityRecoveryExecute extends AppCompatActivity implements View.OnClickListener{
    //Dec Views
    private TextView txtQst;
    private EditText editAns;
    private Button btnProceed;
    private String qst, ans;

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

        loadQuestion();
    }

    private void loadQuestion() {
        recoveryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RecoveryOptions recoveryOptions = dataSnapshot.getValue(RecoveryOptions.class);
                qst = recoveryOptions.getQst();
                ans = recoveryOptions.getAns();
                txtQst.setText(qst);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkAnswer() {
        //compare
        if (ans.equals(editAns.getText().toString().trim())){
            submitRequest();
        }else{
            editAns.setError("Unexpected response, please try again.");
            editAns.requestFocus();
        }
    }

    private void submitRequest() {
        HashMap<String, Object> stateMap = new HashMap<>();
        stateMap.put("state", "true");

        recoveryRef.updateChildren(stateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(ActivityRecoveryExecute.this, ActivityRecoveryConfirmation.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnProceed){
            checkAnswer();
        }
    }


}
