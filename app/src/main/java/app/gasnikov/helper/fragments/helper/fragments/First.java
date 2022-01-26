package app.gasnikov.helper.fragments.helper.fragments;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import app.gasnikov.helper.LocationService;
import app.gasnikov.helper.Map_Activity;
import app.gasnikov.helper.Menu;
import app.gasnikov.helper.R;

public class First extends Fragment {
    private FragmentActivity myContext;


    CardView[] card = new CardView[6];
    TextView[]textViews=new TextView[6];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_first, container, false);
        card[0]=(CardView)v.findViewById(R.id.c1);
        card[1]=(CardView)v.findViewById(R.id.c2);
        card[2]=(CardView)v.findViewById(R.id.c3);
        card[3]=(CardView)v.findViewById(R.id.c4);
        card[4]=(CardView)v.findViewById(R.id.c5);
        card[5]=(CardView)v.findViewById(R.id.c6);
        textViews[0]=(TextView)v.findViewById(R.id.textView3);
        textViews[1]=(TextView)v.findViewById(R.id.textView4);
        textViews[2]=(TextView)v.findViewById(R.id.textView5);
        textViews[3]=(TextView)v.findViewById(R.id.textView6);
        textViews[4]=(TextView)v.findViewById(R.id.textView7);
        textViews[5]=(TextView)v.findViewById(R.id.textView8);
        for(int i=0;i<6;i++) {

            int finalI = i;
            card[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int j= finalI;
                    Intent intent = new Intent(getActivity(), Map_Activity.class);
                    intent.putExtra("type",textViews[j].getText());
                    startActivity(intent);
                }
            });
        }


        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext=(FragmentActivity) context;

    }

}