package app.gasnikov.helper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class User_Data extends AppCompatActivity {
    private User user;
    private TextView email,bt,rh,ar,cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__data);
        email=findViewById(R.id.email1);
        bt=findViewById(R.id.blood_type_4);
        rh=findViewById(R.id.rh_factor_4);
        ar=findViewById(R.id.ar_4);
        cd=findViewById(R.id.cd_4);
        user = (User) getIntent().getParcelableExtra("User");
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            user = arguments.getParcelable(User.class.getSimpleName());
            email.setText(user.getEmail());
            bt.setText(user.getBlood_type());
            rh.setText(user.getRh_factor());
            ar.setText(user.getAr());
            cd.setText(user.getCd());


        }

    }
}