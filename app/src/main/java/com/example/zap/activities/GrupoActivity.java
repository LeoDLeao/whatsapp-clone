package com.example.zap.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.zap.adapter.AdapterContatos;
import com.example.zap.adapter.AdapterGrupoSelecionado;
import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.firebase.UsuarioFirebase;
import com.example.zap.helper.RecyclerItemClickListener;
import com.example.zap.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.zap.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GrupoActivity extends AppCompatActivity {

    private RecyclerView recyclerMembros, recyclerMembrosSelecionados;

    private AdapterContatos adapterMembros;
    private List<Usuario> listaMembros = new ArrayList<>();
    private ValueEventListener valueEventListenerMembros;

    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private AdapterGrupoSelecionado adapterGrupoSelecionado;

    private Toolbar toolbar;
    private FloatingActionButton fabAvancarCadastro;

    private DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.novo_grupo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerMembros = findViewById(R.id.recyclerMembros);
        adapterMembros = new AdapterContatos(listaMembros,getApplicationContext());

        RecyclerView.LayoutManager layoutManagerMembros =  new LinearLayoutManager(getApplicationContext());
        recyclerMembros.setLayoutManager(layoutManagerMembros);
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter(adapterMembros);

        recyclerMembros.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembros,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Usuario membroSelecionado = listaMembros.get(position);

                                listaMembros.remove(membroSelecionado);
                                adapterMembros.notifyDataSetChanged();

                                listaMembrosSelecionados.add(membroSelecionado);
                                adapterGrupoSelecionado.notifyDataSetChanged();

                                atualizarSubtituloToolbar();


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }));


        recyclerMembrosSelecionados = findViewById(R.id.recyclerMembrosSelecionados);
        adapterGrupoSelecionado = new AdapterGrupoSelecionado(listaMembrosSelecionados,getApplicationContext());

        RecyclerView.LayoutManager layoutManagerMembrosSelecionados = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerMembrosSelecionados.setLayoutManager(layoutManagerMembrosSelecionados);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter(adapterGrupoSelecionado);

        recyclerMembrosSelecionados.addOnItemTouchListener
                (new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembrosSelecionados,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Usuario membroSelecionado = listaMembrosSelecionados.get(position);

                                listaMembrosSelecionados.remove(membroSelecionado);
                                adapterGrupoSelecionado.notifyDataSetChanged();

                                listaMembros.add(membroSelecionado);
                                adapterMembros.notifyDataSetChanged();
                                atualizarSubtituloToolbar();

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }

                ));

        fabAvancarCadastro = findViewById(R.id.fabAvancarCadastro);
        fabAvancarCadastro.setOnClickListener(v -> {

            if(listaMembrosSelecionados.isEmpty()){

            Toast.makeText(getApplicationContext(),
                        "O grupo deve contender pelo menos 1 participante",
                        Toast.LENGTH_SHORT).show();
            }else {

                Intent intent = new Intent(getApplicationContext(), CadastroGrupoActivity.class);
                intent.putExtra("membros", (Serializable) listaMembrosSelecionados);

                startActivity(intent);
            }
        });



    }

    private void atualizarSubtituloToolbar(){


        int totalSelecionado = listaMembrosSelecionados.size();
        int total = listaMembros.size() + listaMembrosSelecionados.size();

        String subtitle;

        if(totalSelecionado == 1 ){

            subtitle = totalSelecionado + " de " + total +  " selecionado";

        }else {

            subtitle = totalSelecionado + " de " + total + " selecionados";

        }
        toolbar.setSubtitle(subtitle);

    }


    @Override
    protected void onStart() {
        super.onStart();
        recuperarMembros();

    }

    @Override
    protected void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerMembros);
        listaMembros.clear();
    }

    private void recuperarMembros () {

        valueEventListenerMembros = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dados : dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    if(usuario.getEmail() != UsuarioFirebase.getUsuarioAtual().getEmail()) {
                        listaMembros.add(usuario);
                    }
                }
                adapterMembros.notifyDataSetChanged();
                atualizarSubtituloToolbar();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
