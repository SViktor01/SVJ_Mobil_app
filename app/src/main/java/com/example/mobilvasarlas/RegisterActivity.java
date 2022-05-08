package com.example.mobilvasarlas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG=RegisterActivity.class.getName();
    private static final String PREF_KEY=MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY=99;

    EditText userNameEditText;
    EditText userEmailEditText;
    EditText userPasswordEditText;
    EditText userPasswordAgainEditText;
    EditText phoneEditText;
    EditText addressEditText;
    RadioGroup accountType;

    private SharedPreferences preferences;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

      // Bundle bundle = getIntent().getExtras();
      int secret_key=getIntent().getIntExtra("SECRET_KEY",99);
      if(secret_key!=99){
            finish();
      }
      userNameEditText =findViewById(R.id.userNameEditText);
      userEmailEditText =findViewById(R.id.userEmailEditText);
      userPasswordEditText =findViewById(R.id.userPasswordEditText);
      userPasswordAgainEditText =findViewById(R.id.userPasswordAgainEditText);
      phoneEditText = findViewById(R.id.phoneEditText);
      addressEditText = findViewById(R.id.addressEditText);
      accountType=findViewById(R.id.accountTypeGroup);
      accountType.check(R.id.buyerRadioButton);

      preferences=getSharedPreferences(PREF_KEY,MODE_PRIVATE);
      String email= preferences.getString("email","");
      String password= preferences.getString("password","");
      userEmailEditText.setText(email);
      userPasswordEditText.setText(password);
      userPasswordAgainEditText.setText(password);

      auth=FirebaseAuth.getInstance();

      Log.i(LOG_TAG,"onCreate");
    }

    public void register(View view) {
        String username=userNameEditText.getText().toString();
        String email=userEmailEditText.getText().toString();
        String password=userPasswordEditText.getText().toString();
        String passwordagain=userPasswordAgainEditText.getText().toString();


        if(!password.equals(passwordagain)){
            Log.e(LOG_TAG,"Nem egyezik a két jelszó");
            return;
        }

        String phoneNumber=phoneEditText.getText().toString();
        String address=addressEditText.getText().toString();

        int checkedid=accountType.getCheckedRadioButtonId();
        RadioButton radiobutton=accountType.findViewById(checkedid);
        String accounttype=radiobutton.getText().toString();


        Log.i(LOG_TAG,"Regisztrált: "+username+ ",email: "+email);
        // startShopping();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"User created succesfully");
                    startShopping();
                }else{
                    Log.d(LOG_TAG,"User wasn't created succesfully");
                    Toast.makeText(RegisterActivity.this,"User wasn't created succesfully: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void startShopping(){
        Intent intent=new Intent(this,ShopListActivity.class);
       // intent.putExtra("SECRET_KEY",SECRET_KEY);
        startActivity(intent);

    }

    public void cancel(View view) {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG,"onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG,"onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG,"onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG,"onResume");
    }
}