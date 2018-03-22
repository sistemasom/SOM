package com.som.som;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Inventario extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    final String namespace = "http://tempuri.org/";
    final String url = "http://wss3.som.com.ar/swsincronizador.asmx";

    private class itemInventario
    {
        String codigo;
        String codigoCompleto;
        String Calle;
        String moneda;
        String importe;
        String operacion;
        String vencida;
    }

    private class Propiedad
    {
        String codigo;
        String codCompleto;
        String tipoProd;
        String SubtipoProd;
        String SupCub;
        String SupTot;
        String Destacable;
        String Operacion;
        String SubtipoOp;
        String Moneda;
        String Precio;
        String unidadMedida;
        String Publica;
        String Calle;
        String Altura;
        String Piso;
        String Unidad;
        String Provincia;
        String Barrio;
        String Ref;
        String latitud;
        String longitud;
        String Pais;
        String reservada;
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
                Toast.makeText(getContext(),"Cargando inventario.\nAguarde un instante por favor...",Toast.LENGTH_SHORT).show();
                new CountDownTimer(1000, 1000) {
                    public void onFinish() {
                        cargarInventario(getToken());
                    }

                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
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
        Button eliminarFotos = (Button) getActivity().findViewById(R.id.limpiarfotos);
        if(alta) {
            limpiarFormulario();
        }
        else
        {
            cargarFormulario(pos);
        }
        eliminarFotos.performClick();
        ViewPager vpPager = (ViewPager) getActivity().findViewById(R.id.container);
        vpPager.setCurrentItem(2);
    }

    private void limpiarFormulario() {
        Spinner producto = (Spinner) getActivity().findViewById(R.id.cbTipoProd);
        producto.setEnabled(true);
        Spinner piso = (Spinner) getActivity().findViewById(R.id.cbPiso);
        piso.setSelection(0);
        Spinner unidad = (Spinner) getActivity().findViewById(R.id.cbUnidad);
        unidad.setSelection(0);
        TextView supCub = (TextView) getActivity().findViewById(R.id.etSupCub);
        supCub.setText("");
        TextView codigo = (TextView) getActivity().findViewById(R.id.codigoOferta);
        codigo.setText("0");
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
        TextView ref = (TextView) getActivity().findViewById(R.id.etEntreCalles);
        ref.setText("");
        TextView pais = (TextView) getActivity().findViewById(R.id.etPais);
        pais.setText("Argentina");
        TextView prov = (TextView) getActivity().findViewById(R.id.etProvincia);
        prov.setText("CAPITAL FEDERAL");
        TextView barrio = (TextView) getActivity().findViewById(R.id.etBarrio);
        barrio.setText("");
    }

    private void cargarFormulario(int pos) {
        final String Metodo = "BuscarPorCodigoMovil";
        final String accionSoap = "http://tempuri.org/BuscarPorCodigoMovil";

        itemInventario prop = propiedades.get(pos);

        Toast.makeText(getContext(),prop.codigo,Toast.LENGTH_SHORT).show();

        try {

            // Modelo el request
            SoapObject request = new SoapObject(namespace, Metodo);

            request.addProperty("codigo", prop.codigoCompleto);

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

            Propiedad propie = new Propiedad();

            //Armo los datos de la oferta
            propie.codCompleto = prop.codigoCompleto;
            propie.Calle = resultado.getPropertyAsString(0).replace("anyType{}","");
            propie.Altura = resultado.getPropertyAsString(1).replace("anyType{}","");
            propie.Piso = resultado.getPropertyAsString(2).replace("anyType{}","");
            propie.Unidad = resultado.getPropertyAsString(3).replace("anyType{}","");
            propie.tipoProd = resultado.getPropertyAsString(4).replace("anyType{}","");
            propie.SubtipoProd = resultado.getPropertyAsString(5).replace("anyType{}","");
            propie.Operacion = resultado.getPropertyAsString(6).replace("anyType{}","");
            propie.SubtipoOp = resultado.getPropertyAsString(7).replace("anyType{}","");
            propie.Moneda = resultado.getPropertyAsString(8).replace("anyType{}","");
            propie.Precio = resultado.getPropertyAsString(9).replace("anyType{}","");
            propie.codigo = resultado.getPropertyAsString(10).replace("anyType{}","");
            propie.SupCub = resultado.getPropertyAsString(11).replace("anyType{}","");
            propie.SupTot = resultado.getPropertyAsString(12).replace("anyType{}","");
            propie.Destacable = resultado.getPropertyAsString(13).replace("anyType{}","");
            propie.unidadMedida = resultado.getPropertyAsString(14).replace("anyType{}","");
            propie.Publica = resultado.getPropertyAsString(15).replace("anyType{}","");
            propie.Provincia = resultado.getPropertyAsString(16).replace("anyType{}","");
            propie.Barrio = resultado.getPropertyAsString(17).replace("anyType{}","");
            propie.latitud = resultado.getPropertyAsString(18).replace("anyType{}","");
            propie.longitud = resultado.getPropertyAsString(19).replace("anyType{}","");
            propie.Ref = resultado.getPropertyAsString(20).replace("anyType{}","");
            propie.Pais = resultado.getPropertyAsString(21).replace("anyType{}","");
            propie.reservada = resultado.getPropertyAsString(22).replace("anyType{}","");

            TextView codigo = (TextView) getActivity().findViewById(R.id.codigoOferta);
            codigo.setText(propie.codCompleto);
            EditText supCub = (EditText) getActivity().findViewById(R.id.etSupCub);
            supCub.setText(propie.SupCub);
            EditText supTot = (EditText) getActivity().findViewById(R.id.etSupTot);
            supTot.setText(propie.SupTot);
            EditText destacable = (EditText) getActivity().findViewById(R.id.etDestacable);
            destacable.setText(propie.Destacable);
            EditText precio = (EditText) getActivity().findViewById(R.id.etPrecio);
            precio.setText(propie.Precio);
            EditText calle = (EditText) getActivity().findViewById(R.id.etCalle);
            calle.setText(propie.Calle);
            EditText altura = (EditText) getActivity().findViewById(R.id.etAltura);
            altura.setText(propie.Altura);
            EditText barrio = (EditText) getActivity().findViewById(R.id.etBarrio);
            barrio.setText(propie.Barrio);
            EditText ref = (EditText) getActivity().findViewById(R.id.etEntreCalles);
            ref.setText(propie.Ref);

            TextView provincia = (TextView) getActivity().findViewById(R.id.etProvincia);
            provincia.setText(propie.Provincia);

            Spinner operacion = (Spinner) getActivity().findViewById(R.id.cbOperacion);
            operacion.setSelection(obtenerElementosSpinner(operacion,propie.Operacion),true);

            Spinner suboperacion = (Spinner) getActivity().findViewById(R.id.cbSubOperacion);
            suboperacion.setSelection(obtenerElementosSpinner(suboperacion,propie.SubtipoOp),true);

            Spinner moneda = (Spinner) getActivity().findViewById(R.id.cbMoneda);
            moneda.setSelection(obtenerElementosSpinner(moneda,propie.Moneda),true);

            Spinner piso = (Spinner) getActivity().findViewById(R.id.cbPiso);
            piso.setSelection(obtenerElementosSpinner(piso,propie.Piso),true);

            Spinner unidad = (Spinner) getActivity().findViewById(R.id.cbUnidad);
            unidad.setSelection(obtenerElementosSpinner(unidad,propie.Unidad),true);

            final Spinner producto = (Spinner) getActivity().findViewById(R.id.cbTipoProd);
            producto.setSelection(obtenerElementosSpinner(producto,propie.tipoProd),true);
            producto.setEnabled(false);

            final Spinner subprod = (Spinner) getActivity().findViewById(R.id.cbSubTipoProd);
            subprod.setSelection(obtenerElementosSpinner(subprod,propie.SubtipoProd),true);

            RadioButton supm2 = (RadioButton) getActivity().findViewById(R.id.rbM2);
            supm2.setChecked(true);

            RadioButton supHa = (RadioButton) getActivity().findViewById(R.id.rbHa);
            supHa.setChecked(false);

            Switch reservada = (Switch) getActivity().findViewById(R.id.reservada);

            if(propie.reservada.toUpperCase().equals("SI")) {
                reservada.setChecked(true);
            }

            if(!propie.unidadMedida.toUpperCase().equals("M2"))
            {
                supm2.setChecked(false);
                supHa.setChecked(true);
            }

            TextView lat = (TextView) getActivity().findViewById(R.id.latitud);
            lat.setText("");
            TextView longi = (TextView) getActivity().findViewById(R.id.longitud);
            longi.setText("");

            if(propie.latitud != null && propie.latitud != "") {
                lat.setText(propie.latitud);
                longi.setText(propie.longitud);
                WebView mapa = (WebView) getActivity().findViewById(R.id.webMapa);
                mapa.setWebViewClient(new WebViewClient());
                mapa.getSettings().setJavaScriptEnabled(true);
                String url = "http://sistema.som.com.ar/MapaApp.html?latitud=" + propie.latitud + "&longitud=" + propie.longitud;
                mapa.loadUrl(url);
            }
        }
        catch (Exception e) {
            Toast.makeText(getActivity(),"No es posible conectar con el servidor. Compruebe su conexión a internet.", Toast.LENGTH_SHORT).show();
        }
    }

    public int obtenerElementosSpinner(Spinner spinner, String cadena) {
        int posicion = 0;
        Adapter adapter = spinner.getAdapter();
        int n = adapter.getCount();
        for (int i = 0; i < n; i++) {
            String valor = (String) adapter.getItem(i);
            if(cadena.equals(valor))
            {
                posicion = i;
            }
        }
        return posicion;
    }

    private void cargarInventario(final String token) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final String Metodo = "BuscarInventarioMovil";
        final String accionSoap = "http://tempuri.org/BuscarInventarioMovil";

        final ListView tabla = (ListView) vistaInventario.findViewById(R.id.listaInventario);

        ArrayList<String> items = new ArrayList<String>();

        try {

            // Modelo el request
            SoapObject request = new SoapObject(namespace, Metodo);

            request.addProperty("token", token);

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

            ImageView sinresultados = (ImageView) vistaInventario.findViewById(R.id.sinresultados);

            if(resultado.getPropertyCount() != 0) {
                for (int i = 0; i < resultado.getPropertyCount(); i++) {

                    SoapObject oferta = (SoapObject) resultado.getProperty(i);

                    itemInventario prop = new itemInventario();

                    prop.Calle = oferta.getPropertyAsString(0);
                    prop.operacion = oferta.getPropertyAsString(1);
                    prop.moneda = oferta.getPropertyAsString(2);
                    prop.importe = oferta.getPropertyAsString(3);
                    prop.codigo = oferta.getPropertyAsString(4);
                    prop.codigoCompleto = oferta.getPropertyAsString(5);
                    prop.vencida = oferta.getPropertyAsString(6);
                    propiedades.add(prop);

                    if(prop.vencida.contains("true")) {
                        items.add("♦ " + prop.codigo + " - " + prop.operacion + " " + prop.moneda + " " + prop.importe + "\n" + prop.Calle);
                    }
                    else
                    {
                        items.add(prop.codigo + " - " + prop.operacion + " " + prop.moneda + " " + prop.importe + "\n" + prop.Calle);
                    }

                    sinresultados.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                sinresultados.setVisibility(View.VISIBLE);
            }

            final ArrayAdapter<String> array = new ArrayAdapter<String>(getActivity(), R.layout.itemlista,R.id.item,items);

            tabla.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    final String codigo = propiedades.get(position).codigoCompleto;
                    final String cod = propiedades.get(position).codigo;
                    builder.setMessage("¿Desea eliminar la oferta " + cod + "?")
                            .setTitle("Eliminar")
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                            .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    //Creo un JSON de baja de la oferta
                                    JSONObject jsonOferta = new JSONObject();

                                    try {
                                        jsonOferta.put("Token", token);
                                        jsonOferta.put("Baja", codigo);
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    final Enviar fragEnviar = Enviar.getInstance(getActivity().getSupportFragmentManager(),"https://google.com.ar");

                                    array.remove(array.getItem(position));
                                    propiedades.remove(position);
                                    array.notifyDataSetChanged();

                                    Toast.makeText(getContext(), "Enviando baja de la oferta " + cod + "...", Toast.LENGTH_SHORT).show();
                                    fragEnviar.dataSend = jsonOferta.toString();

                                    fragEnviar.startUpload(true, true);
                                }
                            });
                    builder.create().show();
                    return true;
                }
            });

            tabla.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String vencida = propiedades.get(position).vencida;

                    if(vencida.contains("true")) {
                        Toast.makeText(getActivity(),"Esta oferta se encuentra vencida.", Toast.LENGTH_LONG).show();
                    }
                    mostrarCampos(false,position);
                }
            });

            tabla.setAdapter(array);

        } catch (Exception e) {
            Toast.makeText(getActivity(),"No es posible conectar con el servidor. Compruebe su conexión a internet.", Toast.LENGTH_SHORT).show();
        }
    }

    final String getToken()
    {
        String texto = "";
        try{
            BufferedReader fin = new BufferedReader(new InputStreamReader(getActivity().openFileInput("config.txt")));
            texto = fin.readLine();
            fin.close();
        }
        catch (Exception ex)
        {
            Toast.makeText(getActivity(), "No se ha ingresado un código de autorización, debe ingresar uno para continuar.", Toast.LENGTH_SHORT).show();
        }

        return texto;
    }
}
