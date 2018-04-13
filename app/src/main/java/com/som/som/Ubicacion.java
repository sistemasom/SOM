package com.som.som;

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Ubicacion extends Fragment{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View vistaUbicacion;

    LocationManager locationManager ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Double latitud = 0.0;
    private Double longitud = 0.0;

    private boolean barrioCerrado = false;

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

        cargarPiso();
        cargarUnidad();

        cargarPaises();

        cargarProvincias();

        cargarBarrios();

        ImageButton btnMapa = (ImageButton) vistaUbicacion.findViewById(R.id.btnMapa);

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Obteniendo ubicación, aguarde un instante por favor...",Toast.LENGTH_SHORT).show();
                new CountDownTimer(500, 1000) {
                    public void onFinish()
                    {
                        obtenerDatosDireccion();
                    }

                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });
        return vistaUbicacion;
    }

    private void obtenerDatosDireccion() {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        EditText etPais = (EditText) vistaUbicacion.findViewById(R.id.etPais);
        EditText etProvincia = (EditText) vistaUbicacion.findViewById(R.id.etProvincia);
        EditText etBarrio = (EditText) vistaUbicacion.findViewById(R.id.etBarrio);
        EditText etCalle = (EditText) vistaUbicacion.findViewById(R.id.etCalle);
        EditText etAltura = (EditText) vistaUbicacion.findViewById(R.id.etAltura);
        TextView lat = (TextView) vistaUbicacion.findViewById(R.id.latitud);
        TextView longi = (TextView) vistaUbicacion.findViewById(R.id.longitud);

        String ubicacion = etPais.getText() + ", " + etProvincia.getText() + ", " + etBarrio.getText() + ", " + etCalle.getText() + " " + etAltura.getText();

        try {
            latitud = 0.0;
            longitud = 0.0;

            List<Address> addresses = geocoder.getFromLocationName(ubicacion, 1);

            if(addresses.size() > 0) {
                Address address = addresses.get(0);
                longitud = address.getLongitude();
                latitud = address.getLatitude();
                lat.setText(latitud.toString());
                longi.setText(longitud.toString());
                cargarMapa();
            }
            else
            {
                Toast.makeText(getContext(),"No se pudo obtener la ubicación. ", Toast.LENGTH_SHORT).show();
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

        EditText txtPais = (EditText) vistaUbicacion.findViewById(R.id.etPais);
        String pais = txtPais.getText().toString();

        EditText txtAltura = (EditText) vistaUbicacion.findViewById(R.id.etAltura);
        String altura = txtAltura.getText().toString();

        TextView tvUbicacion = (TextView) vistaUbicacion.findViewById(R.id.tvUbicacion);
        String ubicacion = tvUbicacion.getText().toString();

        TextView etBarrio = (EditText) vistaUbicacion.findViewById(R.id.etBarrio);
        String barrio = etBarrio.getText().toString();

        if(barrio.contains("C.A.B.A."))
        {
            barrio = barrio.replace("C.A.B.A","CIUDAD AUTONOMA BUENOS AIRES");
        }

        TextView tvProvincia = (TextView) vistaUbicacion.findViewById(R.id.etProvincia);
        String provincia = tvProvincia.getText().toString();

        TextView lat = (TextView) vistaUbicacion.findViewById(R.id.latitud);
        TextView longi = (TextView) vistaUbicacion.findViewById(R.id.longitud);

        try {
            json.put("Pais",pais);
            json.put("Piso",pisoSel);
            json.put("Unidad",unidadSel);
            json.put("Referencia",entreCalles);
            json.put("Calle",calle);
            json.put("Provincia",provincia);

            if(barrioCerrado)
            {
                json.put("Country",barrio);
                json.put("Barrio","");
            }
            else
            {
                json.put("Barrio",barrio);
                json.put("Country","");
            }

            json.put("Altura",altura);
            json.put("Ubicacion",ubicacion);
            json.put("latitud",lat.getText());
            json.put("longitud",longi.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargarUnidad() {
        //Combo unidades
        final String[] unidades =
                new String[]{" ","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                        "1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24",
                        "25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46",
                        "47","48","49","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68",
                        "69","70","71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86","87","88","89","90",
                        "91","92","93","94","95","96","97","98","99"};

        ArrayAdapter<String> adapterUnidad = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, unidades);
        Spinner spUnidad = (Spinner)vistaUbicacion.findViewById(R.id.cbUnidad);

        adapterUnidad.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spUnidad.setAdapter(adapterUnidad);
    }

    public void cargarPiso() {

        //Combo pisos
        final String[] pisos =
                new String[]{" ","PB","EP","1SS","2SS","3SS","1","2","3","4","5","6","7","8","9","10",
                        "11","12","13","14","15","16","17","18","19","20","21","22","23","24","25",
                        "26","27","28","29","30","31","32","33","34","35","36","37","38","39","40",
                        "41","42","43","44","45","46","47","48","49","50","51","52","53","54","55",
                        "56","57","58","59","60","VS"};

        ArrayAdapter<String> adapterPiso = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, pisos);
        Spinner spPiso = (Spinner) vistaUbicacion.findViewById(R.id.cbPiso);

        adapterPiso.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spPiso.setAdapter(adapterPiso);
    }

    public void cargarProvincias() {

        final String[] provincias =
            new String[]{
                    "Barcelona_España","CAPITAL FEDERAL_Argentina","Distrito Federal_Brasil","BUENOS AIRES_Argentina","Gerona_España","CATAMARCA_Argentina","CHACO_Argentina","CHUBUT_Argentina",
                    "CORDOBA_Argentina","CORRIENTES_Argentina","ENTRE RIOS_Argentina","FORMOSA_Argentina","Artigas_Uruguay","Distrito Capital_Paraguay","Florida_Estados Unidos","JUJUY_Argentina",
                    "LA PAMPA_Argentina","LA RIOJA_Argentina","MENDOZA_Argentina","MISIONES_Argentina","NEUQUEN_Argentina","RIO NEGRO_Argentina","SALTA_Argentina","SAN JUAN_Argentina","Acre_Brasil",
                    "Alto Paraguay_Paraguay","Canelones_Uruguay","SAN LUIS_Argentina","SANTA CRUZ_Argentina","SANTA FE_Argentina","SANTIAGO DEL ESTERO_Argentina","TIERRA DEL FUEGO_Argentina",
                    "TUCUMAN_Argentina","Alagoas_Brasil","Alto Paraná_Paraguay","Cerro Largo_Uruguay","Amambay_Paraguay","Amapa_Brasil","Colonia_Uruguay","Amazonas_Brasil","Bahia_Brasil",
                    "Boquerón_Paraguay","Durazno_Uruguay","Caaguazú_Paraguay","Ceará_Brasil","Flores_Uruguay","Caazapá_Paraguay","Espíritu Santo_Brasil","Florida_Uruguay","Canindeyu_Paraguay",
                    "Goias_Brasil","Lavalleja_Uruguay","Central_Paraguay","Maldonado_Uruguay","Maranhao_Brasil","Concepción_Paraguay","Mato Grosso_Brasil","Montevideo_Uruguay","Cordillera_Paraguay",
                    "Mato Grosso do Sul_Brasil","Paysandú_Uruguay","Guaira_Paraguay","Minas Geráis_Brasil","Río Negro_Uruguay","Itapuá_Paraguay","Rivera_Uruguay","Misiones_Paraguay","Paraiba_Brasil",
                    "Rocha_Uruguay","Ñeembucu_Paraguay","Paraná_Brasil","Salto_Uruguay","Paraguari_Paraguay","Pernambuco_Brasil","San José_Uruguay","Piauí_Brasil","Presidente Hayes_Paraguay",
                    "Soriano_Uruguay","Río de Janeiro_Brasil","San Pedro_Paraguay","Tacuarembó_Uruguay","Río Grande do Norte_Brasil","Treinta y Tres_Uruguay","Río Grande do Sul_Brasil",
                    "Rondonia_Brasil","Roraima_Brasil","Santa Catarina_Brasil","Sao Paulo_Brasil","Sergipe_Brasil","Tocantins_Brasil"
            };

        ArrayList<String> vProvincias = new ArrayList<String>();

        EditText etPais = (EditText) vistaUbicacion.findViewById(R.id.etPais);
        String pais = etPais.getText().toString();

        for(String prov: provincias)
        {
            if(prov.contains(pais))
            {
                String[] parts = prov.split("_");
                vProvincias.add(parts[0]);
            }
        }

        ArrayAdapter<String> adapterProv = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, vProvincias);

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

        Utilidades ubicaciones = new Utilidades();
        ArrayList<String> sBarrios = ubicaciones.initBarrios(prov);

        ArrayList<String> barrios = new ArrayList<String>();

        for(String barr: sBarrios)
        {
            String[] parts = barr.split("_");
            barrios.add(parts[0]);

            if(parts.length == 3) {
                barrioCerrado = true;
            }
        }

        ArrayAdapter<String> adapterBar = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, barrios);

        AutoCompleteTextView etBarrios = (AutoCompleteTextView) vistaUbicacion.findViewById(R.id.etBarrio);
        etBarrios.setAdapter(adapterBar);
    }

    public void cargarPaises() {

        final String[] paises =
                new String[]{"Argentina","Uruguay","Paraguay","Estados Unidos","Brasil","España"};

        ArrayAdapter<String> adapterPais = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, paises);

        AutoCompleteTextView etPais = (AutoCompleteTextView) vistaUbicacion.findViewById(R.id.etPais);

        etPais.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                EditText etProvincias = (EditText) vistaUbicacion.findViewById(R.id.etProvincia);
                etProvincias.setText("");
                cargarProvincias();
            }
        });

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

        if(barrioCerrado)
        {
            if (!pais.isEmpty()&& !barrio.isEmpty() && !provincia.isEmpty()) {
                valido = true;
            }
        }
        else {
            if (!calle.isEmpty() && !pais.isEmpty() && !altura.isEmpty() && !barrio.isEmpty() && !provincia.isEmpty()) {
                valido = true;
            }
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
}
