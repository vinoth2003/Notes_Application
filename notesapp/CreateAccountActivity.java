package com.example.notesapp;

import android.os.Bundle;
import android.telephony.mbms.StreamingServiceInfo;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText, passwordeditText, confirmPasswordText;
    Button createAccountBut;
    ProgressBar progressBar;
    TextView loginButTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordeditText = findViewById(R.id.password_edit_text);
        confirmPasswordText = findViewById(R.id.confirmpasswoed_edit_text);
        createAccountBut = findViewById(R.id.create_account);
        progressBar = findViewById(R.id.progress_bar);
        loginButTextview = findViewById(R.id.login_text_view_but);

        createAccountBut.setOnClickListener(v -> createAccount());
        loginButTextview.setOnClickListener(v -> finish());
    }

    void createAccount() {
        String email = emailEditText.getText().toString();
        String password = passwordeditText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);

        if (!isValidated) {
            // Proceed with account creation
            return;
        }

        createAccountInFirebase(email, password);
    }
    void createAccountInFirebase(String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            //account is create success
                            Utility.showToast(CreateAccountActivity.this,"Successfully account Created,Check your email");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else{
                            //unable to create
                            Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());

                        }
                    }
                }
                );
    }

    void changeInProgress(boolean inProgress){
        if (inProgress){
           progressBar.setVisibility(View.VISIBLE);
           createAccountBut.setVisibility(View.GONE);

        }else{
            progressBar.setVisibility(View.GONE);
            createAccountBut.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword) {
        // Validate the email id
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordeditText.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordText.setError("Passwords do not match");
            return false;
        }
        return true;
    }
}
