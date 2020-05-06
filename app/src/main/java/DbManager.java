import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.instanote.DatabaseOperations;

import java.sql.SQLException;

public class DbManager {
    private DatabaseOperations dbOperations;
    private Context context;
    private SQLiteDatabase database;
    public DbManager(Context c){
        context=c;
    }
    public DbManager open() throws SQLException{
        //dbOperations = new DatabaseOperations(context);
        database = dbOperations.getWritableDatabase();
        return this;
    }
    public void close(){
        dbOperations.close();
    }
 }
