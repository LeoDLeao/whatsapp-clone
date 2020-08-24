package com.example.zap.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zap.R;
import com.example.zap.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterGrupoSelecionado extends RecyclerView.Adapter<AdapterGrupoSelecionado.ViewHolderGrupoSelecionado> {

    private List<Usuario> listaMembrosSelecionados;
    private Context context;

    public AdapterGrupoSelecionado(List<Usuario> listaMembrosSelecionados, Context context ){
        this.context = context;
        this.listaMembrosSelecionados = listaMembrosSelecionados;

    }


    @NonNull
    @Override
    public ViewHolderGrupoSelecionado onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_grupo_selecionado,parent,false);

        return new ViewHolderGrupoSelecionado(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGrupoSelecionado holder, int position) {

        Usuario membroSelecionado = listaMembrosSelecionados.get(position);

        holder.textNomeMembroSelecionado.setText(membroSelecionado.getNome());

        if(membroSelecionado.getFoto() != null){

            Uri url = Uri.parse(membroSelecionado.getFoto());

            Glide.with(context)
                    .load(url)
                    .into(holder.fotoMembroSelecionado);

        }else{
            holder.fotoMembroSelecionado.setImageResource(R.drawable.padrao);
        }


    }

    @Override
    public int getItemCount() {
        return listaMembrosSelecionados.size();
    }

    public class ViewHolderGrupoSelecionado extends RecyclerView.ViewHolder{

        public TextView textNomeMembroSelecionado;
        public CircleImageView fotoMembroSelecionado;

        public ViewHolderGrupoSelecionado(@NonNull View itemView) {
            super(itemView);

            textNomeMembroSelecionado = itemView.findViewById(R.id.textNomeMembroSelecionado);
            fotoMembroSelecionado = itemView.findViewById(R.id.fotoMembroSelecionado);
        }
    }
}
