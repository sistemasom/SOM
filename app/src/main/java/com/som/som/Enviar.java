package com.som.som;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class Enviar extends Fragment {
    public static final String TAG = "Enviar";

    private static final String URL_KEY = "UrlKey";

    private DownloadCallback mCallback;

    private DownloadCallback mUploadCallback;
    private UploadTask mUploadTask;

    public boolean envioValido = false;
    public boolean baja = false;
    public String dataSend = "";
    private String mUrlString;

    public ProgressDialog msjEnviando;

    public static Enviar getInstance(FragmentManager fragmentManager, String url) {
        Enviar networkFragment = (Enviar) fragmentManager
                .findFragmentByTag(Enviar.TAG);
        if (networkFragment == null) {
            networkFragment = new Enviar();
            Bundle args = new Bundle();
            args.putString(URL_KEY, url);
            networkFragment.setArguments(args);
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        }
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mUrlString = getArguments().getString(URL_KEY);

        msjEnviando = new ProgressDialog(getActivity());
        msjEnviando.setMessage("Aguarde un instante por favor...");
        msjEnviando.setTitle("Enviando oferta");
        msjEnviando.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        mCallback = (DownloadCallback)context;
        mUploadCallback = (DownloadCallback)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity.
        mCallback = null;
        mUploadCallback = null;
    }

    @Override
    public void onDestroy() {
        cancelUpload();
        super.onDestroy();
    }

    public void startUpload(boolean valido, boolean esBaja) {

        baja = esBaja;

        envioValido = valido;

        cancelUpload();
        mUploadTask = new UploadTask();
        mUploadTask.execute(mUrlString);
    }

    public void cancelUpload() {
        if (mUploadTask != null) {
            mUploadTask.cancel(true);
            mUploadTask = null;
        }
    }

    public void mostrarDialogo()
    {
        msjEnviando.show();
    }

    private void ocultarDialogo()
    {
        msjEnviando.cancel();
    }

    private class UploadTask extends AsyncTask<String, Integer, UploadTask.Result> {

        class Result {
            public String mResultValue;
            public Exception mException;
            public Result(String resultValue) {
                mResultValue = resultValue;
            }
            public Result(Exception exception) {
                mException = exception;
            }
        }

        @Override
        protected void onPreExecute() {
            if (mUploadCallback != null) {
                NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() ||
                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    mUploadCallback.updateFromUpload(null);
                    cancel(true);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            if (values.length >= 2) {
                mUploadCallback.onProgressUpdate(values[0], values[1]);
            }
        }

        @Override
        protected void onPostExecute(UploadTask.Result result) {
            if (result != null && mUploadCallback != null) {
                if (result.mException != null) {
                    mUploadCallback.updateFromUpload(result.mException.getMessage());
                    Toast.makeText(getActivity(),"Ha ocurrido un error! Intente nuevamente.",Toast.LENGTH_SHORT).show();
                } else if (result.mResultValue != null) {
                    if(envioValido) {
                        mUploadCallback.updateFromUpload(result.mResultValue);
                        mUploadCallback.finishUploading();

                        String resultado = result.mResultValue.substring(0,2);

                        String codigo = result.mResultValue.substring(2);

                        if(resultado.equals("OK")) {
                            if(!baja) {
                                //Borrar todas las fotos
                                Button eliminarFotos = (Button) getActivity().findViewById(R.id.limpiarfotos);
                                eliminarFotos.performClick();
                                TextView txtCodigo = (TextView) getActivity().findViewById(R.id.codigoOferta);
                                txtCodigo.setText(codigo);
                                String mensaje = "Oferta " + codigo + "\n grabada correctamente.";
                                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String mensaje = "Oferta " + codigo + "\n eliminada correctamente.";
                                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                            }
                            ocultarDialogo();
                        }
                        else {
                            ocultarDialogo();
                            Toast.makeText(getActivity(),"No se ha podido grabar la oferta! Intente nuevamente.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }

        @Override
        protected UploadTask.Result doInBackground(String... urls) {
            UploadTask.Result result = null;

            if (!isCancelled()) {

                String myurl = "http://apmovil.som.com.ar/AndroidService.ashx";

                StringBuilder sb = new StringBuilder();

                HttpURLConnection urlConnection=null;

                try {
                    URL url = new URL(myurl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");

                    urlConnection.connect();

                    PrintStream outStream = new PrintStream(urlConnection.getOutputStream());
                    outStream.print(dataSend);
                    outStream.close();

                    int HttpResult = urlConnection.getResponseCode();
                    if(HttpResult == HttpURLConnection.HTTP_OK)
                    {

                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        String respuesta = "OK" + response.toString();

                        sb.append(respuesta);
                        result = new UploadTask.Result(sb.toString());
                    }
                    else
                    {
                        result = new UploadTask.Result(urlConnection.getResponseMessage());
                    }

                }
                catch(Exception e)
                {
                    result = new UploadTask.Result(e);
                }
            }
            return result;
        }
    }
}
