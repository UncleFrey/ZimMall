package co.zw.sekai.zimmall.HomeFragments;

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
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import co.zw.sekai.zimmall.Prevalent.Prevalent;
import co.zw.sekai.zimmall.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSettings extends Fragment implements View.OnClickListener{

    private ProgressDialog progressDialog;

    //TODO Frag info
    Context fragContext;

    private EditText editName, editPhone, editAddress, editPin, editPinOld;
    private ImageView imgEdit, imgEditPin;
    private CircleImageView imgProfile;
    private Button btnUpdate;

    //TODO Pin Edit Holders
    private String oldPin, newPin;
    private boolean pinUpdate = false;

    //TODO Profile Image
    private Uri imageUri;
    private String imageUrl;
    private StorageReference profilImageRef;
    private StorageTask imageUploadTask;
    private String checker = "";

    public FragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        editName = view.findViewById(R.id.editTextUsername);
        editPhone = view.findViewById(R.id.editTextUserPhone);
        editAddress = view.findViewById(R.id.editTextUserAddress);
        editPin = view.findViewById(R.id.editTextNewPin);
        editPinOld = view.findViewById(R.id.editTextOldPin);
        imgEditPin = view.findViewById(R.id.imageViewEditPin);
        imgProfile = view.findViewById(R.id.imageProfile);
        imgEdit = view.findViewById(R.id.imageViewEdit);
        btnUpdate = view.findViewById(R.id.buttonUpdate);
        profilImageRef = FirebaseStorage.getInstance().getReference().child("User Profile Pictures");
        progressDialog = new ProgressDialog(getActivity());

        imgEdit.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        imgEditPin.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        //Run Script to Get
        userInfoDisplay();

        //LockIt
        editName.setEnabled(Boolean.FALSE);
        editPhone.setEnabled(Boolean.FALSE);
        editAddress.setEnabled(Boolean.FALSE);
        editPin.setEnabled(Boolean.FALSE);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == imgEdit){
            //UnLockIt
            editName.setEnabled(Boolean.TRUE);
            editPhone.setEnabled(Boolean.TRUE);
            editAddress.setEnabled(Boolean.TRUE);
        }

        if (v == imgEditPin){

            if (oldPin.equals(editPinOld.getText().toString().trim())){
                editPin.setEnabled(Boolean.TRUE);
                editPinOld.setEnabled(Boolean.FALSE);
                pinUpdate = true;
            }else{
                editPinOld.setError("This isn't your current Pin.");
            }

        }

        if (v == imgProfile){
            checker = "clicked";

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(getActivity(), this);
        }

        if (v == btnUpdate){
            if (checker.equals("clicked")){
                userInfoSaved();
            }else {
                updateOnlyUserInfo();
            }
        }
    }

    private void userInfoDisplay() {
        progressDialog.setTitle("Profile");
        progressDialog.setMessage("Fetching Profile...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (!dataSnapshot.child("imageUrl").equals("null")){
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        oldPin = dataSnapshot.child("pin").getValue().toString();

                        Picasso.get().load(Prevalent.currentOnlineUser.getImageUrl())
                                .placeholder(R.drawable.profile_icon).into(imgProfile);
                        editName.setText(name);
                        editPhone.setText(phone);
                        editAddress.setText(address);
                        progressDialog.dismiss();
                    }else if (dataSnapshot.child("imageUrl").equals("null")){
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        oldPin = dataSnapshot.child("pin").getValue().toString();

                        editName.setText(name);
                        editPhone.setText(phone);
                        editAddress.setText(address);
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            assert result != null;
            imageUri = result.getUri();
            if (imageUri != null){
                imgProfile.setImageURI(imageUri);
            }
        }

    }

    private void updateOnlyUserInfo() {
        progressDialog.setTitle("Profile");
        progressDialog.setMessage("Updating Profile...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (pinUpdate){
            newPin = editPin.getText().toString();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();

        if (pinUpdate && TextUtils.isEmpty(editPin.getText().toString().trim())){
            userMap.put("name", editName.getText().toString().trim());
            userMap.put("phone", editPhone.getText().toString().trim());
            userMap.put("address", editAddress.getText().toString().trim());
            userMap.put("pin", oldPin);
        }else if (!pinUpdate){
            userMap.put("name", editName.getText().toString().trim());
            userMap.put("phone", editPhone.getText().toString().trim());
            userMap.put("address", editAddress.getText().toString().trim());
        }else if (pinUpdate && !TextUtils.isEmpty(editPin.getText().toString().trim())){
            userMap.put("name", editName.getText().toString().trim());
            userMap.put("phone", editPhone.getText().toString().trim());
            userMap.put("address", editAddress.getText().toString().trim());
            userMap.put("pin", newPin );
        }


        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(fragContext, "Updated profile", Toast.LENGTH_SHORT).show();
                    Prevalent.currentOnlineUser.setPin(newPin);
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(editName.getText().toString().trim())){
            editName.setError("Please enter your name");
            editName.requestFocus();
        }
        else if (TextUtils.isEmpty(editPhone.getText().toString().trim())){
            editName.setError("Please enter your phone");
            editName.requestFocus();
            progressDialog.dismiss();
        }
        else if (TextUtils.isEmpty(editAddress.getText().toString().trim())) {
            editAddress.setError("Update your delivery address");
            editAddress.requestFocus();
            progressDialog.dismiss();
        }
        else if (TextUtils.isEmpty(editPin.getText().toString().trim()) && pinUpdate){
            editPin.setError("Please update the new pin.");
            editPin.requestFocus();
            progressDialog.dismiss();
        }else if (checker.equals("clicked")){

            if(TextUtils.isEmpty(editPin.getText().toString().trim()) || !pinUpdate){
                newPin = oldPin;
            }else if(!TextUtils.isEmpty(editPin.getText().toString().trim()) && pinUpdate){
                newPin = editPin.getText().toString();
            }

            uploadImage();
        }else if (pinUpdate && !checker.equals("clicked")){
            if(TextUtils.isEmpty(editPin.getText().toString().trim()) || !pinUpdate){
                newPin = oldPin;
            }else if(!TextUtils.isEmpty(editPin.getText().toString().trim()) && pinUpdate){
                newPin = editPin.getText().toString();
            }

            updateOnlyUserInfo();
        }

    }

    private void uploadImage() {
        Toast.makeText(fragContext, "Updating Profile", Toast.LENGTH_SHORT).show();
        if (imageUri != null){
            final StorageReference fileRef = profilImageRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            imageUploadTask = fileRef.putFile(imageUri);

            imageUploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        imageUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();

                        if (pinUpdate){
                            userMap.put("name", editName.getText().toString().trim());
                            userMap.put("phone", editPhone.getText().toString().trim());
                            userMap.put("address", editAddress.getText().toString().trim());
                            userMap.put("imageUrl", imageUrl);
                            userMap.put("pin", newPin);
                        }else if (!pinUpdate){
                            userMap.put("name", editName.getText().toString().trim());
                            userMap.put("phone", editPhone.getText().toString().trim());
                            userMap.put("address", editAddress.getText().toString().trim());
                            userMap.put("imageUrl", imageUrl);
                        }

                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(fragContext, "Updated profile, it may take time to Reflect.", Toast.LENGTH_LONG).show();
                                        Picasso.get().load(imageUrl)
                                                .placeholder(R.drawable.profile_icon).into(imgProfile);
                                    }
                                });

                    }
                    else {
                        Toast.makeText(fragContext, "Error Occurred!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String msg = e.toString();
                    Toast.makeText(fragContext, "Error: " + msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(fragContext, "Image Is not Selected", Toast.LENGTH_SHORT).show();
        }
    }
}
