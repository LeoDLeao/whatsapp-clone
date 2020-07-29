package com.example.zap.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissoes {

    public static Boolean validarPermissoes(String[] permissoes, Activity activity, int requestCode){

        if(Build.VERSION.SDK_INT >= 23){

            List<String> listaPermissoes = new ArrayList<>();

            //Percorre permissões passadas, verificando se já tem a permissao liberada

            for (String permissao : permissoes) {
                Boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if(!temPermissao) listaPermissoes.add(permissao);
            }

            //Caso a lista esteja vazia, não é necessário socilicitar permissão
            if(listaPermissoes.isEmpty()) return true;

            //Socilicita permissão
            String[] arrayPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(arrayPermissoes);
            ActivityCompat.requestPermissions(activity,arrayPermissoes,requestCode);
        }

        return true;
    }
}
