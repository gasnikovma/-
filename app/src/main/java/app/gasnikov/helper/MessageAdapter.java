package app.gasnikov.helper;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MViewHolder> {
    private Context context;
    private List<Message> messageList;
    private FirebaseUser firebaseUser;
    public MessageAdapter(Context context,List<Message> messageList){
        this.messageList = messageList;
        this.context=context;
    }
    //перечислить используемые компоненты из макета для отдельного элемента списка
    public class MViewHolder extends RecyclerView.ViewHolder{
        public TextView msg;
        public ImageView imageView;
        public TextView seen;
        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            msg=itemView.findViewById(R.id.msg);
            imageView=itemView.findViewById(R.id.profile);
            seen=itemView.findViewById(R.id.seen);

        }
    }





    @NonNull
    @Override
    //указать идентификатор макета для отдельного элемента списка и вернуть объект ViewHolder
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==1) {
            View view = LayoutInflater.from(context).inflate(R.layout.message_right, parent, false);

            return new MessageAdapter.MViewHolder(view);
        }
        else{
            View view2 = LayoutInflater.from(context).inflate(R.layout.message_left, parent, false);


            return new MessageAdapter.MViewHolder(view2);

        }
    }



    @Override
    //связать используемые текстовые метки с данными
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {
            Message message= messageList.get(position);
            holder.msg.setText(message.getMessage());
       ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getColor(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String fn=Message_Activity.fn;
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig()
                .round();
        TextDrawable drawable=builder.build(fn.substring(0,1),color);
        holder.imageView.setImageDrawable(drawable);
        //проверка для последнего сообщения
        if(position==messageList.size()-1) {
            if (message.isSeen()) {
                holder.seen.setText("Seen");
            } else {
                holder.seen.setText("Delievered");
            }
        }
            else{
                holder.seen.setVisibility(View.GONE);
            }
        }







    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(messageList.get(position).getSender().equals(firebaseUser.getUid())){
            return 1;
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
