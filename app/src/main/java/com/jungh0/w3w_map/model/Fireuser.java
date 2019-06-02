package com.jungh0.w3w_map.model;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Fireuser {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private Activity app;
    static private String user_data = null;

    public Fireuser(Activity act, String id,String pass){
        app = act;
        fire_login(id,pass);
    }

    private void fire_login(String id, String pass){
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(id, pass)
                .addOnCompleteListener(app, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            get_user();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            user_data = "error";
                        }
                    }
                });
    }

    private void get_user(){
        if (user != null) {

            final String uid = user.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            DatabaseReference tmp = mDatabase.child("users");
            tmp.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user_data = dataSnapshot.child(uid).getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("UserListActivity", "Error occured");
                    user_data = "error";
                }});
        }
    }

    public static String get_database(){
        return Fireuser.user_data;
    }

    public static void logout(){
        Fireuser.user_data = null;
    }

}
