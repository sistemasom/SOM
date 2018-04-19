package com.som.som;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
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

        ((ImageView) vistaInicio.findViewById(R.id.twitter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WebView) vistaInicio.findViewById(R.id.redes)).loadUrl("https://twitter.com/elpoderdelsom");
            }
        });

        ((ImageView) vistaInicio.findViewById(R.id.facebook)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WebView) vistaInicio.findViewById(R.id.redes)).loadUrl("https://www.facebook.com/elpoderdelsom");
            }
        });

        validarToken();

        return vistaInicio;
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
