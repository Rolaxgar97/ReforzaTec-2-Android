package com.appsenmoviles.reforzatec_2_android;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class InicioSesion extends AppCompatActivity implements View.OnClickListener {

    private Button botonIngresar,botonRegistrarse,botonRestablecer;
    private TextView TextCorreo,TextContraseña,TextVerificacionContraseña;

    private ProgressDialog procesando;

    private FirebaseAuth Autenticacion; //declaracion de la instancia de Firebase para autentificar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        Autenticacion=FirebaseAuth.getInstance();//se inicializa la instancia

        procesando=new ProgressDialog(this);

        //inicializacion de los botones
        botonIngresar=(Button) findViewById(R.id.botonIngresar);
        botonRegistrarse=(Button) findViewById(R.id.botonRegistrarse);
        botonRestablecer=(Button) findViewById(R.id.botonRestablecer);

        //agregamos el escuchador
        botonIngresar.setOnClickListener(this);
        botonRegistrarse.setOnClickListener(this);
        botonRestablecer.setOnClickListener(this);

        //inicializacion de las cajas de texto
        TextCorreo=(TextView) findViewById(R.id.TextCorreo);
        TextContraseña=(TextView) findViewById(R.id.TextContraseña);
    }

    //Verifica si algun usuario tiene sesion iniciada
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = Autenticacion.getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        String correo=TextCorreo.getText().toString(),contraseña=TextContraseña.getText().toString();//se pasa el valor de las cajas a esas dos variables

        switch (v.getId())
        {
            case R.id.botonIngresar:
                Acceder(correo,contraseña);
            break;

            case R.id.botonRegistrarse:
                AlertVerificacionContraseña(correo,contraseña);
            break;

            case R.id.botonRestablecer:
            {
                if(TextUtils.isEmpty(correo))
                {
                    AlertResetContraseña(R.string.alertFaltaCorreoReset);
                }
                else RestablecerContraseña(correo);
            }//R.string.mensajeLimpieza
            break;
        }
    }

    void AlertVerificacionContraseña(final String correo, final String contraseña)
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(R.string.alertValidacionContraseña);
        TextVerificacionContraseña= new EditText(this);
        TextVerificacionContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());

        builder.setView(TextVerificacionContraseña);
        builder.setPositiveButton(R.string.botonRegistrar, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(TextContraseña.getText().toString().equals(TextVerificacionContraseña.getText().toString()))
                    CreandoUsuario(correo,contraseña);

                else
                {
                    Toast.makeText(getApplication(),R.string.alertContraseñasDiferentes,Toast.LENGTH_SHORT).show();
                    AlertVerificacionContraseña(correo,contraseña);
                }

            }
        });
        builder.setNegativeButton(android.R.string.cancel,null);
        Dialog dialogo=builder.create();
        dialogo.show();
    }

    void CreandoUsuario(String Correo, String Contraseña){
        if(TextUtils.isEmpty(Correo) || TextUtils.isEmpty(Contraseña))
            Toast.makeText(this,R.string.camposObligatorios, Toast.LENGTH_SHORT).show();

        else
        {
            procesando.setMessage(getResources().getString(R.string.dialogRegistrando)); procesando.show();

            Autenticacion.createUserWithEmailAndPassword(Correo,Contraseña).
                    addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                InterfazMenu();
                            } else {
                                //para detectar cuando ya se ha creado una cuenta con el mismo correo
                                if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                    Toast.makeText(InicioSesion.this, R.string.colisionCorreo,Toast.LENGTH_SHORT).show();

                                else
                                    Toast.makeText(InicioSesion.this, R.string.mensajeError,Toast.LENGTH_SHORT).show();
                            }

                            procesando.dismiss();
                        }
                    });
        }
    }

    void Acceder(String Correo, String Contraseña)
    {
        if(TextUtils.isEmpty(Correo) || TextUtils.isEmpty(Contraseña))
            Toast.makeText(this,R.string.camposObligatorios, Toast.LENGTH_SHORT).show();

        else
        {
           procesando.setMessage(getResources().getString(R.string.dialogIngresando)); procesando.show();

            Autenticacion.signInWithEmailAndPassword(Correo,Contraseña).
                    addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                InterfazMenu();
                            } else {
                                Toast.makeText(InicioSesion.this, R.string.mensajeError,Toast.LENGTH_SHORT).show();
                            }

                            procesando.dismiss();
                        }
                    });
        }

    }

    void RestablecerContraseña(String correo)
    {
        try {
            Autenticacion.sendPasswordResetEmail(correo);
            AlertResetContraseña(R.string.alertNotificacionMensajeReset);
        }catch (Exception e)
        {
            AlertResetContraseña(R.string.alertErrorReset);
        }
    }

    void AlertResetContraseña(int mensaje)
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(mensaje);
        builder.setPositiveButton("Ok", null);
        Dialog dialogo=builder.create();
        dialogo.show();
    }

    void InterfazMenu()
    {
        Intent siguienteVista=new Intent(getApplicationContext(),Menu.class);
        startActivity(siguienteVista);
    }
}
