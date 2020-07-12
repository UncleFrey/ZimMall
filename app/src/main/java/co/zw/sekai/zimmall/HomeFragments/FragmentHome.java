package co.zw.sekai.zimmall.HomeFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import co.zw.sekai.zimmall.ActivityProductView;
import co.zw.sekai.zimmall.AdminActivities.ActivityAdminProductEdit;
import co.zw.sekai.zimmall.Models.Product;
import co.zw.sekai.zimmall.R;
import co.zw.sekai.zimmall.ViewHolder.ProductViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {

    String userType;

    public RecyclerView recyclerViewProducts;
    public RecyclerView.LayoutManager layoutManager;

    private DatabaseReference productsRef;

    public FragmentHome() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Intent i = getActivity().getIntent();
        Bundle bundle = i.getExtras();
        if (bundle != null){
            userType = getActivity().getIntent().getStringExtra("type");
        }else {
            userType = "User";
        }


        recyclerViewProducts = view.findViewById(R.id.recyclerHome);
        recyclerViewProducts.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerViewProducts.setLayoutManager(layoutManager);
        populateView();
        return view;
    }

    public void populateView(){
        //Init Firebase
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(productsRef, Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.card_view_product_listing, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {
                        //Curate Data
                        final String description = model.getDescription();
                        final String price = "US$ " + model.getPrice();
                        final String date = model.getDate() + " @ " + model.getTime();

                        holder.txtProductName.setText(model.getName());
                        holder.txtProductDescription.setText(description);
                        holder.txtProductPrice.setText(price);
                        holder.txtPostDate.setText(date);
                        Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.img_loading)
                                .into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (userType.equals("Admin")){
                                    Intent intent  = new Intent(getActivity(), ActivityAdminProductEdit.class);
                                    intent.putExtra("pid", model.getPid());
                                    intent.putExtra("state", model.getState());
                                    intent.putExtra("name", model.getName());
                                    intent.putExtra("description", model.getDescription());
                                    intent.putExtra("price", model.getPrice());
                                    intent.putExtra("imgUrl", model.getImageUrl());
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(getActivity(), ActivityProductView.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }

                            }
                        });
                    }

                };
        recyclerViewProducts.setAdapter(adapter);
        adapter.startListening();
    }

}
