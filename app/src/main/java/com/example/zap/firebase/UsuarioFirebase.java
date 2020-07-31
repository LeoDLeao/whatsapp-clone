package com.example.zap.firebase;

import android.net.Uri;
import android.util.Log;

import com.example.zap.helper.Base64Custom;
import com.example.zap.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static Usuario getDadosUsuarioLogado (){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario =new Usuario();
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setEmail(firebaseUser.getEmail());

        if(firebaseUser.getPhotoUrl() != null){

            usuario.setFoto(firebaseUser.getPhotoUrl().toString());

        }
        else {

            usuario.setFoto("");
        }


        return usuario;
    }


    public static String getIdUsuario(){

        String email = getUsuarioAtual().getEmail();
        return Base64Custom.codificarBase64(email);

    }

    public static FirebaseUser getUsuarioAtual(){

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth();
        return usuario.getCurrentUser();

    }

    public static boolean atualizaFotoUsuario(Uri url){

        try{
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(task -> {
                        if(! task.isSuccessful()){
                            Log.d("Perfil","Erro ao atualizar foto de perfil");
                        }
                    });

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static boolean atualizaNomeUsuario(String nomeUsuario){

        try{
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nomeUsuario)
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(task -> {
                        if(!task.isSuccessful()){

                            Log.d("Perfil","Erro ao atualizar nome do usuário");
                        }
                    });

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
