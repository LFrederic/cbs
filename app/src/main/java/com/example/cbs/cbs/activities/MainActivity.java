package com.example.cbs.cbs.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.cbs.cbs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseDemandeRetour;
    private final Integer REQUEST_CODE_FINE_GPS = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  FirebaseApp.initializeApp(this);

        //Demande de permission à l'utilisateur pour l'accès à sa position GPS, l'envoi de SMS et accès aux contacts
        System.out.print("MainActivity.java : Application lancée");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE},
                REQUEST_CODE_FINE_GPS);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseDemandeRetour = FirebaseDatabase.getInstance().getReference("DemandeRetour");

        //Affichage d'un bouton 'Je suis Safe' avec un Intent vers RenseignerNumeroActivity
        ImageButton toAccueil = findViewById(R.id.mainButton);
        toAccueil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RenseignerNumeroActivity.class));
            }
        });


        //Affichage d'un bouton paramètres avec un Intent vers ParametreDefautActivity
        ImageButton param = findViewById(R.id.paramButton);
        param.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ParametreDefautActivity.class));
            }
        });


        //Connexion à la BDD Firebase
        mAuth.signInWithEmailAndPassword("test@gmail.com", "testtest").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) //Si on arrive à se connecter
                {
                    System.out.println("connecté");
                    DatabaseReference node = mDatabaseDemandeRetour.push();

                    node.child("numero").child("parent").setValue("0666666666");
                    node.child("numero").child("enfant").setValue("0666666666");

                    node.child("coordonnées").child("latitude").setValue("1");
                    node.child("coordonnées").child("longitude").setValue("2");

                    node.child("date").setValue("14/03/2018 18:59");

                    node.child("message").setValue("message");
                } else {
                    System.out.println("MainActivity.java : Impossible de se connecter avec les informations fournies - " + task.getException());
                    Toast.makeText(MainActivity.this, "Adresse email ou mot de passe incorrect.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
