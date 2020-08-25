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

public class AdapterContatos extends RecyclerView.Adapter<AdapterContatos.ViewHolderContatos> {

    private List<Usuario> contatos;
    private Context context;


    public AdapterContatos(List<Usuario> contatos, Context c) {
        this.contatos = contatos;
        this.context = c;
    }

    public List<Usuario> getContatos(){
        return this.contatos;
    }

    @NonNull
    @Override
    public ViewHolderContatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent,false);

        return  new ViewHolderContatos(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderContatos holder, int position) {

        Usuario usuario = contatos.get(position);
        boolean cabecalho = usuario.getEmail().isEmpty();



        holder.textNomeContato.setText(usuario.getNome());
        holder.textEmailContato.setText(usuario.getEmail());

        if(usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());

            Glide.with(context)
                    .load(uri)
                    .into(holder.fotoContato);
        }
        else {
            if(cabecalho){
                holder.fotoContato.setImageResource(R.drawable.icone_grupo);
                holder.textEmailContato.setVisibility(View.GONE);
            }else {
                holder.fotoContato.setImageResource(R.drawable.padrao);

            }
        }



    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class ViewHolderContatos extends RecyclerView.ViewHolder {

        public TextView textNomeContato, textEmailContato;
        public CircleImageView fotoContato;

        public ViewHolderContatos(@NonNull View itemView) {
            super(itemView);

            fotoContato = itemView.findViewById(R.id.fotoContato);
            textNomeContato = itemView.findViewById(R.id.textNomeContato);
            textEmailContato = itemView.findViewById(R.id.textEmailContato);

        }
    }
}
