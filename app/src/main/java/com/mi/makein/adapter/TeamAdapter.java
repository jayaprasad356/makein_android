package com.mi.makein.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mi.makein.R;
import com.mi.makein.model.Team;

import java.util.ArrayList;


public class TeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final Activity activity;
    final ArrayList<Team> teams;

    public TeamAdapter(Activity activity, ArrayList<Team> teams) {
        this.activity = activity;
        this.teams = teams;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.team_lyt, parent, false);
        return new ItemHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderParent, int position) {
        final ItemHolder holder = (ItemHolder) holderParent;
        final Team team = teams.get(position);
        String s1 = team.getMobile().substring(0,2);
        String s2 = team.getMobile().substring(8,10);

        holder.tvName.setText(team.getName());
        holder.mobile.setText(s1+"******"+s2);


    }


    @Override
    public int getItemCount() {
        return teams.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {

        final TextView tvName,mobile;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mobile = itemView.findViewById(R.id.tvMobile);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}

