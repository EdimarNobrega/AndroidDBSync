package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class SyncAndroid {
    private static final int TIMEOUT_CONEXAO = 20000; // 20 segundos
    private static final int TIMEOUT_SOCKET = 30000; // 30 segundos
    private static final int TAM_MAX_BUFFER = 10240; // 10Kbytes
    private DB bd;
    private int quantRegistro;
    private Context contexro;
    private String nomeBanco;
    private Map<String, String> m;

    public SyncAndroid(Context ctx, String nomebanco) {
        super();
        contexro = ctx;
        nomeBanco = nomebanco;
        quantRegistro = 0;
        m = new HashMap<String, String>();
    }

    public void atualizarRegistros(JSONArray registroServ, JSONArray registroLocal, JSONArray chave, JSONArray tipo, String nomeTabela) throws JSONException {

        bd = new DB(contexro, nomeBanco);
        String colunaChave = null;
        Log.i("bl", "------>>>-llll----" + nomeTabela + "-" + registroLocal);
        Log.i("bl", "------>>>-llll----" + nomeTabela + "-" + registroServ);
        if (!m.containsKey(nomeTabela)) {
            m.put(nomeTabela, nomeTabela);
            Log.i("bl", "------>>>-ssss----" + registroServ);
            Log.i("bl", "------>>>-llll----" + registroLocal);
            //pega a coluna chave da tabela
            for (int l = 0; l < chave.length(); l++) {
                if (chave.getJSONObject(l).getString("Tabela").equals(nomeTabela)) {
                    String[] c = chave.getJSONObject(l).getString("chave").split("_");
                    if (c[0].equals("PK")) {
                        colunaChave = chave.getJSONObject(l).getString("Coluna");
                    }
                }
            }

            //verifica se existe chave
            if (colunaChave != null) {
                for (int j = 0; j < registroServ.length(); j++) {
                    int valorChave, auxs = 0;
                    //Log.i("bl", "------>>>-----"+linhasTabServidor.getJSONObject(j).names());
                    //Retorna um array de nome dos campos da tabela
                    JSONArray linhaTabServidor = registroServ.getJSONObject(j).names();
                    valorChave = (Integer) registroServ.getJSONObject(j).get(colunaChave);
                    for (int i = 0; i < registroLocal.length(); i++) {
                        JSONArray linhaTabLocal = registroLocal.getJSONObject(i).names();
                        int tempL = (Integer) registroLocal.getJSONObject(i).get(colunaChave);
                        if (tempL == valorChave) {
                            auxs++;
                            //atualize se for diferente
                            for (int h = 0; h < linhaTabLocal.length(); h++) {

                                if (!(registroServ.getJSONObject(j).getString(linhaTabServidor.getString(h)).equals(
                                        registroLocal.getJSONObject(i).getString(linhaTabServidor.getString(h))))) {
                                    atualizarLinha(linhaTabServidor, j, registroServ, colunaChave, valorChave, nomeTabela);
                                }
                            }

                        }
                    }
                    Log.i("bl", "------>>>-valorChave------" + valorChave + "--" + auxs);
                    if (auxs == 0) {

                        Log.i("bl", "------>>>-valorChave------" + valorChave + "--" + auxs);
                        // insere a linha
                        insereLinha(linhaTabServidor, j, registroServ, colunaChave, valorChave, nomeTabela);
                    }
                    auxs = 0;
                    Log.i("bl", "------>>>-valorChave------" + valorChave + "--" + auxs);


                }
            }

        }
        bd.fechar();


    }

    private void atualizarLinha(JSONArray linhaTabServidor, int j, JSONArray registroServ, String colunaChave, int valorChave, String nomeTabela) throws JSONException {
        ContentValues valores = new ContentValues();
        String s = colunaChave + "=" + valorChave;
        for (int k = 0; k < linhaTabServidor.length(); k++) {

            if (registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(String.class)) {
                Log.i("bl", "------>>>-cond----" + linhaTabServidor.getString(k));
                valores.put(linhaTabServidor.getString(k), registroServ.getJSONObject(j).getString(linhaTabServidor.getString(k)));

            }
            if (registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Integer.class)) {
                //Log.i("bl", "------>>>-----"+linhasTabServidor.getJSONObject(j).getInt(linhaTabServidor.getString(k)));
                valores.put(linhaTabServidor.getString(k), registroServ.getJSONObject(j).getInt(linhaTabServidor.getString(k)));


            }
            if (registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Double.class)) {
                valores.put(linhaTabServidor.getString(k), registroServ.getJSONObject(j).getDouble(linhaTabServidor.getString(k)));
                //Log.i("bl", "------>"+nomeTabelaServ+">>-----"+linhasTabServidor.getJSONObject(j).getString(linhaTabServidor.getString(k)));

            }

            if (registroServ.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Boolean.class)) {
                Log.i("bl", "------>>>---bbbins--" + registroServ.getJSONObject(j).getInt(linhaTabServidor.getString(k)));
                valores.put(linhaTabServidor.getString(k), registroServ.getJSONObject(j).getInt(linhaTabServidor.getString(k)));

            }

        }
        Log.i("bl", "------>>>Regis----" + nomeTabela + "--" + valores + "--" + s);
        bd.executarAtualizar(nomeTabela, valores, s, null);
        quantRegistro++;

    }

    private void insereLinha(JSONArray linhaTabServidor, int posicaoLinha, JSONArray registroServ, String colunaChave, int valorChave, String nomeTabela) throws JSONException {
        ContentValues valores = new ContentValues();
        String s = colunaChave + "=" + valorChave;
        for (int k = 0; k < linhaTabServidor.length(); k++) {

            if (registroServ.getJSONObject(posicaoLinha).get(linhaTabServidor.getString(k)).getClass().equals(String.class)) {
                Log.i("bl", "------>>>-cond----" + linhaTabServidor.getString(k));
                valores.put(linhaTabServidor.getString(k), registroServ.getJSONObject(posicaoLinha).getString(linhaTabServidor.getString(k)));

            }
            if (registroServ.getJSONObject(posicaoLinha).get(linhaTabServidor.getString(k)).getClass().equals(Integer.class)) {
                //Log.i("bl", "------>>>-----"+linhasTabServidor.getJSONObject(j).getInt(linhaTabServidor.getString(k)));
                valores.put(linhaTabServidor.getString(k), registroServ.getJSONObject(posicaoLinha).getInt(linhaTabServidor.getString(k)));


            }
            if (registroServ.getJSONObject(posicaoLinha).get(linhaTabServidor.getString(k)).getClass().equals(Double.class)) {
                valores.put(linhaTabServidor.getString(k), registroServ.getJSONObject(posicaoLinha).getDouble(linhaTabServidor.getString(k)));
                //Log.i("bl", "------>"+nomeTabelaServ+">>-----"+linhasTabServidor.getJSONObject(j).getString(linhaTabServidor.getString(k)));

            }

            if (registroServ.getJSONObject(posicaoLinha).get(linhaTabServidor.getString(k)).getClass().equals(Boolean.class)) {
                Log.i("bl", "------>>>---bbbins--" + registroServ.getJSONObject(posicaoLinha).getInt(linhaTabServidor.getString(k)));
                valores.put(linhaTabServidor.getString(k), registroServ.getJSONObject(posicaoLinha).getInt(linhaTabServidor.getString(k)));

            }

        }
        Log.i("bl", "------>>>Regis----" + nomeTabela + "--" + valores + "--" + s);
        bd.executarInsert(nomeTabela, valores);
        quantRegistro++;
    }

    public int sicronizarBServidoComBLocal(JSONArray bancoServidor, JSONArray bancoLocal) throws JSONException {
        Log.i("bl", "------>>>--bbbbs---" + bancoServidor);
        Log.i("bl", "------>>>--bbbbl---" + bancoLocal);
        String tipo = null, chave = null;
        JSONObject tabelaTipo = null;
        JSONObject tabelaChave = null;
        JSONArray Tipo = null;
        JSONArray Chave = null;
        for (int i = 0; i < bancoServidor.length(); i++) {
            //Obitem o nome da tabela mais o array de registros
            JSONObject tabelaServidor = bancoServidor.getJSONObject(i);

            //Obitem o nome da tabela
            String nomeTabela = tabelaServidor.names().get(0).toString();
            if (nomeTabela.equals("Tipos")) {
                tipo = "Tipos";
                tabelaTipo = bancoServidor.getJSONObject(i);
                Tipo = tabelaTipo.getJSONArray(tipo);
            }
            if (nomeTabela.equals("Chaves")) {
                chave = "Chaves";
                tabelaChave = bancoServidor.getJSONObject(i);
                Chave = tabelaChave.getJSONArray(chave);
            }

        }


        for (int i = 0; i < bancoServidor.length(); i++) {
            JSONArray linhasTabServidor = null;
            JSONArray linhasTabLocal = null;
            //Obitem o nome da tabela mais o array de registros do servidor
            JSONObject tabelaServidor = bancoServidor.getJSONObject(i);
            //Nome das tabelas no banco serv
            String nomeTabelaServ = tabelaServidor.names().get(0).toString();


            for (int j = 0; j < bancoLocal.length(); j++) {

                //Obitem o nome da tabela mais o array de registros do banco local
                JSONObject tabelaLocal = bancoLocal.getJSONObject(j);
                //Log.i("bl", "------>>>-----"+nomeTabelaServ);
                //Nome das tabelas no banco local
                String nomeTabelaLocal = tabelaLocal.names().get(0).toString();
                //Log.i("bl", "------>>>-----"+nomeTabelaLocal);
                if (nomeTabelaLocal.equals(nomeTabelaServ)) {
                    Log.i("bl", "------>>>--nttt---" + nomeTabelaServ + "--" + nomeTabelaLocal);
                    //Obitem os registros do banco serv
                    linhasTabServidor = tabelaServidor.getJSONArray(nomeTabelaServ);
                    //Obitem os registros do banco local
                    linhasTabLocal = tabelaLocal.getJSONArray(nomeTabelaLocal);

                    Log.i("bl", "------>>>--nttts---" + linhasTabServidor);
                    Log.i("bl", "------>>>--ntttl---" + linhasTabLocal);

                    atualizarRegistros(linhasTabServidor, linhasTabLocal, Chave, Tipo, nomeTabelaServ);

                }


                //			Log.i("bl", "------>>>-----"+linhasTabLocal);
                //			Log.i("bl", "------>>>-----"+nomeTabelaLocal);
                // Sync data implemntar
                //if(linhasTabServidor.length()>linhasTabLocal.length()){
                //	inserirRegistro	(linhasTabServidor, linhasTabLocal, nomeTabelaServ);
                //}


            }

            //Log.i("bl", "------>>>qr-----"+quantRegistro);
        }


        return quantRegistro;

    }

    //Inseri o bando do servido no banco local
    public int inserirBServidorNoBLocal(JSONArray bancoServidor) throws JSONException {
        bd = new DB(contexro, nomeBanco);
        String tipo = null, chave = null;
        JSONObject tabelaTipo = null;
        JSONObject tabelaChave = null;

        for (int i = 0; i < bancoServidor.length(); i++) {
            //Obitem o nome da tabela mais o array de registros
            JSONObject tabelaServidor = bancoServidor.getJSONObject(i);
            //Obitem o nome da tabela
            String nomeTabela = tabelaServidor.names().get(0).toString();
            if (nomeTabela.equals("Tipos")) {
                tipo = "Tipos";
                tabelaTipo = bancoServidor.getJSONObject(i);
            }
            if (nomeTabela.equals("Chaves")) {
                chave = "Chaves";
                tabelaChave = bancoServidor.getJSONObject(i);
            }
        }

        for (int i = 0; i < bancoServidor.length(); i++) {

            //Obitem o nome da tabela mais o array de registros
            JSONObject tabelaServidor = bancoServidor.getJSONObject(i);
            //Obitem o nome da tabela
            String nomeTabela = tabelaServidor.names().get(0).toString();
            //Obitem  o array de registros
            JSONArray registros = tabelaServidor.getJSONArray(nomeTabela);
            //retorna a tabela tipo
            JSONArray Tipo = tabelaTipo.getJSONArray(tipo);
            //retorna a tabela tipo
            JSONArray Chave = tabelaChave.getJSONArray(chave);

            inserirTabela(nomeTabela, registros, Tipo, Chave);
        }
        bd.setVersao(1);
        bd.fechar();
        return quantRegistro;


    }

    private void inserirTabela(String nomeTabela, JSONArray registros, JSONArray tipo, JSONArray chave) throws JSONException {
        if (nomeTabela.equals("Chaves")) {
            bd.executarInsertSql(" create table Chaves (chave string, Tabela string, Coluna string );");

        }
        if (nomeTabela.equals("Tipos")) {

            bd.executarInsertSql(" create table Tipos (coluna string, tipo int, tabela string );");
        }
        Log.i("bl", "------>>>-true----" + m.containsKey(nomeTabela) + "--tb--" + nomeTabela);
        if (!m.containsKey(nomeTabela)) {
            m.put(nomeTabela, nomeTabela);
            for (int j = 0; j < registros.length(); j++) {
                String sql = "";
                ContentValues valores = new ContentValues();
                //Log.i("bl", "------>>>-----"+linhasTabServidor.getJSONObject(j).names());
                //Retorna um array de nome dos campos da tabela
                JSONArray linhaTabServidor = registros.getJSONObject(j).names();

                for (int k = 0; k < linhaTabServidor.length(); k++) {

                    //Log.i("bl", "------>>>-----"+linhaTabServidor.getString(k));

                    //Log.i("bl", "------>>>-----"+linhasTabServidor.getJSONObject(j).getString("Nome_string"));
                    if (registros.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(String.class)) {
                        Log.i("bl", "------>>>-cond----" + linhaTabServidor.getString(k));
                        valores.put(linhaTabServidor.getString(k), registros.getJSONObject(j).getString(linhaTabServidor.getString(k)));

                    }
                    if (registros.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Integer.class)) {
                        //Log.i("bl", "------>>>-----"+linhasTabServidor.getJSONObject(j).getInt(linhaTabServidor.getString(k)));
                        valores.put(linhaTabServidor.getString(k), registros.getJSONObject(j).getInt(linhaTabServidor.getString(k)));


                    }
                    if (registros.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Double.class)) {
                        valores.put(linhaTabServidor.getString(k), registros.getJSONObject(j).getDouble(linhaTabServidor.getString(k)));
                        //Log.i("bl", "------>"+nomeTabelaServ+">>-----"+linhasTabServidor.getJSONObject(j).getString(linhaTabServidor.getString(k)));

                    }

                    if (registros.getJSONObject(j).get(linhaTabServidor.getString(k)).getClass().equals(Boolean.class)) {
                        Log.i("bl", "------>>>---bbbins--" + registros.getJSONObject(j).getInt(linhaTabServidor.getString(k)));
                        valores.put(linhaTabServidor.getString(k), registros.getJSONObject(j).getInt(linhaTabServidor.getString(k)));

                    }


                }


                if (j == 0) {
                    if (!(nomeTabela.equals("Chaves") || nomeTabela.equals("Tipos"))) {
                        for (int l = 0; l < tipo.length(); l++) {

                            //Log.i("bl", "------>>>22221-eeeeee----"+tipo.getJSONObject(l).getString("tipo"));


                            if (tipo.getJSONObject(l).getString("tabela").equals(nomeTabela)) {

                                if (tipo.getJSONObject(l).getString("tipo").equals("56")) {
                                    sql = sql + " " + tipo.getJSONObject(l).getString("coluna") + " int,";

                                }
                                if (tipo.getJSONObject(l).getString("tipo").equals("167")) {
                                    sql = sql + " " + tipo.getJSONObject(l).getString("coluna") + " string,";

                                }
                                if (tipo.getJSONObject(l).getString("tipo").equals("62")) {
                                    sql = sql + " " + tipo.getJSONObject(l).getString("coluna") + " float,";

                                }
                                if (tipo.getJSONObject(l).getString("tipo").equals("61")) {
                                    sql = sql + " " + tipo.getJSONObject(l).getString("coluna") + " datetime,";

                                }
                                if (tipo.getJSONObject(l).getString("tipo").equals("104")) {
                                    sql = sql + " " + tipo.getJSONObject(l).getString("coluna") + " boolean,";

                                }

                            }


                        }

                    }
                }
                if (j == 0) {
                    if (!(nomeTabela.equals("Chaves") || nomeTabela.equals("Tipos"))) {
                        for (int l = 0; l < chave.length(); l++) {

                            //Log.i("bl", "------>>>22221-eeeeee----"+tipo.getJSONObject(l).getString("tipo"));


                            if (chave.getJSONObject(l).getString("Tabela").equals(nomeTabela)) {
                                String[] c = chave.getJSONObject(l).getString("chave").split("_");
                                if (c[0].equals("PK")) {

                                    sql = sql + "PRIMARY KEY (" + chave.getJSONObject(l).getString("Coluna") + "),";
                                    //Log.i("bl", "------>>>2222-cpeeeee----"+sql);

                                }
                                if (c[0].equals("FK")) {
                                    //foreign key (CodProprietario) REFERENCES Proprietario(CodProprietario),
                                    sql = sql + "foreign key (" + chave.getJSONObject(l).getString("Coluna") + ")" +
                                            " REFERENCES " + nomeTabela + "(" + chave.getJSONObject(l).getString("Coluna") + "),";
                                    //Log.i("bl", "------>>>2222-cpeeeee----"+sql);

                                }

                            }


                        }

                        char s[] = sql.toCharArray();
                        String ss = "";
                        for (int i = 0; i < s.length - 1; i++) {

                            ss = ss + s[i];
                        }

                        if (!ss.equals("")) {
                            Log.i("bl", "------>>>2222-eeeeee----" + ss);
                        }

                        bd.executarInsertSql("create table " + nomeTabela + " (" + ss + " );");
                    }

                }

                quantRegistro++;

                Log.i("bl", "------>>>--vvvv---" + valores.toString());
                //insere os registro na tabela.
                bd.executarInsert(nomeTabela, valores);

                Log.i("bl", "------>>>13-----" + valores + "--->>" + quantRegistro);
            }
        }

    }

    // Monta o banco local em um json

    //Metodo que passa o nome da tabela e retorna um array com todas as linhas da tabela


    public String getBancoServidor(String url) throws JSONException {
        String bancoServidor = "";

        try {
            HttpParams httpParameters = new BasicHttpParams();
            // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida
            HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONEXAO);
            // Configura o timeout do socket em milisegundos do tempo que seria utilizado para aguardar os dados
            HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
            Log.i("bl", "------>>>--sicronização---");
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> name = new ArrayList<NameValuePair>();
            name.add(new BasicNameValuePair("banco", "Edimar"));
            httppost.setHeader("Accept", "application/json");
            httppost.setEntity(new UrlEncodedFormEntity(name));
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                    "ISO-8859-1"), TAM_MAX_BUFFER);
            StringBuilder builder = new StringBuilder();

            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            bancoServidor = builder.toString();


        } catch (ClientProtocolException e) {
            Log.e("WebService", e.toString());
        } catch (IOException e) {
            Log.e("WebService", e.toString());
        }
        Log.i("bl", "Retorna o banco do servidor---->>>");
        return bancoServidor;

    }

    public String enviaBancoServidor(JSONArray bancoLocal, String url) throws JSONException {
        String bancoServidor = "";

        try {
            HttpParams httpParameters = new BasicHttpParams();
            // Configura o timeout da conexão em milisegundos até que a conexão
            // seja estabelecida
            HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONEXAO);

            // Configura o timeout do socket em milisegundos do tempo
            // que seria utilizado para aguardar os dados
            HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> name = new ArrayList<NameValuePair>();
            name.add(new BasicNameValuePair("banco", bancoLocal.toString()));
            name.add(new BasicNameValuePair("Nomes", "Edimarrsssssr"));
            httppost.setHeader("Accept", "application/json");
            httppost.setEntity(new UrlEncodedFormEntity(name));
            HttpResponse response = httpclient.execute(httppost);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                    "ISO-8859-1"), TAM_MAX_BUFFER);

            StringBuilder builder = new StringBuilder();

            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }

            bancoServidor = builder.toString();


        } catch (ClientProtocolException e) {
            Log.e("WebService", e.toString());
        } catch (IOException e) {
            Log.e("WebService", e.toString());
        }
        Log.i("bl", "Retorna o banco do servidor---->>>");
        return bancoServidor;
    }
}
