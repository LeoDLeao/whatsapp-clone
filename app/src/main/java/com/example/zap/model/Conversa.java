package com.example.zap.model;

import com.example.zap.firebase.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {

    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;
    private String ultimaMensagem;
    private Usuario usuarioExibicao;
    private String isGroup;

    private Grupo grupo;

    public Conversa() {
        this.setIsGroup("false");
    }

    public void salvar(){

        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference conversaRef = databaseRef
                .child("conversas")
                .child(this.idUsuarioRemetente)
                .child(this.idUsuarioDestinatario);

        conversaRef.setValue(this);
    }

    public String getIdUsuarioRemetente() {
        return idUsuarioRemetente;
    }

    public void setIdUsuarioRemetente(String idUsuarioRemetente) {
        this.idUsuarioRemetente = idUsuarioRemetente;
    }

    public String getIdUsuarioDestinatario() {
        return idUsuarioDestinatario;
    }

    public void setIdUsuarioDestinatario(String idUsuarioDestinatario) {
        this.idUsuarioDestinatario = idUsuarioDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }
}
