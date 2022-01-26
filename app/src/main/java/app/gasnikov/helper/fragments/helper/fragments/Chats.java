package app.gasnikov.helper.fragments.helper.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import app.gasnikov.helper.Message;

import app.gasnikov.helper.Notifications.Token;
import app.gasnikov.helper.R;
import app.gasnikov.helper.User;
import app.gasnikov.helper.UserAdapter;


public class Chats extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> musers;
    private List<String> ul;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ul = new ArrayList<>();
        db.getReference().child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ul.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if(message!=null) {
                        if ((message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) && !ul.contains(message.getReceiver())) {
                            ul.add(message.getReceiver());
                        }
                        if (message.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && !ul.contains(message.getSender())) {
                            ul.add(message.getSender());
                        }
                    }
                }
                read();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }
    private void read(){
        musers=new ArrayList<>();
        db.getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                musers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user=dataSnapshot.getValue(User.class);
                    // display 1 user
                    for(String id:ul){
                        if(user.getId().equals(id)){
                            musers.add(user);
                        }
                    }

                }
                userAdapter=new UserAdapter(getContext(),musers);
                recyclerView.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}