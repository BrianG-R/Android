package com.example.actividad_clase24_9;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;

public class AResultado extends AppCompatActivity {
    private EditText etNombreFoto;
    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Inicializar el EditText y ImageView
        etNombreFoto = findViewById(R.id.ETNombreFoto);
        imageView = findViewById(R.id.imageView2);  // Asegúrate de que el ImageView esté en el layout

        // Obtener el dato del Intent
        String stNombre = getIntent().getStringExtra("STNombre");

        // Verificar si stNombre no es nulo ni vacío
        if (stNombre != null && !stNombre.isEmpty()) {
            // Concatenar el nombre con la frase adicional
            String textoFinal = "El nombre de la foto es: " + stNombre;

            // Establecer el texto concatenado en el EditText
            etNombreFoto.setText(textoFinal);

            // El nombre de la imagen (por ejemplo, "hol1", "hol2", etc.)
            String nombreImagen = "hol" + stNombre; // Si stNombre es "1", será "hol1"

            // Obtener el ID del recurso drawable
            int imageResId = getResources().getIdentifier(nombreImagen, "drawable", getPackageName());

            // Verificar si la imagen existe
            if (imageResId != 0) {
                // Si la imagen se encuentra, cargarla
                imageView.setImageResource(imageResId);
            } else {
                // Si la imagen no se encuentra, mostrar un mensaje
                Toast.makeText(AResultado.this, "Imagen no encontrada", Toast.LENGTH_SHORT).show();
            }

            // Mostrar un mensaje Toast con la confirmación de los datos
            String mensajeToast = "Datos recibidos";
            Toast.makeText(AResultado.this, mensajeToast, Toast.LENGTH_LONG).show();
        } else {
            // Si no se recibe el nombre, mostrar un mensaje de error
            Toast.makeText(AResultado.this, "Nombre no recibido", Toast.LENGTH_SHORT).show();
        }




    }
}