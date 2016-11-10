package com.taller2.manuel.comunicacion_tcp_auto.TCP;

import android.os.AsyncTask;
import android.widget.ProgressBar;
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
        // Inicialización de elementos de GUI
//        TextView displayMessageMotor1 = (TextView) TcpSocketData.getInstance().getUI_context().findViewById(R.id.incoming_message);
//        TextView displayMessageMotor2 = (TextView) TcpSocketData.getInstance().getUI_context().findViewById(R.id.incoming_message2);
//        ProgressBar[] progressBars = new ProgressBar[2];
//        progressBars[0] = (ProgressBar) TcpSocketData.getInstance().getUI_context().findViewById(R.id.motor_0_progress);
//        progressBars[1] = (ProgressBar) TcpSocketData.getInstance().getUI_context().findViewById(R.id.motor_1_progress);
//        TextView distancia = (TextView) TcpSocketData.getInstance().getUI_context().findViewById(R.id.dist_textView);
        // Lógica de actualización de elements de GUI
//        switch (values[0].charAt(0)){
//            case 'S':
//                if ((values[0].charAt(1) == '0') && (displayMessageMotor1 != null))
//                    displayMessageMotor1.setText(values[0].substring(1,values[0].length()));
//                else if ((values[0].charAt(1) == '1') && (displayMessageMotor2 != null))
//                    displayMessageMotor2.setText(values[0].substring(1,values[0].length()));
//                break;
//            case 'M':
//                if ((values[0].charAt(1) == '0') && (progressBars[0] != null)) {
//                    addValueToRpmCurve(values[0]);
//                    progressBars[0].incrementProgressBy(1);
//                }
//                else if ((values[0].charAt(1) == '1') && (progressBars[1] != null)) {
//                    addValueToRpmCurve(values[0]);
//                    progressBars[1].incrementProgressBy(1);
//                }
//                break;
//            case 'D':
//                distancia.setText(values[0]);
//                break;
//        }
    }

    private void receiveData() throws IOException {
        String incomingMessage = "";
        Character currentCharacter;
        int limitCounter = 0;
        if (TcpSocketData.getInstance().canReceiveData()) {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()), 50);
            while (TcpSocketData.getInstance().canReceiveData()){
                if ((currentCharacter = (char) input.read()) != null){
                    if (currentCharacter == '$')
                        limitCounter++;
                    else {
                        incomingMessage += currentCharacter;
                    }
                    if (limitCounter == 2){
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
        String output;
        switch (command){
            case "DISTA":
                output = incomingMessage;
                break;
            case "SPEED":
                output = processMotorVelocity(incomingMessage.charAt(5), incomingMessage);
                break;

            default:
                output = " ... ";
                break;
        }
        return output;

    }

    private String processMotorVelocity(char motorId, String message){
        String output, value, dataType;
        output = " Motor ";
        value = message.substring(7,message.length());
        dataType = "";
        switch (message.charAt(6)){
            case '0':
                dataType = "RPM";
                break;
            case '1':
                dataType = "RPS";
                break;
        }
        // Construcción de mensaje para mostrar
        return ("S" + motorId + dataType + output + motorId + ": " + value);
    }


}
