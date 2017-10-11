package com.som.som;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class Fotos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static int RESULT_LOAD_IMG = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String fotos = "";

    LinearLayout main; //Layout dinámico para link de imagenes

    private int idFoto = 0;

    private View vistaFotos;

    private class Foto {
        public int id;
        public Bitmap thumbnail;
        public Bitmap fullImage;
    }

    ArrayList<Foto> aFotos = new ArrayList<Foto>();

    private String pictureImagePath = "";

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    ImageButton button;
    private OnFragmentInteractionListener mListener;

    public Fotos() {
    }

    // TODO: Rename and change types and number of parameters
    public static Fotos newInstance(String param1, String param2) {
        Fotos fragment = new Fotos();
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
        vistaFotos = inflater.inflate(R.layout.fragment_fotos, container, false);
        button = (ImageButton) vistaFotos.findViewById(R.id.btnFoto);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        main = (LinearLayout) vistaFotos.findViewById(R.id.layoutFotos);

        return vistaFotos;
    }
    private static final int PICK_IMAGE = 1;

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Uri imageUri = data.getData();

            try {
                InputStream image_stream = getContext().getContentResolver().openInputStream(imageUri);
                Bitmap fullImage = BitmapFactory.decodeStream(image_stream);

                Foto foto = new Foto();

                int ancho = fullImage.getWidth();
                int alto = fullImage.getHeight();
                float proporcion = (float)calcularProporcion(ancho,alto);

                foto.fullImage = redimensionarImagen(fullImage,ancho*proporcion,alto*proporcion);
                foto.thumbnail = redimensionarImagen(fullImage,ancho*(float)0.15,alto*(float)0.15);
                foto.id = idFoto;
                aFotos.add(foto);
                //Agrego la foto a la vista actual
                main.addView(crearFoto(foto));

                idFoto++; //Incremento el ID
            }
            catch (FileNotFoundException ex) {
            }
        }
    }

    private double calcularProporcion(int ancho, int alto) {

        double proporcion = 0.0;
        int maxAncho = 800;
        int maxAlto = 600;

        if(ancho > alto) {
            //La imagen es horizontal
            proporcion = ((double)maxAncho/ancho);
        }
        else
        {
            //La imagen es vertical
            proporcion = ((double)maxAlto/alto);
        }
        return proporcion;
    }

    public void obtenerFotos()
    {
        //Recorro el arraylist con las fotos
        for(int i = 0;i < aFotos.size(); i++) {
            Bitmap bitmap = aFotos.get(i).fullImage;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] imageInByte = baos.toByteArray();
            fotos = fotos + "Foto" + Base64.encodeToString(imageInByte, Base64.NO_WRAP);
        }
    }

    public Bitmap redimensionarImagen(Bitmap bm, float ancho, float alto) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = (ancho) / width;
        float scaleHeight = (alto) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    private ImageView crearFoto(final Foto foto)
    {
        final ImageView ivFoto = new ImageView(getContext());
        ivFoto.setImageBitmap(foto.thumbnail);
        ivFoto.setId(idFoto);
        ivFoto.setPadding(5,5,5,5);
        ivFoto.setClickable(true);
        //Click comun
        ivFoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int posicion = aFotos.indexOf(foto);
                Foto fotoAux = aFotos.get(posicion);
                fotoAux.thumbnail = rotarImagen(fotoAux.thumbnail);
                fotoAux.fullImage = rotarImagen(fotoAux.fullImage);

                aFotos.set(posicion,fotoAux);

                //Seteo el thumbnail rotado
                ivFoto.setImageBitmap(fotoAux.thumbnail);

                Toast.makeText(getContext(), "Imagen " + foto.id + " rotada.", Toast.LENGTH_SHORT).show();
            }
        });
        //Click largo
        ivFoto.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                //Elimina la foto

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("¿Desea eliminar la imagen?")
                        .setTitle("Eliminar")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                main.removeView(ivFoto);
                                //Borrar foto de la lista para enviar
                                int posicion = aFotos.indexOf(foto);
                                aFotos.remove(posicion);
                                Toast.makeText(getContext(), "Imagen eliminada", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.create().show();
                return true;
            }
        });
        return ivFoto;
    }

    public void obtenerValores(JSONObject json) {
        fotos = ""; //Limpio el string de fotos
        obtenerFotos();
        try {
            json.put("Fotos",fotos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotarImagen(Bitmap bitmap)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void EnviarFoto(Bitmap foto);
    }

}
