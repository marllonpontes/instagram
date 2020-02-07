package com.example.mars.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mars.instagram.R;
import com.example.mars.instagram.adapter.AdapterComentario;
import com.example.mars.instagram.helper.ConfiguracaoFirebase;
import com.example.mars.instagram.helper.UsuarioFirebase;
import com.example.mars.instagram.model.Comentario;
import com.example.mars.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {

    private EditText editComentario;
    private RecyclerView recyclerComentarios;

    private String idPostagem;
    private Usuario usuario;
    private AdapterComentario adapterComentario;
    private List<Comentario> listaComentarios = new ArrayList<>();

    private DatabaseReference firebaseref;
    private DatabaseReference comentarioRef;
    private ValueEventListener valueEventListenerComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //Inicializar componentes
        editComentario = findViewById(R.id.editComentario);
        recyclerComentarios = findViewById(R.id.recyclerComentarios);

        //Configurações iniciais
        usuario = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseref = ConfiguracaoFirebase.getFirebase();


        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentários");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Configura recyclerview
        adapterComentario = new AdapterComentario( listaComentarios, getApplicationContext() );
        recyclerComentarios.setHasFixedSize( true );
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerComentarios.setAdapter( adapterComentario );

        //Recupera id da postagem
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null){
            idPostagem = bundle.getString( "idPostagem" );
        }

    }

    private void recuperarComentarios(){

        comentarioRef = firebaseref.child("comentarios")
                .child( idPostagem );
        valueEventListenerComentarios = comentarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaComentarios.clear();
                for ( DataSnapshot ds: dataSnapshot.getChildren() ){
                    listaComentarios.add( ds.getValue(Comentario.class) );
                }
                adapterComentario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }//fim do recuperarComentarios

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentarioRef.removeEventListener( valueEventListenerComentarios );
    }

    public void salvarComentario(View view){

        String textoComentario = editComentario.getText().toString();
        if ( textoComentario != null && !textoComentario.equals("") ){

            esconderTeclado( getApplicationContext(), editComentario );

            Comentario comentario = new Comentario();
            comentario.setIdPostagem( idPostagem );
            comentario.setIdUsuario( usuario.getId() );
            comentario.setNomeUsuario( usuario.getNome() );
            comentario.setCaminhoFoto( usuario.getCaminhoFoto() );
            comentario.setComentario( textoComentario );
            if (comentario.salvar()){
                Toast.makeText(this, "Comentário salvo com sucesso!",
                        Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, "escreva um comentário!",
                    Toast.LENGTH_SHORT).show();
        }

        //Limpar comentário digitado
        editComentario.setText("");

    }//fim do salvarComentario

    public static void esconderTeclado(Context context, View editText){

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
