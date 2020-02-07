package com.example.mars.instagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mars.instagram.R;
import com.example.mars.instagram.model.Comentario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.MyViewHolder> {

    private List<Comentario> listaComentarios;
    private Context context;

    public AdapterComentario(List<Comentario> listaComentarios, Context context) {
        this.listaComentarios = listaComentarios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comentario, parent, false);
        return new AdapterComentario.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Comentario comentario = listaComentarios.get( position );

        holder.nomeUsuario.setText( comentario.getNomeUsuario() );
        holder.comentario.setText( comentario.getComentario() );

        if ( comentario.getCaminhoFoto() != null ){
            Uri uri = Uri.parse( comentario.getCaminhoFoto() );
            Glide.with(context).load(uri).into(holder.imagemPerfil);
        }else {
            holder.imagemPerfil.setImageResource(R.drawable.avatar);
        }

    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imagemPerfil;
        TextView nomeUsuario, comentario;

        public MyViewHolder(View itemView){
            super(itemView);

            imagemPerfil = itemView.findViewById(R.id.imageFotoComentario);
            nomeUsuario = itemView.findViewById(R.id.textNomeComentario);
            comentario = itemView.findViewById(R.id.textComentario);

        }

    }


}
