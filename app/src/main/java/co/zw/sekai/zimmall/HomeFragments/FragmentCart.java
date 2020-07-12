package co.zw.sekai.zimmall.HomeFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import co.zw.sekai.zimmall.ActivityConfirmOrder;
import co.zw.sekai.zimmall.ActivityHome;
import co.zw.sekai.zimmall.ActivityProductView;
import co.zw.sekai.zimmall.Models.Cart;
import co.zw.sekai.zimmall.Prevalent.Prevalent;
import co.zw.sekai.zimmall.R;
import co.zw.sekai.zimmall.ViewHolder.CartViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCart extends Fragment implements View.OnClickListener{
    //Dec Views
    private TextView txtTotal, txtLockMsg;
    private RecyclerView recyclerCart;
    public RecyclerView.LayoutManager layoutManager;
    public ImageView imgLock;
    private Button btnCheckOut;

    public float totalBill = 0;

    public FragmentCart() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        //Views Bind
        txtTotal = view.findViewById(R.id.textViewTotal);
        txtLockMsg = view.findViewById(R.id.textViewLockMsg);
        recyclerCart = view.findViewById(R.id.recyclerViewProducts);
        btnCheckOut = view.findViewById(R.id.buttonCheckOut);
        imgLock = view.findViewById(R.id.imageViewLock);

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gotoCheckOut
                if (totalBill != 0){
                    Intent intent = new Intent(getActivity(), ActivityConfirmOrder.class);
                    intent.putExtra("totalBill", Float.toString(totalBill));
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "No Products In cart", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //recyclerCart.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerCart.setLayoutManager(layoutManager);

        populateCart();

        return view;
    }

    public void populateCart(){
        checkOrderState();

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart Lists");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getPhone()).child("Products"), Cart.class)
                        .build();

        final FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                        holder.txtProductName.setText(model.getProductName());
                        holder.txtProductPrice.setText("Unit Price: US$ " + model.getProductPrice());
                        holder.txtProductQty.setText("Qty: " + model.getProductQty());
                        holder.txtProductTotal.setText("Total Price: US$ " + model.getSubTotal());

                        totalBill = totalBill + Float.parseFloat(model.getSubTotal());
                        txtTotal.setText("Total: US$ " + totalBill);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Edit", "Remove"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Cart Options:");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0){
                                            Intent intent = new Intent(getActivity(), ActivityProductView.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                        if (which == 1){
                                            cartRef.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            cartRef.child("Admin View")
                                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                                    .child("Products")
                                                                    .child(model.getPid())
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            totalBill = totalBill - Float.parseFloat(model.getSubTotal());
                                                                            txtTotal.setText("Total: US$ " + totalBill);
                                                                            Toast.makeText(getActivity(), "Item Removed", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    }
                                });

                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_cart_listing, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerCart.setAdapter(adapter);
        adapter.startListening();
    }

    public void checkOrderState(){
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String message = dataSnapshot.child("stateMsg").getValue().toString();
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    if (shippingState.equals("Review")){
                        imgLock.setVisibility(View.VISIBLE);
                        txtLockMsg.setText("Your last order is still in review.\n You can only have " +
                                "one order placed until its reviewed");
                        txtLockMsg.setVisibility(View.VISIBLE);
                    }else if (shippingState.equals("Packaging")){
                        imgLock.setVisibility(View.VISIBLE);
                        txtLockMsg.setText("Your last order is being acquired.\n Please relax " +
                                "as we take care of you.");
                        txtLockMsg.setVisibility(View.VISIBLE);
                    }else if (shippingState.equals("OnFlight")){
                        imgLock.setVisibility(View.VISIBLE);
                        txtLockMsg.setText("Your last order is on incoming Flight.\n Please relax" +
                                ". Your order will be in Zimbabwe in a day or slightly more.");
                        txtLockMsg.setVisibility(View.VISIBLE);
                    }else if(shippingState.equals("Enroute")){
                        imgLock.setVisibility(View.VISIBLE);
                        txtLockMsg.setText("Your last order is In Route.\n Your order arrived in Zim " +
                                ". We are now getting it to you.");
                        txtLockMsg.setVisibility(View.VISIBLE);
                    }else if (shippingState.equals("Local")){
                        imgLock.setVisibility(View.VISIBLE);
                        txtLockMsg.setText("Your last order is almost there.\n You can now warm your hands" +
                                ". You can't place another order until delivery, but your last order" +
                                "is hours out. Visit Orders panel for details.");
                        txtLockMsg.setVisibility(View.VISIBLE);
                    }else if (shippingState.equals("Custom")){
                        imgLock.setVisibility(View.VISIBLE);
                        txtLockMsg.setText(message);
                        txtLockMsg.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
