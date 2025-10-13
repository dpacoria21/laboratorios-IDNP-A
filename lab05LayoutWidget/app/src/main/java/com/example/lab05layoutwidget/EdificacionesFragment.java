package com.example.lab05layoutwidget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class EdificacionesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edificaciones, container, false);

        ImageView catedralImageView = rootView.findViewById(R.id.catedralImage);
        ImageUtils.applyCircularMask(catedralImageView, R.drawable.catedral_image_v2, getResources());

        ImageView claustroImageView = rootView.findViewById(R.id.claustroImage);
        ImageUtils.applyCircularMask(claustroImageView, R.drawable.claustro_image_v2, getResources());

        rootView.findViewById(R.id.catedralLayout).setOnClickListener(v -> navigateToCatedralFragment());
        rootView.findViewById(R.id.claustroLayout).setOnClickListener(v -> navigateToClaustroFragment());

        return rootView;
    }

    private void navigateToCatedralFragment() {
        Fragment catedralFragment = new CatedralFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, catedralFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToClaustroFragment() {
        Fragment claustroFragment = new ClaustroFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, claustroFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

