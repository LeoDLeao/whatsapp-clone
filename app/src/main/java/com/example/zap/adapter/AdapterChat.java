package com.example.zap.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zap.R;
import com.example.zap.firebase.UsuarioFirebase;
import com.example.zap.model.Mensagem;

import java.util.List;

public class AdapterChat  extends RecyclerView.Adapter<AdapterChat.ViewHolderChat> {

    private List<Mensagem> mensagens;
    private Context context;

    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public AdapterChat (List<Mensagem> lista, Context c){
        this.mensagens = lista;
        this.context = c;

    }

    @NonNull
    @Override
    public ViewHolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;

        if(viewType == TIPO_REMETENTE){

            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chat_remetente,parent,false);
        }
        else if(viewType == TIPO_DESTINATARIO){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chat_destinatario,parent,false);

        }

        return new ViewHolderChat(item);



    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderChat holder, int position) {

        Mensagem mensagem = mensagens.get(position);
        String msg = mensagem.getTextoMensagem();
        String imagem = mensagem.getImagem();

        if(imagem != null){
            Uri url = Uri.parse(imagem);
            Glide.with(context)
                    .load(url)
                    .into(holder.imagemMensagem);

            holder.textMensagem.setVisibility(View.GONE);


        }
        holder.textMensagem.setText(msg);
        holder.imagemMensagem.setVisibility(View.GONE);



    }

    @Override
    public int getItemCount() {

        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {

        Mensagem mensagem = mensagens.get(position);
        String idUsuario = UsuarioFirebase.getIdUsuario();

        if(idUsuario.equals(mensagem.getIdUsuario())){

            return TIPO_REMETENTE;
        }

        return TIPO_DESTINATARIO;
    }

    public class ViewHolderChat extends RecyclerView.ViewHolder {

        TextView textMensagem;
        ImageView imagemMensagem;


        public ViewHolderChat(@NonNull View itemView) {

            super(itemView);

            textMensagem = itemView.findViewById(R.id.textMensagem);
            imagemMensagem = itemView.findViewById(R.id.imageMensagem);



        }
    }
}
