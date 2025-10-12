package com.example.lab4_fragments.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.lab4_fragments.R;

public class StartFragment extends Fragment {

    public StartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        rootView.findViewById(R.id.btnLogin).setOnClickListener(v -> goToLogin());
        rootView.findViewById(R.id.btnRegister1).setOnClickListener(v -> goToRegister1());

        return rootView;
    }

    private void goToLogin() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    private void goToRegister1() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new Register1Fragment())
                .addToBackStack(null)
                .commit();
    }
}
