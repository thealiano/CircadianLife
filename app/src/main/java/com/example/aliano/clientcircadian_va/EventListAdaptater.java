package com.example.aliano.clientcircadian_va;

/**
 * Created by Aliano on 18/05/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

/**
 * Created by Aliano on 18/05/2016.
 */

/**
 * Created by Aliano on 05/05/2016.
 */


/**
 * Created by Aliano on 23.12.2015.
 */
public class EventListAdaptater extends ArrayAdapter<View_Event> {


    public EventListAdaptater(Context context, List<View_Event> myevent) {
        super(context, 0, myevent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Android nous fournit un convertView null lorsqu'il nous demande de la créer
        //dans le cas contraire, cela veux dire qu'il nous fournit une vue recyclée
        if(convertView == null){
            //Nous récupérons notre row_event via un LayoutInflater,
            //qui va charger un layout xml dans un objet View
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_event,parent, false);
        }

        EventViewHolder viewHolder = (EventViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new EventViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.value = (TextView) convertView.findViewById(R.id.value);
            viewHolder.eType = (TextView) convertView.findViewById(R.id.event_type);
            convertView.setTag(viewHolder);
        }


        View_Event myevent = getItem(position);

        //Remplissage de notre vue
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        //Color.red();

        viewHolder.eType.setBackgroundColor(color); // couleur aléatoire
        viewHolder.eType.setText(myevent.getType());
        viewHolder.name.setText(myevent.getName());
        viewHolder.value.setText(myevent.getValue());

        return convertView;
    }

    private class EventViewHolder {
        public TextView eType;
        public TextView name;
        public TextView value;
    }
}

