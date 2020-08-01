package com.example.zap.activities;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.zap.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zap.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textNomeContato;
    private CircleImageView FotoContato;

    private Usuario usuarioDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textNomeContato = findViewById(R.id.textNomeContatoChat);
        FotoContato = findViewById(R.id.circleImageFotoChat);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            usuarioDestinatario = (Usuario) bundle.getSerializable("contato");

            textNomeContato.setText(usuarioDestinatario.getNome());

            if(usuarioDestinatario.getFoto() != null){
                Uri uriFotoContato = Uri.parse(usuarioDestinatario.getFoto());

                Glide.with(ChatActivity.this)
                        .load(uriFotoContato)
                        .into(FotoContato);
            }else {
                FotoContato.setImageResource(R.drawable.padrao);
            }


        }



    }

}
