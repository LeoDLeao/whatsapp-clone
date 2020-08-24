package com.example.zap.model;

import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.helper.Base64Custom;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Grupo implements Serializable {

    private String idGrupo;
    private String nome;
    private String foto;

    private List<Usuario> membros;

    public Grupo(){
        DatabaseReference grupoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("grupos");
        String idFirebase = grupoRef.push().getKey();
        setIdGrupo(idFirebase);
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }

    public void salvar() {

        DatabaseReference grupoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("grupos");

        grupoRef.child(idGrupo)
                .setValue(this);

        for(Usuario membro : getMembros()){

            String idRemetente = Base64Custom.codificarBase64(membro.getEmail());
            String idDestinatario = getIdGrupo();

            Conversa conversa = new Conversa();

            conversa.setIdUsuarioDestinatario(idDestinatario);
            conversa.setIdUsuarioRemetente(idRemetente);
            conversa.setUltimaMensagem("");
            conversa.setIsGroup("true");
            conversa.setGrupo(this);

            conversa.salvar();
        }
    }
}
