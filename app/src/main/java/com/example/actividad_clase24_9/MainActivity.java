package com.example.actividad_clase24_9;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;



public class MainActivity extends AppCompatActivity {
    private ImageView imageView;

    private EditText etNombre;
    private Button btnAceptar;
    private Button btnDescargar;
    private ProgressBar progressBar;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private SensorEventListener rotationListener;
    private TextView tvOrientacion;


    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de los componentes UI
        etNombre = findViewById(R.id.ETNombre);
        btnAceptar = findViewById(R.id.btnAceptar);
        imageView = findViewById(R.id.imageView);
        btnDescargar = findViewById(R.id.btnDescargar);
        progressBar = findViewById(R.id.progressBar);  // Inicializa el ProgressBar
        tvOrientacion = findViewById(R.id.tvOrientacion);

        // Establecer una imagen predeterminada
        imageView.setImageResource(R.drawable.hol1);

        // Configurar el botón "Aceptar"
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el nombre del EditText
                String stNombre = etNombre.getText().toString();
                String nombreImagen = "hol1";

                // Pasar los datos a la siguiente actividad
                Intent sIntent = new Intent(MainActivity.this, AResultado.class);
                sIntent.putExtra("STNombre", stNombre);
                sIntent.putExtra("ImagenNombre", nombreImagen);
                startActivity(sIntent);
            }
        });

        // Configurar el botón "Descargar Imagen"
        btnDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar el ProgressBar mientras se descarga la imagen
                progressBar.setVisibility(View.VISIBLE);

                // Descargar la imagen en un hilo separado
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Reemplazar con la URL de tu imagen
                        String imageUrl = "https://tse1.mm.bing.net/th/id/OIP.IXZgKnnp3Iehk72cZqlxsQHaKh?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3";
                        Log.d(TAG, "Iniciando descarga de la imagen desde: " + imageUrl);
                        final Bitmap bitmap = loadImageFromNetwork(imageUrl);

                        // Actualizar la UI en el hilo principal
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                // Ocultar el ProgressBar después de la descarga
                                progressBar.setVisibility(View.GONE);

                                // Mostrar la imagen si se descargó correctamente
                                if (bitmap != null) {
                                    Log.d(TAG, "Imagen descargada correctamente");
                                    imageView.setImageBitmap(bitmap);
                                } else {
                                    Log.e(TAG, "Error al descargar la imagen");
                                    // Si hubo un error en la descarga
                                    Toast.makeText(MainActivity.this, "Error al descargar la imagen", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();  // Iniciar el hilo
            }
        });

        // Inicializar el SensorManager y el sensor de rotación
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (rotationSensor == null) {
            Toast.makeText(this, "Sensor de rotación no disponible", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Sensor de rotación no disponible en este dispositivo.");
        } else {
            rotationListener = new SensorEventListener() {

                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                        float[] rotationMatrix = new float[9];
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

                        float[] orientations = new float[3];
                        SensorManager.getOrientation(rotationMatrix, orientations);

                        // Convertir radianes a grados
                        float azimuth = (float) Math.toDegrees(orientations[0]);
                        float pitch = (float) Math.toDegrees(orientations[1]);
                        float roll = (float) Math.toDegrees(orientations[2]);

                        String orientationMsg = String.format("Azimuth: %.1f°\nPitch: %.1f°\nRoll: %.1f°", azimuth, pitch, roll);

                        Log.d(TAG, "Orientación del dispositivo: " + orientationMsg);

                        // Actualizar el TextView en pantalla
                        tvOrientacion.setText("Cambio de orientación:\n" + orientationMsg);
                    }
                }


                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // No es necesario implementarlo por ahora
                }
            };
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rotationSensor != null && rotationListener != null) {
            sensorManager.registerListener(rotationListener, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rotationListener != null) {
            sensorManager.unregisterListener(rotationListener);
        }
    }



    private Bitmap loadImageFromNetwork(String urlString) {
        try {
            Log.d(TAG, "Conectando a la URL: " + urlString);
            // Crear la conexión HTTP
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);  // Permitir la entrada de datos
            connection.connect();  // Conectar

            // Obtener el flujo de entrada
            InputStream input = connection.getInputStream();
            Log.d(TAG, "Conexión establecida. Descargando imagen...");

            // Decodificar la imagen a un objeto Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();  // Cerrar el flujo de entrada
            return bitmap;  // Retornar el Bitmap descargado
        } catch (IOException e) {
            Log.e(TAG, "Error de conexión o descarga: " + e.getMessage());
            e.printStackTrace();
            return null;  // Si ocurre un error, retornar null
        }
    }
}