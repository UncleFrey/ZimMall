package co.zw.sekai.zimmall.AdminActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import co.zw.sekai.zimmall.R;

public class ActivityAdminProductEdit extends AppCompatActivity implements View.OnClickListener{
    String pid, name, state, description, price, imgUrl;
    //Dec Views
    private ImageView imgProductImage;
    private EditText editProductName, editProductDescription, editProductPrice;
    private Spinner spinState;
    private Button btnEdit, btnOpen, btnDelete;
    private ProgressDialog loadingBar;

    //Firebase
    private StorageReference productImageStorageRef;
    private DatabaseReference productsRef;
    private StorageReference updationRef;

    //TODO InputData
    private String productName, productDescription, productPrice, productState;

    //Action Codes & Vars
    private static final int IMAGE_BROWSE_CODE = 1;
    private Uri productImageUri;
    private String downLoadImageUrl;
    private boolean editBool = false;
    private boolean editImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_edit);
        pid = getIntent().getStringExtra("pid");
        name = getIntent().getStringExtra("name");
        description = getIntent().getStringExtra("description");
        state = getIntent().getStringExtra("state");
        price = getIntent().getStringExtra("price");
        imgUrl = getIntent().getStringExtra("imgUrl");

        imgProductImage = (ImageView)findViewById(R.id.imageViewProductImage);
        editProductName = (EditText)findViewById(R.id.editTextProductName);
        editProductDescription = (EditText)findViewById(R.id.editTextProductDescription);
        spinState = (Spinner) findViewById(R.id.spinnerState);
        editProductPrice = (EditText)findViewById(R.id.editTextProductPrice);
        btnEdit = (Button)findViewById(R.id.buttonEditProduct);
        btnOpen = (Button)findViewById(R.id.buttonEdit);
        btnDelete = (Button)findViewById(R.id.buttonDelete);
        loadingBar = new ProgressDialog(this);

        //Firebase Init
        productImageStorageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(imgUrl);;
                updationRef = FirebaseStorage.getInstance()
                        .getReference("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        //Set Onclick
        imgProductImage.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnOpen.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        loadProductData(imgUrl);
    }

    private void loadProductData(String imgUrl) {
        Picasso.get().load(imgUrl)
                .placeholder(R.drawable.img_loading).into(imgProductImage);
        //setSpinner
        if (state.equals("PreOrder")){
            spinState.setSelection(0);
        }else if (state.equals("InStock")){
            spinState.setSelection(1);
        }else if (state.equals("Out of Stock")){
            spinState.setSelection(2);
        }

        editProductName.setText(name);
        editProductDescription.setText(description);
        editProductPrice.setText(price);

        editProductName.setEnabled(false);
        editProductPrice.setEnabled(false);
        editProductDescription.setEnabled(false);
        spinState.setEnabled(false);
        imgProductImage.setEnabled(false);
        btnEdit.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_BROWSE_CODE && resultCode == RESULT_OK && data != null){
            productImageUri = data.getData();
            imgProductImage.setImageURI(productImageUri);
        }
    }

    private void validate() {
        if (TextUtils.isEmpty(editProductName.getText().toString().trim())){
            editProductName.setError("Product name required");
            editProductName.requestFocus();
        }else if (TextUtils.isEmpty(editProductDescription.getText().toString().trim())){
            editProductDescription.setError("Description required");
            editProductDescription.requestFocus();
        }else if (TextUtils.isEmpty(editProductPrice.getText().toString().trim())){
            editProductPrice.setError("Price required");
            editProductPrice.requestFocus();
        }else if (editImage){
            uploadImageAndInfo();
        }else if (!editImage){
            uploadInfoOnly();
        }
    }

    private void uploadInfoOnly() {
        loadingBar.setTitle("Edit Product");
        loadingBar.setMessage("Please wait, while we update product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        productState = spinState.getSelectedItem().toString().trim();
        productName = editProductName.getText().toString().trim();
        productDescription = editProductDescription.toString().trim();
        productPrice = editProductPrice.toString().trim();

        HashMap<String, Object> productInfoMap = new HashMap<>();
        productInfoMap.put("state", productState);
        productInfoMap.put("name", productName);
        productInfoMap.put("description", productDescription);
        productInfoMap.put("price", productPrice);

        productsRef.child(pid).updateChildren(productInfoMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ActivityAdminProductEdit.this, "Info Updated", Toast.LENGTH_SHORT).show();
                            finish();
                            loadingBar.dismiss();
                        }else {
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private void uploadImageAndInfo() {
        loadingBar.setTitle("New Product");
        loadingBar.setMessage("Please wait, while we upload product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        final StorageReference filePath = updationRef.child(pid + ".jpg");

        final UploadTask imageUpload = filePath.putFile(productImageUri);

        imageUpload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = e.toString();
                Toast.makeText(ActivityAdminProductEdit.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = imageUpload.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()){
                            throw task.getException();
                        }

                        downLoadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downLoadImageUrl = task.getResult().toString();
                            productState = spinState.getSelectedItem().toString().trim();
                            productName = editProductName.getText().toString().trim();
                            productDescription = editProductDescription.getText().toString().trim();
                            productPrice = editProductPrice.getText().toString().trim();

                            HashMap<String, Object> productInfoMap = new HashMap<>();
                            productInfoMap.put("state", productState);
                            productInfoMap.put("name", productName);
                            productInfoMap.put("description", productDescription);
                            productInfoMap.put("price", productPrice);
                            productInfoMap.put("imageUrl", downLoadImageUrl);

                            productsRef.child(pid).updateChildren(productInfoMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ActivityAdminProductEdit.this, "Info Updated", Toast.LENGTH_SHORT).show();
                                                finish();
                                                loadingBar.dismiss();
                                            }else {
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }

    private void deleteProduct() {

        productImageStorageRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            productsRef.child(pid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ActivityAdminProductEdit
                                                        .this, "Product Deleted", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == btnOpen){
            editBool = true;
            editProductName.setEnabled(true);
            editProductPrice.setEnabled(true);
            editProductDescription.setEnabled(true);
            spinState.setEnabled(true);
            imgProductImage.setEnabled(true);
            btnEdit.setEnabled(true);
        }

        if (v == imgProductImage){
            editImage = true;
            Intent browseImage = new Intent();
            browseImage.setAction(Intent.ACTION_GET_CONTENT);
            browseImage.setType("image/*");
            startActivityForResult(browseImage, IMAGE_BROWSE_CODE);
        }

        if (v == btnEdit){
            validate();
        }

        if (v == btnDelete){
            deleteProduct();
        }
    }


}
