package com.example.aliano.clientcircadian_va;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.GridView;

import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TBadge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aliano on 15/08/2016.
 */
public class CollectionActivity  extends AppCompatActivity implements OnNavigationItemSelectedListener { // to get the same navigation drawer
/** TO DO :
 *  - parcourir la base et regarder pour tout les badges ceux qui ont été débloqués
 * - Ajouter des paramètres aux images générés (marges, id)
 * - Ajouter une méthode onclic générique qui affiche l'image et sa description
 * - Fixer une taille pour toutes les images
 * - Remplacer le layout relatif par un linéaire et gérer le cas ou l'on arrive en fin de ligne
 *   par l'ajout d'un autre layout linéaire en dessous ( ou créer un gros table layout ou le gérer directement avec la boucle)
 *   par ex : calcul de la (largeur de l'écran / largeur d'une image) -1
 * - mettre la génération d'image dans une boucle qui parcours la bbd si obtain = true !
 * - associer chaque image au onClickListener Generique

 *
 */

    // for navigation
    private ViewPager viewPager;
    private DrawerLayout drawer;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_collection);
        //a
        viewPager = (ViewPager) findViewById(R.id.view_pager); // VIEWPAGER
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        // create default navigation toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //handling navigation view item event
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        // set viewpager (onglet) adapter
        viewPager.setAdapter(new PagerAdapterCollection(getSupportFragmentManager(),this));

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0); // selectionne le 1er onglet par défaut

        //b
        //FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within the main activity
       // getLayoutInflater().inflate(R.layout.content_collection_grid, contentFrameLayout);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    // convert a String Ressource Name to a res identifier
    private int getImageResourceByName(String resName) {
        int picId = getResources().getIdentifier(resName, "drawable", getApplicationContext().getPackageName());
        return picId;
    } // maybe need to do the reverse one for the alert


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent anIntent;
        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            anIntent = new Intent(getApplicationContext(), MainActivity.class);
            //anIntent.putExtra("theprofile",profileNumber);
            startActivity(anIntent);
            // viewPager.setCurrentItem(0); // test si activité créées
        } else if (id == R.id.nav_consomation){

        }
        else if (id == R.id.nav_collection) {
            anIntent = new Intent(getApplicationContext(), CollectionActivity.class);
            //anIntent.putExtra("theprofile",profileNumber);
            startActivity(anIntent);
            //viewPager.setCurrentItem(1);
        } else if (id == R.id.nav_datas){
            anIntent = new Intent(getApplicationContext(), DataAccessActivity.class);
            //anIntent.putExtra("theprofile",profileNumber);
            startActivity(anIntent);

        } else if (id == R.id.nav_aboutus){
            anIntent = new Intent(getApplicationContext(), AboutUsActivity.class);
            startActivity(anIntent);
        }
        else{
            finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}