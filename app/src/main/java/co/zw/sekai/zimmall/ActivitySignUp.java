package co.zw.sekai.zimmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ActivitySignUp extends AppCompatActivity implements View.OnClickListener{
    //Dec Views
    private EditText editName, editPhone, editPin;
    private Button btnSignUp;
    private ProgressDialog loadingBar;

    //Firebase Init
    private FirebaseDatabase usersDB;
    public FirebaseDatabase walletDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Bind Values
        editName = (EditText)findViewById(R.id.editTextName);
        editPhone = (EditText)findViewById(R.id.editTextPhone);
        editPin = (EditText)findViewById(R.id.editTextPin);
        btnSignUp = (Button)findViewById(R.id.buttonSignUp);
        loadingBar = new ProgressDialog(this);

        //Set Onlick
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSignUp){
            createAccount();
        }
    }

    private void createAccount() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String pin = editPin.getText().toString().trim();

        if (TextUtils.isEmpty(name))
        {
            editName.setError("Enter Name");
            editName.requestFocus();
            return;
        }
        else if (TextUtils.isEmpty(phone) || phone.length() < 10)
        {
            editPhone.setError("Invalid Phone");
            editPhone.requestFocus();
            return;
        }
        else if (TextUtils.isEmpty(pin) || pin.length() < 6)
        {
            editPin.setError("Invalid Pin");
            editPin.requestFocus();
            return;
        }
        else
        {
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhoneNumber(name, phone, pin);
        }
    }

    private void validatePhoneNumber(final String name,final String phone, final String pin) {
        final DatabaseReference usersRef;
        final DatabaseReference walletRef;
        usersRef = usersDB.getInstance().getReference().child("Users");
        walletRef = walletDB.getInstance().getReference().child("User Wallets");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("name", name);
                    userdataMap.put("phone", phone);
                    userdataMap.put("pin", pin);
                    userdataMap.put("balance", "0");
                    userdataMap.put("address", "null");
                    userdataMap.put("imageUrl", "null");
                    userdataMap.put("firstTime", "yes");

                    final HashMap<String, Object> walletMap = new HashMap<>();
                    walletMap.put("phone", phone);
                    walletMap.put("balance", "0");

                    usersRef.child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        walletRef.child(phone).updateChildren(walletMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(ActivitySignUp.this, "Your account has been created.", Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();

                                                            Intent intent = new Intent(ActivitySignUp.this, ActivityLogin.class);
                                                            startActivity(intent);
                                                        }

                                                    }
                                                });

                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ActivitySignUp.this, "Network Error: Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    loadingBar.dismiss();
                    editPhone.setError("This Phone Number is Registered");
                    editPhone.requestFocus();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

