package co.zw.sekai.zimmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import co.zw.sekai.zimmall.AdminActivities.ActivityAdminCategory;
import co.zw.sekai.zimmall.AdminActivities.ActivityAdminMenu;
import co.zw.sekai.zimmall.Models.UserWallet;
import co.zw.sekai.zimmall.Models.Users;
import co.zw.sekai.zimmall.Prevalent.Prevalent;
import io.paperdb.Paper;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener{
    //Dec Values
    private EditText editPhone, editPin;
    private Button btnLogin;
    private CheckBox checkRemember;
    private TextView txtSignature;

    //Firebase
    private FirebaseDatabase usersDB;

    //Global Values
    private String parentDbName = "Users";
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Bind Views
        editPhone = (EditText)findViewById(R.id.editTextPhone);
        editPin = (EditText)findViewById(R.id.editTextPin);
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        checkRemember = (CheckBox)findViewById(R.id.checkBoxRemember);
        loadingBar = new ProgressDialog(this);
        txtSignature = (TextView)findViewById(R.id.textViewSignature);
        //Paper
        Paper.init(this);

        //Set Onclick
        btnLogin.setOnClickListener(this);
        txtSignature.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin){
            checkAcc();
        }

        if (v == txtSignature) {
            toggleMode();
        }
    }

    private void toggleMode() {
        if (parentDbName.equals("Users")) {
            parentDbName = "Admins";
            Toast.makeText(this, "King Mode", Toast.LENGTH_SHORT).show();

        }else if (parentDbName.equals("Admins")) {
            parentDbName = "Users";
            Toast.makeText(this, "Queen Mode", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkAcc() {
        String phone = editPhone.getText().toString().trim();
        String pin = editPin.getText().toString().trim();
        
        if (TextUtils.isEmpty(phone) || phone.length() != 10)
        {
            editPhone.setError("Invalid Pin");
            editPhone.requestFocus();
            return;
        }
        else if (TextUtils.isEmpty(pin) || pin.length() != 6)
        {
            editPin.setError("Invalid Pin");
            editPin.requestFocus();
            return;
        }
        else
        {
            loadingBar.setTitle("Checking Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validateLogin(phone, pin);
        }

    }

    private void validateLogin(final String phone,final String pin) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference balanceRef;
        balanceRef = FirebaseDatabase.getInstance().getReference().child("User Wallets").child(phone);

        final DatabaseReference viewBalanceRef;
        viewBalanceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    final Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPin().equals(pin))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(ActivityLogin.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(ActivityLogin.this, ActivityAdminMenu.class);
                                startActivity(intent);
                                finish();
                            }
                            else if (parentDbName.equals("Users"))
                            {
                                //Take Paper Remeber Data
                                if(checkRemember.isChecked())
                                {
                                    Paper.book().destroy();
                                    Paper.book().write(Prevalent.UserPhoneKey, phone);
                                    Paper.book().write(Prevalent.UserPinKey, pin);
                                }

                                Prevalent.currentOnlineUser = usersData;

                                balanceRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserWallet userWallet = dataSnapshot.getValue(UserWallet.class);
                                        HashMap<String, Object> balMap = new HashMap<>();
                                        balMap.put("balance", userWallet.getBalance());
                                        viewBalanceRef.updateChildren(balMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    Toast.makeText(ActivityLogin.this, "Log In Successful", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();

                                                    if (usersData.getFirstTime().equals("yes")){
                                                        Intent intent = new Intent(ActivityLogin.this, ActivitySecurityQuestions.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }else if (usersData.getFirstTime().equals("no")){
                                                        Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            editPin.setError("Pin is Incorrect");
                            editPin.requestFocus();
                        }
                    }
                }
                else
                {
                    loadingBar.dismiss();
                    editPhone.setError("Account Does Not Exist");
                    editPhone.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
