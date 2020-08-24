package com.example.zap.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.zap.adapter.AdapterGrupoSelecionado;
import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.firebase.UsuarioFirebase;
import com.example.zap.model.Grupo;
import com.example.zap.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zap.R;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity extends AppCompatActivity {

    private List<Usuario> listaMembros = new ArrayList<>();
    private TextView textParticipantes;
    private EditText editNomeGrupo;
    private CircleImageView fotoCadastroGrupo;

    private RecyclerView recyclerViewMembrosSelecionados;
    private AdapterGrupoSelecionado adapterGrupoSelecionado;

    private Grupo grupo;
    private Usuario usuarioAtual;

    private StorageReference storageReference = ConfiguracaoFirebase.getStorageReference();

    private static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.novo_grupo);
        toolbar.setSubtitle(R.string.subtitulo_novo_grupo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            List<Usuario> listaCadastro = (List<Usuario>) bundle.getSerializable("membros");

            listaMembros.addAll(listaCadastro);

        }

        FloatingActionButton fab = findViewById(R.id.fabSalvarCadastro);
        textParticipantes = findViewById(R.id.textParticipantes);
        fotoCadastroGrupo = findViewById(R.id.fotoCadastroGrupo);
        editNomeGrupo = findViewById(R.id.editNomeGrupo);

        grupo = new Grupo();
        usuarioAtual = UsuarioFirebase.getDadosUsuarioLogado();

        textParticipantes.setText("Participantes : " + listaMembros.size());

        recyclerViewMembrosSelecionados = findViewById(R.id.recyclerViewCadastroGrupo);
        adapterGrupoSelecionado = new AdapterGrupoSelecionado(listaMembros,getApplicationContext());

        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerViewMembrosSelecionados.setLayoutManager(layoutManager);
        recyclerViewMembrosSelecionados.setHasFixedSize(true);
        recyclerViewMembrosSelecionados.setAdapter(adapterGrupoSelecionado);

        fotoCadastroGrupo.setOnClickListener(v -> {

            Intent intentGaleria = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            if(intentGaleria.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intentGaleria,SELECAO_GALERIA);
            }
        });

        fab.setOnClickListener(view -> {
            String nomeGrupo = editNomeGrupo.getText().toString();

            listaMembros.add(usuarioAtual);
            grupo.setNome(nomeGrupo);
            grupo.setMembros(listaMembros);

            grupo.salvar();
            finish();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();

                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                        break;
                }
                if(imagem != null){
                    fotoCadastroGrupo.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("grupo")
                            .child(grupo.getIdGrupo() + ".jpeg");


                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(e -> {
                        Log.i("Upload error", "erro : " + e.getMessage());
                        e.printStackTrace();

                        Toast.makeText(CadastroGrupoActivity.this,
                                R.string.erro_upload_imagem,
                                Toast.LENGTH_SHORT).show();
                    }).addOnSuccessListener(taskSnapshot -> {

                        imagemRef.getDownloadUrl().addOnCompleteListener(task -> {

                            String url = task.getResult().toString();

                            grupo.setFoto(url);

                            Toast.makeText(CadastroGrupoActivity.this,
                                    R.string.sucesso_upload_imagem,
                                    Toast.LENGTH_SHORT)
                                    .show();
                        });

                    });

                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
