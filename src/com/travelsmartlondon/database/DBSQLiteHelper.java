package com.travelsmartlondon.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSQLiteHelper extends SQLiteOpenHelper{
	
    private static String DB_PATH = "/data/data/com.travelsmartlondon/databases/";
 
    private static String DB_NAME = "tslDB";
 
    private SQLiteDatabase tslDatabase; 
	
	private final Context myContext;

	public DBSQLiteHelper(Context context){
		super(context,DB_NAME,null, 1);
		this.myContext = context;
	}
	
	public void createDataBase() throws IOException{
		boolean dbExist = checkDataBase();
		
		if(!dbExist)
		{
			//This method creates an empty database, which we will over-write with our own database
			this.getReadableDatabase();
			
			try{
				copyDataBase();
			}
			catch(IOException e){
				throw new Error("Error occured copying database");
			}
		}

	}
	
	//Check if the database already exists or not to avoid copying the database everytime the application opens.
	private boolean checkDataBase() {
		
		SQLiteDatabase checkDB = null;
		
		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch(SQLiteException e){
			// Database doesn't exist yet.
		}
		
		if(checkDB != null){
			checkDB.close();
			return true;
		}
		
		return false;
	}

	private void copyDataBase() throws IOException{
		
		//Open the existing database to be copied as an input stream
		InputStream inputDB = myContext.getAssets().open(DB_NAME);
		
		//Path to the just created empty DB
		String EmptyDBPath = DB_PATH + DB_NAME;
		
		//Open the empty DB as an output stream
		OutputStream outputDB = new FileOutputStream(EmptyDBPath);
		
		//Transfer bytes from the input file to the output file
		byte[] buffer = new byte[1024];
		int length;
		
		while((length = inputDB.read(buffer))>0 ){
			outputDB.write(buffer,0,length);
		}
		
		//close streams
		outputDB.flush();
		outputDB.close();
		inputDB.close();
		
	}


	@Override
	public void onCreate(SQLiteDatabase arg0) {
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
		
	}
}
