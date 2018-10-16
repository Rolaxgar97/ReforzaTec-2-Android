package com.appsenmoviles.reforzatec_2_android;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Menu extends AppCompatActivity implements View.OnClickListener {

    TextView TextBienvenidos;
    Button botonSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextBienvenidos= (TextView) findViewById(R.id.TextBienvenidos);
        botonSalir=(Button) findViewById(R.id.botonCerrarSesion);

        botonSalir.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AlertaCerrarSesion();
    }

    //para detectar cuando se precione el boton atras
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            AlertaCerrarSesion();
        }
        return super.onKeyDown(keyCode, event);
    }

    void AlertaCerrarSesion()
    {
        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("¿Desea cerrar la sesion?");
        builder.setPositiveButton("Cerrar", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplication(),InicioSesion.class));
            }
        });
        builder.setNegativeButton(android.R.string.cancel,null);
        Dialog dialogo=builder.create();
        dialogo.show();
    }
}
