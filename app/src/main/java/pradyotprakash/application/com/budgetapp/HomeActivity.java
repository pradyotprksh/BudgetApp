package pradyotprakash.application.com.budgetapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private String userId;
    private FirebaseFirestore mFirestore, mFirestore1;
    private TextView mainName;
    private CircleImageView mainImage;
    private TextView currentMoney;
    private Button addMoney, subMoney;
    private LottieAnimationView successView;
    private Double currentMoneyValue = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mainName = findViewById(R.id.main_name);
        mainImage = findViewById(R.id.main_image);
        currentMoney = findViewById(R.id.moneyValue);
        addMoney = findViewById(R.id.addBtn);
        subMoney = findViewById(R.id.subBtn);
        addMoney.setEnabled(false);
        subMoney.setEnabled(false);
        successView = findViewById(R.id.success_view);
        successView.setVisibility(View.GONE);
        mFirestore1 = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(HomeActivity.this);
                View promptsView = li.inflate(R.layout.prompts, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        HomeActivity.this);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("ADD",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {
                                        mFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (task.getResult().exists()) {
                                                        try {
                                                            currentMoneyValue = task.getResult().getDouble("currentMoney");
                                                        } catch (Exception e) {
                                                            currentMoneyValue = 0.0;
                                                        }
                                                        if (currentMoneyValue == null) {
                                                            currentMoneyValue = 0.0;
                                                        }
                                                        dialog.cancel();
                                                        successView.setVisibility(View.VISIBLE);
                                                        successView.playAnimation();
                                                        Double addValue = Double.valueOf(userInput.getText().toString());
                                                        HashMap<String, Object> addMoney = new HashMap<>();
                                                        addMoney.put("currentMoney", currentMoneyValue + addValue);
                                                        currentMoney.setText(String.valueOf(currentMoneyValue + addValue));
                                                        if ((currentMoneyValue + addValue) <= 0.0) {
                                                            currentMoney.setTextColor(Color.RED);
                                                        } else {
                                                            currentMoney.setTextColor(Color.BLACK);
                                                        }
                                                        mFirestore1.collection("Users").document(userId).update(addMoney).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toasty.success(HomeActivity.this, "Success!", Toast.LENGTH_SHORT, true).show();
                                                                successView.setVisibility(View.GONE);
                                                                successView.cancelAnimation();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toasty.success(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                                                successView.setVisibility(View.GONE);
                                                                successView.cancelAnimation();
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        subMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(HomeActivity.this);
                View promptsView = li.inflate(R.layout.prompts, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        HomeActivity.this);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("SUBTRACT",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {
                                        mFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (task.getResult().exists()) {
                                                        try {
                                                            currentMoneyValue = task.getResult().getDouble("currentMoney");
                                                        } catch (Exception e) {
                                                            currentMoneyValue = 0.0;
                                                        }
                                                        if (currentMoneyValue == null) {
                                                            currentMoneyValue = 0.0;
                                                        }
                                                        dialog.cancel();
                                                        successView.setVisibility(View.VISIBLE);
                                                        successView.playAnimation();
                                                        Double addValue = Double.valueOf(userInput.getText().toString());
                                                        HashMap<String, Object> addMoney = new HashMap<>();
                                                        addMoney.put("currentMoney", currentMoneyValue - addValue);
                                                        currentMoney.setText(String.valueOf(currentMoneyValue - addValue));
                                                        if ((currentMoneyValue - addValue) <= 0.0) {
                                                            currentMoney.setTextColor(Color.RED);
                                                        } else {
                                                            currentMoney.setTextColor(Color.BLACK);
                                                        }
                                                        mFirestore1.collection("Users").document(userId).update(addMoney).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toasty.success(HomeActivity.this, "Success!", Toast.LENGTH_SHORT, true).show();
                                                                successView.setVisibility(View.GONE);
                                                                successView.cancelAnimation();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toasty.success(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                                                successView.setVisibility(View.GONE);
                                                                successView.cancelAnimation();
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout_btn:
                LayoutInflater li = LayoutInflater.from(HomeActivity.this);
                View promptsView = li.inflate(R.layout.prompts4, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        HomeActivity.this);
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mAuth.signOut();
                                        Intent intent = new Intent(HomeActivity.this, BudgetLoginScreen.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            case R.id.action_setting_btn:
                Intent intent = new Intent(HomeActivity.this, AccountSettings.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userId = mAuth.getCurrentUser().getUid();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(HomeActivity.this, BudgetLoginScreen.class);
            startActivity(intent);
            finish();
        } else {
            mFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String name = task.getResult().getString("name");
                            mainName.setText("Welcome, " + name);
                            String image = task.getResult().getString("image");
                            RequestOptions placeHolderRequest = new RequestOptions();
                            placeHolderRequest.placeholder(R.mipmap.account_home);
                            currentMoneyValue = task.getResult().getDouble("currentMoney");
                            try {
                                if (currentMoneyValue <= 0.0) {
                                    currentMoney.setText(String.valueOf(currentMoneyValue));
                                    currentMoney.setTextColor(Color.RED);
                                } else {
                                    currentMoney.setText(String.valueOf(currentMoneyValue));
                                    currentMoney.setTextColor(Color.BLACK);
                                }
                            } catch (Exception e) {
                                currentMoney.setText("Nothing Entered Yet");
                                currentMoneyValue = 0.0;
                            }
                            Glide.with(HomeActivity.this).setDefaultRequestOptions(placeHolderRequest).load(image).into(mainImage);
                            addMoney.setEnabled(true);
                            subMoney.setEnabled(true);
                        } else {
                            Intent intent = new Intent(HomeActivity.this, AccountSettings.class);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
    }
}
