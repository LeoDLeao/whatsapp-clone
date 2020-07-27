package com.example.zap.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.zap.R;
import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.function.Predicate;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void cadastrarUsuario(View view){

        Predicate<String> validar = campo -> !campo.isEmpty();

        String nomeUsuario = validarNome(campoNome.getText().toString(), validar);
        String emailUsuario = validarEmail(campoEmail.getText().toString(),validar);
        String senhaUsuario = validarSenha(campoSenha.getText().toString(), validar);

        if(nomeUsuario != null && emailUsuario != null && senhaUsuario != null){

            Usuario usuario= new Usuario(nomeUsuario,emailUsuario,senhaUsuario);

            salvarUsuario(usuario);
        }
    }

    private void salvarUsuario(Usuario usuario) {

        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();

        auth.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(CadastroActivity.this,
                                "Cadastro efetuado com sucesso! ",
                                Toast.LENGTH_SHORT).show();

                        finish();

                    }
                    else{
                        String exception;

                        try {
                            throw task.getException();
                        }catch (FirebaseAuthWeakPasswordException e){
                            exception = "Digite uma senha mais forte";
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            exception = "Por favor, digite um email válido";
                        }catch (FirebaseAuthUserCollisionException e){
                            exception = "Este email já foi cadastrado !";
                        }catch (Exception e){
                            exception = "Erro ao cadastrar usuário" + e.getMessage();
                            e.printStackTrace();
                        }
                        Toast.makeText(CadastroActivity.this,
                                exception,
                                Toast.LENGTH_SHORT).show();

                    }

                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String validarNome(String campo, Predicate predicate){

        if(predicate.test(campo)){
            return campo;

        }else{
            Toast.makeText(CadastroActivity.this,
                    "Preencha seu nome! ",
                    Toast.LENGTH_SHORT).show();

            return null;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String validarEmail(String campo, Predicate predicate){

        if(predicate.test(campo)){
            return campo;

        }else{
            Toast.makeText(CadastroActivity.this,
                    "Preencha seu email! ",
                    Toast.LENGTH_SHORT).show();

            return null;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String validarSenha(String campo, Predicate predicate){

        if(predicate.test(campo)){
            return campo;

        }else{
            Toast.makeText(CadastroActivity.this,
                    "Preencha sua senha! ",
                    Toast.LENGTH_SHORT).show();

            return null;
        }

    }

}
