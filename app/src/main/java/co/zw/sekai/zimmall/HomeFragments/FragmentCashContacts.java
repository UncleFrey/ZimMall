package co.zw.sekai.zimmall.HomeFragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import co.zw.sekai.zimmall.ActivityHome;
import co.zw.sekai.zimmall.AdminActivities.ActivityAdminCategory;
import co.zw.sekai.zimmall.AdminActivities.ActivityAdminNewProduct;
import co.zw.sekai.zimmall.Models.Deposit;
import co.zw.sekai.zimmall.Prevalent.Prevalent;
import co.zw.sekai.zimmall.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCashContacts extends Fragment implements View.OnClickListener{
    Context fragContext;
    private boolean deposit = false;
    private String depositState;

    //Views
    private static final int IMAGE_BROWSE_CODE = 1;
    private ImageView imgDeposit;
    private Button btnUpload;
    private TextView txtState;
    private EditText editAmount;

    //Image Var
    private Uri imageUri;
    private String downLoadImageUrl;

    //Dialog
    private ProgressDialog loadingBar;

    //Firebase
    private StorageReference depositImageStorageRef;
    private DatabaseReference depositRef;

    public FragmentCashContacts() {
        // Required empty public constructor
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        fragContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_contacts, container, false);

        imgDeposit = view.findViewById(R.id.imageViewDeposit);
        btnUpload = view.findViewById(R.id.buttonUpload);
        txtState = view.findViewById(R.id.textViewDepositStatus);
        editAmount = view.findViewById(R.id.editTextAmount);

        depositImageStorageRef = FirebaseStorage.getInstance().getReference().child("Deposit Images");
        depositRef = FirebaseDatabase.getInstance().getReference().child("Deposit Info");

        loadingBar = new ProgressDialog(getActivity());

        imgDeposit.setOnClickListener(this);
        btnUpload.setOnClickListener(this);


        getDeposit();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == imgDeposit){
            Intent browseImage = new Intent();
            browseImage.setAction(Intent.ACTION_GET_CONTENT);
            browseImage.setType("image/*");
            startActivityForResult(browseImage, IMAGE_BROWSE_CODE);
        }

        if (v == btnUpload){
            if (imageUri!=null){
                if (!deposit){
                    if (!TextUtils.isEmpty(editAmount.getText().toString().trim())){
                        uploadRequest();
                    }else {
                        editAmount.setError("Correct Amount Required");
                        editAmount.requestFocus();
                    }

                }else {
                    Toast.makeText(getActivity(), "Another deposit still in Review...", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getActivity(), "Select Image first", Toast.LENGTH_SHORT).show();
            }
        }

    }



    public void getDeposit(){
        depositRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            deposit = true;
                            //getDepositStatus
                            depositRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                Deposit deposit = dataSnapshot.getValue(Deposit.class);
                                                depositState = deposit.getState();
                                                txtState.setText(depositState);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }else {
                            deposit = false;
                            txtState.setText("No Pending Deposit");
                            Toast.makeText(getActivity(), "No Pending Deposit", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void uploadRequest(){
        loadingBar.setTitle("New Deposit");
        loadingBar.setMessage("Please wait, while we upload deposit.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        final StorageReference filePath = depositImageStorageRef.child(Prevalent.currentOnlineUser.getPhone())
                .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

        final UploadTask imageUpload = filePath.putFile(imageUri);

        imageUpload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = e.toString();
                Toast.makeText(getActivity(), "Error: " + msg, Toast.LENGTH_SHORT).show();
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
                            saveDepositInfo();
                        }
                    }
                });
            }
        });
    }

    public void saveDepositInfo(){
        final String currentTime, currentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY");
        currentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        currentTime = timeFormat.format(calendar.getTime());

        final HashMap<String, Object> depositMap = new HashMap<>();

        depositMap.put("phone", Prevalent.currentOnlineUser.getPhone());
        depositMap.put("username", Prevalent.currentOnlineUser.getName());
        depositMap.put("imageUrl", downLoadImageUrl);
        depositMap.put("state", "In Review");
        depositMap.put("value", editAmount.getText().toString().trim());
        depositMap.put("date", currentDate);
        depositMap.put("time", currentTime);

        depositRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).updateChildren(depositMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Deposit Request Successful", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(getActivity(), ActivityHome.class);
                            startActivity(intent);
                        }
                        else{
                            String msg =  task.getException().toString();
                            Toast.makeText(getActivity(), "Error: " + msg, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            if (requestCode == IMAGE_BROWSE_CODE && resultCode == RESULT_OK && data != null){
                imageUri = data.getData();
                imgDeposit.setImageTintMode(null);
                imgDeposit.setImageURI(imageUri);
            }
        }else {
            Toast.makeText(fragContext, "Upload cancelled", Toast.LENGTH_SHORT).show();
        }
    }

}
