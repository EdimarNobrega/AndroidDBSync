package modelo;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SyncServidor implements Serializable{
	private	JSONArray banco;
	private JSONArray arrayLinhas;
	private JSONObject nomeColunaValor;
	private List<String> tabelas;
	private JSONObject tabela;
	private String sql, nomeTabela;
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	private String ConnectionUrl;
	private int quantRegistro=0;
	private Map<String, String> m;

	private static final long serialVersionUID = 1L;

	public SyncServidor( String connectionUrl) throws ClassNotFoundException, SQLException {
		super();
		this.ConnectionUrl=connectionUrl;
		this.con = null;
		this.stmt = null;
		this.rs = null;
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); 
		con = DriverManager.getConnection(ConnectionUrl);
		m =new HashMap<String, String>();
	}

	public ResultSet executarStatement(String sql) throws SQLException {
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
		return this.rs;
	}
	public int executarUpdate(String sql) throws SQLException{
		stmt = con.createStatement();
		return stmt.executeUpdate(sql);
	}
	public void fechar(){
		try {
			this.rs.close();
			this.stmt.close();
			this.con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}	

	//Retorna o banco em formato json
	public  JSONArray getBancoLocal()throws NumberFormatException, SQLException, ClassNotFoundException, JSONException, IOException {
		String tipos="Tipos";	
		JSONObject tipo  = new JSONObject();
		String chaves="Chaves";	
		JSONObject chave  = new JSONObject();
		tabelas = new ArrayList<String>();
		banco  = new JSONArray();
		sql = "select  sys.tables.name as tabela from sys.tables "+  
				" inner join information_schema.key_column_usage  on sys.tables.name=information_schema.key_column_usage.table_name  and "+    
				" sys.tables.name <>'sysdiagrams' order by information_schema.key_column_usage.constraint_name desc";
		ResultSet r = executarStatement(sql);
		ResultSetMetaData rsmd = r.getMetaData();
		tabelas.add("");
		while (r.next()) {
			tabela  = new JSONObject();			

			nomeTabela=r.getString(1);

			for(int i=0; i<tabelas.size(); i++){
				if(tabelas.get(i)!=nomeTabela){
					tabelas.add(nomeTabela);
					tabela.put(nomeTabela, getArrayLinhasJson(nomeTabela));
				}
			}


			// le os registros de uma tabela em um array
			// monta um objeto com nome da tabela:conteudo

			banco.put(tabela); // adiciona tabela no banco (array de tabela)
					
		}
		chave.put(chaves, getArrayChaves());
		tipo.put(tipos,getArrayTipos());
		banco.put(chave);
		banco.put(tipo);

		fechar();		
		return banco;		
	}
	//Retorna um array de registros
	public  JSONArray getArrayLinhasJson( String tabela)throws NumberFormatException, SQLException, ClassNotFoundException, JSONException, FileNotFoundException {

		sql = "select * from  "+tabela;		
		arrayLinhas = new JSONArray();
		ResultSet r = executarStatement(sql);
		ResultSetMetaData rsmd = r.getMetaData();		
		rsmd = r.getMetaData();
		while (r.next()) {
			nomeColunaValor  = new JSONObject();
			for(int i=1;i<=rsmd.getColumnCount();i++){
				if(r.getObject(i)==null){

					nomeColunaValor.put(rsmd.getColumnName(i),"");	
				}else{
					if(r.getObject(i).equals(true)){
						nomeColunaValor.put(rsmd.getColumnName(i),1);
					}else{
						if(r.getObject(i).equals(false)){
							nomeColunaValor.put(rsmd.getColumnName(i),0);
						}else{
							nomeColunaValor.put(rsmd.getColumnName(i),r.getObject(i));
						}
					}
				}
			}			
			arrayLinhas.put(nomeColunaValor);
		}
		return arrayLinhas;
	}

	public  JSONArray getArrayChaves() throws SQLException, JSONException{

		sql = "select table_name as Tabela, column_name as Coluna ,  constraint_name as chave from "+
				"information_schema.key_column_usage where  table_name <>'sysdiagrams' order by tabela";

		
		arrayLinhas = new JSONArray();

		ResultSet r = executarStatement(sql);
		ResultSetMetaData rsmd = r.getMetaData();

	 
		rsmd = r.getMetaData();

		while (r.next()) {
			nomeColunaValor  = new JSONObject();
			for(int i=1;i<=rsmd.getColumnCount();i++){
				if(r.getObject(i)==null){

					nomeColunaValor.put(rsmd.getColumnName(i),"");	
				}else{
					if(r.getObject(i).equals(true)){
						nomeColunaValor.put(rsmd.getColumnName(i),1);
					}else{
						if(r.getObject(i).equals(false)){
							nomeColunaValor.put(rsmd.getColumnName(i),0);
						}else{
							nomeColunaValor.put(rsmd.getColumnName(i),r.getObject(i));
						}
					}
				}
			}			
			arrayLinhas.put(nomeColunaValor);

		}


		return arrayLinhas;

	}


	public JSONArray  getArrayTipos() throws SQLException, JSONException{

		sql="select distinct sys.columns.name as coluna, system_type_id as tipo , sys.tables.name as tabela "+
				" from sys.columns inner join sys.tables on sys.columns.object_id = sys.tables.object_id  inner join "+
				" information_schema.key_column_usage on  sys.tables.name= information_schema.key_column_usage.table_name "+
				" and  sys.tables.name<>'sysdiagrams' order by tabela";


		
		arrayLinhas = new JSONArray();

		ResultSet r = executarStatement(sql);
		ResultSetMetaData rsmd = r.getMetaData();

		 
		rsmd = r.getMetaData();

		while (r.next()) {
			nomeColunaValor  = new JSONObject();
			for(int i=1;i<=rsmd.getColumnCount();i++){
				if(r.getObject(i)==null){

					nomeColunaValor.put(rsmd.getColumnName(i),"");	
				}else{
					if(r.getObject(i).equals(true)){
						nomeColunaValor.put(rsmd.getColumnName(i),1);
					}else{
						if(r.getObject(i).equals(false)){
							nomeColunaValor.put(rsmd.getColumnName(i),0);
						}else{
							nomeColunaValor.put(rsmd.getColumnName(i),r.getObject(i));
						}
					}
				}
			}			
			arrayLinhas.put(nomeColunaValor);

		}


		return arrayLinhas;

	}
	
	public void atualizarRegistros(JSONArray registroServ, JSONArray registroLocal,  JSONArray chave, JSONArray tipo, String nomeTabela) throws JSONException, SQLException{

		
		String colunaChave = null;
		if(!m.containsKey(nomeTabela)){
			m.put(nomeTabela, nomeTabela);
			for(int l=0; l<chave.length();l++){
				if(chave.getJSONObject(l).getString("Tabela").equals(nomeTabela)){
					String[] c=chave.getJSONObject(l).getString("chave").split("_");
					if(c[0].equals("PK")){
						colunaChave=chave.getJSONObject(l).getString("Coluna");
					}
				}
			}
			
			//verifica se existe chave
			if(colunaChave!=null){
				for ( int j=0; j<registroServ.length();j++){
					int tempS, auxs = 0;
					//Log.i("bl", "------>>>-----"+linhasTabServidor.getJSONObject(j).names());
					//Retorna um array de nome dos campos da tabela
					JSONArray linhaTabServidor =registroServ.getJSONObject(j).names();
					tempS=(Integer) registroServ.getJSONObject(j).get(colunaChave);
					for ( int i=0; i<registroLocal.length();i++){
						JSONArray linhaTabLocal =registroLocal.getJSONObject(i).names();
						int tempL=(Integer) registroLocal.getJSONObject(i).get(colunaChave);
						if(tempL==tempS){
							auxs++;
							//atualize se for diferente
							for(int h=0;h<linhaTabLocal.length();h++){
								
								if(!(registroServ.getJSONObject(j).getString(linhaTabServidor.getString(h)).equals(
										registroLocal.getJSONObject(i).getString(linhaTabServidor.getString(h))))){
									atualizarLinha(linhaTabServidor,j,registroServ,colunaChave,tempS,nomeTabela);
								}
							}
							
						}
					}	
					if(auxs==0){
						
						// insere a linha
						insereLinha(linhaTabServidor,j,registroServ,colunaChave,tempS,nomeTabela);
					}				
					auxs=0;
					
								

				}
			}

		}
		//fecha banco

	}

	private void atualizarLinha(JSONArray linhaTabServidor, int j,	JSONArray registroServ, String colunaChave, int tempS, String nomeTabela) throws JSONException, SQLException {
		sql="";
		
		for(int k=0; k<linhaTabServidor.length();k++){		
				
				
				

				if(registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(String.class)){
					sql=sql+linhaTabServidor.getString(k)+"="+"'"+ registroServ.getJSONObject(j).get(linhaTabServidor.getString(k))+"'";
					sql=sql+",";
				}
				if(registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Integer.class)){
					sql=sql+linhaTabServidor.getString(k)+"="+ registroServ.getJSONObject(j).get(linhaTabServidor.getString(k));
					sql=sql+",";
				}
				if(registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Double.class)){
					sql=sql+linhaTabServidor.getString(k)+"="+ registroServ.getJSONObject(j).get(linhaTabServidor.getString(k));
					sql=sql+",";
				}

				if(registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Boolean.class)){
					
					sql=sql+linhaTabServidor.getString(k)+"="+ registroServ.getJSONObject(j).get(linhaTabServidor.getString(k));
					sql=sql+",";
				}
				
				
				
				
		}
		char sq[]=sql.toCharArray();
		String ss="";
		for(int i=0;i<sq.length-1;i++){

			ss=ss+sq[i];
		}
		
		System.out.println("update "+nomeTabela+" set "+ss+ " where "+colunaChave+"="+tempS);
		executarUpdate("update "+nomeTabela+" set "+ss+ " where "+colunaChave+"="+tempS);
		quantRegistro++;
		
	}

	private void insereLinha(JSONArray linhaTabServidor, int j, JSONArray registroServ, String colunaChave, int tempS, String nomeTabela) throws JSONException, SQLException {
		String c="",v="";
		for(int k=0; k<linhaTabServidor.length();k++){
			c=c+linhaTabServidor.getString(k);
			c=c+",";			
			if(registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(String.class)){
				v=v+"'"+registroServ.getJSONObject(j).get(linhaTabServidor.getString(k))+"'";
				v=v+",";
			}
			if(registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Integer.class)){
				v=v+registroServ.getJSONObject(j).get(linhaTabServidor.getString(k));
				v=v+",";
			}
			if(registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Double.class)){
				v=v+registroServ.getJSONObject(j).get(linhaTabServidor.getString(k));
				v=v+",";
			}
			if(registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Boolean.class)){				
				v=v+registroServ.getJSONObject(j).get(linhaTabServidor.getString(k));
				v=v+",";
			}			
		}
		char sq[]=v.toCharArray();
		String vv="";
		for(int i=0;i<sq.length-1;i++){
			vv=vv+sq[i];
		}
		char sr[]=c.toCharArray();
		String cc="";
		for(int i=0;i<sr.length-1;i++){

			cc=cc+sr[i];
		}		
		System.out.println("insert into "+nomeTabela+"( "+cc +" )  values( "+vv+ ")");
		executarUpdate("insert into "+nomeTabela+"( "+cc +" )  values( "+vv+ ")");
		quantRegistro++;
	}
	
	
	public int sicronizarBServidoComBLocal(JSONArray bancoAndroid, JSONArray bancoLocal) throws JSONException, SQLException {
		
		String tipo=null, chave = null;
		JSONObject tabelaTipo = null;
		JSONObject tabelaChave = null;
		JSONArray Tipo=null;
		JSONArray Chave = null;
		for (int i = 0; i < bancoAndroid.length(); i++) { 
			//Obitem o nome da tabela mais o array de registros
			JSONObject tabelaServidor=bancoAndroid.getJSONObject(i);

			//Obitem o nome da tabela 
			String nomeTabela=tabelaServidor.names().get(0).toString();
			if(nomeTabela.equals("Tipos")){
				tipo="Tipos";
				tabelaTipo=bancoAndroid.getJSONObject(i);
				Tipo=tabelaTipo.getJSONArray(tipo);
			}
			if(nomeTabela.equals("Chaves")){
				chave="Chaves";
				tabelaChave=bancoAndroid.getJSONObject(i);
				Chave=tabelaChave.getJSONArray(chave);
			}		

		}



		for (int i = 0; i < bancoAndroid.length(); i++) { 
			JSONArray linhasTabServidor=null;
			JSONArray linhasTabLocal=null;
			//Obitem o nome da tabela mais o array de registros do servidor
			JSONObject tabelaServidor=bancoAndroid.getJSONObject(i);
			//Nome das tabelas no banco serv
			String nomeTabelaServ=tabelaServidor.names().get(0).toString();



			for (int j = 0; j < bancoLocal.length(); j++) { 

				//Obitem o nome da tabela mais o array de registros do banco local
				JSONObject tabelaLocal=bancoLocal.getJSONObject(j);			
				//Log.i("bl", "------>>>-----"+nomeTabelaServ);
				//Nome das tabelas no banco local
				String nomeTabelaLocal=tabelaLocal.names().get(0).toString();
				//Log.i("bl", "------>>>-----"+nomeTabelaLocal);
				if(nomeTabelaLocal.equals(nomeTabelaServ)){	
					//Obitem os registros do banco serv
					linhasTabServidor=tabelaServidor.getJSONArray(nomeTabelaServ);
					//Obitem os registros do banco local
					linhasTabLocal=tabelaLocal.getJSONArray(nomeTabelaLocal);
					
					
					
					atualizarRegistros(linhasTabServidor, linhasTabLocal, Chave, Tipo, nomeTabelaServ);

				}




			}
		}

		
		return quantRegistro;
		

	}

}
