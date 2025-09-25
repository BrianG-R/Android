package com.example.actividad_clase24_9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;

    private EditText etNombre;
    private Button btnAceptar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = findViewById(R.id.ETNombre);
        btnAceptar = findViewById(R.id.btnAceptar);
        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.hol1);

        // Configurar el clic del botón
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el valor del EditText
                String stNombre = etNombre.getText().toString();


                String nombreImagen = "hol1";


                Intent sIntent = new Intent(MainActivity.this, AResultado.class);
                sIntent.putExtra("STNombre", stNombre);
                sIntent.putExtra("ImagenNombre", nombreImagen);
                startActivity(sIntent);
            }
        });
    }
}