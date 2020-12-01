package com.example.admin.sinch.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.sinch.Home;
import com.example.admin.sinch.R;
import com.example.admin.sinch.modelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>{
    ArrayList<modelUser> arrayList = new ArrayList<>();
    Context context;
    FirebaseUser currentuser;

    public HomeAdapter(ArrayList<modelUser> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler_output, parent, false);
        return new MyViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        final modelUser myData = arrayList.get(i);
        holder.name.setText(myData.getName());
        holder.imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Home)context).callUser(myData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View holderview;
        ImageButton imageCall;
        TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);
            holderview = itemView;
            imageCall =  itemView.findViewById(R.id.imageCall);
            name = (TextView) itemView.findViewById(R.id.userName);
        }
    }
}
