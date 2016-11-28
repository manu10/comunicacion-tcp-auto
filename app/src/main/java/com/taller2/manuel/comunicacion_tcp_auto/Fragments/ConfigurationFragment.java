package com.taller2.manuel.comunicacion_tcp_auto.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.taller2.manuel.comunicacion_tcp_auto.ToastManager;
import com.taller2.manuel.comunicacion_tcp_auto.R;
import com.taller2.manuel.comunicacion_tcp_auto.TCP.TcpSocketData;
import com.taller2.manuel.comunicacion_tcp_auto.TCP.TcpSocketManager;

public class ConfigurationFragment extends AppCompatDialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText ip_address;
    private EditText port_number;
    private Button default_config_button;
    private Button modify_button;
    private Button connect_button;
    private Button disconnect_button;
    private Toast toast;
    private Spinner spinner;

    public ConfigurationFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_config, container, false);

        if (v != null) {

            default_config_button = ((Button) v.findViewById(R.id.default_config_button));
            modify_button = ((Button) v.findViewById(R.id.modify_button));
            connect_button = ((Button) v.findViewById(R.id.connect_button));
            disconnect_button = ((Button) v.findViewById(R.id.disconnect_button));
            ip_address = ((EditText) v.findViewById(R.id.ip_address));
            ip_address.setText(TcpSocketData.getInstance().getIpAddress());
            port_number = ((EditText) v.findViewById(R.id.port_number));
            port_number.setText(String.valueOf(TcpSocketData.getInstance().getPortNumber()));
            toast = new Toast(getActivity().getApplicationContext());
            spinner = (Spinner) v.findViewById(R.id.spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.options_obj_det, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
        }

        return v;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        default_config_button.setOnClickListener(this);
        modify_button.setOnClickListener(this);
        connect_button.setOnClickListener(this);
        disconnect_button.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false); //Indicamos que este Fragment no tiene su propio menu de opciones
    }

    /* CLick listener */

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect_button:
                ToastManager.displayInformationMessage(TcpSocketManager.connectToSocket(), toast, getActivity());
                break;
            case R.id.disconnect_button:
                ToastManager.displayInformationMessage(TcpSocketManager.disconnectFromSocket(), toast, getActivity());
                break;
            case R.id.default_config_button:
                ip_address.setText(R.string.IP_default);
                port_number.setText(R.string.PORT_default);
                break;
            case R.id.modify_button:
                if (ip_address.getText().toString().isEmpty() || port_number.getText().toString().isEmpty())
                    Toast.makeText(view.getContext(), "Error al configurar datos", Toast.LENGTH_SHORT).show();
                else{
                    TcpSocketData.getInstance().setIpAddress(ip_address.getText().toString());
                    TcpSocketData.getInstance().setPortNumber(Integer.parseInt(port_number.getText().toString()));
                    Toast.makeText(view.getContext(), "Datos configurados correctamente", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Comandos en mayuscula significan que no se van a ejecutar sino guardar configuracion
        String comando = (position == 0) ? getContext().getString(R.string.comando_obst_frenar) : getContext().getString(R.string.comando_obst_cambiar);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.option_selected_key), comando);
        editor.apply();
        TcpSocketManager.sendDataToSocket(comando);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}