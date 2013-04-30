package tcc.tela;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListarRegistroTabelaActivity extends ListActivity {
    private List<String> estados;
    private JSONArray registros;
    private String tr,tipo, chave, nomeTabela;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        estados = new ArrayList<String>();
        Bundle extra = getIntent().getExtras();
        try {        	
        	nomeTabela=extra.getString("nomeTabela");
        	tipo=extra.getString("tipo");
        	chave=extra.getString("chave");
        	tr=extra.getString("TabelaResg");
        	registros= new JSONArray(tr);			
			for(int i=0; i<registros.length();i++){				
				estados.add(registros.get(i).toString());				
			}				
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        estados.add("Sair");
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, estados));      
        
    }
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		
			
			try {				
				

					Intent itent = new Intent(this, EditarLinhaTabelaActivity.class);
					itent.putExtra("nomeTabela", nomeTabela);
					itent.putExtra("tipo", tipo);
					itent.putExtra("chave", chave);
					itent.putExtra("linha", registros.getString(position));					
					startActivity(itent);					
					
				
				if(position== registros.length()-1){
					//Encerra a activity (encerra o ciclo de vida)
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		   	

	}


}