package com.example.exemplo;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity  {
	private List<String> estados;	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		//setContentView(R.layout.main);
		estados = new ArrayList<String>();
		estados.add("Sua Aplicação");
		estados.add("Importação");
		estados.add("Exportação");
		estados.add("Sair");
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, estados));
		

	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0:
			//startActivity(new Intent(this,ImportacaoActivity.class));
			finish();
			break;
			case 1:
				startActivity(new Intent(this,ImportacaoActivity.class));
				finish();
				break;
			case 2:
				startActivity(new Intent(this,ExportacaoActivity.class));
				finish();
				break;
			
			default:
				//Encerra a activity (encerra o ciclo de vida)
				finish();
		}
	}	
}


