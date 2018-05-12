package pradyotprakash.application.com.budgetapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class AccountSettings extends AppCompatActivity {

    private Toolbar mToolbar;
    private CircleImageView profileImage;
    private EditText username, name, emailEdit;
    private Button upload;
    public static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore, mFirestore1;
    private String email, userId;
    private LottieAnimationView loadingAnimation;
    private Boolean isChanged = false;
    private String imageLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        imageUri = null;
        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.usernameValue);
        name = findViewById(R.id.nameValue);
        upload = findViewById(R.id.uploadBtn);
        emailEdit = findViewById(R.id.emailValue);
        emailEdit.setEnabled(false);
        loadingAnimation = findViewById(R.id.success_view);
        loadingAnimation.setVisibility(View.GONE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
            emailEdit.setText(email);
        }
        mStorageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mFirestore1 = FirebaseFirestore.getInstance();
        mFirestore1.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        username.setText(task.getResult().getString("username"));
                        name.setText(task.getResult().getString("name"));
                        emailEdit.setText(task.getResult().getString("email"));
                        imageLink = task.getResult().getString("image");
                        imageUri = Uri.parse(imageLink);
                        RequestOptions placeHolderRequest = new RequestOptions();
                        placeHolderRequest.placeholder(R.mipmap.account_home);
                        Glide.with(AccountSettings.this).setDefaultRequestOptions(placeHolderRequest).load(imageLink).into(profileImage);
                    }
                }
            }
        });
        mFirestore = FirebaseFirestore.getInstance();
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    final String nameValue = name.getText().toString();
                    final String usernameValue = username.getText().toString();
                    if (!TextUtils.isEmpty(nameValue) && !TextUtils.isEmpty(usernameValue)) {
                        loadingAnimation.setVisibility(View.VISIBLE);
                        loadingAnimation.playAnimation();
                        loadingAnimation.bringToFront();
                        upload.setVisibility(View.GONE);
                        if (isChanged) {
                            StorageReference userProfile = mStorageReference.child(userId + ".jpg");
                            userProfile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        String downloadUrl = task.getResult().getDownloadUrl().toString();
                                        HashMap<String, Object> uploadTask = new HashMap<>();
                                        uploadTask.put("username", usernameValue);
                                        uploadTask.put("name", nameValue);
                                        uploadTask.put("email", email);
                                        uploadTask.put("image", downloadUrl);
                                        mFirestore.collection("Users").document(userId).set(uploadTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toasty.success(AccountSettings.this, "Success!", Toast.LENGTH_SHORT, true).show();
                                                loadingAnimation.cancelAnimation();
                                                loadingAnimation.setVisibility(View.GONE);
                                                upload.setVisibility(View.VISIBLE);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toasty.error(AccountSettings.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                                loadingAnimation.cancelAnimation();
                                                loadingAnimation.setVisibility(View.GONE);
                                                upload.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    } else {
                                        Toasty.error(AccountSettings.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                                        loadingAnimation.cancelAnimation();
                                        loadingAnimation.setVisibility(View.GONE);
                                        upload.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        } else {
                            HashMap<String, Object> uploadTask = new HashMap<>();
                            uploadTask.put("username", usernameValue);
                            uploadTask.put("name", nameValue);
                            uploadTask.put("email", email);
                            uploadTask.put("image", imageLink);
                            mFirestore.collection("Users").document(userId).set(uploadTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toasty.success(AccountSettings.this, "Success!", Toast.LENGTH_SHORT, true).show();
                                    loadingAnimation.cancelAnimation();
                                    loadingAnimation.setVisibility(View.GONE);
                                    upload.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toasty.error(AccountSettings.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                    loadingAnimation.cancelAnimation();
                                    loadingAnimation.setVisibility(View.GONE);
                                    upload.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    } else {
                        Toasty.error(AccountSettings.this, "Fill All The Details.", Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    Toasty.error(AccountSettings.this, "Select A Profile Image.", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            isChanged = true;
        }
    }

}
