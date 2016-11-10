package com.taller2.manuel.comunicacion_tcp_auto.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taller2.manuel.comunicacion_tcp_auto.TCP.TcpSocketData;
import com.taller2.manuel.comunicacion_tcp_auto.TCP.TcpSocketManager;
import com.taller2.manuel.comunicacion_tcp_auto.R;

import java.util.ArrayList;

public class BehaviourFragment extends AppCompatDialogFragment implements View.OnClickListener {

    private Toast toast;
    private Button btn_command;

    private final int SPEECH_REQUEST_CODE = 123;
    private TextView speech_output;
    private String comando;
    private Button btn_command_hardc;
    private EditText comando_text;

    public BehaviourFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        TcpSocketData.getInstance().setUI_context(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        if(view != null){
            btn_command = (Button) view.findViewById(R.id.btn_send_voice);
            btn_command_hardc = (Button) view.findViewById(R.id.btn_send_Hardcode);
            speech_output= (TextView) view.findViewById(R.id.tv_comando_enviado);
            comando_text= (EditText) view.findViewById(R.id.tf_comando);
            btn_command_hardc.setOnClickListener(this);
            btn_command.setOnClickListener(this);
            toast = new Toast(getActivity().getApplicationContext());
        }

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false); //Indicamos que este Fragment no tiene su propio menú de opciones
    }

    private static String formatMessageToSend(int value, int motorId){
        String string = String.valueOf(value);
        return ("%" + String.valueOf(string.length() + 1) + String.valueOf(motorId) + string);
    }


    public void onClick(View view) {
        if (view.getId() == R.id.btn_send_voice){
            showGoogleInputDialog();
        }else if (view.getId() == R.id.btn_send_Hardcode){
            TcpSocketManager.sendDataToSocket(comando_text.getText().toString().toLowerCase());
        }

    }


    public void showGoogleInputDialog() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(), "Your device is not supported!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    comando = result.get(0)+'\n';
                    speech_output.setText(comando );
                    TcpSocketManager.sendDataToSocket(comando);
                }
                break;
            }

        }
    }

}