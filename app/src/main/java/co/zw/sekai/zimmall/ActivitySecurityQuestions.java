package co.zw.sekai.zimmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import co.zw.sekai.zimmall.Prevalent.Prevalent;

public class ActivitySecurityQuestions extends AppCompatActivity implements View.OnClickListener {
    private RadioGroup radios;
    private RadioButton radioBtn;
    private Button btnSet;
    
    private String qst, ans;

    private EditText editAns;

    private DatabaseReference recoveryRef, userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_questions);
        radios = findViewById(R.id.radiosGroup);
        btnSet = findViewById(R.id.buttonSet);
        editAns = findViewById(R.id.editTextAns);


        btnSet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnSet){
            int pos = radios.getCheckedRadioButtonId();
            radioBtn = findViewById(pos);
            qst = radioBtn.getText().toString().trim();
            ans = editAns.getText().toString().trim();
            uploadRecoveryInfo(qst, ans);
        }
    }

    private void uploadRecoveryInfo(String qst, String ans) {
        recoveryRef = FirebaseDatabase.getInstance().getReference().child("Recovery Options")
        .child(Prevalent.currentOnlineUser.getPhone());
        userRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> recoveryMap = new HashMap<>();
        recoveryMap.put("phone", Prevalent.currentOnlineUser.getPhone());
        recoveryMap.put("state", "false");
        recoveryMap.put("qst", qst);
        recoveryMap.put("ans", ans);

        final HashMap<String, Object> firstTimeMap = new HashMap<>();
        firstTimeMap.put("firstTime", "no");

        recoveryRef.updateChildren(recoveryMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //Update USe Info
                            userRef.updateChildren(firstTimeMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                //Updated, Proceed to Home
                                                Intent intent  = new Intent(ActivitySecurityQuestions.this,
                                                        ActivityHome.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });


                        }
                    }
                });
    }
}
