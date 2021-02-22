package com.tosi.wastehunters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    String id;
    ProgressDialog dialog;
    int introduction;
    final FirebaseDatabase database_fb = FirebaseDatabase.getInstance();
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    void getFromFirebaseAndSaveToPrefsString(String fb_item, final String pref_name){
        DatabaseReference ref = database_fb.getReference("users/" + id + "/" + fb_item);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value_local = dataSnapshot.getValue(String.class);
                SharedPreferences sp6 = getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor6 = sp6.edit();
                editor6.putString(pref_name + "_k", value_local);
                editor6.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getFromFirebaseAndSaveToPrefsStringID(String fb_item, final String pref_name){
        DatabaseReference ref = database_fb.getReference("users/" + id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value_local = dataSnapshot.getValue(String.class);
                SharedPreferences sp6 = getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor6 = sp6.edit();
                editor6.putString(pref_name + "_k", value_local);
                editor6.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getFromFirebaseAndSaveToPrefsInt(String fb_item, final String pref_name){
        final int[] value = new int[1];
        DatabaseReference ref = database_fb.getReference("users/" + id + "/" + fb_item);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int value_local = dataSnapshot.getValue(Integer.class);
                value[0] = value_local;
                SharedPreferences sp6 = getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor6 = sp6.edit();
                editor6.putInt(pref_name + "_k", value[0]);
                editor6.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private static final String TAG = "MainActivity";
    GoogleApiClient mGoogleApiClient;
    Button button;
    boolean exist = false, loaded = false;
    int RC_SIGN_IN = 12568;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        SharedPreferences sp5 = getSharedPreferences("show_introduction", Activity.MODE_PRIVATE);
        introduction = sp5.getInt("show_introduction_k", 0);

        if (introduction == 0){
            Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
            startActivity(intent);
            finish();
        }

        final SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()){
                    signIn(); 
                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
                
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
               // .enableAutoManage(this MainPanel.class, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        // Write a message to the database

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        final GoogleSignInAccount acct = result.getSignInAccount();
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            id = acct.getId();
            dialog = ProgressDialog.show(MainActivity.this, "",
                    getString(R.string.loading_dialog_box), true);
            FirebaseDatabase database_fb = FirebaseDatabase.getInstance();
            DatabaseReference rootRef = database_fb.getReference("users");
            rootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(acct.getId())) {
                        exist = true;
                        getFromFirebaseAndSaveToPrefsString("name", "name");
                        getFromFirebaseAndSaveToPrefsString("surname", "surname");
                        getFromFirebaseAndSaveToPrefsInt("count", "count");
                        getFromFirebaseAndSaveToPrefsString("friends", "friends");
                        getFromFirebaseAndSaveToPrefsString("profile_photo", "profile_photo");
                        getFromFirebaseAndSaveToPrefsInt("paper", "paper");
                        getFromFirebaseAndSaveToPrefsInt("plast", "plast");
                        getFromFirebaseAndSaveToPrefsInt("textil", "textil");
                        getFromFirebaseAndSaveToPrefsInt("other", "other");
                        getFromFirebaseAndSaveToPrefsInt("bio_waste", "bio_waste");
                        getFromFirebaseAndSaveToPrefsInt("glass", "glass");
                        getFromFirebaseAndSaveToPrefsInt("metals", "metals");
                        getFromFirebaseAndSaveToPrefsInt("wood_g_l", "wood_g_l");
                        getFromFirebaseAndSaveToPrefsString("id", "id");
                        SharedPreferences spp = getSharedPreferences("loged", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editorp = spp.edit();
                        editorp.putInt("loged_k", 1);
                        editorp.commit();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getApplicationContext(), MainPanel.class);
                        startActivity(intent);
                        finish();


                    } else {
                        loaded = true;
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference ref = database.getReference("users");
                        String mail = acct.getId();
                        DatabaseReference usersRef = ref.child(mail);
                        usersRef.child("id").setValue(acct.getId());
                        usersRef.child("name").setValue(acct.getGivenName());
                        usersRef.child("surname").setValue(acct.getFamilyName());
                        usersRef.child("count").setValue(0);
                        usersRef.child("friends").setValue("");
                        usersRef.child("profile_photo").setValue("" + acct.getPhotoUrl());
                        usersRef.child("paper").setValue(0);
                        usersRef.child("plast").setValue(0);
                        usersRef.child("textil").setValue(0);
                        usersRef.child("other").setValue(0);
                        usersRef.child("bio_waste").setValue(0);
                        usersRef.child("glass").setValue(0);
                        usersRef.child("metals").setValue(0);
                        usersRef.child("wood_g_l").setValue(0);

                      final DatabaseReference ref2 = database.getReference("users/global/users");
                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int value_local = dataSnapshot.getValue(Integer.class);
                                value_local++;
                                ref2.setValue(value_local);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                        SharedPreferences spp = getSharedPreferences("loged", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editorp = spp.edit();
                        editorp.putInt("loged_k", 1);
                        editorp.commit();


                        SharedPreferences sp0 = getSharedPreferences("id", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor0 = sp0.edit();
                        editor0.putString("id_k", acct.getId());
                        editor0.commit();

                        SharedPreferences sp = getSharedPreferences("name", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("name_k", acct.getGivenName());
                        editor.commit();

                        SharedPreferences sp2 = getSharedPreferences("surname", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = sp2.edit();
                        editor2.putString("surname_k", acct.getFamilyName());
                        editor2.commit();

                        SharedPreferences sp3 = getSharedPreferences("count", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor3 = sp3.edit();
                        editor3.putInt("count_k", 0);
                        editor3.commit();

                        SharedPreferences sp4 = getSharedPreferences("friends", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor4 = sp4.edit();
                        editor4.putString("friends_k", acct.getGivenName());
                        editor4.commit();

                        SharedPreferences sp5 = getSharedPreferences("profile_photo", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor5 = sp5.edit();
                        editor5.putString("profile_photo_k", "" + acct.getPhotoUrl());
                        editor5.commit();

                        SharedPreferences sp6 = getSharedPreferences("paper", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor6 = sp6.edit();
                        editor6.putInt("paper_k", 0);
                        editor6.commit();

                        SharedPreferences sp7 = getSharedPreferences("plast", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor7 = sp7.edit();
                        editor7.putInt("plast_k", 0);
                        editor7.commit();

                        SharedPreferences sp8 = getSharedPreferences("textil", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor8 = sp8.edit();
                        editor8.putInt("textil_k", 0);
                        editor8.commit();

                        SharedPreferences sp9 = getSharedPreferences("other", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor9 = sp9.edit();
                        editor9.putInt("other_k", 0);
                        editor9.commit();

                        SharedPreferences sp10 = getSharedPreferences("bio_waste", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor10 = sp10.edit();
                        editor10.putInt("bio_waste_k", 0);
                        editor10.commit();

                        SharedPreferences sp11 = getSharedPreferences("glass", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor11 = sp11.edit();
                        editor11.putInt("glass_k", 0);
                        editor11.commit();

                        SharedPreferences sp12 = getSharedPreferences("metals", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor12 = sp12.edit();
                        editor12.putInt("metals_k", 0);
                        editor12.commit();

                        SharedPreferences sp13 = getSharedPreferences("wood_g_l", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor13 = sp13.edit();
                        editor13.putInt("wood_g_l_k", 0);
                        editor13.commit();
                        Intent intent = new Intent(getApplicationContext(), MainPanel.class);
                        startActivity(intent);
                        finish();
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
