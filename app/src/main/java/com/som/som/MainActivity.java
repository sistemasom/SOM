package com.som.som;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements DownloadCallback, Fotos.OnFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Inicio fragInicio;
    private Inventario fragInventario;
    private Producto fragProducto;
    private Ubicacion fragUbicacion;
    private Fotos fragFotos;
    private Operacion fragOperacion;
    private Atributos fragAtributos;
    private String Token;

    private JSONObject jsonOferta;

    public boolean valido;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setTitle("   SOM");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        final Enviar fragEnviar = Enviar.getInstance(getSupportFragmentManager(),"https://google.com.ar");

        mViewPager = (ViewPager) findViewById(R.id.container);
        //Cantidad de pantallas.
        mViewPager.setOffscreenPageLimit(7);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        InstanciarFragmentos();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Traigo valores de la propiedad

                Toast.makeText(getApplicationContext(),"Enviando oferta...",Toast.LENGTH_SHORT).show();
                new CountDownTimer(1000, 1000) {
                    public void onFinish() {
                        jsonOferta = fragProducto.obtenerValores();
                        fragUbicacion.obtenerValores(jsonOferta);
                        fragAtributos.obtenerValores(jsonOferta);
                        fragOperacion.obtenerValores(jsonOferta);
                        fragFotos.obtenerValores(jsonOferta);

                        valido = validarEnvio();

                        try {
                            jsonOferta.put("Token",Token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(valido) {
                            //Seteo el texto a enviar
                            fragEnviar.dataSend = jsonOferta.toString();
                            fragEnviar.startUpload(valido,false);
                        }
                    }

                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            trimCache(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
        }
    }

    public boolean validarEnvio()
    {
        boolean permitido = false;

        Token = getToken();

        if(Token != "")
        {
            if(fragUbicacion.validarCampos() && fragProducto.validarCampos() && fragOperacion.validarCampos())
            {
                permitido = true;
            }
            else
            {
                Toast.makeText(getBaseContext(),"Error al enviar. Hay campos obligatorios sin completar.",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getBaseContext(),"Error al enviar. Debe ingresar un código de autorización para continuar.",Toast.LENGTH_SHORT).show();
        }

        return permitido;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ingrese el código de autorización");
            builder.setCancelable(true);
            final EditText inputToken = new EditText(MainActivity.this);
            inputToken.setText(getToken());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            inputToken.setLayoutParams(lp);
            builder.setView(inputToken);

            builder.setPositiveButton(
                    "Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Guardo el Token en el archivo de configuración
                            Token = inputToken.getText().toString();
                            guardarToken(Token);
                            if(fragInicio.validarToken())
                            {
                                Toast.makeText(getBaseContext(), "¡Código autorizado!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            builder.setNegativeButton(
                    "Cancelar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    private void InstanciarFragmentos() {
        fragInicio = Inicio.newInstance(null,null);
        fragInventario = Inventario.newInstance(null,null);
        fragProducto = Producto.newInstance(null,null);
        fragUbicacion = Ubicacion.newInstance(null, null);
        fragOperacion = Operacion.newInstance(null, null);
        fragFotos = Fotos.newInstance(null,null);
        fragAtributos = Atributos.newInstance(null,null);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: {
                    return fragInicio;
                }
                case 1: {
                    return fragInventario;
                }
                case 2: {
                    return fragProducto;
                }
                case 3: {
                    return fragOperacion;
                }
                case 4: {
                    return fragAtributos;
                }
                case 5: {
                    return fragUbicacion;
                }
                case 6: {
                    return fragFotos;
                }
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "INICIO";
                case 1:
                    return "INVENTARIO";
                case 2:
                    return "PRODUCTO";
                case 3:
                    return "UBICACION";
                case 4:
                    return "ATRIBUTOS";
                case 5:
                    return "OPERACION";
                case 6:
                    return "FOTOS";
            }
            return null;
        }
    }

    // FRAGMENT DE ENVIO

    private Enviar mNetworkFragment;

    private boolean mUploading = false;

    private void startUpload() {
        if (!mUploading && mNetworkFragment != null) {
            // Execute the async upload.
            mNetworkFragment.startUpload(valido,false);
            mUploading = true;
        }
    }

    @Override
    public void updateFromUpload(String result) {

    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void finishUploading() {
        mUploading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelUpload();
        }
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            case Progress.ERROR:
                break;
            case Progress.CONNECT_SUCCESS:
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Salir")
                .setMessage("¿Está seguro?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void EnviarFoto(Bitmap foto)
    {
        //
    }

    // Configuración de TOKEN
    public void guardarToken(String token) {

        try {
            OutputStreamWriter fout = new OutputStreamWriter(openFileOutput("config.txt", this.MODE_PRIVATE));
            String string = token;
            fout.write(string);
            fout.close();
            Toast.makeText(this, "Configuración guardada correctamente.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "No se ha podido guardar la configuración. Intente nuevamente.", Toast.LENGTH_SHORT).show();
        }
    }

    public final String getToken()
    {
        String texto = "";
        try{
            BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput("config.txt")));
            texto = fin.readLine();
            fin.close();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "No se ha ingresado un código de autorización, debe ingresar uno para continuar.", Toast.LENGTH_SHORT).show();
        }

        return texto;
    }
}
