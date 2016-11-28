package com.taller2.manuel.comunicacion_tcp_auto.TCP;


import java.io.IOException;

import java.net.Socket;

public class TcpSocketManager {

    public static String connectToSocket(){
        String ipAddress = TcpSocketData.getInstance().getIpAddress();
        String result;
        boolean connected = false;
        int portNumber = TcpSocketData.getInstance().getPortNumber();
        if (!isSocketConnected()){
            if (TcpSocketData.getInstance().isDataCompleted()){
                try {
                    TcpSocketData.getInstance().setSocket(new Socket(ipAddress, portNumber));
                    result = "Conexión establecida con " + ipAddress + ":" + String.valueOf(portNumber) + ".";
                    connected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    result = "Error al intentar establecer conexión: destino inalcanzable";
                }
                if (connected){
                    TcpSocketData.getInstance().startDataRequest();
                    TcpAsyncReceive tcpCommunication = new TcpAsyncReceive(TcpSocketData.getInstance().getSocket());
                    tcpCommunication.executeOnExecutor(TcpAsyncReceive.THREAD_POOL_EXECUTOR);
                }
            }
            else
                result = "Error al intentar establecer conexión. Configure IP y presione GUARDAR.";
            return result;
        }
        else
            return "Ya existe una conexión. Es necesario cerrar la conexión existente.";
    }

    public static String disconnectFromSocket(){
        String result;
        if (isSocketConnected()){
            try {
                TcpSocketData.getInstance().stopDataRequest();
                Socket socket = TcpSocketData.getInstance().getSocket();
                socket.shutdownOutput();
                socket.close();
                if (!isSocketConnected())
                    result = "Conexión cerrada exitosamente.";
                else
                    result = "No se pudo cerrar la conexion.";
            } catch (IOException e) {
                e.printStackTrace();
                result = "Error al intentar finalizar conexión.";
            }
            return result;
        }
        else
            return "No existe conexión.";
    }

    public static String sendDataToSocket(String message){
        if (isSocketConnected()){
            Socket socket = TcpSocketData.getInstance().getSocket();
            TcpAsyncSend tcpCommunication = new TcpAsyncSend(socket,message);
            tcpCommunication.executeOnExecutor(TcpAsyncSend.THREAD_POOL_EXECUTOR);
            return "";
        }
        else
            return "Datos no enviados por no existir conexión.";
    }
//
//    public static char receiveCharFromSocket(){
//        if (isSocketConnected()){
//            char result = 0;
//            Socket socket = TcpSocketData.getInstance().getSocket();
//            BufferedReader input;
//            try {
//                input = new BufferedReader(new InputStreamReader(socket.getInputStream()), 50);
//                try {
//                    result = (char) input.read();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return result;
//        }
//        return 0;
//    }

    private static boolean isSocketConnected(){
        Socket socket = TcpSocketData.getInstance().getSocket();
        if (socket != null){
            if (!socket.isClosed())
                return true;
        }
        return false;
    }

}
