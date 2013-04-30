package modelo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DB {
    protected SQLiteDatabase db;
    private Context context;
    private String nomeBanco;

    public DB(Context ctx, String nomebanco) {
        super();
        nomeBanco = nomebanco;
        context = ctx;
        db = context.openOrCreateDatabase(nomeBanco, Context.MODE_PRIVATE, null);
    }

    protected DB() {
        // Apenas para criar uma subclasse...
    }

    public int getVercao() {

        return db.getVersion();

    }

    public void setVersao(int v) {

        db.setVersion(v);

    }

    public int executarAtualizar(String tabelaName, ContentValues contentValues, String whereClause, String[] whereArgs) {
        Log.i("bl", "------>>>-;-BANCO---" + tabelaName + ">>><<<<<" + contentValues);
        //retorna quantas linhas foram afetadas


        return db.update(tabelaName, contentValues, whereClause, whereArgs);
    }

    public int executarDelete(String sql) {
        db.execSQL(sql);
        return 1;
    }


    public Cursor executarSelect(String nomeTabela, String[] colunas, String where, String orderBy) {
        return db.query(nomeTabela, colunas, where, null, null, null, orderBy);
    }

    public long executarInsert(String tabela, ContentValues valores) {
        //Log.i("bl", "------>>>-;-Inseriu---"+tabela+">>><<<<<"+valores.toString());
        return db.insert(tabela, null, valores);
    }

    public int executarInsertSql(String sql) {
        db.execSQL(sql);

        return 1;
    }

    public void fechar() {
        db.close();
        Log.i("bl", "------>>>-;-Inseriu---" + db.getSyncedTables());


    }


}
