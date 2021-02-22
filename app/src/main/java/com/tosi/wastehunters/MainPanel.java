package com.tosi.wastehunters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPanel extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAnalytics mFirebaseAnalytics;
    String id, name, image_url, profile_photo, surname;
    TextView nav_text_count, nav_name;
    ImageView nav_image_black, nav_image;
    ProgressBar first_p, second_p, third_p;
    TextView count_text, first_t, second_t, third_t, first_txt, second_txt, third_txt, stats_error;
    RelativeLayout relativeLayout;
    ViewPager pager;
    CircleImageView circle_l;
    int co, offline_get;
    int offline_count, get_fr = 0;
    TextView t1, t2, t3, t4, t5, t6, t7, t8;
    ProgressBar p1, p2, p3 ,p4, p5, p6, p7, p8;
    ViewSwitcher sw;
    final FirebaseDatabase database_fb = FirebaseDatabase.getInstance();
    int count, paper, plast, textil, other, bio_waste, glass, metals, wood_g_l, max = 0;

    int getSharedPreferncesInt(String item){
        SharedPreferences sep2 = getSharedPreferences(item, Activity.MODE_PRIVATE);
        int i = sep2.getInt(item + "_k", 0);
        return i;
    }

    void ifOnlineSaveToFirebaseInt(String fb_item){
        SharedPreferences sep2 = getSharedPreferences(fb_item, Activity.MODE_PRIVATE);
        final int i = sep2.getInt(fb_item + "_k", 0);
        if (isOnline()) {
            DatabaseReference ref = database_fb.getReference("users/" + id + "/" + fb_item);
            ref.setValue(i);
        }
        SharedPreferences sp6 = getSharedPreferences(fb_item, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor6 = sp6.edit();
        editor6.putInt(fb_item + "_k", i);
        editor6.apply();

        SharedPreferences sep22 = getSharedPreferences("count", Activity.MODE_PRIVATE);
        final int j = sep22.getInt("count" + "_k", 0);

        final int[] f = {0};
        if (isOnline()) {
            DatabaseReference ref2 = database_fb.getReference("users/" + id + "/count");
            ref2.setValue(j);
        }
        SharedPreferences sp7 = getSharedPreferences("count", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor7 = sp7.edit();
        editor7.putInt("count" + "_k", j);
        editor7.apply();
        count_text.setText("" + j);
        nav_text_count.setText("" + j);
    }

    void saveToFirebaseAndPrefsInt(String fb_item){
        SharedPreferences sep2 = getSharedPreferences(fb_item, Activity.MODE_PRIVATE);
        int i = sep2.getInt(fb_item + "_k", 0);
        i++;
        if (isOnline()) {
            DatabaseReference ref = database_fb.getReference("users/" + id + "/" + fb_item);
            ref.setValue(i);
        }
        SharedPreferences sp6 = getSharedPreferences(fb_item, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor6 = sp6.edit();
        editor6.putInt(fb_item + "_k", i);
        editor6.apply();

        SharedPreferences sep22 = getSharedPreferences("count", Activity.MODE_PRIVATE);
        int j = sep22.getInt("count" + "_k", 0);
        j++;
        if (isOnline()) {
            DatabaseReference ref2 = database_fb.getReference("users/" + id + "/count");
            ref2.setValue(j);
        }
        SharedPreferences sp7 = getSharedPreferences("count", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor7 = sp7.edit();
        editor7.putInt("count" + "_k", j);
        editor7.apply();
        count_text.setText("" + j);
        nav_text_count.setText("" + j);
    }

    void statisticsRefresh(){
        int i;
        TextView txt[] = { first_txt, second_txt, third_txt };
        ProgressBar p_array[] = { first_p, second_p, third_p };
        TextView t_array[] = { first_t, second_t, third_t };
        int large[] = new int[3];
        int array[] = { paper, plast, textil, other, bio_waste, glass, metals, wood_g_l };
        String names[] = { getString(R.string.paper), getString(R.string.plast), getString(R.string.textil), getString(R.string.other), getString(R.string.bio_waste), getString(R.string.glass), getString(R.string.metals), getString(R.string.wood_g_l_short) };
        int max_i = 0, index;
        for (int j = 0; j < 3; j++) {
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

            if (count == 0){
                p_array[j].setVisibility(View.INVISIBLE);
                t_array[j].setVisibility(View.INVISIBLE);
                txt[j].setVisibility(View.INVISIBLE);
                stats_error.setVisibility(View.VISIBLE);
            }else {
                p_array[j].setProgress(large[j]);
                p_array[j].setMax(max);
                t_array[j].setText("" + names[index]);
                txt[j].setText("" + large[j]);
                stats_error.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void logWaste(){

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainPanel.this);
        builderSingle.setTitle(getString(R.string.dialog_box_title));
        builderSingle.setIcon(R.drawable.recycle_green);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainPanel.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add(getString(R.string.plast));
        arrayAdapter.add(getString(R.string.paper));
        arrayAdapter.add(getString(R.string.textil));
        arrayAdapter.add(getString(R.string.glass));
        arrayAdapter.add(getString(R.string.metals));
        arrayAdapter.add(getString(R.string.bio_waste));
        arrayAdapter.add(getString(R.string.wood_g_l));
        arrayAdapter.add(getString(R.string.other));

        builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long strName = arrayAdapter.getItemId(which);
                switch ((int) strName){
                    case 0:
                        saveToFirebaseAndPrefsInt("plast");
                        break;
                    case 1:
                        saveToFirebaseAndPrefsInt("paper");
                        break;
                    case 2:
                        saveToFirebaseAndPrefsInt("textil");
                        break;
                    case 3:
                        saveToFirebaseAndPrefsInt("glass");
                        break;
                    case 4:
                        saveToFirebaseAndPrefsInt("metals");
                        break;
                    case 5:
                        saveToFirebaseAndPrefsInt("bio_waste");
                        break;
                    case 6:
                        saveToFirebaseAndPrefsInt("wood_g_l");
                        break;
                    case 7:
                        saveToFirebaseAndPrefsInt("other");
                        break;
                }
                final DatabaseReference ref22 = database_fb.getReference("users/global/count");
                ref22.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int value_local = dataSnapshot.getValue(Integer.class);
                        value_local++;
                        ref22.setValue(value_local);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                a.reset();
                count_text.startAnimation(a);
                Snackbar.make(relativeLayout, getString(R.string.snack_bar_text), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        builderSingle.show();
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stats_error = (TextView)findViewById(R.id.stats_error);

        SharedPreferences sp = getSharedPreferences("id", Activity.MODE_PRIVATE);
        id = sp.getString("id_k", "");
        
        int g = 0;

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(0);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        // View view=navigationView.inflateHeaderView(R.layout.nav_header_main_panel);
        nav_name = (TextView)header.findViewById(R.id.name_nav_txt);
        nav_image_black = (ImageView)header.findViewById(R.id.imageView11);
        nav_image = (ImageView)header.findViewById(R.id.profile_image);
        nav_text_count = (TextView)header.findViewById(R.id.textView6);

        paper = getSharedPreferncesInt("paper");
        plast = getSharedPreferncesInt("plast");
        textil = getSharedPreferncesInt("textil");
        other = getSharedPreferncesInt("other");
        bio_waste = getSharedPreferncesInt("bio_waste");
        glass = getSharedPreferncesInt("glass");
        metals = getSharedPreferncesInt("metals");
        wood_g_l = getSharedPreferncesInt("wood_g_l");

        SharedPreferences sp33 = getSharedPreferences("loged", Activity.MODE_PRIVATE);
        int loged = sp33.getInt("loged_k", 0);

        ImageView black = (ImageView)findViewById(R.id.imageView11);
        first_txt = (TextView)findViewById(R.id.textView12);
        second_txt = (TextView)findViewById(R.id.textView13);
        third_txt = (TextView)findViewById(R.id.textView11);
        relativeLayout = (RelativeLayout)findViewById(R.id.content_main_panel);
        first_t = (TextView)findViewById(R.id.textView9);
        second_t = (TextView)findViewById(R.id.textView10);
        third_t = (TextView)findViewById(R.id.textView8);
        first_p = (ProgressBar)findViewById(R.id.p1);
        second_p = (ProgressBar)findViewById(R.id.progressBar2);
        third_p = (ProgressBar)findViewById(R.id.progressBar5);

        count_text = (TextView)findViewById(R.id.textView6);
        circle_l = (CircleImageView) findViewById(R.id.profile_image);
        count_text.setText("" + count);
        nav_text_count.setText("" + count);
        SharedPreferences sep2 = getSharedPreferences("count", Activity.MODE_PRIVATE);
        count = sep2.getInt("count_k", 0);
        count_text.setText("" + count);
        nav_text_count.setText("" + count);
        if (loged == 1) {

        }

        if (isOnline()){
            ifOnlineSaveToFirebaseInt("plast");
            ifOnlineSaveToFirebaseInt("paper");
            ifOnlineSaveToFirebaseInt("textil");
            ifOnlineSaveToFirebaseInt("other");
            ifOnlineSaveToFirebaseInt("bio_waste");
            ifOnlineSaveToFirebaseInt("glass");
            ifOnlineSaveToFirebaseInt("metals");
            ifOnlineSaveToFirebaseInt("wood_g_l");
            ifOnlineSaveToFirebaseInt("count");
        }
        final TextView welcome;
        welcome = (TextView)findViewById(R.id.textView4);


        if (loged == 0){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        SharedPreferences sp3 = getSharedPreferences("profile_photo", Activity.MODE_PRIVATE);
        profile_photo = sp3.getString("profile_photo_k", "");

        SharedPreferences sp2 = getSharedPreferences("name", Activity.MODE_PRIVATE);
        name = sp2.getString("name_k", "");

        SharedPreferences sp5 = getSharedPreferences("surname", Activity.MODE_PRIVATE);
        surname = sp5.getString("surname_k", "");

        max = (paper + plast + textil + other + bio_waste + glass + metals + wood_g_l);
        first_p.setMax(max);
        second_p.setMax(max);
        third_p.setMax(max);

        nav_name.setText(name + " " + surname);

        statisticsRefresh();

        ScrollView sView = (ScrollView)findViewById(R.id.scrollView);
        sView.setVerticalScrollBarEnabled(false);
        sView.setHorizontalScrollBarEnabled(false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        welcome.setText(getString(R.string.welcome_string) + " " + name);

        if (loged == 1) {
            if (profile_photo.equals("null") || profile_photo.equals(null) || profile_photo.equals("")){
                black.setVisibility(View.INVISIBLE);
                nav_image_black.setVisibility(View.INVISIBLE);
                Picasso.with(getApplicationContext()).load(R.drawable.green_c).into(circle_l);
                Picasso.with(getApplicationContext()).load(R.drawable.green_c).into(nav_image);
            }else{
                //Picasso.with(getApplicationContext()).load(profile_photo).into(circle_l);
                if (isOnline()) {
                    Picasso.with(getApplicationContext()).load(profile_photo).into(circle_l);
                    Picasso.with(getApplicationContext()).load(profile_photo).into(nav_image);
                }else{
                    Picasso.with(getApplicationContext()).load(R.drawable.green_c).into(circle_l);
                    Picasso.with(getApplicationContext()).load(R.drawable.green_c).into(nav_image);
                    black.setVisibility(View.INVISIBLE);
                    nav_image_black.setVisibility(View.INVISIBLE);
                }
            }
        }
        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/" + id + "profile_photo");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photo = dataSnapshot.getValue(String.class);
                welcome.setText(getString(R.string.welcome_string) + " " + name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

// Attach a listener to read the data at our posts reference



            ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar, null);

        actionBar.setCustomView(v);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logWaste();
            }
        });

        fab.setImageResource(R.drawable.trash);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    FragmentManager fragmentManager;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(get_fr == 2){
                Intent intent = new Intent(getApplicationContext(), MainPanel.class);
                startActivity(intent);
                finish();
            }else{
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_panel, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment objFragment = null;

       if (id == R.id.nav_addwaste) {
            logWaste();
           get_fr = 1;
        }  else if (id == R.id.nav_statistics) {
           objFragment = new Statistics_f();
           get_fr = 2;

        }else if (id == R.id.nav_home){
           Intent in = new Intent(getApplicationContext(), MainPanel.class);
           startActivity(in);
           finish();
           get_fr = 3;
       }else if(id == R.id.nav_ecology){
           objFragment = new ecology_f();
           get_fr = 2;
       }else if(id == R.id.nav_web){
           Uri uri = Uri.parse("http://ekologia6.webnode.sk/"); // missing 'http://' will cause crashed
           Intent intent = new Intent(Intent.ACTION_VIEW, uri);
           startActivity(intent);
       }else if (id == R.id.nav_containers){
           objFragment = new containers_f();
           get_fr = 2;
       }else if (id == R.id.nav_about){
           objFragment = new about();
           get_fr = 2;
       }else if(id == R.id.nav_rate){
           final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
           try {
               startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
           } catch (android.content.ActivityNotFoundException anfe) {
               startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
           }
       }else if(id == R.id.nav_exit){
           System.exit(1);
       }else if(id == R.id.nav_share){
           try {
               Intent i = new Intent(Intent.ACTION_SEND);
               i.setType("text/plain");
               i.putExtra(Intent.EXTRA_SUBJECT, "Wastehunters");
               String sAux = "\nStiahni si aplikáciu a buď aj ty Wastehunter\n\n";
               sAux = sAux + "https://play.google.com/store/apps/details?id=com.tosi.wastehunters \n\n";
               i.putExtra(Intent.EXTRA_TEXT, sAux);
               startActivity(Intent.createChooser(i, "Choose one"));
           } catch(Exception e) {
               //e.toString();
           }
       }
        if (objFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.enter, R.anim.exit);
            ft.replace(R.id.content_main_panel, objFragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void trash(View v){
        logWaste();
    }

    public void statistics(View v){
        Fragment objFragment = null;
        objFragment = new Statistics_f();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit);
        get_fr = 2;
        ft.replace(R.id.content_main_panel, objFragment);
        ft.commit();
    }
    public void containers(View v){
        Fragment objFragment = null;
        objFragment = new containers_f();
        get_fr = 2;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit);
        ft.replace(R.id.content_main_panel, objFragment);
        ft.commit();
    }


    public void share(View v){
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Wastehunters");
            String sAux = "\nStiahni si aplikáciu a buď aj ty Wastehunter\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.tosi.wastehunters \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    public void y_ok(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainPanel.this);
        builderSingle.setIcon(R.drawable.plast_t);
        builderSingle.setTitle("Čo môžeš vhodiť:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainPanel.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Prázdne fľaše od nápojov");
        arrayAdapter.add("Kelímky od jogurtov");
        arrayAdapter.add("Nádoby od šampónov");
        arrayAdapter.add("Čisté číre fólie");
        arrayAdapter.add("Farebné plastové tašky");
        arrayAdapter.add("Tašky");
        arrayAdapter.add("Sáčky");
        arrayAdapter.add("Streč fólie");
        arrayAdapter.add("Obal od mlieka");

        builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builderSingle.show();
    }
    public void y_bad(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainPanel.this);
        builderSingle.setIcon(R.drawable.plast_t);
        builderSingle.setTitle("Čo nesmieš vhodiť:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainPanel.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Celofán");
        arrayAdapter.add("Znešistené fólie");
        arrayAdapter.add("OBaly od nebezpečných látok");
        arrayAdapter.add("Obaly od chemikálií");
        arrayAdapter.add("Podlahové krytiny");
        arrayAdapter.add("Guma");
        arrayAdapter.add("Molitan");

        builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builderSingle.show();
    }

    public void b_ok(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainPanel.this);
        builderSingle.setIcon(R.drawable.paper_t);
        builderSingle.setTitle("Čo môžeš vhodiť:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainPanel.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Noviny");
        arrayAdapter.add("Časopisy");
        arrayAdapter.add("Kancelársky papier");
        arrayAdapter.add("Reklamné letáky");
        arrayAdapter.add("Knihy");
        arrayAdapter.add("Zošity");
        arrayAdapter.add("Krabice");
        arrayAdapter.add("Lepenka");
        arrayAdapter.add("Kartón");

        builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builderSingle.show();
    }

    public void b_bad(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainPanel.this);
        builderSingle.setIcon(R.drawable.paper_t);
        builderSingle.setTitle("Čo nesmieš vhodiť:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainPanel.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Mastný papier");
        arrayAdapter.add("Celofán");
        arrayAdapter.add("Fóliu");
        arrayAdapter.add("Obaly od kávy");
        arrayAdapter.add("Obali na vajíčka");
        arrayAdapter.add("Tetrapaky");

        builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builderSingle.show();
    }
    public void g_ok(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainPanel.this);
        builderSingle.setIcon(R.drawable.glass_t);
        builderSingle.setTitle("Čo môžeš vhodiť:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainPanel.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Fľaše od nápojov");
        arrayAdapter.add("Fľaše od zváranín");
        arrayAdapter.add("Fľaše od kozmetiky");
        arrayAdapter.add("Sklenené nádoby");
        arrayAdapter.add("Rozbité tabuľové sklo");

        builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builderSingle.show();
    }
    public void g_bad(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainPanel.this);
        builderSingle.setIcon(R.drawable.glass_t);
        builderSingle.setTitle("Čo nesmieš vhodiť:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainPanel.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Žiarovky");
        arrayAdapter.add("Keramika");
        arrayAdapter.add("Zrkadlá");
        arrayAdapter.add("Naplnené zaváraninové fľaše");

        builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builderSingle.show();
    }
    public void send_mail(View v){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"tosiapps@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Wastehunter " + name + " má nový podnet.");
        i.putExtra(Intent.EXTRA_TEXT   , "");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainPanel.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
