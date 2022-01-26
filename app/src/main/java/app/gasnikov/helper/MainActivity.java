package app.gasnikov.helper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="1213214" ;
    private TextView registernow,fpassword;
    private EditText email,password;
    private Button login;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar=findViewById(R.id.progressBar);
        registernow=(TextView)findViewById(R.id.register);
        registernow.setOnClickListener(this);
        login=(Button)findViewById(R.id.resetpasssword);
        login.setOnClickListener(this);
        user= FirebaseAuth.getInstance().getCurrentUser();
        email=(EditText)findViewById(R.id.email);
        mauth=FirebaseAuth.getInstance();
        fpassword=(TextView)findViewById(R.id.fpassword);
        fpassword.setOnClickListener(this);
        password=(EditText)findViewById(R.id.password);
        SessionManager sessionManager=new SessionManager(MainActivity.this);
        HashMap<String,String>r=sessionManager.h();
        email.setText(r.get(SessionManager.KEY_EMAIL));
        password.setText(r.get(SessionManager.KEY_PASSWORD));




        
        if(user!=null){
            Intent intent = new Intent(this,Menu.class);
            startActivity(intent);
            finish();
        }

    }
    @Override
    public void onBackPressed() {

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);


    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.register){
            Intent intent = new Intent(this,Registration.class);
            startActivity(intent);
            finish();
        }
        if(v.getId()==R.id.resetpasssword){
            login();


        }
        if(v.getId()==R.id.fpassword){
            Intent intent = new Intent(this,FPassword.class);
            startActivity(intent);
            finish();
        }
    }
    private void login(){
        SessionManager sessionManager=new SessionManager(MainActivity.this);
        String nemail=email.getText().toString().trim();
        String npassword=password.getText().toString().trim();
        sessionManager.remember(nemail,npassword);

        if(nemail.isEmpty()||npassword.isEmpty()){
            Toast.makeText(MainActivity.this, "All fields must be filled", Toast.LENGTH_LONG).show();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(nemail).matches()){
            email.setError("Please enter valid email");
            return;
        }
        if(password.length()<6){
            password.setError("Password must be of 6 characters at least");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mauth.signInWithEmailAndPassword(nemail,npassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //если успешно вошел
                        if(task.isSuccessful()){
                            FirebaseUser u=FirebaseAuth.getInstance().getCurrentUser();
                            if(u.isEmailVerified()) {

                                Intent i = new Intent(MainActivity.this, Menu.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                u.sendEmailVerification();
                                Toast.makeText(MainActivity.this, "Check your email address to confirm your account", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);


                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Authorization failed!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }


                        }



});
    }

}