package com.example.zap.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.zap.R;
import com.example.zap.helper.Permissoes;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private CircleImageView imageButtonCamera;
    private CircleImageView circleImageViewPerfil;

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        Permissoes.validarPermissoes(permissoesNecessarias,this,1);

        imageButtonCamera = findViewById(R.id.imageCamera);
        circleImageViewPerfil = findViewById(R.id.imageFoto);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.titulo_configuracoes);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        imageButtonCamera.setOnClickListener(v -> {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                circleImageViewPerfil.setImageURI(resultUri);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
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
