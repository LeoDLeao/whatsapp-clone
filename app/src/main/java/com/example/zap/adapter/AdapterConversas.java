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
import com.example.zap.model.Conversa;
import com.example.zap.model.Grupo;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterConversas extends RecyclerView.Adapter<AdapterConversas.ViewHolderConversas> {

    private List<Conversa> listaConversas;
    private Context context;

    public AdapterConversas(List<Conversa> listaConversas, Context c) {
        this.listaConversas = listaConversas;
        this.context = c;
    }

    @NonNull
    @Override
    public ViewHolderConversas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversas,parent,false);



        return new ViewHolderConversas(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderConversas holder, int position) {

        Conversa conversa = listaConversas.get(position);

        holder.mensagemConversa.setText(conversa.getUltimaMensagem());
        if(conversa.getIsGroup().equals("true")){

            Grupo grupo = conversa.getGrupo();
            holder.nomeConversa.setText(grupo.getNome());

            if(grupo.getFoto() != null){
                Uri url = Uri.parse(grupo.getFoto());

                Glide.with(context)
                        .load(url)
                        .into(holder.fotoConversa);
            }
            else{
                holder.fotoConversa.setImageResource(R.drawable.padrao);
            }
        }
        else {
            holder.nomeConversa.setText(conversa.getUsuarioExibicao().getNome());

            if(conversa.getUsuarioExibicao().getFoto() != null){
                Uri url = Uri.parse(conversa.getUsuarioExibicao().getFoto());

                Glide.with(context)
                        .load(url)
                        .into(holder.fotoConversa);
            }
            else{
                holder.fotoConversa.setImageResource(R.drawable.padrao);
            }
        }


    }

    @Override
    public int getItemCount() {
        return listaConversas.size();
    }

    public class ViewHolderConversas extends RecyclerView.ViewHolder{

        private CircleImageView fotoConversa;
        private TextView nomeConversa, mensagemConversa;

        public ViewHolderConversas(@NonNull View itemView) {
            super(itemView);

            fotoConversa = itemView.findViewById(R.id.fotoConversa);
            nomeConversa = itemView.findViewById(R.id.textNomeConversa);
            mensagemConversa = itemView.findViewById(R.id.textMensagemConversa);

        }
    }
}
