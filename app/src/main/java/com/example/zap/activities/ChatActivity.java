package com.example.zap.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.zap.adapter.AdapterChat;
import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.firebase.UsuarioFirebase;
import com.example.zap.helper.Base64Custom;
import com.example.zap.model.Conversa;
import com.example.zap.model.Grupo;
import com.example.zap.model.Mensagem;
import com.example.zap.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textNomeContato;
    private CircleImageView FotoContato;

    private FloatingActionButton fabEnviarChat;

    private EditText editTextChat;

    private RecyclerView recyclerView;
    private AdapterChat adapterChat;

    private Usuario usuarioDestinatario;
    private Usuario usuarioRemetente = UsuarioFirebase.getDadosUsuarioLogado();

    private Grupo grupo;

    private List<Mensagem> mensagens = new ArrayList<>();

    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference listaMensagemsRef;
    private String idDestinatario;
    private String idRemetente = ConfiguracaoFirebase.getIdUsuario();

    private ChildEventListener childEventListenerMensagens;

    private static final int SELECAO_CAMERA = 100;
    private StorageReference storageReference = ConfiguracaoFirebase.getStorageReference();

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
            if(bundle.containsKey("grupo")){

                grupo = (Grupo) bundle.getSerializable("grupo");
                idDestinatario = grupo.getIdGrupo();
                textNomeContato.setText(grupo.getNome());

                if(grupo.getFoto() != null){
                    Uri uriFotoGrupo = Uri.parse(grupo.getFoto());

                    Glide.with(ChatActivity.this)
                            .load(uriFotoGrupo)
                            .into(FotoContato);
                }
                else{
                    FotoContato.setImageResource(R.drawable.padrao);
                }

            }
            else{

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



    }

    public void abrirCameraChat(View view){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent,SELECAO_CAMERA);
        }

    }

    public void enviarMensagem(View view){
        String textoMensagem = editTextChat.getText().toString();

        if(!textoMensagem.isEmpty()){

            if(usuarioDestinatario != null){

                Mensagem mensagem = new Mensagem();
                mensagem.setIdUsuario(idRemetente);
                mensagem.setTextoMensagem(textoMensagem);


                salvarMensagem(idRemetente,idDestinatario,mensagem);
                salvarMensagem(idDestinatario,idRemetente,mensagem);

                salvarConversa(idRemetente, idDestinatario,usuarioDestinatario, mensagem,false);
                salvarConversa(idDestinatario, idRemetente,usuarioRemetente, mensagem,false);


            }
            else{

                for(Usuario membro : grupo.getMembros()){

                    String idRemetenteGrupo = Base64Custom.codificarBase64(membro.getEmail());
                    String idUsuarioLogadoGrupo = UsuarioFirebase.getIdUsuario();

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioLogadoGrupo);
                    mensagem.setTextoMensagem(textoMensagem);

                    mensagem.setNomeUsuario(usuarioRemetente.getNome());

                    salvarMensagem(idRemetenteGrupo,idDestinatario,mensagem);

                    salvarConversa(idRemetenteGrupo, idDestinatario,usuarioDestinatario,mensagem,true);

                }
            }


        }
        else{
            Toast.makeText(ChatActivity.this,
                    R.string.alerta_chat,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void salvarConversa( String idRemetente, String idDestinatario,Usuario usuarioExibicao,Mensagem mensagem, boolean isGroup) {

        Conversa conversaRemetente = new Conversa();

        conversaRemetente.setIdUsuarioRemetente(idRemetente);
        conversaRemetente.setIdUsuarioDestinatario(idDestinatario);
        conversaRemetente.setUltimaMensagem(mensagem.getTextoMensagem());

        if(isGroup){

            conversaRemetente.setIsGroup("true");
            conversaRemetente.setGrupo(grupo);

        }
        else{

            conversaRemetente.setUsuarioExibicao(usuarioExibicao);
            conversaRemetente.setIsGroup("false");

        }

        conversaRemetente.salvar();


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
        mensagens.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            Bitmap imagem = null;

            try{

                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");

                        break;
                }

                if(imagem != null){

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    String nomeImagem = UUID.randomUUID().toString();



                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("fotos")
                            .child(idRemetente)
                            .child(nomeImagem + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(e -> {

                        Log.i("Upload error", "erro : " + e.getMessage());
                        e.printStackTrace();

                        Toast.makeText(ChatActivity.this,
                                R.string.erro_upload_imagem,
                                Toast.LENGTH_SHORT).show();

                    }).addOnSuccessListener(taskSnapshot -> imagemRef.getDownloadUrl().addOnCompleteListener(task -> {

                        String url = task.getResult().toString();

                        Mensagem mensagem = new Mensagem();
                        mensagem.setIdUsuario(idRemetente);
                        mensagem.setTextoMensagem("imagem.jpeg");
                        mensagem.setImagem(url);

                        salvarMensagem(idRemetente,idDestinatario,mensagem);
                        salvarMensagem(idDestinatario,idRemetente,mensagem);

                    }));


                }


            }catch (Exception e){
                e.printStackTrace();
            }



        }
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
