package co.zw.sekai.zimmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import co.zw.sekai.zimmall.Models.Product;
import co.zw.sekai.zimmall.Prevalent.Prevalent;

public class ActivityProductView extends AppCompatActivity implements View.OnClickListener{

    //Preliminary Order State
    boolean orderState;

    //Vars
    String productId;
    Product product;

    //Totals
    float totalBill;

    //Dec Views
    private TextView txtProductName, txtProductPrice, txtProductDescription, txtDate, txtQty
            ,txtBill, txtState;

    private ImageView imgProductImage;

    private SeekBar seekQty;
    private FloatingActionButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        productId = getIntent().getStringExtra("pid");

        //Bind Views
        txtProductName = findViewById(R.id.textViewProductName);
        txtProductPrice = findViewById(R.id.textViewPrice);
        txtProductDescription = findViewById(R.id.textViewProductDescription);
        txtDate = findViewById(R.id.textViewPostDate);
        txtQty = findViewById(R.id.textViewQty);
        txtBill = findViewById(R.id.textViewBill);
        txtState = findViewById(R.id.textViewState);
        seekQty = findViewById(R.id.seekBarQty);
        imgProductImage = findViewById(R.id.imageViewProductImage);
        btnAdd = findViewById(R.id.buttonAdd);

        //OnClick
        btnAdd.setOnClickListener(this);

        seekQty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtQty.setText("Qty: " + progress);
                float price = Float.parseFloat(product.getPrice()) ;
                totalBill = (price*progress);
                txtBill.setText("Total Bill: US$ "+ totalBill);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        checkOrderState();

    }

    private void getProductDetails(final String productId) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    product = dataSnapshot.getValue(Product.class);
                    txtProductName.setText(product.getName());
                    txtProductPrice.setText("US$ " + product.getPrice());
                    txtDate.setText(product.getDate() + " @ " + product.getTime());
                    txtProductDescription.setText(product.getDescription());
                    Picasso.get().load(product.getImageUrl()).placeholder(R.drawable.img_loading).into(imgProductImage);
                    txtBill.setText("Total Bill: US$ "+ product.getPrice());
                    txtState.setText("Availability: " + product.getState());
                    totalBill = Float.parseFloat(product.getPrice());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == btnAdd){
            //Add to Cart
            if (orderState){
                Toast.makeText(this, "You can't shop until last order is delivered, " +
                        "Use the wish list.", Toast.LENGTH_LONG).show();
            }else {
                addToCart();
            }
        }

    }

    private void addToCart() {
        final String currentTime, currentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY");
        currentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        currentTime = timeFormat.format(calendar.getTime());

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart Lists");

        final HashMap<String, Object>  cartMap = new HashMap<>();
        cartMap.put("pid", productId);
        cartMap.put("productName", product.getName());
        cartMap.put("productPrice", product.getPrice());
        cartMap.put("subTotal", Float.toString(totalBill) );
        cartMap.put("productDate", currentDate);
        cartMap.put("productTime", currentTime);
        cartMap.put("productQty", Integer.toString(seekQty.getProgress()));
        cartMap.put("productDiscount", "");

        cartRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products")
                .child(productId).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            cartRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products")
                                    .child(productId).updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ActivityProductView.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ActivityProductView.this, ActivityHome.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void checkOrderState(){
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    orderState = true;
                    getProductDetails(productId);
                }else {
                    orderState = false;
                    getProductDetails(productId);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
