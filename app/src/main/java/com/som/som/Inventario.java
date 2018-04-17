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
import android.widget.FrameLayout;
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
    final String url = "http://wsmovil.som.com.ar/swsincronizador.asmx";

    private class itemInventario
    {
        String codigo;
        String codigoCompleto;
        String Calle;
        String moneda;
        String importe;
        String operacion;
        String vencida;
        String id;
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
        String atributos;
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

        Button btnPropio =(Button) vistaInventario.findViewById(R.id.btnPropio);
        Button btnVigente =(Button) vistaInventario.findViewById(R.id.btnVigente);

        btnVigente.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Cargando inventario.\nAguarde un instante por favor...",Toast.LENGTH_SHORT).show();
                new CountDownTimer(1000, 1000) {
                    public void onFinish() {
                        cargarInventario(getToken(),false);
                    }

                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });

        btnPropio.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Cargando inventario.\nAguarde un instante por favor...",Toast.LENGTH_SHORT).show();
                new CountDownTimer(1000, 1000) {
                    public void onFinish() {
                        cargarInventario(getToken(),true);
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
            ((TextView) getActivity().findViewById(R.id.esModificacion)).setText("NO");

            Spinner prod = (Spinner) getActivity().findViewById(R.id.cbTipoProd);
            prod.setSelection(0,true);
            Spinner subprod = (Spinner) getActivity().findViewById(R.id.cbSubTipoProd);
            subprod.setSelection(0,true);

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

        WebView webMapa = (WebView) getActivity().findViewById(R.id.webMapa);
        webMapa.setWebViewClient(new WebViewClient());
        webMapa.getSettings().setJavaScriptEnabled(true);
        webMapa.loadUrl("");

        limpiarAtributos();
    }

    private void limpiarAtributos()
    {
        ((Spinner) getActivity().findViewById(R.id.cbCategoria)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbTipoUnidad)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbUbiPlanta)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbCantCoch)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbLuminosidad)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbEstado)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbCanBan)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbAntigDepto)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbAmueDepto)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbAptoProfDepto)).setSelection(0,true);
        ((EditText) getActivity().findViewById(R.id.etExpDepto)).setText("");
        ((Spinner) getActivity().findViewById(R.id.cbCochCasa)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbCantDorCasa)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbCantPlanCasa)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbAntCasa)).setSelection(0,true);
        ((EditText) getActivity().findViewById(R.id.etExpCasa)).setText("");
        ((Spinner) getActivity().findViewById(R.id.cbEstCasa)).setSelection(0,true);
        ((EditText) getActivity().findViewById(R.id.etFrenteCasa)).setText("");
        ((Spinner) getActivity().findViewById(R.id.cbCantBanosCasa)).setSelection(0,true);
        ((EditText) getActivity().findViewById(R.id.etLargoCasa)).setText("");
        ((Spinner) getActivity().findViewById(R.id.cbAmuebCasa)).setSelection(0,true);
        ((EditText) getActivity().findViewById(R.id.etExpCasa)).setText("");
        ((Spinner) getActivity().findViewById(R.id.cbCatOfi)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbCantAscOfi)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbUbiPlOfi)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbCantCocOfi)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbLumiOfi)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbEstOfi)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbCanBanOfi)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbAntigOfi)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbBanExcOfi)).setSelection(0,true);
        ((EditText) getActivity().findViewById(R.id.etDivOfi)).setText("");
        ((Spinner) getActivity().findViewById(R.id.cbOfficeOfi)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbAmuebOfi)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbEquipOfi)).setSelection(0,true);
        ((EditText) getActivity().findViewById(R.id.etExpOfi)).setText("");
        ((Spinner) getActivity().findViewById(R.id.cbLumLoc)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbEstLoc)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbCanBanLoc)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbAntigLoc)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbOfficeLoc)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbCatLoc)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbLoteLoc)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbAptoTodoDest)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbAptoGastr)).setSelection(0,true);
        ((EditText) getActivity().findViewById(R.id.etAnchoPBLoc)).setText("");
        ((EditText) getActivity().findViewById(R.id.etFondoPBLoc)).setText("");
        ((EditText) getActivity().findViewById(R.id.etAnchVidLoc)).setText("");
        ((EditText) getActivity().findViewById(R.id.etExpLoc)).setText("");
        ((Spinner) getActivity().findViewById(R.id.cbDemolicion)).setSelection(0,true);
        ((Spinner) getActivity().findViewById(R.id.cbPlaApro)).setSelection(0,true);
        ((EditText) getActivity().findViewById(R.id.etFrenteTer)).setText("");
        ((EditText) getActivity().findViewById(R.id.etLargoTer)).setText("");
        ((EditText) getActivity().findViewById(R.id.etSupEdTer)).setText("");
        ((EditText) getActivity().findViewById(R.id.etFot)).setText("");
    }

    private void cargarFormulario(int pos) {
        final String Metodo = "BuscarPorIdMovil";
        final String accionSoap = "http://tempuri.org/BuscarPorIdMovil";

        itemInventario prop = propiedades.get(pos);

        Toast.makeText(getContext(),prop.codigo,Toast.LENGTH_SHORT).show();

        try {
            // Modelo el request
            SoapObject request = new SoapObject(namespace, Metodo);

            request.addProperty("idPropiedad", prop.id);

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
            propie.atributos = resultado.getPropertyAsString(23).replace("anyType{}","");

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

            ((TextView) getActivity().findViewById(R.id.etProvincia)).setText(propie.Provincia);

            obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbOperacion)),propie.Operacion);

            obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbSubOperacion)),propie.SubtipoOp);

            obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbMoneda)),propie.Moneda);

            obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbPiso)),propie.Piso);

            obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbUnidad)),propie.Unidad);

            final Spinner producto = (Spinner) getActivity().findViewById(R.id.cbTipoProd);
            int posit = obtenerElementosSpinner(producto,propie.tipoProd);
            producto.setEnabled(false);

            obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbSubTipoProd)),propie.SubtipoProd);

            ((TextView) getActivity().findViewById(R.id.esModificacion)).setText("SI");

            Utilidades util = new Utilidades();

            obtenerSubproducto(util.initSubproductos(posit),propie.SubtipoProd);

            RadioButton supm2 = (RadioButton) getActivity().findViewById(R.id.rbM2);
            supm2.setChecked(true);

            RadioButton supHa = (RadioButton) getActivity().findViewById(R.id.rbHa);
            supHa.setChecked(false);

            Switch reservada = (Switch) getActivity().findViewById(R.id.reservada);
            Switch publica = (Switch) getActivity().findViewById(R.id.publica);

            if(propie.reservada.toUpperCase().equals("SI")) {
                reservada.setChecked(true);
            }
            else
            {
                reservada.setChecked(false);
            }

            if(propie.Publica.toUpperCase().contains("TRUE")) {
                publica.setChecked(true);
            }
            else
            {
                publica.setChecked(false);
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

            //Departamentos
            if(producto.getSelectedItem().toString().toUpperCase().contains("DEPART"))
            {
                obtenerAtributos(propie.atributos,"DEPART");
            }

            //Casas
            if(producto.getSelectedItem().toString().toUpperCase().contains("CASA"))
            {
                obtenerAtributos(propie.atributos,"CASA");
            }
            //Oficinas
            if(producto.getSelectedItem().toString().toUpperCase().contains("OFICINA"))
            {
                obtenerAtributos(propie.atributos,"OFICINA");
            }
            //Locales
            if(producto.getSelectedItem().toString().toUpperCase().contains("LOCAL"))
            {
                obtenerAtributos(propie.atributos,"LOCAL");
            }
            //Locales
            if(producto.getSelectedItem().toString().toUpperCase().contains("TERRENO"))
            {
                obtenerAtributos(propie.atributos,"TERRENO");
            }
        }
        catch (Exception e) {
            Toast.makeText(getActivity(),"Ha ocurrido un error, intente nuevamente.", Toast.LENGTH_SHORT).show();
        }
    }

    public void obtenerAtributos(String atr, String tipo)
    {
        String[] atributos = atr.split(";");

        if(atributos != null) {
            for (String atrib : atributos) {

                //Departamentos
                if (tipo.contains("DEPART")) {
                    String[] iAtr = atrib.split(":");

                    if (iAtr[0].contains("Categoria")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCategoria)),iAtr[1]);
                    }

                    if (iAtr[0].contains("TipoUnidad")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbTipoUnidad)),iAtr[1]);
                    }

                    if (iAtr[0].contains("UbicacionPlanta")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbUbiPlanta)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Cocheras")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCantCoch)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Luminosidad")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbLuminosidad)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Estado")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbEstado)),iAtr[1]);
                    }

                    if (iAtr[0].contains("CantidadBanos")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCanBan)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Antiguedad")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbAntigDepto)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Amueblado")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbAmueDepto)),iAtr[1]);
                    }

                    if (iAtr[0].contains("AptoProfesional")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbAptoProfDepto)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Expensas")) {
                        ((EditText) getActivity().findViewById(R.id.etExpDepto)).setText(iAtr[1]);
                    }
                }

                //Casas
                if (tipo.contains("CASA")) {
                    String[] iAtr = atrib.split(":");

                    if (iAtr[0].contains("Cocheras")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCochCasa)),iAtr[1]);
                    }

                    if (iAtr[0].contains("CantidadDormitorios")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCantDorCasa)),iAtr[1]);
                    }

                    if (iAtr[0].contains("CantidadPlantas")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCantPlanCasa)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Antiguedad")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbAntCasa)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Expensas")) {
                        ((EditText) getActivity().findViewById(R.id.etExpCasa)).setText(iAtr[1]);
                    }

                    if (iAtr[0].contains("Estado")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbEstCasa)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Frente")) {
                        ((EditText) getActivity().findViewById(R.id.etFrenteCasa)).setText(iAtr[1]);
                    }

                    if (iAtr[0].contains("CantidadBanos")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCantBanosCasa)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Largo")) {
                        ((EditText) getActivity().findViewById(R.id.etLargoCasa)).setText(iAtr[1]);
                    }

                    if (iAtr[0].contains("Amueblado")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbAmuebCasa)),iAtr[1]);
                    }

                    if (iAtr[0].contains("Expensas")) {
                        ((EditText) getActivity().findViewById(R.id.etExpCasa)).setText(iAtr[1]);
                    }
                }

                //Oficina
                if (tipo.contains("OFICI")) {
                    String[] iAtr = atrib.split(":");

                    if (iAtr[0].contains("Categoria")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCatOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("CantidadAscensores")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCantAscOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("UbicacionPlanta")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbUbiPlOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Cocheras")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCantCocOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Luminosidad")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbLumiOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Estado")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbEstOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("CantidadBanos")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCanBanOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Antiguedad")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbAntigOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("BañosExclusivos")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbBanExcOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Divisiones")) {
                        ((EditText) getActivity().findViewById(R.id.etDivOfi)).setText(iAtr[1]);
                    }

                    if (iAtr[0].contains("Office")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbOfficeOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Amueblado")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbAmuebOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Equipamiento")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbEquipOfi)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Expensas")) {
                        ((EditText) getActivity().findViewById(R.id.etExpOfi)).setText(iAtr[1]);
                    }
                }

                //Locales
                if (tipo.contains("LOCAL")) {
                    String[] iAtr = atrib.split(":");

                    if (iAtr[0].contains("Luminosidad")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbLumLoc)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Estado")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbEstLoc)), iAtr[1]);
                    }

                    if (iAtr[0].contains("CantidadBanos")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCanBanLoc)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Antiguedad")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbAntigLoc)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Office")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbOfficeLoc)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Categoria")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbCatLoc)), iAtr[1]);
                    }

                    if (iAtr[0].contains("LotePropio")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbLoteLoc)), iAtr[1]);
                    }

                    if (iAtr[0].contains("TodoDestino")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbAptoTodoDest)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Gastronomia")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbAptoGastr)), iAtr[1]);
                    }

                    if (iAtr[0].contains("AnchoPB")) {
                        ((EditText) getActivity().findViewById(R.id.etAnchoPBLoc)).setText(iAtr[1]);
                    }

                    if (iAtr[0].contains("FondoPB")) {
                        ((EditText) getActivity().findViewById(R.id.etFondoPBLoc)).setText(iAtr[1]);
                    }

                    if (iAtr[0].contains("AnchoVidriera")) {
                        ((EditText) getActivity().findViewById(R.id.etAnchVidLoc)).setText(iAtr[1]);
                    }

                    if (iAtr[0].contains("Expensas")) {
                        ((EditText) getActivity().findViewById(R.id.etExpLoc)).setText(iAtr[1]);
                    }
                }

                //Terrenos
                if (tipo.contains("TERRENO")) {
                    String[] iAtr = atrib.split(":");

                    if (iAtr[0].contains("Demolicion")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbDemolicion)), iAtr[1]);
                    }

                    if (iAtr[0].contains("PlanosAprobados")) {
                        obtenerElementosSpinner(((Spinner) getActivity().findViewById(R.id.cbPlaApro)), iAtr[1]);
                    }

                    if (iAtr[0].contains("Frente")) {
                        ((EditText) getActivity().findViewById(R.id.etFrenteTer)).setText(iAtr[1]);
                    }

                    if (iAtr[0].contains("Largo")) {
                        ((EditText) getActivity().findViewById(R.id.etLargoTer)).setText(iAtr[1]);
                    }

                    if (iAtr[0].contains("SuperficieEdificable")) {
                        ((EditText) getActivity().findViewById(R.id.etSupEdTer)).setText(iAtr[1]);
                    }

                    if (iAtr[0].contains("FOT")) {
                        ((EditText) getActivity().findViewById(R.id.etFot)).setText(iAtr[1]);
                    }
                }
            }
        }
    }

    public void obtenerSubproducto(String[] array, String subtipo)
    {
        int n = array.length;

        int posicion = 0;

        for (int i = 0; i < n; i++) {
            if(array[i].toString().contains(subtipo))
            {
                posicion = i;
            }
        }

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, array);

        Spinner subprod = (Spinner) getActivity().findViewById(R.id.cbSubTipoProd);

        subprod.setPadding(0,0,0,0);

        subprod.setAdapter(adp);

        subprod.setSelection(posicion,true);
    }

    public int obtenerElementosSpinner(Spinner spinner, String cadena) {
        int posicion = 0;
        Adapter adapter = null;

        try
        {
            adapter = spinner.getAdapter();
        }
        catch (Exception e) {
            int i = 0;
        }


        ArrayList<String> items = new ArrayList<String>();

        int n = adapter.getCount();

        for (int i = 0; i < n; i++) {
            String valor = (String) adapter.getItem(i);
            items.add(valor);
        }

        for (int i = 0; i < n; i++) {
            String valor = (String) adapter.getItem(i);
            if(cadena.equals(valor))
            {
                posicion = i;
            }
            else {
                if(!items.contains(cadena))
                {
                    items.add(cadena);
                    posicion = n;
                }
            }
        }

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, items);

        spinner.setPadding(0,0,0,0);

        spinner.setAdapter(adp);

        spinner.setSelection(posicion,true);

        return posicion;
    }

    private void cargarInventario(final String token, Boolean propio) {

        propiedades.clear();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final String Metodo = "BuscarInventarioMovil";
        final String accionSoap = "http://tempuri.org/BuscarInventarioMovil";

        final ListView tabla = (ListView) vistaInventario.findViewById(R.id.listaInventario);

        ArrayList<String> items = new ArrayList<String>();

        String inventarioPropio = "false";

        if(propio) {
            inventarioPropio = "true";
        }

        try {

            // Modelo el request
            SoapObject request = new SoapObject(namespace, Metodo);

            request.addProperty("token", token);
            request.addProperty("propio", inventarioPropio);

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
            TextView mensajeInventario = (TextView) vistaInventario.findViewById(R.id.mensajeInventario);

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
                    prop.id = oferta.getPropertyAsString(7);
                    propiedades.add(prop);

                    if(prop.vencida.contains("true")) {
                        items.add("♦ " + prop.codigo + " - " + prop.operacion + " " + prop.moneda + " " + prop.importe + "\n" + prop.Calle);
                    }
                    else
                    {
                        items.add(prop.codigo + " - " + prop.operacion + " " + prop.moneda + " " + prop.importe + "\n" + prop.Calle);
                    }
                }
                mensajeInventario.setVisibility(View.INVISIBLE);
                sinresultados.setVisibility(View.INVISIBLE);
            }
            else
            {
                Toast.makeText(getContext(),"No se encontraron ofertas en su inventario.", Toast.LENGTH_SHORT).show();
                mensajeInventario.setVisibility(View.VISIBLE);
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
