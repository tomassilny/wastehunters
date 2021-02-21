package com.tosi.wastehunters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Belal on 18/09/16.
 */


public class Statistics_f extends Fragment {
    TextView t1, t2, t3, t4, t5, t6, t7, t8;
    TextView textView26, textView27, textView28, textView29, textView30, textView31, textView32, textView25;
    ProgressBar p1, p2, p3 ,p4, p5, p6, p7, p8;
    View rootview;
    int count, paper, plast, textil, other, bio_waste, glass, metals, wood_g_l, max = 0;
    int getSharedPreferncesInt(String item){
        SharedPreferences sep2 = getActivity().getSharedPreferences(item, Activity.MODE_PRIVATE);
        int i = sep2.getInt(item + "_k", 0);
        return i;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.friends_fragment, container, false);

        t1 = (TextView) rootview.findViewById(R.id.first_t);
        t2 = (TextView) rootview.findViewById(R.id.second_t);
        t3 = (TextView) rootview.findViewById(R.id.third_t);
        t4 = (TextView) rootview.findViewById(R.id.fourt_t);
        t5 = (TextView) rootview.findViewById(R.id.fifth_t);
        t6 = (TextView) rootview.findViewById(R.id.sixth_t);
        t7 = (TextView) rootview.findViewById(R.id.seventh_t);
        t8 = (TextView) rootview.findViewById(R.id.eight_t);

        p1 = (ProgressBar)rootview.findViewById(R.id.p1);
        p2 = (ProgressBar)rootview.findViewById(R.id.p2);
        p3 = (ProgressBar)rootview.findViewById(R.id.p3);
        p4 = (ProgressBar)rootview.findViewById(R.id.p4);
        p5 = (ProgressBar)rootview.findViewById(R.id.p5);
        p6 = (ProgressBar)rootview.findViewById(R.id.p6);
        p7 = (ProgressBar)rootview.findViewById(R.id.p7);
        p8 = (ProgressBar)rootview.findViewById(R.id.p8);

        textView26 = (TextView)rootview.findViewById(R.id.textView26);
        textView27 = (TextView)rootview.findViewById(R.id.textView27);
        textView28 = (TextView)rootview.findViewById(R.id.textView28);
        textView29 = (TextView)rootview.findViewById(R.id.textView29);
        textView30 = (TextView)rootview.findViewById(R.id.textView30);
        textView31 = (TextView)rootview.findViewById(R.id.textView31);
        textView32 = (TextView)rootview.findViewById(R.id.textView32);
        textView25 = (TextView)rootview.findViewById(R.id.textView25);

        MainPanel mainPanel = new MainPanel();

        paper = getSharedPreferncesInt("paper");
        plast = getSharedPreferncesInt("plast");
        textil = getSharedPreferncesInt("textil");
        other = getSharedPreferncesInt("other");
        bio_waste = getSharedPreferncesInt("bio_waste");
        glass = getSharedPreferncesInt("glass");
        metals = getSharedPreferncesInt("metals");
        wood_g_l = getSharedPreferncesInt("wood_g_l");

        max = (paper + plast + textil + other + bio_waste + glass + metals + wood_g_l);

        int i;
        TextView txt[] = { textView26, textView27, textView28, textView29, textView30, textView31, textView32, textView25 };
        ProgressBar p_array[] = { p1, p2, p3 ,p4, p5, p6, p7, p8 };
        TextView t_array[] = {  t1, t2, t3, t4, t5, t6, t7, t8 };
        int large[] = new int[8];
        int array[] = { paper, plast, textil, other, bio_waste, glass, metals, wood_g_l };
        String names[] = { getString(R.string.paper), getString(R.string.plast), getString(R.string.textil), getString(R.string.other), getString(R.string.bio_waste), getString(R.string.glass), getString(R.string.metals), getString(R.string.wood_g_l_short) };
        int max_i = 0, index;
        for (int j = 0; j < 8; j++) {
            max_i = array[0];
            index = 0;
            for (i = 1; i < array.length; i++) {
                if (max_i < array[i]) {
                    max_i = array[i];
                    index = i;
                }
            }
            large[j] = max_i;
            array[index] = Integer.MIN_VALUE;

            p_array[j].setProgress(large[j]);
            p_array[j].setMax(max);
            t_array[j].setText("" + names[index]);
            txt[j].setText("" + large[j]);
        }

        return rootview;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
    }
}