package es.upm.miw.ficheros;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

public class FicherosActivity extends AppCompatActivity {

    String NOMBRE_FICHERO;
    String RUTA_FICHERO;
    EditText lineaTexto;
    Button botonAniadir;
    TextView contenidoFichero;
    boolean activo;


    @Override
    protected void onStart() {
        super.onStart();
        mostrarContenido(contenidoFichero);
        this.activo = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("example_switch", true);
        this.NOMBRE_FICHERO = PreferenceManager.getDefaultSharedPreferences(this).getString("example_text", "myData.txt");
        RUTA_FICHERO = getExternalFilesDir(null) + "/" + NOMBRE_FICHERO;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficheros);
        lineaTexto       = (EditText) findViewById(R.id.textoIntroducido);
        botonAniadir     = (Button)   findViewById(R.id.botonAniadir);
        contenidoFichero = (TextView) findViewById(R.id.contenidoFichero);
    }

    public void accionAniadir(View v) {
        if(activo) {
            accionAniadirSD(v);
        } else {
            accionAniadirLocal(v);
        }
    }

    public void accionAniadirLocal(View v) {
        try {
            FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_APPEND);
            //FileOutputStream fos = new FileOutputStream(RUTA_FICHERO, true);
            fos.write(lineaTexto.getText().toString().getBytes());
            fos.write('\n');
            fos.close();
            mostrarContenido(contenidoFichero);
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void accionAniadirSD(View v) {
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                // FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_APPEND);
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO, true);
                fos.write(lineaTexto.getText().toString().getBytes());
                fos.write('\n');
                fos.close();
                mostrarContenido(contenidoFichero);
                Log.i("FICHERO", "Click botón Añadir -> AÑADIR al fichero");
                Log.d("ACTIVO", new Boolean(this.activo).toString());
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void mostrarContenido(View textviewContenidoFichero) {
        if(activo) {
            mostrarContenidoSD(textviewContenidoFichero);
        } else {
            mostrarContenidoLocal(textviewContenidoFichero);
        }
    }

    public void mostrarContenidoLocal(View textviewContenidoFichero) {
        boolean hayContenido = false;
        contenidoFichero.setText("");
        try {
                 BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput(NOMBRE_FICHERO)));
                //BufferedReader fin = new BufferedReader(new FileReader(new File(RUTA_FICHERO)));
                String linea = fin.readLine();
                while (linea != null) {
                    hayContenido = true;
                    contenidoFichero.append(linea + '\n');
                    linea = fin.readLine();
                }
                fin.close();
                Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    public void mostrarContenidoSD(View textviewContenidoFichero) {
        boolean hayContenido = false;
        File fichero = new File(RUTA_FICHERO);
        String estadoTarjetaSD = Environment.getExternalStorageState();
        contenidoFichero.setText("");
        try {
            if (fichero.exists() && estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                // BufferedReader fin =
                //        new BufferedReader(new InputStreamReader(openFileInput(NOMBRE_FICHERO)));
                BufferedReader fin = new BufferedReader(new FileReader(new File(RUTA_FICHERO)));
                String linea = fin.readLine();
                while (linea != null) {
                    hayContenido = true;
                    contenidoFichero.append(linea + '\n');
                    linea = fin.readLine();
                }
                fin.close();
                Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // case 1:
            case R.id.accionVaciar:
                borrarContenido();
                break;
            case R.id.accionAjustes:
                mostrarAjustes();
                break;
        }

        return true;
    }

    public void borrarContenido() {
        if(activo) {
            borrarContenidoSD();
        } else {
            borrarContenidoLocal();
        }
    }

    public void borrarContenidoLocal() {
        try {
                FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
                //FileOutputStream fos = new FileOutputStream(RUTA_FICHERO);
                fos.close();
                Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
                lineaTexto.setText(""); // limpio la linea de edición
                mostrarContenido(contenidoFichero);
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void borrarContenidoSD() {
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                // FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO);
                fos.close();
                Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
                lineaTexto.setText(""); // limpio la linea de edición
                mostrarContenido(contenidoFichero);
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void mostrarAjustes() {
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

}
