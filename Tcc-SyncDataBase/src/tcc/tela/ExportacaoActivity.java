package tcc.tela;


import modelo.BancoLocal;
import modelo.SyncAndroid;

import org.json.JSONArray;
import org.json.JSONException;
import tcc.tela.AlertaActivity;
import tcc.tela.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;

public class ExportacaoActivity extends Activity implements Runnable {

    private String resultado;
    private JSONArray bancoLocal;
    private ProgressDialog dialog;
    private int quantRegistro = 0;
    private SyncAndroid exportacao;
    private BancoLocal banco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importacao);
        dialog = ProgressDialog.show(this, "Iniciando Exporta��o", "Por favor aguarde ...", false, true);
        dialog.setIcon(R.drawable.sync);
        //Aconselha-se iniciar uma nova thread .
        new Thread(this).start();
    }
    public void run() {
        //cria a conex�o com o banco local
    	exportacao = new SyncAndroid(this, "tcc.db");
        banco = new BancoLocal(this, "tcc.db");
        try {
            //Monta o banco local
            bancoLocal = banco.getBancoLocal();
            Log.i("bl", "------>>>expo----" + bancoLocal);
            //Recebido WS o banco do servidor
            resultado = exportacao.enviaBancoServidor(bancoLocal, "http://192.168.43.133:8080/TestSync/Servidor");
            JSONArray resul = new JSONArray(resultado);
            Log.i("bl", "------>>>expo----" + resultado);
            Log.i("bl", "------>>>expo-1---" + resul.get(0));
            if (resultado != null) {
                quantRegistro = (Integer) resul.get(0);
            }

        } catch (JSONException e) {
            Log.e("WebService", e.toString());
        } finally {
            dialog.dismiss();
            Intent itent = new Intent(this, AlertaActivity.class);
            itent.putExtra("nome", "Exporta��o");
            itent.putExtra("qr", quantRegistro);
            startActivity(itent);
            finish();
        }

    }
}

