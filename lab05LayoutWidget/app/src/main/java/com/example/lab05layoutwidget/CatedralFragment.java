package com.example.lab05layoutwidget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CatedralFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_catedral, container, false);

        ImageView imageView = view.findViewById(R.id.catedralImage);
        ImageUtils.applyCircularMask(imageView, R.drawable.catedral_image_v2, getResources());

        return view;
    }
}
