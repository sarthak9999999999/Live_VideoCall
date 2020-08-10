package com.example.lve_videocallchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Registration_Activity  extends AppCompatActivity {

  private CountryCodePicker ccp;
  private EditText phonetext;
  private EditText otp;
  private Button register;
  String checker="";
  String phnumber="";
  RelativeLayout relativeLayout;
  private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks;
  private PhoneAuthProvider.ForceResendingToken resendingToken;
  private FirebaseAuth mAuth;
  private String verificationid;
  private ProgressDialog loadingbar;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        FirebaseApp.initializeApp(this);

        mAuth=FirebaseAuth.getInstance();
        loadingbar=new ProgressDialog(this);

        ccp=findViewById(R.id.ccp);
        phonetext=findViewById(R.id.phoneText);
        otp=findViewById(R.id.otp);
        register=findViewById(R.id.continueNextButton);
        relativeLayout=findViewById(R.id.phoneAuth);
        ccp.registerCarrierNumberEditText(phonetext);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (register.getText().equals("Submit") || checker.equals("OTPSent")) {

                    String rec_otp=otp.getEditableText().toString();
                    if(rec_otp.equals(""))
                    {
                        Toast.makeText(Registration_Activity.this,"Please enter your OTP!",Toast.LENGTH_LONG).show();
                    }
                    else{
                        loadingbar.setTitle("Verifying OTP");
                        loadingbar.setMessage("Verifying OTP from server...");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();

                        PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(verificationid,rec_otp);
                        signInWithPhoneAuthCredential(phoneAuthCredential);

                    }

                } else {
                    phnumber = ccp.getFullNumberWithPlus();
                    if (!phnumber.equals("")) {
                        loadingbar.setTitle("Sending OTP for Verification");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phnumber,60,TimeUnit.SECONDS,Registration_Activity.this,mcallbacks);
                    } else {
                        Toast.makeText(Registration_Activity.this, "Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mcallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
            {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential);

                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(Registration_Activity.this,"Please Provide A Valid Phone Number",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    relativeLayout.setVisibility(View.VISIBLE);
                    register.setText("Continue");
                    otp.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);

                    verificationid=s;
                    forceResendingToken=forceResendingToken;
                    relativeLayout.setVisibility(View.INVISIBLE);
                    checker="OTPSent";
                    register.setText("Submit");
                    otp.setVisibility(View.VISIBLE);
                    loadingbar.dismiss();
                    Toast.makeText(Registration_Activity.this,"OTP Sent! Please wait for the message.",Toast.LENGTH_SHORT).show();
                }
            };
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null)
        {
          Intent alreadyregistered=new Intent(Registration_Activity.this,MainActivity.class);
          startActivity(alreadyregistered);
          this.finish();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                           loadingbar.dismiss();
                           authenticatetoMainActivity();

                        } else
                        {
                            loadingbar.dismiss();
                            String error=task.getException().toString();
                            Toast.makeText(Registration_Activity.this,"Error:" + error,Toast.LENGTH_SHORT).show();
                            }
                        }
                });
    }

    private void authenticatetoMainActivity()
    {
        Intent intent=new Intent(Registration_Activity.this,MainActivity.class);
        startActivity(intent);
        finish();
}
}
