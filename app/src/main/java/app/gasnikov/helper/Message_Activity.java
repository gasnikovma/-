package app.gasnikov.helper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.gasnikov.helper.Notifications.ApiService;
import app.gasnikov.helper.Notifications.Client;
import app.gasnikov.helper.Notifications.Data;
import app.gasnikov.helper.Notifications.Response;
import app.gasnikov.helper.Notifications.Sender;
import app.gasnikov.helper.Notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class Message_Activity extends AppCompatActivity {

    private TextView username;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
private Toolbar toolbar;
private ImageButton send;

private EditText txt_send;
private MessageAdapter messageAdapter;
private List<Message> messageList;
public static String uid,fn;
private ImageView avatar;
private RecyclerView recyclerView;
ValueEventListener seen;
ApiService apiService;
public boolean notify1 =false;
public LocationModel s;
public Incident d;
public User user1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);
        toolbar = findViewById(R.id.toolbar);
        send = findViewById(R.id.send);
        txt_send = findViewById(R.id.txt_send);
        recyclerView = findViewById(R.id.recycler_view);
        avatar = findViewById(R.id.profile);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        setSupportActionBar(toolbar);//заменяем toolbar на actionbar и гарантируем что не null
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        username = findViewById(R.id.username);
        Bundle dataBundle = getIntent().getExtras();
        if(dataBundle.getString("userid")!=null){
             uid= dataBundle.getString("userid");
             db.getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                     User user=snapshot.getValue(User.class);
                         fn=user.fullname;
                     username.setText(fn);
                     setAvatar(fn);

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {

                 }
             });


        }
        else {
             Intent intent;
            intent = getIntent();
            uid = intent.getStringExtra("getuserid");
            fn = intent.getStringExtra("getfullname");
            username.setText(fn);
            setAvatar(fn);

        }
        db.getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user1=snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db.getReference("Incidents").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    d=snapshot.getValue(Incident.class);

                }
                else {
                    db.getReference("Incidents").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.getValue()!=null){
                                d=snapshot.getValue(Incident.class);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }


        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        readMsg(FirebaseAuth.getInstance().getCurrentUser().getUid(),uid);
        db.getReference("User Locations").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserLocation userLocation=snapshot.getValue(UserLocation.class);
                s= userLocation.getGeo_point();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        seenMsg(uid);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        apiService= Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify1=true;
                String msg = txt_send.getText().toString();
                if (!msg.equals("")) {
                    sendMsg(FirebaseAuth.getInstance().getCurrentUser().getUid(), uid, msg);

                } else {
                    Toast.makeText(Message_Activity.this, "Empty field", Toast.LENGTH_LONG).show();
                }
                txt_send.setText("");
            }
        });

    }
  /*  private LocationModel getLocation(String id){
        db.getReference("User Locations").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserLocation userLocation=snapshot.getValue(UserLocation.class);
                 s =userLocation.getGeo_point();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return s;


    }*/
    private void setAvatar(String s) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getColor(FirebaseAuth.getInstance().getCurrentUser().getUid());

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig()
                .round();
        TextDrawable drawable = builder.build(s.substring(0, 1), color);
        avatar.setImageDrawable(drawable);
    }












    private void seenMsg(String userid){


        seen=db.getReference("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Message message=  snapshot1.getValue(Message.class);
                    if(message.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && message.getSender().equals(userid)) {
                        HashMap<String,Object>s=new HashMap<>();
                        s.put("seen",true);
                        snapshot1.getRef().updateChildren(s);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendMsg( String sender, String receiver, String message){
        HashMap<String,Object> msg=new HashMap<>();
        msg.put("receiver",receiver);
        msg.put("sender",sender);
        msg.put("message",message);
        msg.put("seen",false);
        db.getReference().child("Chats").push().setValue(msg);
        final String msg1=message;
        db.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(notify1) {
                    sendnotify(receiver, user.getFullname(), msg1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void sendnotify(String receiver,final String fullname,final String smessage) {
        DatabaseReference tokens =db.getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Token token=snapshot1.getValue(Token.class);
                    Data data=new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(),R.mipmap.ic_launcher,fullname+": " +smessage,"New message",uid);
                    Sender sender=new Sender(data,token.getToken());
                    apiService.sendnotification(sender).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if(response.code()==235){
                                if(response.body().success!=1){
                                    Toast.makeText(Message_Activity.this,"Fail",Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void readMsg(String mid,String yid){
        messageList=new ArrayList<>();
        db.getReference().child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Message message = snapshot1.getValue(Message.class);
                   if (message.getReceiver().equals(mid) && message.getSender().equals(yid) || message.getSender().equals(mid) && message.getReceiver().equals(yid)) {
                            messageList.add(message);
                        }
                        messageAdapter = new MessageAdapter(Message_Activity.this, messageList);
                        recyclerView.setAdapter(messageAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


            }
            private void curuser(String userid){
                SharedPreferences.Editor editor=getSharedPreferences("PREFERS",MODE_PRIVATE).edit();
                editor.putString("curuser",userid);
                editor.apply();

            }

    @Override
    public void onPause() {
        super.onPause();
        db.getReference().removeEventListener(seen);
        curuser("nobody");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.chatroom_map:{
                        Intent intent=new Intent(Message_Activity.this,MapsActivity.class);
                        intent.putExtra("Loc", new LocationModel(s.getLat(),s.getLng()));
                        if(d!=null) {
                            intent.putExtra("Inc", new Incident(d.getLocationModel(),d.getType()));
                            intent.putExtra(Incident.class.getSimpleName(), d);
                        }
                intent.putExtra(LocationModel.class.getSimpleName(), s);
                intent.putExtra("name",fn);
                intent.putExtra("id",uid);
                startActivity(intent);
                        return true;
                    }

            case R.id.user_data:{
                Intent intent=new Intent(Message_Activity.this, User_Data.class);
               intent.putExtra("User", new User(user1.fullname,user1.email,user1.blood_type,user1.rh_factor,user1.cd,user1.ar,user1.id));
                intent.putExtra(User.class.getSimpleName(), user1);
                startActivity(intent);
                return true;
            }

            default:{
                return super.onOptionsItemSelected(item);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        curuser(uid);
    }


}
