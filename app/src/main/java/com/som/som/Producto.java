package com.som.som;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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

        cargarProductos(vistaProducto);

        return vistaProducto;
    }

    public void cargarProductos(final View vista) {

        //Combo producto
        final String[] productos =
                new String[]{"Departamentos","Casas","Oficinas","Locales","Terrenos","Cocheras",
                        "Countries / Barrios Privados","Depositos / Industrias","Edificios en block",
                        "Fondos de comercio","Hoteles / Otros productos","Propiedades rurales"};

        ArrayAdapter<String> adapterProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, productos);
        Spinner spProductos = (Spinner)vista.findViewById(R.id.cbTipoProd);

        adapterProd.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spProductos.setAdapter(adapterProd);

        spProductos.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                        AdapterView<?> parent, View view, int position, long id) {
                        cargarSubproductos(vista,position);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        cargarSubproductos(vista,0); //Paso el tipo departamento
    }

    public JSONObject obtenerValores() {

        JSONObject json = new JSONObject();

        Spinner spProducto = (Spinner) vistaProducto.findViewById(R.id.cbTipoProd);
        String prodSel = spProducto.getSelectedItem().toString();

        Spinner spSubProducto = (Spinner) vistaProducto.findViewById(R.id.cbSubTipoProd);
        String subProdSel = spSubProducto.getSelectedItem().toString();

        EditText txtSupCub = (EditText) vistaProducto.findViewById(R.id.etSupCub);
        String supCub = txtSupCub.getText().toString();

        EditText txtSupTot = (EditText) vistaProducto.findViewById(R.id.etSupTot);
        String supTot = txtSupTot.getText().toString();

        RadioGroup radio = (RadioGroup) vistaProducto.findViewById(R.id.grupoRbSup);
        int selectedId = radio.getCheckedRadioButtonId();

        RadioButton boton = (RadioButton) vistaProducto.findViewById(selectedId);
        String valorRB = boton.getText().toString();

        EditText txtDestacable = (EditText) vistaProducto.findViewById(R.id.etDestacable);
        String destacable = txtDestacable.getText().toString();

        TextView txtCodigo = (TextView) vistaProducto.findViewById(R.id.codigoOferta);
        String cod = txtCodigo.getText().toString();

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

        Spinner spProducto = (Spinner) vistaProducto.findViewById(R.id.cbTipoProd);
        String prodSel = spProducto.getSelectedItem().toString();

        Spinner spSubProducto = (Spinner) vistaProducto.findViewById(R.id.cbSubTipoProd);
        String subProdSel = spSubProducto.getSelectedItem().toString();

        EditText txtSupCub = (EditText) vistaProducto.findViewById(R.id.etSupCub);
        String supCub = txtSupCub.getText().toString();

        EditText txtSupTot = (EditText) vistaProducto.findViewById(R.id.etSupTot);
        String supTot = txtSupTot.getText().toString();

        if(prodSel != "" && subProdSel != "" && supCub != "" && supTot != "")
        {
            valido = true;
        }

        return valido;
    }

    public interface OnFragmentInteractionListener {
        void EnviarJSON(JSONObject json);
    }

    public void cargarSubproductos(View vista, int productoSeleccionado)
    {

        ArrayAdapter<String> adapterSubProd;

        //Subtipos de producto, para cada tipo de producto
        final String[] subTipoDepto = new String[]
                {"1 ambiente","2 ambientes","2 dormitorios c/dep.","3 ambientes","3 dormitorios","3 dormitorios c/dep.", "4 o más dormitorios"};

        final String[] subTipoCasa = new String[]
                {"Casa","Chalet","Duplex","En barrio privado / Country","Petit hotel","Prop. Horizontal", "Quinta"};

        final String[] subTipoOficina = new String[]
                {"Planta dividida","Planta libre"};

        final String[] subTipoLocales = new String[]
                {"A la calle","Con vivienda","En esquina","En galería","En shopping"};

        final String[] subTipoTerrenos = new String[]
                {"Fracciones industriales","Fracciones rurales","Fracciones urbanas","Lote en barrio privado / Country","Loteo","Terreno"};

        final String[] subTipoCocheras = new String[]
                {"Cocheras individuales"};

        final String[] subTipoCountry = new String[]
                {"Barrio privado","Club de campo","Country","Country náutico","Megaemprendimiento"};

        final String[] subTipoDepositos = new String[]
                {"Depósitos","Establecimientos industriales","Galpones"};

        final String[] subTipoEdificios = new String[]
                {"Edificios en block","Estructuras","Obras"};

        final String[] subTipoFondos = new String[]
                {"","Agencia comercial","Albergue transitorio","Almacén","Autoservicio","Bar","Carnicería","Centro de copiado",
                        "Centro médico","Confitería de masas y lunch","Consultorio","Droguería","Estación de servicio","Fábrica de pastas",
                        "Farmacia","Ferretería","Frutería","Garage","Geriátrico","Heladería","Lavadero de autos","Lavadero de ropa","Librería",
                        "Locutorio","Otros negocios","Panadería","Pañalera","Parada de diarios","Parrilla","Pizzería","Prode","Quiosco","Restaurante",
                        "Rotisería","Salón de fiestas","Taller mecánico","Transporte","Video club","Zapatería"};

        final String[] subTipoHoteles = new String[]
                {"Bóveda/Parcela/Nicho","Hotel","Isla","Otros productos"};

        final String[] subTipoRurales = new String[]
                {"","Agrícola","Chacra","Cría","Criaderos","Floricultura","Forestal","Frutícola","Ganadera","Granja",
                        "Haras","Industrialización","Invernada","Minería","Mixto","Tambo"};

        switch (productoSeleccionado) {
            case 0:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoDepto);
                break;
            case 1:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoCasa);
                break;
            case 2:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoOficina);
                break;
            case 3:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoLocales);
                break;
            case 4:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoTerrenos);
                break;
            case 5:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoCocheras);
                break;
            case 6:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoCountry);
                break;
            case 7:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoDepositos);
                break;
            case 8:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoEdificios);
                break;
            case 9:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoFondos);
                break;
            case 10:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoHoteles);
                break;
            case 11:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoRurales);
                break;
            default:
                adapterSubProd = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, subTipoDepto);
        }

        Spinner spSubProductos = (Spinner)vista.findViewById(R.id.cbSubTipoProd);
        adapterSubProd.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spSubProductos.setAdapter(adapterSubProd);
    }
}
