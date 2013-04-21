package com.example.exemplo;

import org.json.JSONArray;
import org.json.JSONException;

import tcc.modelo.Importacao;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;

public class ExportacaoActivity extends Activity implements Runnable {
	
	private String resultado;
	private JSONArray bancoLocal;
	private ProgressDialog dialog;
	private int quantRegistro=0;
	private Importacao imp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_importacao);
		dialog=ProgressDialog.show(this,"Iniciando Exportação", "Por favor aguarde ...",false,true );
		dialog.setIcon(R.drawable.sync);
		//Inícia uma nova thread aconselhado.
		new Thread(this).start();

	}



	public void run() {

		
		//cria a conecção com o banco local
		imp=new Importacao(this,"tcc.db"); 
		try {
			//Monta o banco local
			bancoLocal=imp.getSyncDataBaseItensJsonBancoLocal();
			Log.i("bl", "------>>>expo----"+bancoLocal);
			//Recebido WS o banco do servidor
			resultado = imp.enviaBancoServidor(bancoLocal,"http://192.168.43.133:8080/ExemploServidor/ExemploServidor");
			JSONArray resul  = new JSONArray(resultado);
			
			Log.i("bl", "------>>>expo----"+resultado);
			Log.i("bl", "------>>>expo-1---"+resul.get(0));
			if(resultado!=null){
				quantRegistro =(Integer) resul.get(0);
			}
			

		} catch (JSONException e) {
			Log.e("WebService", e.toString());
		}
		finally{
			dialog.dismiss();
			Intent itent= new Intent(this,AlertaActivity.class); 
			itent.putExtra("nome", "Exportação");
			itent.putExtra("qr",quantRegistro);
			startActivity(itent);
			finish();
		}

	}
}
