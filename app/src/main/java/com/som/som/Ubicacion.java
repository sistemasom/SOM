package com.som.som;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Ubicacion extends Fragment implements LocationListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View vistaUbicacion;

    ArrayList<Barrio> aBarrios = new ArrayList<Barrio>();
    LocationManager locationManager ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Double latitud = 0.0;
    private Double longitud = 0.0;

    private class Barrio
    {
        String nombre;
        String provincia;
    }

    public Ubicacion() {
    }

    // TODO: Rename and change types and number of parameters
    public static Ubicacion newInstance(String param1, String param2) {
        Ubicacion fragment = new Ubicacion();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vistaUbicacion = inflater.inflate(R.layout.fragment_ubicacion, container, false);

        cargarPiso(vistaUbicacion);
        cargarUnidad(vistaUbicacion);

        cargarPaises(vistaUbicacion);

        cargarProvincias(vistaUbicacion);

        cargarBarrios();

        ImageButton btnMapa = (ImageButton) vistaUbicacion.findViewById(R.id.btnMapa);

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Obteniendo ubicación, aguarde un instante por favor...",Toast.LENGTH_SHORT).show();
                new CountDownTimer(500, 1000) {
                    public void onFinish()
                    {
                        obtenerCoordenadas();
                    }

                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });
        return vistaUbicacion;
    }

    public Boolean puedePublicar()
    {
        if(longitud != 0.0 && latitud != 0.0) {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void obtenerCoordenadas() {

        Location location;

        try {
            locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled) {
                location = new Location("network");
                if (locationManager != null) {
                    if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    }
                    if (location != null) {
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                        cargarMapa();

                        TextView lat = (TextView) vistaUbicacion.findViewById(R.id.latitud);
                        TextView longi = (TextView) vistaUbicacion.findViewById(R.id.longitud);
                        lat.setText(latitud.toString());
                        longi.setText(longitud.toString());

                        obtenerDatosDireccion(latitud,longitud);
                    }
                } else {
                    Toast.makeText(getContext(), "No se ha podido obtener la ubicación, reintente por favor.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(), "Debe activar servicios de ubicación y conexión a internet para continuar.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }

    private void obtenerDatosDireccion(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                if(addresses.get(0).getSubLocality() != null) {
                    strAdd = addresses.get(0).getSubLocality() + ", ";
                }
                strAdd = strAdd + addresses.get(0).getPostalCode() + ", ";
                strAdd = strAdd + addresses.get(0).getLocality() + ", ";
                strAdd = strAdd + addresses.get(0).getCountryName();

                TextView ubicacion = (TextView) vistaUbicacion.findViewById(R.id.tvUbicacion);
                ubicacion.setText(strAdd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void obtenerValores(JSONObject json) {

        Spinner spPiso = (Spinner) vistaUbicacion.findViewById(R.id.cbPiso);
        String pisoSel = spPiso.getSelectedItem().toString();

        Spinner spUnidad = (Spinner) vistaUbicacion.findViewById(R.id.cbUnidad);
        String unidadSel = spUnidad.getSelectedItem().toString();

        EditText txtEntreCalles = (EditText) vistaUbicacion.findViewById(R.id.etEntreCalles);
        String entreCalles = txtEntreCalles.getText().toString();

        EditText txtCalle = (EditText) vistaUbicacion.findViewById(R.id.etCalle);
        String calle = txtCalle.getText().toString();

        EditText txtAltura = (EditText) vistaUbicacion.findViewById(R.id.etAltura);
        String altura = txtAltura.getText().toString();

        TextView tvUbicacion = (TextView) vistaUbicacion.findViewById(R.id.tvUbicacion);
        String ubicacion = tvUbicacion.getText().toString();

        TextView etBarrio = (EditText) vistaUbicacion.findViewById(R.id.etBarrio);
        String barrio = etBarrio.getText().toString();

        TextView tvProvincia = (TextView) vistaUbicacion.findViewById(R.id.etProvincia);
        String provincia = tvProvincia.getText().toString();

        TextView lat = (TextView) vistaUbicacion.findViewById(R.id.latitud);
        TextView longi = (TextView) vistaUbicacion.findViewById(R.id.longitud);

        try {
            json.put("Piso",pisoSel);
            json.put("Unidad",unidadSel);
            json.put("Referencia",entreCalles);
            json.put("Calle",calle);
            json.put("Provincia",provincia);
            json.put("Barrio",barrio);
            json.put("Altura",altura);
            json.put("Ubicacion",ubicacion);
            json.put("latitud",lat.getText());
            json.put("longitud",longi.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargarUnidad(final View vista) {
        //Combo unidades
        final String[] unidades =
                new String[]{" ","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                        "1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24",
                        "25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46",
                        "47","48","49","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68",
                        "69","70","71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86","87","88","89","90",
                        "91","92","93","94","95","96","97","98","99"};

        ArrayAdapter<String> adapterUnidad = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, unidades);
        Spinner spUnidad = (Spinner)vista.findViewById(R.id.cbUnidad);

        adapterUnidad.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spUnidad.setAdapter(adapterUnidad);
    }

    public void cargarPiso(final View vista) {

        //Combo pisos
        final String[] pisos =
                new String[]{" ","PB","EP","1SS","2SS","3SS","1","2","3","4","5","6","7","8","9","10",
                        "11","12","13","14","15","16","17","18","19","20","21","22","23","24","25",
                        "26","27","28","29","30","31","32","33","34","35","36","37","38","39","40",
                        "41","42","43","44","45","46","47","48","49","50","51","52","53","54","55",
                        "56","57","58","59","60","VS"};

        ArrayAdapter<String> adapterPiso = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, pisos);
        Spinner spPiso = (Spinner)vista.findViewById(R.id.cbPiso);

        adapterPiso.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spPiso.setAdapter(adapterPiso);
    }

    public void cargarProvincias(final View vista) {

        final String[] provincias =
            new String[]{
                    "Barcelona", "CAPITAL FEDERAL", "Distrito Federal", "BUENOS AIRES", "Gerona", "CATAMARCA", "CHACO", "CHUBUT", "CORDOBA", "CORRIENTES", "ENTRE RIOS", "FORMOSA",
                    "Artigas", "Distrito Capital", "Florida", "JUJUY", "LA PAMPA", "LA RIOJA", "MENDOZA", "MISIONES", "NEUQUEN", "RIO NEGRO", "SALTA", "SAN JUAN", "Acre", "Alto Paraguay",
                    "Canelones", "SAN LUIS", "SANTA CRUZ", "SANTA FE", "SANTIAGO DEL ESTERO", "TIERRA DEL FUEGO", "TUCUMAN", "Alagoas", "Alto Paraná", "Cerro Largo", "Amambay", "Amapa",
                    "Colonia", "Amazonas", "Bahia", "Boquerón", "Durazno", "Caaguazú", "Ceará", "Flores", "Caazapá", "Espíritu Santo", "Florida", "Canindeyu", "Goias", "Lavalleja", "Central",
                    "Maldonado", "Maranhao", "Concepción", "Mato Grosso", "Montevideo", "Cordillera", "Mato Grosso do Sul", "Paysandú", "Guaira", "Minas Geráis", "Río Negro", "Itapuá",
                    "Rivera", "Misiones", "Paraiba", "Rocha", "Ñeembucu", "Paraná", "Salto", "Paraguari", "Pernambuco", "San José", "Piauí", "Presidente Hayes", "Soriano", "Río de Janeiro",
                    "San Pedro", "Tacuarembó", "Río Grande do Norte", "Treinta y Tres", "Río Grande do Sul", "Rondonia", "Roraima", "Santa Catarina", "Sao Paulo", "Sergipe", "Tocantins"
            };

        ArrayAdapter<String> adapterProv = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, provincias);

        AutoCompleteTextView etProvincia = (AutoCompleteTextView) vistaUbicacion.findViewById(R.id.etProvincia);

        etProvincia.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                EditText etBarrios = (EditText) vistaUbicacion.findViewById(R.id.etBarrio);
                etBarrios.setText("");
                cargarBarrios();
            }
        });

        etProvincia.setAdapter(adapterProv);
    }

    public void cargarBarrios()
    {
        EditText etProvincia = (EditText) vistaUbicacion.findViewById(R.id.etProvincia);
        String prov = etProvincia.getText().toString();

        Ubicaciones ubicaciones = new Ubicaciones();
        ArrayList<String> sBarrios = ubicaciones.initBarrios(prov);

        ArrayList<String> barrios = new ArrayList<String>();

        for(String barr: sBarrios)
        {
            String[] parts = barr.split("_");
            barrios.add(parts[0]);
        }

        ArrayAdapter<String> adapterBar = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, barrios);

        AutoCompleteTextView etBarrios = (AutoCompleteTextView) vistaUbicacion.findViewById(R.id.etBarrio);
        etBarrios.setAdapter(adapterBar);
    }

    public void cargarPaises(final View vista) {

        final String[] paises =
                new String[]{"Argentina","Uruguay","Paraguay","Estados Unidos","Brasil","España"};

        ArrayAdapter<String> adapterPais = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, paises);

        AutoCompleteTextView etProvincia = (AutoCompleteTextView) vistaUbicacion.findViewById(R.id.etPais);
        etProvincia.setAdapter(adapterPais);
    }

    public boolean validarCampos()
    {
        boolean valido = false;

        EditText txtCalle = (EditText) vistaUbicacion.findViewById(R.id.etCalle);
        String calle = txtCalle.getText().toString();

        EditText txtPais = (EditText) vistaUbicacion.findViewById(R.id.etPais);
        String pais = txtPais.getText().toString();

        EditText txtAltura = (EditText) vistaUbicacion.findViewById(R.id.etAltura);
        String altura = txtAltura.getText().toString();

        TextView etBarrio = (EditText) vistaUbicacion.findViewById(R.id.etBarrio);
        String barrio = etBarrio.getText().toString();

        TextView tvProvincia = (TextView) vistaUbicacion.findViewById(R.id.etProvincia);
        String provincia = tvProvincia.getText().toString();

        if(!calle.isEmpty() && !pais.isEmpty() && !altura.isEmpty() && !barrio.isEmpty() && !provincia.isEmpty())
        {
            valido = true;
        }

        return valido;
    }

    public void cargarMapa() {

        WebView webMapa = (WebView) vistaUbicacion.findViewById(R.id.webMapa);

        //Evita que se abra el navegador
        webMapa.setWebViewClient(new WebViewClient());
        webMapa.getSettings().setJavaScriptEnabled(true);

        String url = "";

        url = "http://sistema.som.com.ar/MapaApp.html?latitud=" + latitud.toString() + "&longitud=" + longitud.toString();

        webMapa.loadUrl(url);
    }

    /*
    ---------------------------
    IMPLEMENTACION DEL LISTENER
    ---------------------------
     */
    @Override
    public void onLocationChanged(Location location) {
        //latitud = location.getLatitude();
        //longitud = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}
