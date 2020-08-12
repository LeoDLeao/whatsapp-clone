package com.example.zap.activities;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.zap.adapter.AdapterChat;
import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.firebase.UsuarioFirebase;
import com.example.zap.helper.Base64Custom;
import com.example.zap.model.Mensagem;
import com.example.zap.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zap.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textNomeContato;
    private CircleImageView FotoContato;

    private FloatingActionButton fabEnviarChat;

    private EditText editTextChat;

    private RecyclerView recyclerView;
    private AdapterChat adapterChat;

    private Usuario usuarioDestinatario;

    private List<Mensagem> mensagens = new ArrayList<>();

    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference listaMensagemsRef;
    private String idDestinatario;
    private String idRemetente = ConfiguracaoFirebase.getIdUsuario();

    private ChildEventListener childEventListenerMensagens;

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
        editTextChat = findViewById(R.id.editTextChat);
        fabEnviarChat = findViewById(R.id.fabEnviarChat);
        recyclerView = findViewById(R.id.recyclerViewChat);

        adapterChat = new AdapterChat(mensagens, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterChat);



        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            usuarioDestinatario = (Usuario) bundle.getSerializable("contato");

            textNomeContato.setText(usuarioDestinatario.getNome());
            idDestinatario = Base64Custom.codificarBase64(usuarioDestinatario.getEmail());

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

    public void enviarMensagem(View view){
        String textoMensagem = editTextChat.getText().toString();

        if(textoMensagem != null ){

        Mensagem mensagem = new Mensagem();
        mensagem.setIdUsuario(idRemetente);
        mensagem.setTextoMensagem(textoMensagem);


            salvarMensagem(idRemetente,idDestinatario,mensagem);
            salvarMensagem(idDestinatario,idRemetente,mensagem);
        }
        else{
            Toast.makeText(ChatActivity.this,
                    R.string.alerta_chat,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem) {



        DatabaseReference mensagemRef = databaseReference
                .child("mensagens")
                .child(idRemetente)
                .child(idDestinatario)
                .push();

        mensagemRef.setValue(mensagem);

        editTextChat.setText("");



    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        listaMensagemsRef.removeEventListener(childEventListenerMensagens);
    }

    private void recuperarMensagens(){

        listaMensagemsRef = databaseReference.child("mensagens")
                .child(idRemetente)
                .child(idDestinatario);

        childEventListenerMensagens = listaMensagemsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
                adapterChat.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
