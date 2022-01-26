package app.gasnikov.helper;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;


import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

public class Settings extends AppCompatActivity {
    private String[]blood,rh,cd,ad;
    private SwitchCompat switchCompat1,switchCompat2;
    private TextInputLayout chronic_diseases,allergic_reactions;
    private AutoCompleteTextView a1,a2;
    private MultiAutoCompleteTextView a3,a4;
    private ArrayAdapter arap1,arap2,arap3,arap4;
    private Button save;
    private ProgressBar progressBar;
    FirebaseDatabase db = FirebaseDatabase.getInstance();






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        Intent intent=getIntent();
        chronic_diseases=(TextInputLayout) findViewById(R.id.chronic_diseases_1);
        allergic_reactions=(TextInputLayout)findViewById(R.id.allergic_diseases_1);
        save=(Button)findViewById(R.id.save);
        a1=(AutoCompleteTextView)findViewById(R.id.blood_type);
        String blood_type_1=intent.getStringExtra("blood_type");
        a1.setText(blood_type_1);
        switchCompat1 = (SwitchCompat)findViewById(R.id.switch1);
        switchCompat1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==false){
                    chronic_diseases.setVisibility(View.GONE);
                    a3.setText("");

                }
                else chronic_diseases.setVisibility(View.VISIBLE);


            }
        });
        switchCompat2=(SwitchCompat)findViewById(R.id.switch2);
        switchCompat2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==false){
                    allergic_reactions.setVisibility(View.GONE);
                    a4.setText("");

                }
                else allergic_reactions.setVisibility(View.VISIBLE);


            }
        });
        chronic_diseases.setVisibility(View.GONE);
        allergic_reactions.setVisibility(View.GONE);
        blood=getResources().getStringArray(R.array.blood_type);
        arap1 = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_item,blood);
        a1.setAdapter(arap1);
        a2=(AutoCompleteTextView)findViewById(R.id.rh_factor);
        String rh_factor_1=intent.getStringExtra("rh_factor");
        a2.setText(rh_factor_1);

        a3=(MultiAutoCompleteTextView)findViewById(R.id.chronic_diseases);
        String cd_1=intent.getStringExtra("cd");
        if(cd_1.trim().length()!=0){
            switchCompat1.setChecked(true);
            a3.setText(cd_1);
        }
        cd=getResources().getStringArray(R.array.chronic_diseases);
        arap3=new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_item,cd);
        a3.setAdapter(arap3);
        a3.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        a4=(MultiAutoCompleteTextView)findViewById(R.id.allergic_diseases);
        String ar_1=intent.getStringExtra("ar");
        if(ar_1.trim().length()!=0){
            switchCompat2.setChecked(true);
            a4.setText(ar_1);
        }
        a4.setText(ar_1);
        ad=getResources().getStringArray(R.array.allergic_reactions);
        arap4=new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_item,ad);
        a4.setAdapter(arap4);
        a4.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        rh=getResources().getStringArray(R.array.rh_factor);
        arap2=new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_item,rh);
        a2.setAdapter(arap2);




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String nbt = a1.getText().toString().trim();
                String nrh = a2.getText().toString().trim();
                String ncd = a3.getText().toString().trim();
                String nar = a4.getText().toString().trim();
                HashMap<String, Object> result = new HashMap<>();
                result.put("blood_type", nbt);
                result.put("rh_factor", nrh);
                result.put("cd", ncd);
                result.put("ar", nar);

                db.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Settings.this, "Saved", Toast.LENGTH_LONG).show();
                            
                        } else {
                            Toast.makeText(Settings.this, "Try again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });
    }
    }



