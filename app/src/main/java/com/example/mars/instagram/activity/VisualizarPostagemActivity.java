package com.example.mars.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mars.instagram.R;
import com.example.mars.instagram.helper.ConfiguracaoFirebase;
import com.example.mars.instagram.helper.UsuarioFirebase;
import com.example.mars.instagram.model.Feed;
import com.example.mars.instagram.model.Postagem;
import com.example.mars.instagram.model.PostagemCurtida;
import com.example.mars.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {

    private TextView textPerfilPostagem, textQtdCurtidasPostagem,
            textDescricaoPostagem, textVisualizarComentariosPostagem;
    private ImageView imagePostagemSelecionada, visualizacaoComentario;
    private CircleImageView imagePerfilPostagem;
    private LikeButton likeButton;
    private Postagem postagem;
    private Feed feed;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        //Inicializar componentes
        inicializarComponentes();

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Visualizar postagem");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Recuperar dados da activity
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ){

            postagem = (Postagem) bundle.getSerializable("postagem");
            Usuario usuario = (Usuario) bundle.getSerializable("usuario");

            //Exibe dados de usuário
            Uri uri = Uri.parse( usuario.getCaminhoFoto() );
            Glide.with( VisualizarPostagemActivity.this)
                    .load( uri )
                    .into( imagePerfilPostagem );
            textPerfilPostagem.setText( usuario.getNome() );

            //Exibe dados da postagem
            Uri uriPostagem = Uri.parse( postagem.getCaminhoFoto() );
            Glide.with( VisualizarPostagemActivity.this)
                    .load( uriPostagem )
                    .into( imagePostagemSelecionada );
            textDescricaoPostagem.setText( postagem.getDescricao() );

        }

        //feed = (Feed) bundle.getSerializable("postagem");

        //Adiciona evento de clique nos comentários
        visualizacaoComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ComentariosActivity.class);
                i.putExtra("idPostagem", postagem.getId() );
                startActivity( i );
            }
        });

        //Recuperar dados da postagem curtida
        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens-curtidas")
                .child( postagem.getId() );
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int qtdCurtidas = 0;
                if ( dataSnapshot.hasChild("qtdCurtidas") ){
                    PostagemCurtida postagemCurtida = dataSnapshot.getValue(PostagemCurtida.class);
                    qtdCurtidas = postagemCurtida.getQtdCurtidas();
                }

                //Verificar se já foi clicado
                if ( dataSnapshot.hasChild( usuarioLogado.getId() ) ){
                    likeButton.setLiked(true);
                }else {
                    likeButton.setLiked(false);
                }

                //Montar objeto postagem curtida
                final PostagemCurtida curtida = new PostagemCurtida();
                //curtida.setFeed( feed );
                curtida.setUsuario( usuarioLogado );
                curtida.setQtdCurtidas( qtdCurtidas );

                //Adicionar evento para curtir uma foto
                likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {

                        curtida.salvar();
                        textQtdCurtidasPostagem.setText( curtida.getQtdCurtidas() + " Curtidas" );
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {

                        curtida.remover();
                        textQtdCurtidasPostagem.setText( curtida.getQtdCurtidas() + " Curtidas" );

                    }
                });

                textQtdCurtidasPostagem.setText( curtida.getQtdCurtidas() + " Curtidas" );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }//fim do onCreate

    private void inicializarComponentes(){
        textPerfilPostagem = findViewById(R.id.textPerfilPostagem);
        textQtdCurtidasPostagem = findViewById(R.id.textQtdCurtidasPostagem);
        textDescricaoPostagem = findViewById(R.id.textDescricaoPostagem);
        imagePostagemSelecionada = findViewById(R.id.imagePostagemSelecionada);
        imagePerfilPostagem = findViewById(R.id.imagePerfilPostagem);
        visualizacaoComentario = findViewById(R.id.imageComentarioFeed);
        likeButton = findViewById(R.id.likeButtonFeed);
    }//fim do inicializarComponentes

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}//fim da classe
