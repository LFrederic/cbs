package com.example.cbs.cbs;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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

        System.out.print("MainActivity.java : Application lancée");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_FINE_GPS);
        startService(new Intent(this, GPSLocalisationService.class));

        mAuth = FirebaseAuth.getInstance();
        mDatabaseDemandeRetour = FirebaseDatabase.getInstance().getReference("DemandeRetour");

        Button toAccueil = findViewById(R.id.mainButton);
        toAccueil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RenseignerNumeroActivity.class));
            }
        });


        ImageButton param = findViewById(R.id.paramButton);
        param.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DefaultParamActivity.class));
            }
        });


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