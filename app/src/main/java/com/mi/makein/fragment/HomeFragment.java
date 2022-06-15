package com.mi.makein.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mi.makein.BonusActivity;
import com.mi.makein.MinorActivity;
import com.mi.makein.R;
import com.mi.makein.RechargeActivity;
import com.mi.makein.SpinWheelActivity;
import com.mi.makein.WithdrawalListActivity;
import com.mi.makein.helper.Constant;
import com.mi.makein.helper.Session;

public class HomeFragment extends Fragment {

    RelativeLayout recharge_layout,withdrawal_layout,activity_layout,spin_layout;
    TextView tvEarn,tvRecharge;
    Session session;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);

        recharge_layout = rootview.findViewById(R.id.recharge_layout);
        withdrawal_layout = rootview.findViewById(R.id.withdrawal_layout);
        activity_layout = rootview.findViewById(R.id.miner_layout);
        spin_layout = rootview.findViewById(R.id.spin_layout);
        tvEarn = rootview.findViewById(R.id.tvEarn);
        tvRecharge = rootview.findViewById(R.id.tvRecharge);
        session = new Session(getActivity());
        if (session.getData(Constant.EARN).equals("")){
            tvEarn.setText("0");

        }else {
            tvEarn.setText(session.getData(Constant.EARN));
        }


        recharge_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                startActivity(intent);
            }
        });
        withdrawal_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WithdrawalListActivity.class);
                startActivity(intent);
            }
        });
        activity_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MinorActivity.class);
                startActivity(intent);
            }
        });
        spin_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SpinWheelActivity.class);
                startActivity(intent);
            }
        });
        tvRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                startActivity(intent);
            }
        });



        return rootview;
    }

}