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
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import co.zw.sekai.zimmall.R;

public class ActivityAdminNewProduct extends AppCompatActivity implements View.OnClickListener{
    //Action Codes & Vars
    private static final int IMAGE_BROWSE_CODE = 1;
    private Uri productImageUri;
    private String downLoadImageUrl;

    //TODO Parsed Data
    private String categoryName;
    private String categoryHeading;
    //TODO InputData
    private String productName, productDescription, productPrice, productDate, productTime, productKey;

    //Declare Views
    private TextView txtHeading;
    private ImageView imgProductImage;
    private EditText editProductName, editProductDescription, editProductPrice;
    private Spinner spinState;
    private Button btnAdd;
    private ProgressDialog loadingBar;

    //Firebase
    private StorageReference productImageStorageRef;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_product);
        //Get Category
        categoryName = getIntent().getExtras().get("category").toString();
        categoryHeading = getIntent().getExtras().get("heading").toString();
        //Set Heading
        txtHeading = (TextView)findViewById(R.id.textViewHeading);
        txtHeading.setText(categoryHeading);

        //Bind
        imgProductImage = (ImageView)findViewById(R.id.imageViewProductImage);
        editProductName = (EditText)findViewById(R.id.editTextProductName);
        editProductDescription = (EditText)findViewById(R.id.editTextProductDescription);
        spinState = (Spinner) findViewById(R.id.spinnerState);
        editProductPrice = (EditText)findViewById(R.id.editTextProductPrice);
        btnAdd = (Button)findViewById(R.id.buttonAddProduct);
        loadingBar = new ProgressDialog(this);

        //Firebase Init
        productImageStorageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        //Set Onclick
        imgProductImage.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == imgProductImage){
            Intent browseImage = new Intent();
            browseImage.setAction(Intent.ACTION_GET_CONTENT);
            browseImage.setType("image/*");
            startActivityForResult(browseImage, IMAGE_BROWSE_CODE);
        }

        if (v == btnAdd){
            validateProductData();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_BROWSE_CODE && resultCode == RESULT_OK && data != null){
            productImageUri = data.getData();
            imgProductImage.setImageURI(productImageUri);
        }
    }

    private void validateProductData() {
        productName = editProductName.getText().toString().trim();
        productDescription = editProductDescription.getText().toString().trim();
        productPrice = editProductPrice.getText().toString().trim();

        //Validate Data
        if (productImageUri == null){
            Toast.makeText(this, "Product Image Required", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(productName)){
            editProductName.setError("Name Required");
            editProductName.requestFocus();
            return;
        }
        else if (TextUtils.isEmpty(productDescription)){
            editProductDescription.setError("Description Required");
            editProductDescription.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(productPrice)){
            editProductPrice.setError("Price Required");
            editProductPrice.requestFocus();
            return;
        }
        else {
            storeProductImage();
        }

    }

    private void storeProductImage() {
        loadingBar.setTitle("New Product");
        loadingBar.setMessage("Please wait, while we upload product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        //Get Date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        productDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        productTime = currentTime.format(calendar.getTime());

        //Make key
        productKey = productDate + productTime;

        final StorageReference filePath = productImageStorageRef.child(productKey + ".jpg");

        final UploadTask imageUpload = filePath.putFile(productImageUri);

        imageUpload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = e.toString();
                Toast.makeText(ActivityAdminNewProduct.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
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
                            saveProductInfo();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfo() {
        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("pid", productKey);
        productMap.put("date", productDate);
        productMap.put("time", productTime);
        productMap.put("name", productName);
        productMap.put("imageUrl", downLoadImageUrl);
        productMap.put("description", productDescription);
        productMap.put("price", productPrice);
        productMap.put("category", categoryName);
        productMap.put("state", spinState.getSelectedItem().toString().trim());

        productsRef.child(productKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ActivityAdminNewProduct.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent toCategory = new Intent(ActivityAdminNewProduct.this, ActivityAdminCategory.class);
                            startActivity(toCategory);
                            finish();
                        }
                        else{
                            String msg =  task.getException().toString();
                            Toast.makeText(ActivityAdminNewProduct.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

    }
}
