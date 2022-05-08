package com.example.mobilvasarlas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG=MainActivity.class.getName();
    private static final String PREF_KEY=MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY=99;

    EditText email;
    EditText password;

    private SharedPreferences preferences;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email= findViewById(R.id.EditTextUsername);
        password=findViewById(R.id.EditTextPassword);

        preferences=getSharedPreferences(PREF_KEY,MODE_PRIVATE);


        Log.i(LOG_TAG,"onCreate");
        auth=FirebaseAuth.getInstance();
    }

    public void login(View view) {
        String uname=email.getText().toString();
        String pass=password.getText().toString();
        //Log.i("MainActivity","Bejelentkezett: "+uname+ ",jelszó: "+pass);
        if(uname.length()==0 || pass.length()==0){
            Log.d(LOG_TAG,"Nem töltöttél ki minden mezőt!");
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        auth.signInWithEmailAndPassword(uname,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"User login succesfully");
                    startShopping();
                }else{
                    Log.d(LOG_TAG,"User login fail");
                    Toast.makeText(MainActivity.this,"User login fail: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void startShopping(){
        Intent intent=new Intent(this,ShopListActivity.class);
        startActivity(intent);
    }

    public void register(View view) {
        Intent intent= new Intent(this,RegisterActivity.class);
        intent.putExtra("SECRET KEY",99);
        //TODO.
        startActivity(intent);
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
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("email",email.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.apply(); //Az editorba lementjük az állapotot

        Log.i(LOG_TAG,"onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG,"onResume");
    }


    public void anonym_login(View view) {
        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"Anonym user login succesfully");
                    startShopping();
                }else{
                    Log.d(LOG_TAG,"Anonym user login fail");
                    Toast.makeText(MainActivity.this,"User login fail: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}