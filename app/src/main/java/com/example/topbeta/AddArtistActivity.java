package com.example.topbeta;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddArtistActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int RC_PHOTO_PICKER =17;

    @BindView(R.id.imgFoto)
    AppCompatImageView imgFoto;
    @BindView(R.id.etNombre)
    TextInputEditText etNombre;
    @BindView(R.id.etApellidos)
    TextInputEditText etApellidos;
    @BindView(R.id.etFechaNacimiento)
    TextInputEditText etFechaNacimiento;
    @BindView(R.id.etEstatura)
    TextInputEditText etEstatura;
    @BindView(R.id.etLugarNacimiento)
    TextInputEditText etLugarNacimiento;
    @BindView(R.id.etNotas)
    TextInputEditText etNotas;


    private Artista mArtista;
    private Calendar mCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artist);
        ButterKnife.bind(this);

        configActionBar();
        configArtista(getIntent());
        configCalendar();

    }

    private void configCalendar() {
        mCalendar = Calendar.getInstance(Locale.ROOT);
        etFechaNacimiento.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(
                System.currentTimeMillis()));

    }

    private void configArtista(Intent intent) {
        mArtista = new Artista();
        mArtista.setFechaNacimiento(System.currentTimeMillis());
        mArtista.setOrden(intent.getIntExtra(Artista.ORDEN, 0));
    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                saveArtist();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveArtist() {
        if (ValidateFields()) {
            mArtista.setNombre(etNombre.getText().toString().trim());
            mArtista.setApellidos(etApellidos.getText().toString().trim());
            mArtista.setEstatura(Short.valueOf(etEstatura.getText().toString().trim()));
            mArtista.setLugarNacimiento(etLugarNacimiento.getText().toString().trim());
            mArtista.setNotas(etNotas.getText().toString().trim());
            try {
                mArtista.save();
                Log.i("DBFlow","Insercion correcta de datos.");
            }catch (Exception e){
                e.printStackTrace();
                Log.i("DBFlow","Error al insertar datos.");
            }
            /*MainActivity.sArtista.setNombre(etNombre.getText().toString().trim());
            MainActivity.sArtista.setApellidos(etApellidos.getText().toString().trim());
            MainActivity.sArtista.setEstatura(Short.valueOf(etEstatura.getText().toString().trim()));
            MainActivity.sArtista.setLugarNacimiento(etLugarNacimiento.getText().toString().trim());
            MainActivity.sArtista.setNotas(etNotas.getText().toString().trim());
            MainActivity.sArtista.setOrden(mArtista.getOrden());
            MainActivity.sArtista.setFotoUrl(mArtista.getFotoUrl());
            setResult(RESULT_OK);*/
            finish();
        }

    }

    private boolean ValidateFields() {
        boolean isValid=true;
        if (etEstatura.getText().toString().trim().isEmpty() ||
        Integer.valueOf(etEstatura.getText().toString().trim())< getResources().getInteger(R.integer.estatura_min)){
            etEstatura.setError(getString(R.string.addArtist_error_estaturaMin));
            etEstatura.requestFocus();
            isValid=false;
        }
        if (etApellidos.getText().toString().trim().isEmpty()){
            etApellidos.setError(getString(R.string.addArtist_error_required));
            etApellidos.requestFocus();
            isValid=false;
        }
        if (etNombre.getText().toString().trim().isEmpty()){
            etNombre.setError(getString(R.string.addArtist_error_required));
            etNombre.requestFocus();
            isValid=false;
        }

        return isValid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){

            switch (requestCode){
                case RC_PHOTO_PICKER:
                    configImageView(data.getDataString());
            }

        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        etFechaNacimiento.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(
                mCalendar.getTimeInMillis()));

        mArtista.setFechaNacimiento(mCalendar.getTimeInMillis());
    }

    @OnClick(R.id.etFechaNacimiento)
    public void onSetfecha() {
        DialogSelectorFecha selectorFecha = new DialogSelectorFecha();
        selectorFecha.setListener(AddArtistActivity.this);

        Bundle args = new Bundle();
        args.putLong(DialogSelectorFecha.FECHA, mArtista.getFechaNacimiento());
        selectorFecha.setArguments(args);
        selectorFecha.show(getSupportFragmentManager(), DialogSelectorFecha.SELECTED_DATE);

    }

    @OnClick({R.id.imgDeleteFoto, R.id.imgFromGallery, R.id.imgFromUrl})
    public void imageEvents(View view) {
        switch (view.getId()) {
            case R.id.imgDeleteFoto:

                AlertDialog.Builder builder =new AlertDialog.Builder(this)
                        .setTitle(R.string.detalle_dialogDelete_title)
                        .setMessage(String.format(Locale.ROOT ,
                                getString(R.string.detalle_dialogDelete_message),
                                mArtista.getNombreCompleto()))
                        .setPositiveButton(R.string.label_dialog_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                configImageView(null);
                            }
                        })
                        .setNegativeButton(R.string.label_dialog_cancel,null);
                builder.show();
                break;
            case R.id.imgFromGallery:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.detalle_chooser_title)),RC_PHOTO_PICKER);
                break;
            case R.id.imgFromUrl:
                showAddPhotoDialog();
                break;
        }
    }

    private void showAddPhotoDialog() {
        final EditText etFotoUrl = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.addArtist_dialogUrl_title)
                .setPositiveButton(R.string.label_dialog_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        configImageView(etFotoUrl.getText().toString().trim());
                    }
                })
                .setNegativeButton(R.string.label_dialog_cancel,null);
        builder.setView(etFotoUrl);
        builder.show();
    }

    private void configImageView(String fotoUrl) {
        if (fotoUrl!=null){
            RequestOptions options=new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop();

            Glide.with(this).load(fotoUrl).apply(options).into(imgFoto);
        }else{
            imgFoto.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_photo_size_select_actual));
        }
        mArtista.setFotoUrl(fotoUrl);
    }
}
