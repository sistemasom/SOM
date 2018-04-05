package com.som.som;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Operacion extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View vistaOperacion;

    public Operacion() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Operacion newInstance(String param1, String param2) {
        Operacion fragment = new Operacion();
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

        vistaOperacion = inflater.inflate(R.layout.fragment_operacion, container, false);

        cargarMoneda(vistaOperacion);
        cargarOperaciones(vistaOperacion);

        return vistaOperacion;
    }

    public void cargarMoneda(final View vista) {

        final String[] monedas =
                new String[]{"U$S","EUR$","$"};

        ArrayAdapter<String> adapterMoneda = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, monedas);
        Spinner spOperacion = (Spinner)vista.findViewById(R.id.cbMoneda);

        adapterMoneda.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spOperacion.setAdapter(adapterMoneda);
    }

    public void cargarOperaciones(final View vista) {

        final String[] Operaciones =
                new String[]{"Alquiler","Venta"};

        ArrayAdapter<String> adapterOperacion = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Operaciones);
        Spinner spOperacion = (Spinner)vista.findViewById(R.id.cbOperacion);

        adapterOperacion.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spOperacion.setAdapter(adapterOperacion);

        spOperacion.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        cargarSubOperaciones(vista,position);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        cargarSubOperaciones(vista,0); //Paso alquiler
    }

    public void cargarSubOperaciones(View vista, int operacionSeleccionada)
    {

        ArrayAdapter<String> adapterSubOperacion;

        final String[] subTipoAlquiler = new String[]
                {" - ","Turístico"};

        final String[] subTipoVenta = new String[]
                {" - ","Inversión con renta"};

        switch (operacionSeleccionada) {
            case 0:
                adapterSubOperacion = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoAlquiler);
                break;
            case 1:
                adapterSubOperacion = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoVenta);
                break;
            default:
                adapterSubOperacion = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoAlquiler);
        }

        Spinner spSubOperacion = (Spinner)vista.findViewById(R.id.cbSubOperacion);
        adapterSubOperacion.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spSubOperacion.setAdapter(adapterSubOperacion);
    }

    public interface OnFragmentInteractionListener {
        void EnviarJSON(JSONObject json);
    }

    public boolean validarCampos()
    {
        boolean valido = false;

        String precio = ((EditText) vistaOperacion.findViewById(R.id.etPrecio)).getText().toString();

        if(!precio.isEmpty()) {
            valido = true;
        }

        return valido;
    }

    public void obtenerValores(JSONObject json) {

        String operacionSel = ((Spinner) vistaOperacion.findViewById(R.id.cbOperacion)).getSelectedItem().toString();

        String subOperacionSel = ((Spinner) vistaOperacion.findViewById(R.id.cbSubOperacion)).getSelectedItem().toString();

        String moneda = ((Spinner) vistaOperacion.findViewById(R.id.cbMoneda)).getSelectedItem().toString();

        String precio = ((EditText) vistaOperacion.findViewById(R.id.etPrecio)).getText().toString();

        Switch reserv = (Switch) vistaOperacion.findViewById(R.id.reservada);
        String reservada = "No";
        if(reserv.isChecked()) {
            reservada = "Si";
        }

        Switch visibleensistema = (Switch) vistaOperacion.findViewById(R.id.publica);
        String publica = "No";
        if(visibleensistema.isChecked()) {
            publica = "Si";
        }

        try {
            json.put("Operacion",operacionSel);
            json.put("SubOperacion",subOperacionSel);
            json.put("Moneda",moneda);
            json.put("Precio",precio);
            json.put("Publica",publica);
            json.put("Reservada",reservada);
            json.put("FechaPublicacion",ObtenerFechaPublicacion());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String ObtenerFechaPublicacion()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String fecha = dateFormat.format(date);
        fecha.replace("\\", "/");
        return fecha;
    }
}
