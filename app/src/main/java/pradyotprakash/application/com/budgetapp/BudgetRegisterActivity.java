package pradyotprakash.application.com.budgetapp;

import android.animation.Animator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class BudgetRegisterActivity extends AppCompatActivity {

    private EditText emailId, passwordValue;
    private Button login, alreadyAccount;
    private LottieAnimationView loadingAnimation, animationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_register);
        emailId = findViewById(R.id.emailId);
        passwordValue = findViewById(R.id.password);
        login = findViewById(R.id.loginBtn);
        loadingAnimation = findViewById(R.id.success_view);
        animationView = findViewById(R.id.animation_view);
        loadingAnimation.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        alreadyAccount = findViewById(R.id.loginBtn2);
        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudgetRegisterActivity.this, BudgetLoginScreen.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString();
                final String password = passwordValue.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    loadingAnimation.playAnimation();
                    loadingAnimation.bringToFront();
                    loadingAnimation.setVisibility(View.VISIBLE);
                    emailId.setEnabled(false);
                    passwordValue.setEnabled(false);
                    login.setEnabled(false);
                    loadingAnimation.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(BudgetRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(BudgetRegisterActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toasty.error(BudgetRegisterActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT, true).show();
                                        loadingAnimation.setVisibility(View.INVISIBLE);
                                        emailId.setEnabled(true);
                                        passwordValue.setEnabled(true);
                                        login.setEnabled(true);
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toasty.error(BudgetRegisterActivity.this, "Fill All The Details.", Toast.LENGTH_SHORT, true).show();
                    loadingAnimation.setVisibility(View.INVISIBLE);
                    emailId.setEnabled(true);
                    passwordValue.setEnabled(true);
                    login.setEnabled(true);
                }
            }
        });
    }
}
