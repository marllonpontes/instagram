package com.example.mars.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mars.instagram.R;
import com.example.mars.instagram.helper.ConfiguracaoFirebase;
import com.example.mars.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private ProgressBar progressBar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //verificarUsuarioLogado();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticao();

        iniciarlizarComponentes();

        progressBar.setVisibility(View.GONE);
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                    if ( !textoEmail.isEmpty() ){
                        if ( !textoSenha.isEmpty() ){

                            usuario = new Usuario();
                            usuario.setEmail( textoEmail );
                            usuario.setSenha( textoSenha );
                            validarLogin( usuario );

                        }else {
                            Toast.makeText(LoginActivity.this,"Preencha a senha!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(LoginActivity.this,"Preencha o e-mail!", Toast.LENGTH_SHORT).show();
                    }

            }
        });

    }//fim do onCreate


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if ( usuarioAtual != null ){
            abrirTelaPrincipal();
        }
    }

    /*
    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticao();
        if ( autenticacao.getCurrentUser() != null ){

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }
    }//fim do verificarUsuarioLogado

     */

    public void validarLogin( Usuario usuario ){

        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticao();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ){
                    progressBar.setVisibility(View.GONE);
                    abrirTelaPrincipal();
                    //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

                }else {

                    Toast.makeText(LoginActivity.this,
                            "Erro ao fazer login",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                }

            }
        });

    }//fim do validarLogin

    public void abrirCadastro(View view){

        Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity( i );

    }//fim abrirCadastro

    private void iniciarlizarComponentes() {

        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        botaoEntrar = findViewById(R.id.buttonEntrar);
        progressBar = findViewById(R.id.progressLogin);

    }//fim do iniciarlizarComponentes

    public void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity( intent );
        finish();
    }

}//fim da classe
