package modelo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class BancoLocal {
    private DB bd;
    private Context contexro;
    private String nomeBanco;


    public BancoLocal(Context ctx, String nomebanco) {
        super();
        contexro = ctx;
        nomeBanco = nomebanco;

    }


    public JSONArray getBancoLocal() throws JSONException {
        bd = new DB(contexro, nomeBanco);
        JSONArray bancoLocal = new JSONArray();

        Log.i("bl", "------>>>-Versão----" + bd.getVercao());
        if (!(bd.getVercao() == 0)) {

            String[] colunas = {"name"};
            //Retorna um cursor com os campos da tabela SyncDataBaseItens de nome Tabela e Comando
            //nome da tabela  |	Campos	|	condição where
            Cursor c = bd.executarSelect("sqlite_master", colunas, "type <>'index' and name<>'android_metadata' ", " rootpage");
            //Verifica se o banco estilo vazio
            if (c.moveToNext() == false) {
                Log.i("bl", "Banco local Vazio---->>>");
            } else {
                //Monta o BL
                c.moveToPrevious();
                while (c.moveToNext()) {
                    JSONObject tabela = new JSONObject();
                    String nomeTabela = "";
                    for (int i = 0; i < c.getColumnCount(); i++) {
                        if (c.getColumnName(i).equals("name")) {

                            //Log.i("bl", "------>>>-----"+c.getString(i));
                            nomeTabela = c.getString(i);
                        }


                    }
                    Log.i("bl", "------>>>--cccc---" + nomeTabela);
                    // le os registros de uma tabela em um array
                    tabela.put(nomeTabela, getArrayLinhasJson(nomeTabela, bd)); // monta um objeto com nome da tabela:conteudo
                    Log.i("bl", "------>>>-cccc1----" + tabela.toString());
                    bancoLocal.put(tabela); // adiciona tabela no banco (array de tabela)

                }

                c.close();
            }


        } else {

        }
        //Retorna o BL
        bd.fechar();
        Log.i("bl", "------>>>-banco local----" + bancoLocal);
        return bancoLocal;

    }

    public JSONArray getArrayLinhasJson(String nomeTabela, DB bd) throws JSONException {

        JSONArray arrayLinhas = new JSONArray();
        JSONObject linhasSecundaria;
        // faz um select na tabela
        //Log.i("bl", "------>>>-----"+nomeTabela+"---"+condicao);

        //JSONArray Tipo=tabelaTipo.getJSONArray("Tipos");

        if (nomeTabela.equals("Chaves") || nomeTabela.equals("Tipos")) {
            Cursor cursorCT = bd.executarSelect(nomeTabela, null, null, null);
            if (nomeTabela.equals("Chaves")) {
                while (cursorCT.moveToNext()) {
                    linhasSecundaria = new JSONObject();

                    linhasSecundaria.put(cursorCT.getColumnName(0), cursorCT.getString(0));
                    linhasSecundaria.put(cursorCT.getColumnName(1), cursorCT.getString(1));
                    linhasSecundaria.put(cursorCT.getColumnName(2), cursorCT.getString(2));
                    arrayLinhas.put(linhasSecundaria);
                }
                cursorCT.close();
            } else {
                while (cursorCT.moveToNext()) {
                    linhasSecundaria = new JSONObject();

                    linhasSecundaria.put(cursorCT.getColumnName(0), cursorCT.getString(0));
                    linhasSecundaria.put(cursorCT.getColumnName(1), cursorCT.getInt(1));
                    linhasSecundaria.put(cursorCT.getColumnName(2), cursorCT.getString(2));
                    arrayLinhas.put(linhasSecundaria);
                }
                cursorCT.close();

            }


        } else {
            String[] colunas = {"tipo", "coluna"};
            Cursor cursorTabela = bd.executarSelect(nomeTabela, null, null, null);
            Cursor tipo = bd.executarSelect("Tipos", colunas, "tabela='" + nomeTabela + "'", null);

            while (cursorTabela.moveToNext()) {
                linhasSecundaria = new JSONObject();

                for (int j = 0; j < cursorTabela.getColumnCount(); j++) {
                    //Log.i("bl", "------>>>-----"+cursorTabela.getColumnName(j));

                    while (tipo.moveToNext()) {
                        Log.i("bl", "------>>>---cttt--" + cursorTabela.getColumnName(j) + "--ct--" + tipo.getInt(0));
                        if (cursorTabela.getColumnName(j).equals(tipo.getString(1))) {
                            if (tipo.getInt(0) == 167) {

                                Log.i("bl", "------>>>-----" + cursorTabela.getColumnName(j) + "--cctt--" + cursorTabela.getString(j));
                                linhasSecundaria.put(cursorTabela.getColumnName(j), cursorTabela.getString(j));
                            }
                            if (tipo.getInt(0) == 56) {
                                Log.i("bl", "------>>>-----" + cursorTabela.getColumnName(j) + "--cctt--" + cursorTabela.getString(j));
                                //Log.i("bl", "------>>>-----"+cursorTabela.getInt(j));
                                linhasSecundaria.put(cursorTabela.getColumnName(j), cursorTabela.getInt(j));
                            }
                            if (tipo.getInt(0) == 61) {
                                Log.i("bl", "------>>>-----" + cursorTabela.getColumnName(j) + "--cctt--" + cursorTabela.getString(j));
                                //Log.i("bl", "------>>>-----"+cursorTabela.getString(j));
                                linhasSecundaria.put(cursorTabela.getColumnName(j), cursorTabela.getString(j));
                            }
                            if (tipo.getInt(0) == 62) {
                                Log.i("bl", "------>>>-----" + cursorTabela.getColumnName(j) + "--cctt--" + cursorTabela.getString(j));
                                //Log.i("bl", "------>>>-----"+cursorTabela.getLong(j));
                                linhasSecundaria.put(cursorTabela.getColumnName(j), cursorTabela.getDouble(j));
                            }
                            if (tipo.getInt(0) == 104) {
                                Log.i("bl", "------>>>-----" + cursorTabela.getColumnName(j) + "--cctt--" + cursorTabela.getString(j));
                                //Log.i("bl", "------>>><<<iii-----"+cursorTabela.getInt(j)+"----");
                                linhasSecundaria.put(cursorTabela.getColumnName(j), cursorTabela.getInt(j));
                            }
                        }
                    }
                    tipo.close();
                    tipo = bd.executarSelect("Tipos", colunas, "tabela='" + nomeTabela + "'", null);
                }

                arrayLinhas.put(linhasSecundaria);

            }
            cursorTabela.close();
            tipo.close();
        }
        //Log.i("bl", "------>>>-----"+arrayLinhas);
        return arrayLinhas;
    }

}
