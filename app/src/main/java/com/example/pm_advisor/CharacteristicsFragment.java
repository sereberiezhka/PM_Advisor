package com.example.pm_advisor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class CharacteristicsFragment extends Fragment {
    public static CharacteristicsFragment newInstance(String pros) {
        CharacteristicsFragment fragment = new CharacteristicsFragment();
        Bundle args = new Bundle();
        args.putString("pros", pros);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_characteristics, container, false);
        TextView tvPros = view.findViewById(R.id.tvPros);
        if (getArguments() != null) {
            tvPros.setText(getArguments().getString("pros"));
        }
        return view;
    }
}