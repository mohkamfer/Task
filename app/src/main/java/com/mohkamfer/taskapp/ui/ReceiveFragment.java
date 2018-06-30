package com.mohkamfer.taskapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mohkamfer.taskapp.R;

public class ReceiveFragment extends Fragment {

    private TextView mBody;

    public ReceiveFragment() {}

    public static ReceiveFragment newInstance() {
        return new ReceiveFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive, container, false);
        mBody = rootView.findViewById(R.id.receive_body);
        return rootView;
    }

    public void append(String message) {
        mBody.append(String.format("%s\n", message));
    }
}
