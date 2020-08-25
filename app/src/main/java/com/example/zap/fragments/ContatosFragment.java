package com.example.zap.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zap.R;
import com.example.zap.activities.ChatActivity;
import com.example.zap.activities.GrupoActivity;
import com.example.zap.adapter.AdapterContatos;
import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.firebase.UsuarioFirebase;
import com.example.zap.helper.RecyclerItemClickListener;
import com.example.zap.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewContatos;
    private AdapterContatos adapterContatos;
    private List<Usuario> listaContatos = new ArrayList<>();

    private DatabaseReference usuariosRef;

    private FirebaseUser usuarioAtual = UsuarioFirebase.getUsuarioAtual();

    private ValueEventListener valueEventListenerContatos;


    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        recyclerViewContatos = view.findViewById(R.id.recyclerViewListaContatos);
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");


        adapterContatos = new AdapterContatos(listaContatos, getActivity());


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContatos.setLayoutManager(layoutManager);
        recyclerViewContatos.setHasFixedSize(true);
        recyclerViewContatos.setAdapter(adapterContatos);

        recyclerViewContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),
                        recyclerViewContatos
                        , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        List<Usuario> listaContatosAtualizada = adapterContatos.getContatos();

                        Usuario usuarioSelecionado = listaContatosAtualizada.get(position);

                        boolean cabecalho = usuarioSelecionado.getEmail().isEmpty();

                        if(cabecalho ){

                            Intent intentGrupo = new Intent(getActivity(), GrupoActivity.class);
                            startActivity(intentGrupo);

                        }else {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("contato", usuarioSelecionado);

                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));


        Usuario itemGrupo = new Usuario();
        itemGrupo.setNome("Novo Grupo");
        itemGrupo.setEmail("");

        listaContatos.add(itemGrupo);


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerContatos);
        listaContatos.clear();

        Usuario itemGrupo = new Usuario();
        itemGrupo.setNome("Novo Grupo");
        itemGrupo.setEmail("");

        listaContatos.add(itemGrupo);
    }

    private void recuperarContatos(){

       valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dados: dataSnapshot.getChildren()) {
                    Usuario usuario = dados.getValue(Usuario.class);

                    if(!usuarioAtual.getEmail().equals(usuario.getEmail())){
                        listaContatos.add(usuario);
                    }



                }

                adapterContatos.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void recarregarContatos(){
        adapterContatos = new AdapterContatos(listaContatos,getActivity());
        recyclerViewContatos.setAdapter(adapterContatos);
        adapterContatos.notifyDataSetChanged();
    }

    public void pesquisarContatos(String newText){

        List<Usuario> listaContatosBusca = new ArrayList<>();

        for(Usuario contato : listaContatos){

            String nome = contato.getNome().toLowerCase();
            if(nome.contains(newText)){
                listaContatosBusca.add(contato);
            }
        }

        adapterContatos = new AdapterContatos(listaContatosBusca,getActivity());
        recyclerViewContatos.setAdapter(adapterContatos);
        adapterContatos.notifyDataSetChanged();

    }

}
