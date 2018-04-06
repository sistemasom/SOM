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

public class Atributos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View vistaAtributos;
    public Atributos() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Atributos newInstance(String param1, String param2) {
        Atributos fragment = new Atributos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public JSONObject obtenerValores(JSONObject json) {

        String producto = ((Spinner) getActivity().findViewById(R.id.cbTipoProd)).getSelectedItem().toString();

        /* DEPARTAMENTOS */

        if(producto.toUpperCase().contains("DEPART"))
        {
            String catDep = ((Spinner) vistaAtributos.findViewById(R.id.cbCategoria)).getSelectedItem().toString();

            String tipUniDep = ((Spinner) vistaAtributos.findViewById(R.id.cbTipoUnidad)).getSelectedItem().toString();

            String ubPlanta = ((Spinner) vistaAtributos.findViewById(R.id.cbUbiPlanta)).getSelectedItem().toString();

            String cantCocDep = ((Spinner) vistaAtributos.findViewById(R.id.cbCantCoch)).getSelectedItem().toString();

            String lumDep = ((Spinner) vistaAtributos.findViewById(R.id.cbLuminosidad)).getSelectedItem().toString();

            String estDep = ((Spinner) vistaAtributos.findViewById(R.id.cbEstado)).getSelectedItem().toString();

            String canBanDept = ((Spinner) vistaAtributos.findViewById(R.id.cbCanBan)).getSelectedItem().toString();

            String antDept = ((Spinner) vistaAtributos.findViewById(R.id.cbAntigDepto)).getSelectedItem().toString();

            String amueDept = ((Spinner) vistaAtributos.findViewById(R.id.cbAmueDepto)).getSelectedItem().toString();

            String aptoProfDep = ((Spinner) vistaAtributos.findViewById(R.id.cbAptoProfDepto)).getSelectedItem().toString();

            String expDep = ((EditText) vistaAtributos.findViewById(R.id.etExpDepto)).getText().toString();

            try {
                json.put("Categoria",catDep);
                json.put("TipoUnidad",tipUniDep);
                json.put("UbicacionPlanta",ubPlanta);
                json.put("Cocheras",cantCocDep);
                json.put("Luminosidad",lumDep);
                json.put("Estado",estDep);
                json.put("CantidadBanos",canBanDept);
                json.put("Antiguedad",antDept);
                json.put("Amueblado",amueDept);
                json.put("AptoProfesional",aptoProfDep);
                json.put("Expensas",expDep);
            } catch (JSONException e) {e.printStackTrace();}
        }

        /* CASAS */

        if(producto.toUpperCase().contains("CASA"))
        {
            String cochera = ((Spinner) vistaAtributos.findViewById(R.id.cbCochCasa)).getSelectedItem().toString();

            String cantBanoCasa = ((Spinner) vistaAtributos.findViewById(R.id.cbCantBanosCasa)).getSelectedItem().toString();

            String cantDorm = ((Spinner) vistaAtributos.findViewById(R.id.cbCantDorCasa)).getSelectedItem().toString();

            String cbCantPlanCasa = ((Spinner) vistaAtributos.findViewById(R.id.cbCantPlanCasa)).getSelectedItem().toString();

            String cbAntCasa = ((Spinner) vistaAtributos.findViewById(R.id.cbAntCasa)).getSelectedItem().toString();

            String cbEstCasa = ((Spinner) vistaAtributos.findViewById(R.id.cbEstCasa)).getSelectedItem().toString();

            String etFrenteCasa = ((EditText) vistaAtributos.findViewById(R.id.etFrenteCasa)).getText().toString();

            String etLargoCasa = ((EditText) vistaAtributos.findViewById(R.id.etLargoCasa)).getText().toString();

            String etExpCasa = ((EditText) vistaAtributos.findViewById(R.id.etExpCasa)).getText().toString();

            String cbAmuebCasa = ((Spinner) vistaAtributos.findViewById(R.id.cbAmuebCasa)).getSelectedItem().toString();

            try {
                json.put("Cocheras",cochera);
                json.put("CantidadDormitorios",cantDorm);
                json.put("CantidadPlantas",cbCantPlanCasa);
                json.put("Antiguedad",cbAntCasa);
                json.put("Estado",cbEstCasa);
                json.put("Frente",etFrenteCasa);
                json.put("CantidadBanos",cantBanoCasa);
                json.put("Largo",etLargoCasa);
                json.put("Amueblado",cbAmuebCasa);
                json.put("Expensas",etExpCasa);
            } catch (JSONException e) {e.printStackTrace();}
        }

        /* OFICINAS */

        if(producto.toUpperCase().contains("OFICINA"))
        {
            String cbCatOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbCatOfi)).getSelectedItem().toString();

            String cbCantAscOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbCantAscOfi)).getSelectedItem().toString();

            String cbUbiPlOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbUbiPlOfi)).getSelectedItem().toString();

            String cbCantCocOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbCantCocOfi)).getSelectedItem().toString();

            String cbLumiOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbLumiOfi)).getSelectedItem().toString();

            String cbEstOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbEstOfi)).getSelectedItem().toString();

            String cbCanBanOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbCanBanOfi)).getSelectedItem().toString();

            String cbAntigOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbAntigOfi)).getSelectedItem().toString();

            String etDivOfi = ((EditText) vistaAtributos.findViewById(R.id.etDivOfi)).getText().toString();

            String cbBanExcOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbBanExcOfi)).getSelectedItem().toString();

            String cbOfficeOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbOfficeOfi)).getSelectedItem().toString();

            String cbAmuebOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbAmuebOfi)).getSelectedItem().toString();

            String cbEquipOfi = ((Spinner) vistaAtributos.findViewById(R.id.cbEquipOfi)).getSelectedItem().toString();

            String etExpOfi = ((EditText) vistaAtributos.findViewById(R.id.etExpOfi)).getText().toString();

            try {
                json.put("Categoria",cbCatOfi);
                json.put("CantidadAscensores",cbCantAscOfi);
                json.put("UbicacionPlanta",cbUbiPlOfi);
                json.put("Cocheras",cbCantCocOfi);
                json.put("Luminosidad",cbLumiOfi);
                json.put("Estado",cbEstOfi);
                json.put("CantidadBanos",cbCanBanOfi);
                json.put("Antiguedad",cbAntigOfi);
                json.put("Divisiones",etDivOfi);
                json.put("BañosExclusivos",cbBanExcOfi);
                json.put("Office",cbOfficeOfi);
                json.put("Amueblado",cbAmuebOfi);
                json.put("Equipamiento",cbEquipOfi);
                json.put("Expensas",etExpOfi);
            } catch (JSONException e) {e.printStackTrace();}
        }

        /* LOCALES */

        if(producto.toUpperCase().contains("LOCAL"))
        {
            String cbLumLoc = ((Spinner) vistaAtributos.findViewById(R.id.cbLumLoc)).getSelectedItem().toString();

            String cbEstLoc = ((Spinner) vistaAtributos.findViewById(R.id.cbEstLoc)).getSelectedItem().toString();

            String cbCanBanLoc = ((Spinner) vistaAtributos.findViewById(R.id.cbCanBanLoc)).getSelectedItem().toString();

            String cbAntigLoc = ((Spinner) vistaAtributos.findViewById(R.id.cbAntigLoc)).getSelectedItem().toString();

            String cbOfficeLoc = ((Spinner) vistaAtributos.findViewById(R.id.cbOfficeLoc)).getSelectedItem().toString();

            String cbCatLoc = ((Spinner) vistaAtributos.findViewById(R.id.cbCatLoc)).getSelectedItem().toString();

            String cbLoteLoc = ((Spinner) vistaAtributos.findViewById(R.id.cbLoteLoc)).getSelectedItem().toString();

            String cbAptoTodoDest = ((Spinner) vistaAtributos.findViewById(R.id.cbAptoTodoDest)).getSelectedItem().toString();

            String cbAptoGastr = ((Spinner) vistaAtributos.findViewById(R.id.cbAptoGastr)).getSelectedItem().toString();

            String etAnchoPBLoc = ((EditText) vistaAtributos.findViewById(R.id.etAnchoPBLoc)).getText().toString();

            String etFondoPBLoc = ((EditText) vistaAtributos.findViewById(R.id.etFondoPBLoc)).getText().toString();

            String etAnchVidLoc = ((EditText) vistaAtributos.findViewById(R.id.etAnchVidLoc)).getText().toString();

            String etExpLoc = ((EditText) vistaAtributos.findViewById(R.id.etExpLoc)).getText().toString();

            try {
                json.put("Luminosidad",cbLumLoc);
                json.put("Estado",cbEstLoc);
                json.put("CantidadBanos",cbCanBanLoc);
                json.put("Antiguedad",cbAntigLoc);
                json.put("Office",cbOfficeLoc);
                json.put("Categoria",cbCatLoc);
                json.put("LotePropio",cbLoteLoc);
                json.put("TodoDestino",cbAptoTodoDest);
                json.put("Gastronomia",cbAptoGastr);
                json.put("AnchoPB",etAnchoPBLoc);
                json.put("FondoPB",etFondoPBLoc);
                json.put("AnchoVidriera",etAnchVidLoc);
                json.put("Expensas",etExpLoc);
            } catch (JSONException e) {e.printStackTrace();}
        }

        /* TERRENOS */

        if(producto.toUpperCase().contains("TERRENO"))
        {
            String cbDemolicion = ((Spinner) vistaAtributos.findViewById(R.id.cbDemolicion)).getSelectedItem().toString();

            String cbPlaApro = ((Spinner) vistaAtributos.findViewById(R.id.cbPlaApro)).getSelectedItem().toString();

            String etFrenteTer = ((EditText) vistaAtributos.findViewById(R.id.etFrenteTer)).getText().toString();

            String etLargoTer = ((EditText) vistaAtributos.findViewById(R.id.etLargoTer)).getText().toString();

            String etSupEdTer = ((EditText) vistaAtributos.findViewById(R.id.etSupEdTer)).getText().toString();

            String etFot = ((EditText) vistaAtributos.findViewById(R.id.etFot)).getText().toString();

            try {
                json.put("Demolicion",cbDemolicion);
                json.put("PlanosAprobados",cbPlaApro);
                json.put("Frente",etFrenteTer);
                json.put("Largo",etLargoTer);
                json.put("SuperficieEdificable",etSupEdTer);
                json.put("FOT",etFot);
            } catch (JSONException e) {e.printStackTrace();}
        }

        return json;
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

        vistaAtributos = inflater.inflate(R.layout.fragment_atributos, container, false);

        return vistaAtributos;
    }
}
