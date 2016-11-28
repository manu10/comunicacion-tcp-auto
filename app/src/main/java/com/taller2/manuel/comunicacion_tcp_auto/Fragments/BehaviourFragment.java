package com.taller2.manuel.comunicacion_tcp_auto.Fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.taller2.manuel.comunicacion_tcp_auto.TCP.TcpSocketData;
import com.taller2.manuel.comunicacion_tcp_auto.TCP.TcpSocketManager;
import com.taller2.manuel.comunicacion_tcp_auto.R;

import java.util.ArrayList;

public class BehaviourFragment extends AppCompatDialogFragment implements View.OnClickListener {

    private final int SPEECH_REQUEST_CODE = 123;
    private TextView speech_output;
    private EditText comando_text;

    public BehaviourFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //configura el entorno para que permita correr los threads que necesite la comunicacion TCP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //se setea el contexto al socket TCP
        TcpSocketData.getInstance().setUI_context(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        if(view != null){
            ImageButton btn_command_ret = (ImageButton) view.findViewById(R.id.btn_retroc);
            ImageButton btn_command_ava = (ImageButton) view.findViewById(R.id.btn_avanzar);
            ImageButton btn_command_izq = (ImageButton) view.findViewById(R.id.btn_izq);
            ImageButton btn_command_der = (ImageButton) view.findViewById(R.id.btn_dere);
            Button btn_command = (Button) view.findViewById(R.id.btn_send_voice);
            Button btn_command_hardc = (Button) view.findViewById(R.id.btn_send_Hardcode);
            Button btn_emeg_stop = (Button) view.findViewById(R.id.btn_emeg_stop);
            speech_output= (TextView) view.findViewById(R.id.tv_comando_enviado);
            comando_text= (EditText) view.findViewById(R.id.tf_comando);
            btn_command_hardc.setOnClickListener(this);
            btn_emeg_stop.setOnClickListener(this);
            btn_command.setOnClickListener(this);
            btn_command_ava.setOnClickListener(this);
            btn_command_ret.setOnClickListener(this);
            btn_command_izq.setOnClickListener(this);
            btn_command_der.setOnClickListener(this);
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
    //Listener de los botones
    public void onClick(View view) {
        if (view.getId() == R.id.btn_send_voice){
            showGoogleInputDialog();
        }else if (view.getId() == R.id.btn_send_Hardcode){
            TcpSocketManager.sendDataToSocket(comando_text.getText().toString().toLowerCase());
        }else if (view.getId() == R.id.btn_avanzar){
            TcpSocketManager.sendDataToSocket(getContext().getString(R.string.comando_avanzar));
        }else if (view.getId() == R.id.btn_retroc){
            TcpSocketManager.sendDataToSocket(getContext().getString(R.string.comando_retroceder));
        }else if (view.getId() == R.id.btn_dere){
            TcpSocketManager.sendDataToSocket(getContext().getString(R.string.comando_der));
        }else if (view.getId() == R.id.btn_izq){
            TcpSocketManager.sendDataToSocket(getContext().getString(R.string.comando_izq));
        }else if (view.getId() == R.id.btn_emeg_stop){
            TcpSocketManager.sendDataToSocket(getContext().getString(R.string.comando_frenar));
        }


    }

    //metodo para utilizar el servicio de reconocimiento de voz de google
    public void showGoogleInputDialog() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(), "Su dispositivo no soporta reconocimiento de voz! :/",
                    Toast.LENGTH_SHORT).show();
        }
    }
    //Metodo que se utiliza el resultado de ajecutar el reconocimiento de voz
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String comando = result.get(0) + '\n';
                    speech_output.setText(result.get(0));
                    TcpSocketManager.sendDataToSocket(comando);
                }
                break;
            }

        }
    }

}