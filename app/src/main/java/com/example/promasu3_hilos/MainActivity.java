package com.example.promasu3_hilos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private EditText edEnrada;
    private TextView tvSalida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edEnrada = findViewById(R.id.edEntrada);
        tvSalida = findViewById(R.id.tvSalida);

    }

    //Funcion llamada por el botón, que abre Thread o AsyncTask
    public void calcularOperacion (View v){
        int n = Integer.parseInt(edEnrada.getText().toString());
        tvSalida.append(n + "! = ");
//        MiThread thread = new MiThread(n);
//        thread.start();

//        MiTarea tarea = new MiTarea();
//        tarea.execute(n);

        MiTareaProgreso tarea = new MiTareaProgreso();
        tarea.execute(n);
    }

    private int factorial (int num){
        int resultado =1;
        for (int i=1; i<=num; i++){
            resultado *= i;
            SystemClock.sleep(1000);  //Esperamos un segundo
        }return resultado;

    }

    class MiThread extends Thread {

        private int n, res;

        public MiThread(int n) {
            Log.i("HILO","Estamos dentro del hilo");
            this.n = n;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int res = factorial(n);
                    tvSalida.append(res + "\n");
                }
            });
        }

    }

    //###> SEPARADOR ENTRE THREAD Y ASYNCTASK <###

    class MiTarea extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... n) {
            return factorial (n[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            tvSalida.append(result + "\n");
        }

    }

    class MiTareaProgreso extends AsyncTask<Integer, Integer, Integer> {
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            progreso = new ProgressDialog(MainActivity.this);
//            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progreso.setMessage("Calculando ...");
//            progreso.setCancelable(false); //EJEMPLO 1
            progreso.setCancelable(true); //EJEMPLO 2
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    MiTareaProgreso.this.cancel(true);
                }
            });
            progreso.setMax(100);
            progreso.setProgress(0);
            progreso.show();
        }

        @Override
        protected Integer doInBackground(Integer... n) {
            int res = 1;
            // el  && !isCancelled() del ejemplo 2
            for (int i=1; i<= n[0] && !isCancelled(); i++){
                res *= i;
                SystemClock.sleep(1000);
                publishProgress(i*100 / n[0]);
            }
            return res;
        }

        @Override
        protected void onProgressUpdate(Integer... porc) {
            progreso.setProgress(porc[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            progreso.dismiss();
            tvSalida.append(result + "\n");
        }

        //EJEMPLO 2
        @Override
        protected void onCancelled() {
            tvSalida.append("Cancelado\n");
        }
    }

}
