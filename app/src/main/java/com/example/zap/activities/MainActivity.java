package com.example.zap.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.zap.R;
import com.example.zap.firebase.ConfiguracaoFirebase;
import com.example.zap.fragments.ContatosFragment;
import com.example.zap.fragments.ConversasFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        //Config abas

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Conversas", ConversasFragment.class)
                        .add("Contatos", ContatosFragment.class)
                        .create());

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

        searchView = findViewById(R.id.searchview_principal);


        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                fragment.recarregarConversas();
                ContatosFragment contatosFragment = (ContatosFragment) adapter.getPage(1);
                contatosFragment.recarregarContatos();


            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                switch (viewPager.getCurrentItem()){
                    case 0:
                        ConversasFragment conversasFragment = (ConversasFragment) adapter.getPage(0);


                        if(newText != null && !newText.isEmpty()){
                            conversasFragment.pesquisarConversas(newText);
                        }else{
                            conversasFragment.recarregarConversas();
                        }
                        break;

                    case 1:

                        ContatosFragment contatosFragment = (ContatosFragment) adapter.getPage(1);

                        if(newText != null && !newText.isEmpty()){
                            contatosFragment.pesquisarContatos(newText.toLowerCase());
                        }else{
                            contatosFragment.recarregarContatos();
                        }

                        break;
                }




                return true;
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.menuPesquisa);

        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menuSair:

                deslogarUsuario();
                finish();
                break;

            case R.id.menuConfiguracoes:

                abrirTelaConfiguracoes();

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirTelaConfiguracoes() {
        Intent intent = new Intent(MainActivity.this, ConfiguracoesActivity.class);
        startActivity(intent);

    }

    private void deslogarUsuario() {

        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
        try {
            auth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
