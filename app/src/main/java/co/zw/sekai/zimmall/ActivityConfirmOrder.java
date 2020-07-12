package co.zw.sekai.zimmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import co.zw.sekai.zimmall.Prevalent.Prevalent;

public class ActivityConfirmOrder extends AppCompatActivity implements View.OnClickListener{
    //getValues
    String totalBill;
    //Views
    private EditText editName, editPhone, editHomeAddress, editTownCityName, editProvinceName;
    private TextView txtTopUp;
    private Button btnBill;

    //Pay Now
    Dialog dialogPayNow;
    EditText editPin;
    Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        totalBill = getIntent().getStringExtra("totalBill");

        editName = findViewById(R.id.editTextName);
        editPhone = findViewById(R.id.editTextContactPhone);
        editHomeAddress = findViewById(R.id.editTextAddress);
        editTownCityName = findViewById(R.id.editTextCityName);
        editProvinceName = findViewById(R.id.editTextCityProvinceName);
        txtTopUp = findViewById(R.id.textViewTopUpHelp);
        btnBill = findViewById(R.id.buttonPayBill);
        dialogPayNow = new Dialog(this);

        btnBill.setOnClickListener(this);
        txtTopUp.setOnClickListener(this);

        //Set Preliminary
        editName.setText(Prevalent.currentOnlineUser.getName());
        editPhone.setText(Prevalent.currentOnlineUser.getPhone());
        editHomeAddress.setText(Prevalent.currentOnlineUser.getAddress());
    }

    @Override
    public void onClick(View v) {
        if (v == btnBill){

            validate();

        }


        if (v == txtTopUp){
            //Goto TopUp Help Page
        }
    }

    public void payNow(){
        dialogPayNow.setContentView(R.layout.pop_up_pay_now);
        editPin = (EditText) dialogPayNow.findViewById(R.id.editTextPin);
        btnPay = (Button) dialogPayNow.findViewById(R.id.buttonPay);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String validPin = Prevalent.currentOnlineUser.getPin();
                final String pin = editPin.getText().toString();
                if (TextUtils.isEmpty(pin) || pin.length() < 6){
                    editPin.setError("Pin Invalid");
                    editPin.requestFocus();
                }else if (validPin.equals(pin)){
                    //Allow
                    //Proceed to Pay Bill
                    final float balance = Float.parseFloat(Prevalent.currentOnlineUser.getBalance());
                    final float bill = Float.parseFloat(totalBill);

                    if (balance > bill){
                        final DatabaseReference balRef = FirebaseDatabase.getInstance().getReference()
                                .child("User Wallets").child(Prevalent.currentOnlineUser.getPhone());
                        final float change = balance - bill;

                        balRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> balMap = new HashMap<>();
                                balMap.put("balance", Float.toString(change));

                                balRef.updateChildren(balMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Prevalent.currentOnlineUser.setBalance(Float.toString(change));
                                                    confirmOrder();
                                                    dialogPayNow.dismiss();
                                                }

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(), "Insufficient Funds", Toast.LENGTH_SHORT).show();
                    }


                }else if (!validPin.equals(pin)){
                    editPin.setError("Wrong Pin");
                    editPin.requestFocus();
                }
            }
        });

        //setData
        dialogPayNow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPayNow.show();
    }

    private void validate() {
        //Get Vars
        if (TextUtils.isEmpty(editName.getText().toString().trim())){
            editName.setError("Please Enter your name");
            editName.requestFocus();
        }else if (TextUtils.isEmpty(editPhone.getText().toString().trim())){
            editPhone.setError("Please Enter your name");
            editPhone.requestFocus();
        }else if (TextUtils.isEmpty(editHomeAddress.getText().toString().trim())){
            editHomeAddress.setError("Please Enter your name");
            editHomeAddress.requestFocus();
        }else if (TextUtils.isEmpty(editTownCityName.getText().toString().trim())){
            editTownCityName.setError("Please Enter your name");
            editTownCityName.requestFocus();
        }else if (TextUtils.isEmpty(editProvinceName.getText().toString().trim())){
            editProvinceName.setError("Please Enter your name");
            editProvinceName.requestFocus();
        }else{
            payNow();
        }
    }

    private void confirmOrder() {
        final String currentTime, currentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY");
        currentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        currentTime = timeFormat.format(calendar.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("name", editName.getText().toString().trim());
        orderMap.put("phone", editPhone.getText().toString().trim());
        orderMap.put("address", editHomeAddress.getText().toString().trim());
        orderMap.put("townCityName", editTownCityName.getText().toString().trim());
        orderMap.put("provinceName", editProvinceName.getText().toString().trim());
        orderMap.put("totalBill", totalBill);
        orderMap.put("date", currentDate);
        orderMap.put("time", currentTime);
        orderMap.put("state", "Review");
        orderMap.put("stateMsg", "null");
        orderMap.put("btnAction", "Browse");

        ordersRef.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart Lists")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ActivityConfirmOrder.this, "Order has been placed", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), ActivityHome.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
