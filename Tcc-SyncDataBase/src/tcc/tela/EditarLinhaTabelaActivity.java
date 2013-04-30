package tcc.tela;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class EditarLinhaTabelaActivity extends Activity {
	 private JSONObject linha;
	 private JSONArray chaveArray, tipoArray;
	 private String  linh,tipo,chave,nomeTabela, colunaChave;
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);       
	        Log.i("bl", "------>>>edit" + "edimar");
	        Bundle extra = getIntent().getExtras();
	        TableLayout tl= new TableLayout(this);
	        
	        try {
	        	
	        	tipo=extra.getString("tipo");
	        	chave=extra.getString("chave");
	        	nomeTabela=extra.getString("nomeTabela");
	        	linh=extra.getString("linha");
	        	chaveArray = new JSONArray(chave);
	        	tipoArray = new JSONArray(tipo);
	        	linha= new JSONObject(linh.toString());
	        	
	        	  Log.i("bl", "------>>>edit3" + chaveArray);
	        	  Log.i("bl", "------>>>edit3" + tipoArray);
	        	  Log.i("bl", "------>>>edit3" + linha);
	        	  Log.i("bl", "------>>>edit3" + nomeTabela);
	        	  
				for(int i=0; i<linha.length();i++){
					  Log.i("bl", "------>>>edit2" + linha.names().get(0));
					  TableRow tr= new TableRow(this);
						TextView tv = new TextView(this);
							tv.setText(linha.names().get(i).toString()+": ");
						EditText et= new EditText(this);
							et.setWidth(220);
							et.setText(linha.get(linha.names().get(i).toString()).toString());
						tr.addView(tv);
						tr.addView(et);											
					//linha.get(i).toString();
						tl.addView(tr);
				}
				//pega a coluna chave da tabela
				for (int l = 0; l < chaveArray.length(); l++) {
					if (chaveArray.getJSONObject(l).getString("Tabela").equals(nomeTabela)) {
						String[] c = chaveArray.getJSONObject(l).getString("chave").split("_");
						if (c[0].equals("PK")) {
							colunaChave = chaveArray.getJSONObject(l).getString("Coluna");
						}
					}
				}
				
				Button ib= new Button(this);
				//ib.setImageResource(R.drawable.btn_dialog);
				ib.setText(" Editar ");
				Button ib1= new Button(this);
				//ib.setImageResource(R.drawable.edit_text);
				ib1.setText(" Excluir ");
				Button ib2= new Button(this);
				//ib.setImageResource(R.drawable.edit_text);
				ib2.setText("Inserir");
				
				//LinearLayout tr= new LinearLayout(this);
				// tr.addView(ib);
				// tr.addView(ib1);
				 //tr.addView(ib2);
				// tl.addView(tr);
				
				setContentView(tl);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
	        
	        
	    }

}
