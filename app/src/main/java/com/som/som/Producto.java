package com.som.som;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Producto extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View vistaProducto;
    public Producto() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Producto newInstance(String param1, String param2) {
        Producto fragment = new Producto();
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

        vistaProducto = inflater.inflate(R.layout.fragment_producto, container, false);

        cargarProductos();

        return vistaProducto;
    }

    public void cargarProductos() {

        //Combo producto
        final String[] productos =
                new String[]{"Departamentos","Casas","Oficinas","Locales","Terrenos","Cocheras",
                        "Countries / Barrios Privados","Depósitos/Industrias","Edificios en Block",
                        "Fondos de Comercio","Hoteles / Otros productos","Propiedades Rurales"};

        ArrayAdapter<String> adapterProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, productos);
        Spinner spProductos = (Spinner)vistaProducto.findViewById(R.id.cbTipoProd);

        adapterProd.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spProductos.setAdapter(adapterProd);

        spProductos.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                        AdapterView<?> parent, View view, int position, long id) {
                        cargarSubproductos(position);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        cargarSubproductos(0);
    }

    public JSONObject obtenerValores() {

        JSONObject json = new JSONObject();

        String prodSel = ((Spinner) vistaProducto.findViewById(R.id.cbTipoProd)).getSelectedItem().toString();

        String subProdSel = ((Spinner) vistaProducto.findViewById(R.id.cbSubTipoProd)).getSelectedItem().toString();

        String supCub = ((EditText) vistaProducto.findViewById(R.id.etSupCub)).getText().toString();

        String supTot = ((EditText) vistaProducto.findViewById(R.id.etSupTot)).getText().toString();

        int selectedId = ((RadioGroup) vistaProducto.findViewById(R.id.grupoRbSup)).getCheckedRadioButtonId();

        String valorRB = ((RadioButton) vistaProducto.findViewById(selectedId)).getText().toString();

        String destacable = ((EditText) vistaProducto.findViewById(R.id.etDestacable)).getText().toString();

        String cod = ((TextView) vistaProducto.findViewById(R.id.codigoOferta)).getText().toString();

        try {
            json.put("Codigo",cod);
            json.put("Tipo",prodSel);
            json.put("Subtipo",subProdSel);
            json.put("SupCub",supCub);
            json.put("SupTot",supTot);
            json.put("UnidadSup",valorRB);
            json.put("Destacable",destacable);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public boolean validarCampos()
    {
        boolean valido = false;

        String supCub = ((EditText) vistaProducto.findViewById(R.id.etSupCub)).getText().toString();

        String supTot = ((EditText) vistaProducto.findViewById(R.id.etSupTot)).getText().toString();

        if(!supCub.isEmpty() && !supTot.isEmpty())
        {
            valido = true;
        }

        return valido;
    }

    public interface OnFragmentInteractionListener {
        void EnviarJSON(JSONObject json);
    }

    public void limpiarAtributos()
    {
        try {

            FrameLayout atrNoDisp = (FrameLayout) getActivity().findViewById(R.id.atrNoDisp);

            FrameLayout atrDepto = (FrameLayout) getActivity().findViewById(R.id.atrDeptos);
            FrameLayout atrCasas = (FrameLayout) getActivity().findViewById(R.id.atrCasas);
            FrameLayout atrOficinas = (FrameLayout) getActivity().findViewById(R.id.atrOficinas);
            FrameLayout atrLocales = (FrameLayout) getActivity().findViewById(R.id.atrLocales);
            FrameLayout atrTerrenos = (FrameLayout) getActivity().findViewById(R.id.atrTerrenos);

            atrNoDisp.setVisibility(View.VISIBLE);
            atrDepto.setVisibility(View.GONE);
            atrCasas.setVisibility(View.GONE);
            atrOficinas.setVisibility(View.GONE);
            atrLocales.setVisibility(View.GONE);
            atrTerrenos.setVisibility(View.GONE);
        } catch (Exception e) {}
    }

    public void toggleElementos(boolean ocultar)
    {
        ((Spinner) getActivity().findViewById(R.id.cbUnidad)).setVisibility(View.VISIBLE);
        ((TextView) getActivity().findViewById(R.id.tvUnidad)).setVisibility(View.VISIBLE);
        ((Spinner) getActivity().findViewById(R.id.cbPiso)).setVisibility(View.VISIBLE);
        ((TextView) getActivity().findViewById(R.id.tvPiso)).setVisibility(View.VISIBLE);

        if(ocultar)
        {
            ((Spinner) getActivity().findViewById(R.id.cbUnidad)).setVisibility(View.INVISIBLE);
            ((TextView) getActivity().findViewById(R.id.tvUnidad)).setVisibility(View.INVISIBLE);
            ((Spinner) getActivity().findViewById(R.id.cbPiso)).setVisibility(View.INVISIBLE);
            ((TextView) getActivity().findViewById(R.id.tvPiso)).setVisibility(View.INVISIBLE);
        }
    }

    public void cargarSubproductos(int productoSeleccionado)
    {
        ArrayAdapter<String> adapterSubProd = null;

        limpiarAtributos();

        try {
            toggleElementos(false);
        }catch (Exception e) {}

        Spinner spSubProductos = (Spinner) vistaProducto.findViewById(R.id.cbSubTipoProd);

        Utilidades util = new Utilidades();

        switch (productoSeleccionado) {
            case 0:
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                FrameLayout atrDepto = (FrameLayout) getActivity().findViewById(R.id.atrDeptos);
                if(atrDepto != null) {
                    ((FrameLayout) getActivity().findViewById(R.id.atrNoDisp)).setVisibility(View.GONE);
                    atrDepto.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                FrameLayout atrCasa = (FrameLayout) getActivity().findViewById(R.id.atrCasas);
                if(atrCasa != null) {
                    ((FrameLayout) getActivity().findViewById(R.id.atrNoDisp)).setVisibility(View.GONE);
                    atrCasa.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                FrameLayout atrOficinas = (FrameLayout) getActivity().findViewById(R.id.atrOficinas);
                if(atrOficinas != null) {
                    ((FrameLayout) getActivity().findViewById(R.id.atrNoDisp)).setVisibility(View.GONE);
                    atrOficinas.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                FrameLayout atrLocales = (FrameLayout) getActivity().findViewById(R.id.atrLocales);
                if(atrLocales != null) {
                    ((FrameLayout) getActivity().findViewById(R.id.atrNoDisp)).setVisibility(View.GONE);
                    atrLocales.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                FrameLayout atrTerrenos = (FrameLayout) getActivity().findViewById(R.id.atrTerrenos);
                toggleElementos(true);
                if(atrTerrenos != null) {
                    ((FrameLayout) getActivity().findViewById(R.id.atrNoDisp)).setVisibility(View.GONE);
                    atrTerrenos.setVisibility(View.VISIBLE);
                }
                break;
            case 5:
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                break;
            case 6:
                toggleElementos(true);
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                break;
            case 7:
                adapterSubProd = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                break;
            case 8:
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                break;
            case 9:
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                break;
            case 10:
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                break;
            case 11:
                toggleElementos(true);
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
                break;
            default:
                adapterSubProd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, util.initSubproductos(productoSeleccionado));
        }

        // Solo cargo los subproductos si no se trata de una modificacion

        if(((TextView) getActivity().findViewById(R.id.esModificacion)).getText().toString().contains("NO")) {
            adapterSubProd.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spSubProductos.setAdapter(adapterSubProd);
        }
    }
}
