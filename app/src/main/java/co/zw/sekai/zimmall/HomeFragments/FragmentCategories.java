package co.zw.sekai.zimmall.HomeFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import co.zw.sekai.zimmall.ActivityProductView;
import co.zw.sekai.zimmall.Models.Product;
import co.zw.sekai.zimmall.R;
import co.zw.sekai.zimmall.ViewHolder.ProductViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCategories extends Fragment implements View.OnClickListener{
    //Dec Views
    private EditText editSearchTerm;
    private ImageView imgSearch;
    private RecyclerView recycleResults;
    RecyclerView.LayoutManager layoutManager;

    String searchTerm;

    public FragmentCategories() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_categories, container, false);

        editSearchTerm = view.findViewById(R.id.editTextSearchTerm);
        imgSearch = view.findViewById(R.id.imageViewSearch);
        recycleResults = view.findViewById(R.id.recyclerViewResults);
        layoutManager = new LinearLayoutManager(getContext());
        recycleResults.setLayoutManager(layoutManager);

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTerm = editSearchTerm.getText().toString().trim();
                searchProducts(searchTerm);
            }
        });

        searchTerm = editSearchTerm.getText().toString().trim();
        searchProducts(searchTerm);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {

    }

    private void searchProducts(String searchTerm) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Products");

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(reference.orderByChild("name").startAt(searchTerm), Product.class)
                .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {
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
                                Intent intent = new Intent(getActivity(), ActivityProductView.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.card_view_product_listing, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recycleResults.setAdapter(adapter);
        adapter.startListening();

    }
}
