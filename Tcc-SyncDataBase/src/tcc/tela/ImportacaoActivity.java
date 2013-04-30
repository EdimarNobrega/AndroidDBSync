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

public class ImportacaoActivity extends Activity implements Runnable {

    private String resultado;
    private JSONArray bancoServidor;
    private JSONArray bancoLocal;
    private ProgressDialog dialog;
    private int quantRegistro = 0;
    private SyncAndroid importacao;
    private BancoLocal banco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importacao);
        dialog = ProgressDialog.show(this, "Iniciando importação", "Por favor aguarde ...", false, true);
        dialog.setIcon(R.drawable.sync);
        //Aconselha-se iniciar uma nova thread .
        new Thread(this).start();

    }
    public void run() {
        //cria a conexão com o banco local
        importacao = new SyncAndroid(this, "tcc.db");
        banco = new BancoLocal(this, "tcc.db");
        try {

            //Recebido WS o banco do servidor
            resultado = importacao.getBancoServidor("http://192.168.43.133:8080/TestSync/Servidor");
            //Transforma a string do banco servidor em um objeto json
            bancoServidor = new JSONArray(resultado);
            //Monta o banco local
            bancoLocal = banco.getBancoLocal();
            Log.i("bl", "------>>>qqq" + bancoLocal);
            //testa se o banco local está vazio
            if (bancoLocal.length() == 0) {
                Log.i("bl", "------>>>banco vazio");
                //Retorna a quantidade de registro atualizado
                quantRegistro = importacao.inserirBServidorNoBLocal(bancoServidor);
            } else {
                //verifica se existe registros para serem atualizado ou inserido no banco local e retorna a quantidade de registro atualizado
                quantRegistro = importacao.sicronizarBServidoComBLocal(bancoServidor, bancoLocal);
            }

        } catch (JSONException e) {
            Log.e("WebService", e.toString());
        } finally {
            dialog.dismiss();
            Intent itent = new Intent(this, AlertaActivity.class);
            itent.putExtra("nome", "Importado");
            itent.putExtra("qr", quantRegistro);
            startActivity(itent);
            finish();
        }


    }


}
