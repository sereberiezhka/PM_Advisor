package com.example.pm_advisor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class RisksFragment extends Fragment {
    public static RisksFragment newInstance(String cons) {
        RisksFragment fragment = new RisksFragment();
        Bundle args = new Bundle();
        args.putString("cons", cons);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_risks, container, false);
        TextView tvCons = view.findViewById(R.id.tvCons);
        if (getArguments() != null) {
            tvCons.setText(getArguments().getString("cons"));
        }
        return view;
    }
}