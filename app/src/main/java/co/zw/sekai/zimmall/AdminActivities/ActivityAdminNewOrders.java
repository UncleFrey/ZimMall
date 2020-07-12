package co.zw.sekai.zimmall.AdminActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import co.zw.sekai.zimmall.Models.AdminOrders;
import co.zw.sekai.zimmall.R;
import co.zw.sekai.zimmall.ViewHolder.AdminOrderViewHolder;

public class ActivityAdminNewOrders extends AppCompatActivity implements View.OnClickListener{
    //Views
    private RecyclerView recyclerOrders;
    private DatabaseReference ordersRef;
    private DatabaseReference cartRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart Lists").child("Admin View");

        recyclerOrders = findViewById(R.id.recyclerViewOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));

        getOrders();
    }

    private void getOrders() {
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef, AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final AdminOrderViewHolder holder, int position, @NonNull final AdminOrders model) {
                        holder.txtUserName.setText(model.getName());
                        holder.txtDateTime.setText(model.getDate() + " @ " + model.getTime());
                        holder.txtTownCityName.setText(model.getTownCityName());
                        holder.txtOrderValue.setText("US$ " + model.getTotalBill());

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes", "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAdminNewOrders.this);
                                builder.setTitle("Delete this order?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            deleteOrder(model.getPhone());
                                        }else {
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });

                        holder.btnViewOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //To Order View
                                Intent intent = new Intent(ActivityAdminNewOrders.this, ActivityAdminOrderView.class);
                                intent.putExtra("phoneNumber", model.getPhone());
                                intent.putExtra("orderValue", model.getTotalBill());
                                intent.putExtra("state", model.getState());
                                intent.putExtra("btnAction", model.getBtnAction());
                                intent.putExtra("message", model.getStateMsg());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_order,parent, false);
                        return new AdminOrderViewHolder(view);
                    }
                };
        recyclerOrders.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteOrder(final String phone) {

        ordersRef.child(phone).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        cartRef.child(phone).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ActivityAdminNewOrders.this, "Order Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    @Override
    public void onClick(View v) {

    }
}
