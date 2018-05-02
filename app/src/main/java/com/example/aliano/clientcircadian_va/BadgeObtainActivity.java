package com.example.aliano.clientcircadian_va;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aliano.clientcircadian_va.SqLiteDatabase.CircadianLocalBDD;
import com.example.aliano.clientcircadian_va.SqLiteDatabase.TBadge;

import java.util.Locale;

/**
 * Created by Aliano on 02/09/2016.
 */

// done : doit disparaitre au clic sur l'activité
public class BadgeObtainActivity extends Activity {
// to do : ajouter images couz (vérifier taille) + tester l'auto fermeture et l'animation
// done : changer couleur du text de "medaille débloquée" selon la rareté + gérer le français/anglais
    /** Appelée lorsque l'activité est créée. **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_obtain);
// to do : ajouter un call pour tester (il faut la possibilité de faire plusieurs call avec intents différents)
        String[] arrayBadge;
        String badge_ressource_name = getIntent().getStringExtra("badge_string_name"); // like badge_01
        arrayBadge = getStringBadgeArrayByRessourceName(badge_ressource_name);

        String badge_unlock_name = arrayBadge[0]; // getIntent().getStringExtra("badge_unlock_rarity");
        String badge_unlock_description = arrayBadge[1]; // getIntent().getStringExtra("badge_unlock_description");
        String badge_unlock_image = arrayBadge[2]; //getIntent().getStringExtra("badge_unlock_image");
        String badge_unlock_rarity = arrayBadge[3]; //getIntent().getStringExtra("badge_unlock_rarity");
        Log.i("checkBadges","BOAvalue:"+badge_unlock_name);
        unlockBadge(badge_unlock_name); // unlock badge in local bdd
        // set the image and texts
        ImageView badge_img = (ImageView) findViewById(R.id.empty_medal);
        badge_img.setImageResource(getImageResourceByName(badge_unlock_image));

        // rarity
        TextView badge_rarity = (TextView) findViewById(R.id.t_medal);
        TextView tobtain = (TextView)findViewById(R.id.t_medal2);
        String language = Locale.getDefault().getLanguage();   // get the current language
        Log.v("LANGUE"," >"+language+"<");
        if (language.toLowerCase().equals("fr")) {
            badge_unlock_rarity = "Médaille de " + badge_unlock_rarity;
            tobtain.setText("OBTENUE");
        }else {
            badge_unlock_rarity = badge_unlock_rarity + " medal";
        }
        badge_rarity.setText(badge_unlock_rarity);

        TextView badge_name = (TextView) findViewById(R.id.t_medal_name);
        badge_name.setText(badge_unlock_name);
        TextView badge_description = (TextView) findViewById(R.id.t_medal_desc);
        badge_description.setText(badge_unlock_description);

        testAnimation(badge_img); // animation test
        LinearLayout layout=(LinearLayout) findViewById(R.id.l_activity_badge_obtain);
        layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {   // kill itself on clic
                BadgeObtainActivity.this.finish();
            }
        });

    }
    // convert a String Ressource Name to a res identifier
    private int getImageResourceByName(String resName) {
        int picId = getResources().getIdentifier(resName, "drawable", getApplicationContext().getPackageName());
        return picId;
    }

    public String[] getStringBadgeArrayByRessourceName(String name) {
        // return the ressource id of the string array
        int arrayId = getResources().getIdentifier(name, "array", getPackageName());
        String[] tab = getResources().getStringArray(arrayId);
        return tab;
    }
    public void testAnimation(ImageView aniView){
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(aniView, "alpha", 0f);
        fadeOut.setDuration(2000);
        ObjectAnimator mover = ObjectAnimator.ofFloat(aniView, "translationX", -500f, 0f);
        mover.setDuration(2000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(aniView, "alpha", 0f, 1f);
        fadeIn.setDuration(2000);
        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.play(mover).with(fadeIn).after(fadeOut);
        animatorSet.start();
    }
    private void unlockBadge(String badgeName){
        //on extrait le badge de la BDD grâce au nom du badge que l'on a créé précédemment
        CircadianLocalBDD localBdd = new CircadianLocalBDD(this);
        localBdd.open(); //On ouvre la base de données pour écrire dedans

        TBadge badgeFromBdd = localBdd.getBadgeWithName(badgeName);
        //Si un badge est retourné (donc si le badge à bien été ajouté à la BDD)
        if(badgeFromBdd != null){
            //On affiche les infos du livre dans un Toast
            Log.v("badge_String",badgeFromBdd.toString());
            Log.v("badge_NOTUNLOCK",badgeFromBdd.getName() +">"+badgeFromBdd.getUnlock() +"<");
            //Toast.makeText(this, badgeFromBdd.toString(), Toast.LENGTH_LONG).show();

            //Puis on DéBLOQUE LE BADGE a bouger
            localBdd.unlockBadge(badgeFromBdd); // débloque le badge
//to do : changer name pour id
            badgeFromBdd = localBdd.getBadgeWithName(badgeName);
            Log.v("badge_String2", badgeFromBdd.toString());
            Log.v("badge_UNLOCK_", badgeFromBdd.getName() +">" +badgeFromBdd.getUnlock() + "<");
            // badgeFromBdd.updateLivre(livreFromBdd.getId(), livreFromBdd);
        }else
            Log.e("checkBadges","sqlTableBadgeNull");
        localBdd.close(); //On ferme la base de données


    }
}
