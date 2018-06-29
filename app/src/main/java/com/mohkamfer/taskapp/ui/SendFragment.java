package com.mohkamfer.taskapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mohkamfer.taskapp.MainActivity;
import com.mohkamfer.taskapp.R;

public class SendFragment extends Fragment {

    private Context mContext;

    private Button mSendButton;
    private TextInputLayout mSendMessage;
    private TextInputLayout mSendTopic;
    private TextView mStatus;

    private Button mHello;
    private Button mBye;

    public SendFragment() {}

    public static SendFragment newInstance() {
        return new SendFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_send, container, false);

        mSendButton = rootView.findViewById(R.id.send_button);
        mSendMessage = rootView.findViewById(R.id.send_message_input_layout);
        mSendTopic = rootView.findViewById(R.id.send_topic_input_layout);
        mStatus = rootView.findViewById(R.id.send_status);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText topicEdit = mSendTopic.getEditText();
                String topic = topicEdit == null ? "" : topicEdit.getText().toString();

                EditText messageEdit = mSendMessage.getEditText();
                String message = messageEdit == null ? "" : messageEdit.getText().toString();

                if (!"".equals(message) && !"".equals(topic)) {
                    ((MainActivity) mContext).publishMessage(topic, message);
                }
            }
        });

        mHello = rootView.findViewById(R.id.send_hello_button);
        mBye = rootView.findViewById(R.id.send_bye_button);

        mHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = "xiot/task";
                String message = "Hello!";
                ((MainActivity) mContext).publishMessage(topic, message);
            }
        });

        mBye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = "xiot/task";
                String message = "Bye!";
                ((MainActivity) mContext).publishMessage(topic, message);
            }
        });

        return rootView;
    }

    public void updateStatus() {
        if (mStatus == null)
            return;

        mStatus.setText(R.string.message_sent);
        mStatus.animate()
                .alpha(1)
                .setDuration(200)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mStatus.animate()
                                .alpha(0)
                                .setDuration(200)
                                .setStartDelay(400)
                                .start();
                    }
                })
                .start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }
}
