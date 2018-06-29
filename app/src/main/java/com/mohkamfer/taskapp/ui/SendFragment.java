package com.mohkamfer.taskapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mohkamfer.taskapp.MainActivity;
import com.mohkamfer.taskapp.R;

public class SendFragment extends Fragment {

    private Button mSendButton;
    private TextInputLayout mSendMessage;
    private TextInputLayout mSendTopic;
    private TextView mStatus;

    public SendFragment() {}

    public static Fragment newInstance() {
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
                    ((MainActivity) SendFragment.this.getActivity()).publishMessage(topic, message);
                }
            }
        });

        return rootView;
    }
}
