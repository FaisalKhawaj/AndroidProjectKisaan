package com.example.aso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {



    EditText password,email;
    TextView NoAccount,ForgotPassword;
    Button login;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    String userEmail;
    GoogleSignInAccount account;
    SignInButton signInButton;
    int RC_SIGN_IN = 999;
    FirebaseUser currentUser;
    AuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loging In ..");
        login=(Button)findViewById(R.id.login_button);
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.signin_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                progressDialog.setTitle("Accessing Google Credentials");
                progressDialog.show();
                progressDialog.setCancelable(false);
            }
        });
        password=(EditText)findViewById(R.id.password_text);
        email=(EditText)findViewById(R.id.email_text);
        NoAccount=(TextView)findViewById(R.id.no_account_text);
        ForgotPassword=(TextView)findViewById(R.id.forgot_password_text);
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRecoveryDialoge();
            }
        });
        NoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String Email=email.getText().toString().trim();
                String Password=password.getText().toString().trim();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                    {
                        // if email is typed incorrectly ..
                        email.setError("Email is incorrect");
                        email.setFocusable(true);
                    }

                    else if(Password.length()<8)
                    {

                        password.setError("Password is too Short");
                        password.setFocusable(true);
                    }

                    else {
                        progressDialog.show();
                        UserValidation(Email,Password);
                    }
                }
            }
        });
    }

    private void ShowRecoveryDialoge() {

        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Password Recovery");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailText=new EditText(this);
        emailText.setHint("Email");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            emailText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        emailText.setMinEms(16);
        linearLayout.addView(emailText);
        dialog.setView(linearLayout);
        dialog.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // recovery mail ..
                String emailAddress=emailText.getText().toString().trim();
                SendRecoveryMail(emailAddress);


            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    private void SendRecoveryMail(String emailAddress) {
        progressDialog.setTitle("Sending Recovery Email ..");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Mail Sent.",Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void UserValidation(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                String email=user.getEmail();
                                String uid=user.getUid();
                                HashMap<Object,String> hashMap=new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("name","");
                                hashMap.put("phone","");
                                hashMap.put("picture","");
                                hashMap.put("street","");
                                hashMap.put("city","");
                                hashMap.put("province","");
                                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference=firebaseDatabase.getReference("DSMUsers");
                                databaseReference.child(uid).setValue(hashMap);
                            }
                            updateUI(user);
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null)
        {
progressDialog.dismiss();
            startActivity(new Intent(LoginActivity.this,SelectionActivity.class));
            finish();
        }
        else
        {
            //   startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
        }

    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Toast.makeText(LoginActivity.this,"activity started toast",Toast.LENGTH_SHORT).show();
        Log.d("Google Signin","first thing started for result");
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Google Signin","passing account to firebase authentication .. ");

                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
          Toast.makeText(LoginActivity.this, "firebaseAuthWithGoogle:" + acct.getId(), Toast.LENGTH_SHORT).show();

        credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                             Toast.makeText(LoginActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                String email=user.getEmail();
                                String uid=user.getUid();
                                HashMap<Object,String> hashMap=new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("name","");
                                hashMap.put("phone","");
                                hashMap.put("picture","");
                                hashMap.put("street","");
                                hashMap.put("city","");
                                hashMap.put("province","");
                                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference=firebaseDatabase.getReference("DSMUsers");
                                databaseReference.child(uid).setValue(hashMap);
                            }
                            progressDialog.dismiss();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Google Sign in Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void signOut() {


        FirebaseAuth.getInstance().signOut();

    }
}
