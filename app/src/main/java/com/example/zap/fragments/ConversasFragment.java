package com.example.zap.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.zap.R;
import com.example.zap.activities.ChatActivity;
import com.example.zap.adapter.AdapterConversas;
import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.firebase.UsuarioFirebase;
import com.example.zap.helper.RecyclerItemClickListener;
import com.example.zap.model.Conversa;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private RecyclerView recyclerViewConversas;
    private AdapterConversas adapterConversas;

    private List<Conversa> listaConversas = new ArrayList<>();

    private DatabaseReference databaseRef;
    private DatabaseReference conversasRef;


    private ChildEventListener childEventListenerConversas;


    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        recyclerViewConversas = view.findViewById(R.id.recyclerViewConversas);

        adapterConversas = new AdapterConversas(listaConversas, getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager(layoutManager);
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adapterConversas);

        recyclerViewConversas.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),
                        recyclerViewConversas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                List<Conversa> listaConversasAtualizada = adapterConversas.getConversas();

                                Conversa conversaSelecionada = listaConversasAtualizada.get(position);

                                if(conversaSelecionada.getIsGroup().equals("true")){
                                    Intent intent = new Intent(getActivity(),ChatActivity.class);
                                    intent.putExtra("grupo",conversaSelecionada.getGrupo());
                                    startActivity(intent);


                                }
                                else {

                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("contato",conversaSelecionada.getUsuarioExibicao());
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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
        listaConversas.clear();

    }

    private void recuperarConversas() {

       String idUsuarioAtual = UsuarioFirebase.getIdUsuario();

       conversasRef = databaseRef
               .child("conversas")
               .child(idUsuarioAtual);

       childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               Conversa conversa = dataSnapshot.getValue(Conversa.class);
               listaConversas.add(conversa);
               adapterConversas.notifyDataSetChanged();

           }

           @Override
           public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });



    }

    public void recarregarConversas(){

        adapterConversas = new AdapterConversas(listaConversas, getActivity());
        recyclerViewConversas.setAdapter(adapterConversas);
        adapterConversas.notifyDataSetChanged();

    }


    public void pesquisarConversas(String texto){
       // Log.d("pesquisa", texto);

        List<Conversa> listaBusca = new ArrayList<>();

        for (Conversa conversa : listaConversas){

            if(conversa.getUsuarioExibicao() != null){
                String nome = conversa.getUsuarioExibicao().getNome().toLowerCase();
                String msg = conversa.getUltimaMensagem().toLowerCase();

                if(nome.contains(texto) || msg.contains(texto)){
                    listaBusca.add(conversa);
                }
            }
            else {
                String nome = conversa.getGrupo().getNome().toLowerCase();
                String msg = conversa.getUltimaMensagem().toLowerCase();

                if(nome.contains(texto) || msg.contains(texto)){
                    listaBusca.add(conversa);
                }

            }

            adapterConversas = new AdapterConversas(listaBusca, getActivity());
            recyclerViewConversas.setAdapter(adapterConversas);
            adapterConversas.notifyDataSetChanged();

        }
    }
}
