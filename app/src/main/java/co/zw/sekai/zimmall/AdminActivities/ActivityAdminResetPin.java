package co.zw.sekai.zimmall.AdminActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Random;

import co.zw.sekai.zimmall.Models.RecoveryOptions;
import co.zw.sekai.zimmall.R;
import co.zw.sekai.zimmall.ViewHolder.AdminOrderViewHolder;
import co.zw.sekai.zimmall.ViewHolder.PinResetRequestViewHolder;

public class ActivityAdminResetPin extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView recycleRequests;

    private DatabaseReference requestsRef, delRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reset_pin);

        recycleRequests = findViewById(R.id.recycleRequests);
        recycleRequests.setLayoutManager(new LinearLayoutManager(this));

        getRequests();
    }

    private void getRequests() {
        requestsRef = FirebaseDatabase.getInstance().getReference().child("Recovery Options");

        Query requestsQuery = requestsRef.orderByChild("state").equalTo("true");

        FirebaseRecyclerOptions<RecoveryOptions> options =
                new FirebaseRecyclerOptions.Builder<RecoveryOptions>()
                .setQuery(requestsQuery, RecoveryOptions.class).build();

        FirebaseRecyclerAdapter<RecoveryOptions, PinResetRequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<RecoveryOptions, PinResetRequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PinResetRequestViewHolder holder, int position, @NonNull final RecoveryOptions model) {
                        final String newPin = getRandomNumberString();
                        holder.phone.setText(model.getPhone());
                        holder.pin.setText(newPin);

                        holder.imgDEL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delRef = FirebaseDatabase.getInstance().getReference().child("Recovery Options")
                                        .child(model.getPhone());
                                delRef.removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(ActivityAdminResetPin.this,
                                                            "Deleted request", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                        holder.imgSMS.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendMessage(model.getPhone(), newPin);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public PinResetRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_admin_pin_reset,parent, false);
                        return new PinResetRequestViewHolder(view);
                    }
                };

        recycleRequests.setAdapter(adapter);
        adapter.startListening();
    }

    @SuppressLint("IntentReset")
    private void sendMessage(String phone, String newPin) {

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , new String (phone));
        smsIntent.putExtra("sms_body"  , "Your Pin Reset was successful, your new pin is" +
                " " + newPin + ". Please Login and change it to use a pin you can remember. Keep your credentials" +
                " confidential.");

        try {
            startActivity(smsIntent);
            finish();
            Toast.makeText(this, "SMS Sent", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "SMS sending failed, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(900000) + 100000;

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    @Override
    public void onClick(View v) {

    }
}
