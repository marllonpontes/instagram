package com.example.mars.instagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mars.instagram.R;
import com.example.mars.instagram.activity.ComentariosActivity;
import com.example.mars.instagram.helper.ConfiguracaoFirebase;
import com.example.mars.instagram.helper.UsuarioFirebase;
import com.example.mars.instagram.model.Feed;
import com.example.mars.instagram.model.PostagemCurtida;
import com.example.mars.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {

    private List<Feed> listaFeed;
    private Context context;

    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);
        return new AdapterFeed.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Feed feed = listaFeed.get( position );
        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Carregar dados do feed
        Uri uriFotoUsuario = Uri.parse( feed.getFotoUsuario() );
        Uri uriFotoPostagem = Uri.parse( feed.getFotoPostagem() );

        Glide.with( context ).load( uriFotoUsuario ).into(holder.fotoPerfil);
        Glide.with( context ).load( uriFotoPostagem ).into(holder.fotosPostagem);

        holder.descricao.setText( feed.getDescricao() );
        holder.nome.setText( feed.getNomeUsuario() );

        //Adiciona evento de clique nos comentários
        holder.visualizacaoComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ComentariosActivity.class);
                i.putExtra("idPostagem", feed.getId() );
                context.startActivity( i );
            }
        });

        /*
        postagem-curtidas
            id_postagem
                qtdCurtidas
                id_usuario
                    nome_usuario
                    caminho_foto
         */
        //Recuperar dados da postagem curtida
        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens-curtidas")
                .child( feed.getId() );
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
                    holder.likeButton.setLiked(true);
                }else {
                    holder.likeButton.setLiked(false);
                }

                //Montar objeto postagem curtida
                final PostagemCurtida curtida = new PostagemCurtida();
                curtida.setFeed( feed );
                curtida.setUsuario( usuarioLogado );
                curtida.setQtdCurtidas( qtdCurtidas );

                //Adicionar evento para curtir uma foto
                holder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {

                        curtida.salvar();
                        holder.qtdCurtidas.setText( curtida.getQtdCurtidas() + " Curtidas" );
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {

                        curtida.remover();
                        holder.qtdCurtidas.setText( curtida.getQtdCurtidas() + " Curtidas" );

                    }
                });

                holder.qtdCurtidas.setText( curtida.getQtdCurtidas() + " Curtidas" );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas;
        ImageView fotosPostagem, visualizacaoComentario;
        LikeButton likeButton;

        public MyViewHolder(View itemView){
            super(itemView);

            fotoPerfil    = itemView.findViewById(R.id.imagePerfilPostagem);
            fotosPostagem = itemView.findViewById(R.id.imagePostagemSelecionada);
            nome          = itemView.findViewById(R.id.textPerfilPostagem);
            qtdCurtidas   = itemView.findViewById(R.id.textQtdCurtidasPostagem);
            descricao     = itemView.findViewById(R.id.textDescricaoPostagem);
            visualizacaoComentario     = itemView.findViewById(R.id.imageComentarioFeed);
            likeButton = itemView.findViewById(R.id.likeButtonFeed);

        }

    }

}
