package com.example.eelection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eelection.R;
import com.example.eelection.model.Voter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewVotersAdapter extends RecyclerView.Adapter<ViewVotersAdapter.ViewHolder>{

    private Context context;
    private List<Voter> list;
    private FirebaseFirestore firebaseFirestore;

    public ViewVotersAdapter(Context context, List<Voter> list) {
        this.context = context;
        this.list = list;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public ViewVotersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.view_voters_layout,parent,false);
        return new ViewVotersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewVotersAdapter.ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.email.setText(list.get(position).getEmail());
        Glide.with(context).load(list.get(position).getImage()).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView name,email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            email=itemView.findViewById(R.id.email);
        }
    }
}
