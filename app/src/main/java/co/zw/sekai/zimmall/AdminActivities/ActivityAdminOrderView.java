package co.zw.sekai.zimmall.AdminActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import co.zw.sekai.zimmall.Models.Cart;
import co.zw.sekai.zimmall.R;
import co.zw.sekai.zimmall.ViewHolder.CartViewHolder;

public class ActivityAdminOrderView extends AppCompatActivity implements View.OnClickListener{
    private TextView txtOrderValue, txtHead;
    private RecyclerView recyleCart;
    RecyclerView.LayoutManager layoutManager;
    private EditText editStateMessage;
    private Spinner spinState;
    private Spinner spinBtnAction;
    private Button btnChangeState;

    String userPhone, orderValue, state, stateMsg, buttonAction;
    String uploadState, uploadBtnAction, uploadMessage;

    private DatabaseReference cartRef;
    private DatabaseReference orderRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_view);
        userPhone = getIntent().getStringExtra("phoneNumber");
        orderValue = getIntent().getStringExtra("orderValue");
        state = getIntent().getStringExtra("state");
        stateMsg = getIntent().getStringExtra("message");
        buttonAction = getIntent().getStringExtra("btnAction");

        //Init Firebase
        cartRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart Lists").child("Admin View")
                .child(userPhone).child("Products");

        orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(userPhone);

        //Bind Values
        txtOrderValue = findViewById(R.id.textViewOrderValue);
        txtHead = findViewById(R.id.textViewHead);
        recyleCart = findViewById(R.id.recycleCart);
        editStateMessage = findViewById(R.id.editTextStateMsg);
        spinState = findViewById(R.id.spinnerState);
        spinBtnAction = findViewById(R.id.spinnerBtnAction);
        btnChangeState = findViewById(R.id.buttonChangeState);

        //OnClick
        btnChangeState.setOnClickListener(this);
        recyleCart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyleCart.setLayoutManager(layoutManager);

        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 6){
                    editStateMessage.setEnabled(true);
                    editStateMessage.setText("");
                    editStateMessage.setHint("Type custom message here");
                }else {
                    editStateMessage.setText("To edit, select Custom State");
                    editStateMessage.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Init Interface
        txtOrderValue.setText("Order Value: US$ " + orderValue);
        txtHead.setText("Order for " + userPhone);
        editStateMessage.setText(stateMsg);
        editStateMessage.setEnabled(false);

        //Set Current State
        if (state.equals("Review")){
            spinState.setSelection(0);
        }else if (state.equals("Packaging")){
            spinState.setSelection(1);
        }else if (state.equals("OnFlight")){
            spinState.setSelection(2);
        }else if (state.equals("Delayed")){
            spinState.setSelection(3);
        }else if (state.equals("Local")){
            spinState.setSelection(4);
        }else if (state.equals("Enroute")){
            spinState.setSelection(5);
        }else if (state.equals("Custom")){
            spinState.setSelection(6);
        }

        //Set Button Action
        if (buttonAction.equals("Browse")){
            spinBtnAction.setSelection(0);
        }else if (buttonAction.equals("Call")){
            spinBtnAction.setSelection(1);
        }

        getCart();
    }

    private void getCart() {
        //Admin View getCart
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartRef, Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter  = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                holder.txtProductName.setText(model.getProductName());
                holder.txtProductPrice.setText("Unit Price: US$ " + model.getProductPrice());
                holder.txtProductQty.setText("Qty: " + model.getProductQty());
                holder.txtProductTotal.setText("Total Price: US$ " + model.getSubTotal());
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_cart_listing, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyleCart.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onClick(View v) {
        if (v == btnChangeState){
            //Update State
            updateState(spinState.getSelectedItem().toString().trim(),
                    spinBtnAction.getSelectedItem().toString().trim(),
                    editStateMessage.getText().toString().trim());

        }
    }

    private void updateState(String state, String btnAction, String message) {
        if (state.equals("Custom")){
            if (!TextUtils.isEmpty(message)){
                uploadState = state;
                uploadMessage = message;
                uploadBtnAction = btnAction;
            }else {
                editStateMessage.setError("Custom State required");
                editStateMessage.requestFocus();
            }
        }else {
            uploadState = state;
            uploadBtnAction = btnAction;
            uploadMessage = "null";
        }

        HashMap<String, Object> stateRef = new HashMap<>();
        stateRef.put("state", uploadState);
        stateRef.put("stateMsg", uploadMessage);
        stateRef.put("btnAction", uploadBtnAction);

        orderRef.updateChildren(stateRef)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ActivityAdminOrderView.this, "State Updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ActivityAdminOrderView.this, ActivityAdminNewOrders.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}
