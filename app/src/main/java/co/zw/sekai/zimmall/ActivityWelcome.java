package co.zw.sekai.zimmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.zw.sekai.zimmall.AdminActivities.ActivityAdminCategory;
import co.zw.sekai.zimmall.Models.Users;
import co.zw.sekai.zimmall.Prevalent.Prevalent;
import io.paperdb.Paper;

public class ActivityWelcome extends AppCompatActivity implements View.OnClickListener{
    //Declare Views
    private Button btnLogin, btnSignUp;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Bind Views
        btnLogin = (Button)findViewById(R.id.buttonLogin);
        btnSignUp = (Button)findViewById(R.id.buttonSignUp);
        loadingBar = new ProgressDialog(this);

        //Paper Remember
        Paper.init(this);

        //Set OnCLickListeners
        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPinKey = Paper.book().read(Prevalent.UserPinKey);

        if (UserPhoneKey != "" && UserPinKey != ""){
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPinKey)){
                allowAccess(UserPhoneKey, UserPinKey);

                loadingBar.setTitle("Checking Account");
                loadingBar.setMessage("We remember you, logging in.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin){
            Intent tologin = new Intent(ActivityWelcome.this, ActivityLogin.class);
            startActivity(tologin);
        }
        if(v == btnSignUp){
            Intent toSignUp = new Intent(ActivityWelcome.this, ActivitySignUp.class);
            startActivity(toSignUp);
        }

    }

    private void allowAccess(final String phone, final String pin) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("Users").child(phone).exists())
                {
                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPin().equals(pin))
                        {
                            if ("Users".equals("Admins"))
                            {
                                loadingBar.dismiss();

                                Intent intent = new Intent(ActivityWelcome.this, ActivityAdminCategory.class);
                                startActivity(intent);
                            }
                            else if ("Users".equals("Users"))
                            {
                                loadingBar.dismiss();

                                Intent intent = new Intent(ActivityWelcome.this, ActivityHome.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(ActivityWelcome.this, "Password may have been changed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(ActivityWelcome.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
