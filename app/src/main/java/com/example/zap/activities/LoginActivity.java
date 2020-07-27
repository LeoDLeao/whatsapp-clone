package com.example.zap.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zap.R;
import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.function.Predicate;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);

    }

    @Override
    protected void onStart() {
        super.onStart();

        verificarUsuario();
    }

    private void verificarUsuario() {

        FirebaseUser usuarioAtual = auth.getCurrentUser();

        if(usuarioAtual != null){
            abrirTelaPrincipal();
        }
    }

    public void abrirTelaCadastro(View view){
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void logarUsuario(View view){

        Predicate<String> validar = campo -> !campo.isEmpty();

        String emailUsuario = validarEmail(campoEmail.getText().toString(),validar);
        String senhaUsuario = validarSenha(campoSenha.getText().toString(), validar);

        if(emailUsuario != null && senhaUsuario != null){

            Usuario usuario = new Usuario();
            usuario.setEmail(emailUsuario);
            usuario.setSenha(senhaUsuario);

            autenticarLogin(usuario);

        }

    }

    private void autenticarLogin(Usuario usuario) {

        auth.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,
                                "Login efetuado com sucesso",
                                Toast.LENGTH_SHORT).show();

                        abrirTelaPrincipal();
                    }
                    else{
                        String exception;

                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            exception = "Usuário invalido";
                        } catch (FirebaseAuthInvalidCredentialsException e){
                            exception = "Email e senha não correspodem";
                        } catch (Exception e) {
                            exception = "Erro ao efetuar o login";
                            e.printStackTrace();
                        }
                        Toast.makeText(LoginActivity.this,
                                exception,
                                Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void abrirTelaPrincipal() {

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String validarEmail(String campo, Predicate predicate){

        if(predicate.test(campo)){
            return campo;

        }else{
            Toast.makeText(LoginActivity.this,
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
            Toast.makeText(LoginActivity.this,
                    "Preencha sua senha! ",
                    Toast.LENGTH_SHORT).show();

            return null;
        }

    }
}
