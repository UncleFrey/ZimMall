package co.zw.sekai.zimmall.AdminActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import co.zw.sekai.zimmall.Models.AdminOrders;
import co.zw.sekai.zimmall.Models.Deposit;
import co.zw.sekai.zimmall.Models.UserWallet;
import co.zw.sekai.zimmall.Models.Users;
import co.zw.sekai.zimmall.Prevalent.Prevalent;
import co.zw.sekai.zimmall.R;
import co.zw.sekai.zimmall.ViewHolder.AdminOrderViewHolder;
import co.zw.sekai.zimmall.ViewHolder.DepositViewHolder;

public class ActivityAdminDepositsManager extends AppCompatActivity{
    private RecyclerView recyclerDeposits;
    private DatabaseReference depositsRef;
    private DatabaseReference userBalRef;
    private DatabaseReference viewBalRef;
    private DatabaseReference depositBackUp;

    Dialog dialogViewDeposit, dialogExecuteDeposit;
    //Dep
    ImageView imgDeposit;
    //ExDep
    EditText editAmount;
    Button btnDeposit;
    boolean depositFlag = false;

    //BackUp Values
    Deposit backupDeposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_deposits_manager);

        final String currentTime, currentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY");
        currentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        currentTime = timeFormat.format(calendar.getTime());

        dialogViewDeposit = new Dialog(this);
        dialogExecuteDeposit = new Dialog(this);
        depositsRef = FirebaseDatabase.getInstance().getReference().child("Deposit Info").child("User View");
        userBalRef = FirebaseDatabase.getInstance().getReference().child("User Wallets");
        viewBalRef = FirebaseDatabase.getInstance().getReference().child("Users");
        depositBackUp = FirebaseDatabase.getInstance().getReference().child("Deposit BackUp")
                .child(currentDate + currentTime);

        recyclerDeposits = findViewById(R.id.recycleDeposits);
        recyclerDeposits.setLayoutManager(new LinearLayoutManager(this));

        getDeposits();

    }

    private void getDeposits() {
        FirebaseRecyclerOptions<Deposit> options =
                new FirebaseRecyclerOptions.Builder<Deposit>()
                        .setQuery(depositsRef, Deposit.class).build();

        FirebaseRecyclerAdapter<Deposit, DepositViewHolder> adapter =
                new FirebaseRecyclerAdapter<Deposit, DepositViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final DepositViewHolder holder, int position, @NonNull final Deposit model) {
                        holder.txtUserName.setText(model.getUsername());
                        holder.txtPhone.setText(model.getPhone());
                        holder.txtState.setText("Current State: " + model.getState());
                        holder.txtDateTime.setText(model.getDate() + " @ " + model.getTime());
                        final String phone = model.getPhone();

                        holder.btnView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //View Deposit Slip
                                viewDepositSlip(phone);
                            }
                        });

                        holder.btnExecuteDeposit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Pop Depositor
                                backupDeposit = model;
                                viewExecuteForm(phone);
                            }
                        });

                        holder.btnComplete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //State Change, Move Deposit to Done
                                backupDeposit = model;
                                depositBackUp.child(phone).setValue(backupDeposit)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    depositsRef.child(phone).removeValue();
                                                    Toast.makeText(ActivityAdminDepositsManager.this, "Deposit Closed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                        holder.btnSetState.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Append State
                                String state = holder.spinState.getSelectedItem().toString().trim();
                                updateDepositState(phone, state);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public DepositViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_deposit_listing,parent, false);
                        return new DepositViewHolder(view);
                    }
                };
        recyclerDeposits.setAdapter(adapter);
        adapter.startListening();
    }

    private void updateDepositState(final String phone, final String state) {

        HashMap<String, Object> stateRef = new HashMap<>();
        stateRef.put("state", state);
        depositsRef.child(phone).updateChildren(stateRef)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ActivityAdminDepositsManager.this, "State Updates", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void viewExecuteForm(final String userPhone){
        dialogExecuteDeposit.setContentView(R.layout.pop_up_execute_deposit);
        editAmount = (EditText) dialogExecuteDeposit.findViewById(R.id.editTextDepositValue);
        btnDeposit = (Button) dialogExecuteDeposit.findViewById(R.id.buttonExecute);



        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                depositFlag = true;
                final String amnt = editAmount.getText().toString().trim();
                if (!TextUtils.isEmpty(amnt)){
                    final float depositAmount = Float.parseFloat(amnt);
                    viewBalRef.child(userPhone)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (depositFlag = true){
                                        Users users = dataSnapshot.getValue(Users.class);
                                        final float initialAmount = Float.parseFloat(users.getBalance());
                                        final float finalAmount = initialAmount + depositAmount;
                                        HashMap<String, Object> balMap = new HashMap<>();
                                        balMap.put("balance", String.valueOf(finalAmount));
                                        depositFlag = false;
                                        userBalRef.child(userPhone)
                                                .updateChildren(balMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            depositBackUp.child(userPhone).setValue(backupDeposit)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                depositsRef.child(userPhone).removeValue();
                                                                                Toast.makeText(ActivityAdminDepositsManager.this, "Balance Updated", Toast.LENGTH_SHORT).show();
                                                                                dialogExecuteDeposit.dismiss();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }else {
                    editAmount.setError("Deposit Amount Required");
                    editAmount.requestFocus();
                }

            }
        });

        dialogExecuteDeposit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogExecuteDeposit.show();
    }

    private void viewDepositSlip(final String userPhone){
        dialogViewDeposit.setContentView(R.layout.pop_up_deposit_view);
        imgDeposit = (ImageView) dialogViewDeposit.findViewById(R.id.imageViewDeposit);


        depositsRef.child(userPhone)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Deposit deposit = dataSnapshot.getValue(Deposit.class);
                            Picasso.get().load(deposit.getImageUrl()).placeholder(R.drawable.img_loading).into(imgDeposit);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        dialogViewDeposit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogViewDeposit.show();

    }
}
