package com.taller2.manuel.comunicacion_tcp_auto.TCP;


import android.os.AsyncTask;

import android.util.Log;

import android.widget.TextView;

import com.taller2.manuel.comunicacion_tcp_auto.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class TcpAsyncReceive extends AsyncTask<Void, String, Void> {

    private Socket socket;


    public TcpAsyncReceive(Socket socket){
        this.socket = socket;
    }

    protected Void doInBackground(Void... params) {
        while (TcpSocketData.getInstance().canReceiveData()){
            try {
                receiveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void onProgressUpdate(String... values){
        // Actualiza el valor de la distancia en la GUI
          TextView displayDist= (TextView) TcpSocketData.getInstance().getUI_context().findViewById(R.id.tv_dist);
          displayDist.setText("Distancia: "+values[0]+"cm");

    }

    private void receiveData() throws IOException {
        String incomingMessage = "";
        Character currentCharacter;
        int limitCounter = 0;
        if (TcpSocketData.getInstance().canReceiveData()) {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()), 50);
            while (TcpSocketData.getInstance().canReceiveData()) {
                //Cuando se reciben datos se valida que lleguen con el formato requerido "$XXXX$"
                if ((currentCharacter = (char) input.read()) != null) {
                    if (currentCharacter == '$') {
                        limitCounter++;
                    } else {
                        incomingMessage += currentCharacter;
                    }
                    if (limitCounter == 2) {
                        publishProgress(processIncomingMessage(incomingMessage));
                        limitCounter = 0;
                        incomingMessage = "";
                    }
                }
            }
        }
        cancel(true);
    }

    private String processIncomingMessage(String incomingMessage){
        // Procesamiento de string de entrada
        String command = incomingMessage.substring(0, 5);
        String output[] = new String[2];
        Log.v("LOG_MSG RECEIVED",incomingMessage);
        switch (command){
            case "DISTA":
                output = incomingMessage.split(" ");
                break;
            default:
                output[1] = " ... ";
                break;
        }
        return output[1];

    }

}
