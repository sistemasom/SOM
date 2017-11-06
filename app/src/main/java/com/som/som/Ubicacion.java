package com.som.som;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class Ubicacion extends Fragment implements LocationListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View vistaUbicacion;

    LocationManager locationManager ;
    String provider;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Double latitud = 0.0;
    private Double longitud = 0.0;


    public Ubicacion() {
        // Required empty public constructor
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

        ImageButton btnMapa = (ImageButton) vistaUbicacion.findViewById(R.id.btnMapa);

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Obteniendo ubicación, aguarde un instante por favor...",Toast.LENGTH_SHORT).show();
                obtenerCoordenadas();
                /*try {
                    obtenerCoordenadasDireccion();
                    obtenerDatosDireccion(infoLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });
        return vistaUbicacion;
    }

    public Boolean puedePublicar()
    {
        if(longitud != 0.0 && latitud != 0.0) {
        //if(latitud != "" && longitud != ""){
            return true;
        }
        else
        {
            return false;
        }
    }

    /*public void obtenerCoordenadasDireccion() throws IOException {
        new Thread() {
            @Override
            public void run() {
                try {
                    String add = obtenerDireccion().replace(" ","+");
                    String lat = "";
                    String lng = "";
                    String dataLocation = "";
                    String url = "https://maps.googleapis.com/maps/api/geocode/xml?address=" + add + "&key=AIzaSyBGtClDdDlC_-ITTM_JXTw5JiFJgyN_ehY&callback";

                        BufferedReader br = null;
                        try {
                            HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
                            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                            String line;
                            while ((line = br.readLine()) != null) {
                                String a = line;
                                if(line.contains("<lat>")){
                                    lat = a.replace("<lat>","").replace("</lat>","");
                                }
                                if(line.contains("<lng>")) {
                                    lng = a.replace("<lng>","").replace("</lng>","");
                                }
                                if(line.contains("<formatted_address>")) {
                                    dataLocation = a.replace("<formatted_address>","").replace("</formatted_address>","");
                                }
                            }
                            latitud = lat;
                            longitud = lng;
                            infoLocation = dataLocation;

                            cargarMapa(lat,lng);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (br != null) br.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }*/

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

    /*public String obtenerDireccion() {
        String nombre = "";

        EditText txtCalle = (EditText) vistaUbicacion.findViewById(R.id.etCalle);
        EditText txtAltura = (EditText) vistaUbicacion.findViewById(R.id.etAltura);
        EditText txtRef = (EditText) vistaUbicacion.findViewById(R.id.etEntreCalles);

        nombre = txtCalle.getText().toString() + " " + txtAltura.getText().toString() + " " + txtRef.getText().toString();

        return nombre;
    }*/

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

        try {
            json.put("Piso",pisoSel);
            json.put("Unidad",unidadSel);
            json.put("Referencia",entreCalles);
            json.put("Calle",calle);
            json.put("Altura",altura);
            json.put("Ubicacion",ubicacion);
            json.put("latitud",latitud);
            json.put("longitud",longitud);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargarUnidad(final View vista) {
        //Combo producto
        final String[] unidades =
                new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
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

        //Combo producto
        final String[] pisos =
                new String[]{"PB","EP","1SS","2SS","3SS","1","2","3","4","5","6","7","8","9","10",
                        "11","12","13","14","15","16","17","18","19","20","21","22","23","24","25",
                        "26","27","28","29","30","31","32","33","34","35","36","37","38","39","40",
                        "41","42","43","44","45","46","47","48","49","50","51","52","53","54","55",
                        "56","57","58","59","60","VS"};

        ArrayAdapter<String> adapterPiso = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, pisos);
        Spinner spPiso = (Spinner)vista.findViewById(R.id.cbPiso);

        adapterPiso.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spPiso.setAdapter(adapterPiso);
    }

    //public void cargarMapa(String lat, String lng) {
    public void cargarMapa() {

        WebView webMapa = (WebView) vistaUbicacion.findViewById(R.id.webMapa);

        //Evita que se abra el navegador
        webMapa.setWebViewClient(new WebViewClient());
        webMapa.getSettings().setJavaScriptEnabled(true);

        String url = "";

        //url = "http://sistema.som.com.ar/MapaApp.html?latitud=" + latitud.toString() + "&longitud=" + longitud.toString();
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
