package com.lsa.makein.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.lsa.makein.R;
import com.lsa.makein.helper.Constant;
import com.lsa.makein.helper.Session;

public class CallUsFragment extends Fragment {
    Button contact_us_male;
    ImageView whatsapp_join;
    Session session;


    public CallUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_call_us, container, false);

        contact_us_male = root.findViewById(R.id.contact_us_male);
        whatsapp_join = root.findViewById(R.id.whatsapp_join);
        session = new Session(getActivity());
        contact_us_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(session.getData(Constant.BALANCE)) >= 500){
                    String url = "https://t.me/+FAbeEEwoICA5NzI9";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                else {
                    Toast.makeText(getActivity(), "Minimum Recharge Amount 500 to join Our Special Group", Toast.LENGTH_SHORT).show();
                }
            }
        });

        whatsapp_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=+917694995014";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);


            }
        });
        return root;
    }
}