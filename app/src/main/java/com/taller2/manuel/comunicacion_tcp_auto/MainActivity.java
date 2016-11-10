package com.taller2.manuel.comunicacion_tcp_auto;

import android.content.DialogInterface;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.taller2.manuel.comunicacion_tcp_auto.Fragments.BehaviourFragment;
import com.taller2.manuel.comunicacion_tcp_auto.Fragments.ConfigurationFragment;
import com.taller2.manuel.comunicacion_tcp_auto.TCP.TcpSocketData;
import com.taller2.manuel.comunicacion_tcp_auto.TCP.TcpSocketManager;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements DialogInterface.OnClickListener{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private Button connect_button;
        private EditText ip_address;
        private EditText port_number;
        private Button disconnect_button;
        private Toast toast;
        private Button default_config_button;
        private Button modify_button;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    return rootView;

                case 2:
                    rootView = inflater.inflate(R.layout.fragment_config, container, false);
                    if (rootView != null) {

                        default_config_button = ((Button) rootView.findViewById(R.id.default_config_button));
                        modify_button = ((Button) rootView.findViewById(R.id.modify_button));
                        connect_button = ((Button) rootView.findViewById(R.id.connect_button));
                        disconnect_button = ((Button) rootView.findViewById(R.id.disconnect_button));
                        ip_address = ((EditText) rootView.findViewById(R.id.ip_address));
                        ip_address.setText(TcpSocketData.getInstance().getIpAddress());
                        port_number = ((EditText) rootView.findViewById(R.id.port_number));
                        port_number.setText(String.valueOf(TcpSocketData.getInstance().getPortNumber()));
                        toast = new Toast(getActivity().getApplicationContext());
                    }
//                    Button btn = (Button) rootView.findViewById(R.id.btn);
//                    btn.setText("Configurar");

                    return rootView;
            }
            return null;

        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            if (default_config_button != null) {
                default_config_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ip_address.setText("192.168.0.4");
                        port_number.setText("80");
                    }
                });
                modify_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ip_address.getText().toString().isEmpty() || port_number.getText().toString().isEmpty())
                            Toast.makeText(view.getContext(), "Error al configurar datos", Toast.LENGTH_SHORT).show();
                        else {
                            TcpSocketData.getInstance().setIpAddress(ip_address.getText().toString());
                            TcpSocketData.getInstance().setPortNumber(Integer.parseInt(port_number.getText().toString()));
                            Toast.makeText(view.getContext(), "Datos configurados correctamente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                connect_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastManager.displayInformationMessage(TcpSocketManager.connectToSocket(), toast, getActivity());
                    }
                });
                disconnect_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastManager.displayInformationMessage(TcpSocketManager.disconnectFromSocket(), toast, getActivity());
                    }
                });
            }
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new BehaviourFragment();
                case 1:
                    return new ConfigurationFragment();
            }
            return null;
//            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Comunicación";
                case 1:
                    return "Configuración";
            }
            return null;
        }
    }
}
