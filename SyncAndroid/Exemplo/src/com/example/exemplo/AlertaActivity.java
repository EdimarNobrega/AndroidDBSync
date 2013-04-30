package com.example.exemplo;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class AlertaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alerta);
		AlertDialog.Builder ad = new AlertDialog.Builder(AlertaActivity.this);
		Bundle extra= getIntent().getExtras();
      
		ad.setTitle(extra.getString("nome"));
		if(extra.getString("nome").equals("Importado")){
			if(extra.getInt("qr")==1){
				ad.setMessage("Foi atualizado  "+extra.getInt("qr")+" registro!!! Deseja fazer outra operação?");
			}else{
				if(extra.getInt("qr")==0){
					ad.setMessage("Não existe registros para serem atualizadas !!! Deseja fazer outra operação?");
				}else{
					ad.setMessage("Foram atualizados "+extra.getInt("qr")+"  registros!!! Deseja fazer outra operação?");
				}	
				
			}
		}else{
			if(extra.getInt("qr")==1){
				ad.setMessage("Foi atualizado  "+extra.getInt("qr")+" registro!!! Deseja fazer outra operação?");
			}else{
				if(extra.getInt("qr")==0){
					ad.setMessage("Não existe registros para serem atualizadas !!! Deseja fazer outra operação?");
				}else{
					ad.setMessage("Foram atualizados "+extra.getInt("qr")+"  registros!!! Deseja fazer outra operação?");
				}	
				
			}
		}
		

		ad.setIcon(R.drawable.tick);

		ad.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	Intent itent= new Intent();  
            	itent.setClass(getApplicationContext(), MainActivity.class);
    			startActivity(itent);
    			finish();
            }
        });

		ad.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            finish();
            }
        });

		ad.show();
	}
}