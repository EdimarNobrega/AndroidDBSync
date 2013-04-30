package tcc.tela;

import java.util.ArrayList;
import java.util.List;
import modelo.BancoLocal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SuaAplicacaoActivity extends ListActivity {
	private List<String> estados;
	private JSONArray bancoLocal;
	private JSONArray registros;
	private BancoLocal banco;
	private String nomeTabela;
	private JSONObject tabelaLocal;
	private JSONObject tabelaTipo,tabelaChave;
	private String tipo,chave;
	private JSONArray Tipo, Chave;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		banco = new BancoLocal(this, "tcc.db");
		estados = new ArrayList<String>();
		
		try {
			bancoLocal = banco.getBancoLocal();
			for(int i=0; i<bancoLocal.length();i++){
				//Obitem o nome da tabela mais o array de registros
				tabelaLocal = bancoLocal.getJSONObject(i);
				//Obitem o nome da tabela
				nomeTabela = tabelaLocal.names().get(0).toString();
				estados.add(nomeTabela);
				
				 for (int j = 0; j < bancoLocal.length(); j++) {
			            //Obitem o nome da tabela mais o array de registros
			            JSONObject tabela = bancoLocal.getJSONObject(j);

			            //Obitem o nome da tabela
			            String nomeTabela = tabela.names().get(0).toString();
			            if (nomeTabela.equals("Tipos")) {
			                tipo = "Tipos";
			                tabelaTipo = bancoLocal.getJSONObject(j);
			                Tipo = tabelaTipo.getJSONArray(tipo);
			            }
			            if (nomeTabela.equals("Chaves")) {
			                chave = "Chaves";
			                tabelaChave = bancoLocal.getJSONObject(j);
			                Chave = tabelaChave.getJSONArray(chave);
			            }

			        }
			}
			


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		estados.add("Sair");
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, estados));


	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		for(int i=0; i<bancoLocal.length();i++){

			try {
				//Obitem o nome da tabela mais o array de registros
				tabelaLocal = bancoLocal.getJSONObject(i);
				//Obitem o nome da tabela
				nomeTabela = tabelaLocal.names().get(0).toString();
				registros =tabelaLocal.getJSONArray(nomeTabela);
				if(position==i && position< bancoLocal.length()){

					Intent itent = new Intent(this, ListarRegistroTabelaActivity.class);
					itent.putExtra("nomeTabela", nomeTabela);
					itent.putExtra("tipo", Tipo.toString());
					itent.putExtra("chave", Chave.toString());
					itent.putExtra("TabelaResg", registros.toString());
					startActivity(itent);					
					break;
				}
				
				if(position== bancoLocal.length()-1){
					//Encerra a activity (encerra o ciclo de vida)
					finish();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}    	

	}
}