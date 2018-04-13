package com.som.som;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Inicio extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    final String namespace = "http://tempuri.org/";
    final String url = "http://wsmovil.som.com.ar/swsincronizador.asmx";

    View vistaInicio;

    public Inicio() {
    }

    public static Inicio newInstance(String param1, String param2) {
        Inicio fragment = new Inicio();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vistaInicio = inflater.inflate(R.layout.fragment_inicio, container, false);

        TextView tvVersion = (TextView) vistaInicio.findViewById(R.id.tvActualizar);

        validarToken();

        tvVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Verificando versión, aguarde un instante por favor...", Toast.LENGTH_SHORT).show();
                new CountDownTimer(500, 1000) {
                    public void onFinish() {
                        obtenerVersion();
                    }

                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });

        return vistaInicio;
    }

    public void obtenerVersion() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final String Metodo = "VerificarVersion";
        final String accionSoap = "http://tempuri.org/VerificarVersion";

        try {
            SoapObject request = new SoapObject(namespace, Metodo);

            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.setOutputSoapObject(request);
            sobre.dotNet = true;

            HttpTransportSE transporte = new HttpTransportSE(url, 60000);

            transporte.call(accionSoap, sobre);

            final SoapPrimitive resultado = (SoapPrimitive) sobre.getResponse();

            TextView tvVer = (TextView) vistaInicio.findViewById(R.id.copyright);

            String version = tvVer.getText().toString();

            if (!version.contains(resultado.toString())) {
                Toast.makeText(getActivity(), "Está utilizando una versión antigua. Dirijase al Play Store para descargar la versión más reciente.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity(), "Está utilizando la última versión disponible.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "No es posible conectar con el servidor. Compruebe su conexión a internet.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validarToken()
    {
        String texto = "";
        try{
            BufferedReader fin = new BufferedReader(new InputStreamReader(getActivity().openFileInput("config.txt")));
            texto = fin.readLine();
            fin.close();
        }
        catch (Exception ex)
        {
            Toast.makeText(getActivity(), "Error al leer la configuración.", Toast.LENGTH_SHORT).show();
        }

        //FALTA AGREGAR LA VALIDACION DEL TOKEN DISTINTO DE VACIO

        FloatingActionButton btnEnviar = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        if(texto == "" || texto == null) {
            btnEnviar.setEnabled(false);
            Toast.makeText(getActivity(), "No se ha ingresado un código de autorización válido, debe ingresar uno para enviar ofertas.", Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            btnEnviar.setEnabled(true);
            return true;
        }
    }
}
