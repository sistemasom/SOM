package com.som.som;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class Inventario extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    final String namespace = "http://tempuri.org/";
    final String url = "http://wsdatos.som.com.ar/swsincronizador.asmx";

    private class itemInventario
    {
        String codigo;
        String Calle;
    }

    private class Propiedad
    {
        String codigo;
        String tipoProd;
        String SubtipoProd;
        String SupCub;
        String SupTot;
        String Destacable;
        String Operacion;
        String SubtipoOp;
        String Moneda;
        String Precio;
        String AceptaPermutar;
        String Publica;
        String Calle;
        String Altura;
        String Piso;
        String Unidad;
        String Provincia;
        String Localidad;
        String Ref;
        String Coordenadas;
    }

    ArrayList<itemInventario> propiedades = new ArrayList<itemInventario>();

    View vistaInventario;

    public Inventario() {
    }

    public static Inventario newInstance(String param1, String param2) {
        Inventario fragment = new Inventario();
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

        vistaInventario = inflater.inflate(R.layout.fragment_inventario, container, false);

        Button btnInventario =(Button) vistaInventario.findViewById(R.id.btnInventario);

        btnInventario.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Aguarde un instante por favor...",Toast.LENGTH_SHORT).show();
                cargarInventario("3");
            }
        });

        Button btnAlta =(Button) vistaInventario.findViewById(R.id.btnAlta);

        btnAlta.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mostrarCampos(true,0);
            }
        });

        return vistaInventario;
    }

    private void mostrarCampos(boolean alta, int pos)
    {
        if(alta) {
            limpiarFormulario();
        }
        else
        {
            cargarFormulario(pos);
        }
        ViewPager vpPager = (ViewPager) getActivity().findViewById(R.id.container);
        vpPager.setCurrentItem(2);
    }

    private void limpiarFormulario() {
        TextView supCub = (TextView) getActivity().findViewById(R.id.etSupCub);
        supCub.setText("");
        TextView supTot = (TextView) getActivity().findViewById(R.id.etSupTot);
        supTot.setText("");
        TextView destacable = (TextView) getActivity().findViewById(R.id.etDestacable);
        destacable.setText("");
        TextView precio = (TextView) getActivity().findViewById(R.id.etPrecio);
        precio.setText("");
        TextView calle = (TextView) getActivity().findViewById(R.id.etCalle);
        calle.setText("");
        TextView altura = (TextView) getActivity().findViewById(R.id.etAltura);
        altura.setText("");
        TextView localidad = (TextView) getActivity().findViewById(R.id.etLocalidad);
        localidad.setText("");
        TextView ref = (TextView) getActivity().findViewById(R.id.etEntreCalles);
        ref.setText("");
    }

    private void cargarFormulario(int pos)
    {
        itemInventario prop = propiedades.get(pos);
        String dato = prop.Calle;

        TextView supCub = (TextView) getActivity().findViewById(R.id.etSupCub);
        supCub.setText("");
        TextView supTot = (TextView) getActivity().findViewById(R.id.etSupTot);
        supTot.setText("");
        TextView destacable = (TextView) getActivity().findViewById(R.id.etDestacable);
        destacable.setText(dato);
        TextView precio = (TextView) getActivity().findViewById(R.id.etPrecio);
        precio.setText("");
        TextView calle = (TextView) getActivity().findViewById(R.id.etCalle);
        calle.setText("");
        TextView altura = (TextView) getActivity().findViewById(R.id.etAltura);
        altura.setText("");
        TextView localidad = (TextView) getActivity().findViewById(R.id.etLocalidad);
        localidad.setText("");
        TextView ref = (TextView) getActivity().findViewById(R.id.etEntreCalles);
        ref.setText("");
    }

    private void cargarInventario(String token) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final String Metodo = "BuscarInventario";
        final String accionSoap = "http://tempuri.org/BuscarInventario";

        final ListView tabla = (ListView) vistaInventario.findViewById(R.id.listaInventario);

        ArrayList<String> items = new ArrayList<String>();

        try {

            // Modelo el request
            SoapObject request = new SoapObject(namespace, Metodo);

            request.addProperty("idSucursal", token);

            // Modelo el Sobre
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.setOutputSoapObject(request);
            sobre.dotNet = true;

            // Modelo el transporte
            HttpTransportSE transporte = new HttpTransportSE(url,60000);

            // Llamada
            transporte.call(accionSoap, sobre);

            // Resultado
            final SoapObject resultado = (SoapObject)sobre.getResponse();

            for(int i=0; i<resultado.getPropertyCount(); i++) {

                SoapObject oferta =  (SoapObject) resultado.getProperty(i);
                itemInventario prop = new itemInventario();

                prop.codigo = oferta.getPropertyAsString(5);
                prop.Calle = oferta.getPropertyAsString(0);
                propiedades.add(prop);

                items.add(oferta.getPropertyAsString(5) + " - " + oferta.getPropertyAsString(0));
            }

            tabla.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mostrarCampos(false,position);
                }
            });

            ArrayAdapter<String> array = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, items);

            tabla.setAdapter(array);

        } catch (Exception e) {
            Toast.makeText(getContext(),"No es posible conectar con el servidor. Compruebe su conexión a internet.", Toast.LENGTH_SHORT);
        }
    }
}
