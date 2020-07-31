package com.example.zap.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.zap.R;
import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.firebase.UsuarioFirebase;
import com.example.zap.helper.Permissoes;
import com.example.zap.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private CircleImageView imageButtonCamera;
    private CircleImageView circleImageViewPerfil;
    private EditText editNomeUsuario;

    private StorageReference storageReference = ConfiguracaoFirebase.getStorageReference();

    private Usuario usuarioLogado;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        Permissoes.validarPermissoes(permissoesNecessarias,this,1);

        imageButtonCamera = findViewById(R.id.imageCamera);
        circleImageViewPerfil = findViewById(R.id.imageFoto);
        editNomeUsuario = findViewById(R.id.editNomeUsuario);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.titulo_configuracoes);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        editNomeUsuario.setText(usuario.getDisplayName());


        Uri url = usuario.getPhotoUrl();

        if(url != null){

            Glide.with(ConfiguracoesActivity.this)
                    .load(url)
                    .into(circleImageViewPerfil);

        }
        else {
            circleImageViewPerfil.setImageResource(R.drawable.padrao);
        }

        imageButtonCamera.setOnClickListener(v -> CropImage.activity()
                .setActivityMenuIconColor(R.color.colorPrimary)
                .setBackgroundColor(R.color.colorPrimary)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                circleImageViewPerfil.setImageURI(resultUri);
                final StorageReference imagemRef = storageReference
                        .child("imagens")
                        .child("perfil")
                        .child(UsuarioFirebase.getIdUsuario())
                        .child("perfil.jpeg");

                UploadTask uploadTask = imagemRef.putFile(resultUri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(ConfiguracoesActivity.this,
                            R.string.sucesso_upload_imagem,
                            Toast.LENGTH_SHORT)
                            .show();

                    imagemRef.getDownloadUrl().addOnCompleteListener(task -> {
                        Uri url = task.getResult();
                        atualizaFotoUsuario(url);
                    });

                }).addOnFailureListener(e -> {
                    Log.i("Upload error", "erro : " + e.getMessage());
                    e.printStackTrace();

                    Toast.makeText(ConfiguracoesActivity.this,
                            R.string.erro_upload_imagem,
                            Toast.LENGTH_SHORT).show();

                });
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void atualizaFotoUsuario(Uri url) {
        if (UsuarioFirebase.atualizaFotoUsuario(url)){

            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();

            Toast.makeText(ConfiguracoesActivity.this,
                    "Sua foto foi alterada com sucesso!",
                    Toast.LENGTH_SHORT).show();

        }

    }

    public void atualizaNomeUsuario(View v ){
        String nomeUsuario = editNomeUsuario.getText().toString();

        if(UsuarioFirebase.atualizaNomeUsuario(nomeUsuario)){

            usuarioLogado.setNome(nomeUsuario);
            usuarioLogado.atualizar();

            Toast.makeText(ConfiguracoesActivity.this,"Nome de usuário atualizado com sucesso",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado :
                grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }

        }
    }

    private void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.titulo_alerta_permissao);
        builder.setMessage(R.string.mensagem_alerta_permissao);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.bt_alerta_permissao, ((dialog,which) -> finish() ));

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
