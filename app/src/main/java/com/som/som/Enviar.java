package com.som.som;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class Enviar extends Fragment {
    public static final String TAG = "Enviar";

    private static final String URL_KEY = "UrlKey";

    private DownloadCallback mCallback;

    private ProgressDialog progress;

    private DownloadCallback mUploadCallback;
    private UploadTask mUploadTask;

    public String dataSend = "";

    private String mUrlString;

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
        // Retain this Fragment across configuration changes in the host Activity.
        setRetainInstance(true);
        mUrlString = getArguments().getString(URL_KEY);

        progress = new ProgressDialog(getContext());
        progress.setMessage("Enviando la oferta, por favor espere...");
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

    public void startUpload() {
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
                progress.show();
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
                } else if (result.mResultValue != null) {
                    mUploadCallback.updateFromUpload(result.mResultValue);
                }
                mUploadCallback.finishUploading();
            }
            //Termina el proceso
            progress.dismiss();
            Toast.makeText(getActivity(),"Oferta enviada!",Toast.LENGTH_LONG).show();
        }

        @Override
        protected UploadTask.Result doInBackground(String... urls) {
            UploadTask.Result result = null;

            if (!isCancelled()) {

                String myurl = "http://api2.som.com.ar/AndroidService.ashx";

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

                    //Create JSONObject here
                    //
                    // JSONObject jsonParam = new JSONObject();
                    // jsonParam.put("ID", "25");
                    // jsonParam.put("description", "Real");
                    // jsonParam.put("enable", "true");
                    // OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
                    // out.write(jsonParam.toString());
                    // out.close();

                    int HttpResult = urlConnection.getResponseCode();
                    if(HttpResult == HttpURLConnection.HTTP_OK)
                    {
                        // BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
                        // String line = null;
                        // while ((line = br.readLine()) != null)
                        // {
                        //     sb.append(line + "\n");
                        // }
                        // br.close();

                        sb.append("OK");
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
