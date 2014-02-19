package com.azilen.waiterpad.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static String DB_PATH = null;
	private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "WaiterPadDb";

	public static final String ID = "_id";

	// Related to the category master
	public static final String CATEGORY_TABLE = "CategoryMaster";

	// column names
	public static final String CATEGORY_PARENT_ID = "parent_id";
	public static final String CATEGORY_ID = "category_id";
	public static final String CATEGORY_NAME = "category_name";
	public static final String CATEGORY_NAME_LOWER = "category_name_lower";

	// Related to the section item mapping table
	public static final String SECTION_ITEM_TABLE = "SectionItemMaster";

	// column names (will be reused)
	public static final String ITEM_ID = "item_id";
	public static final String SECTION_ID = "section_id";

	// Related to the item master table
	public static final String ITEM_TABLE = "ItemMaster";

	// column names
	// include the item id and the category id
	public static final String ITEM_CODE = "item_code";
	public static final String ITEM_PRICE = "item_price";
	public static final String ITEM_IS_ACTIVATED = "isactivated";
	public static final String ITEM_IS_RESTRICTED = "isrestricted";
	public static final String ITEM_DESC = "item_desc";
	public static final String ITEM_FULLNAME = "full_name";
	public static final String ITEM_NAME = "item_name";
	public static final String ITEM_NAME_LOWER = "item_name_lower";
	public static final String HAVE_MODIFIERS = "have_modifiers";

	// Related to the Modifiers table
	public static final String MODIFIERS_TABLE = "ModifiersMaster";

	// Related to the columns
	public static final String MODIFIER_ID = "modifier_id";
	public static final String MODIFIER_NAME = "modifier_name";
	public static final String MODIFIER_PRICE = "modifier_price";
	public static final String MODIFIER_DESC = "modifier_desc";
	public static final String MODIFIER_IS_ACTIVE = "modifier_isactive";
	public static final String MODIFIER_QTY = "modifier_qty";
	public static final String GROUP_ID = "group_id";

	// Related to the item modifiers mapping table
	// columns the same as the modifiers table and an addition itemid, and group
	// id
	public static final String ITEM_MODIFIERS_MAPPING = "ItemModifierMapping";
	public static final String DEFAULT_AMOUNT = "default_amount";
	public static final String MAX_AMOUNT = "max_amount";
	public static final String MIN_AMOUNT = "min_amount";

	// related to group modifiers master table
	// and columns related to modifiers master, group id
	public static final String GROUP_MODIFIER_MASTER = "GroupModifiersMaster";
	public static final String GROUP_NAME = "group_name";

	private static final int DATABASE_VERSION = 1;

	// likhit
	// private final DatabaseOpenHelper mDatabaseOpenHelper;

	private static DBHelper dbHelper;
	private static SQLiteDatabase sqLiteDatabase;

	private Context mContext;

	/**
	 * @return the sqLiteDatabase
	 */
	public SQLiteDatabase getSqLiteDatabase() {
		return sqLiteDatabase;
	}

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
		DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";

		try {
			createdatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * private void establishDb() { try { createdatabase(); } catch (IOException
	 * e) { e.printStackTrace(); } }
	 */

	public static DBHelper getInstance(Context context) {
		if (dbHelper == null) {
			dbHelper = new DBHelper(context);
		}

		return dbHelper;

	}

	public void createdatabase() throws IOException {
		boolean dbexist = checkdatabase();
		if (dbexist) {
			openDataBase();
			close();
			// System.out.println(" Database exists.");
		} else {
			this.getReadableDatabase();
			try {
				copyDataBase();
				// likhit
				// mDatabaseOpenHelper.getReadableDatabase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	private boolean checkdatabase() {
		// SQLiteDatabase checkdb = null;
		boolean checkdb = false;
		try {
			File dbfile = new File(DB_PATH + DATABASE_NAME);
			// checkdb =
			// SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
			checkdb = dbfile.exists();
		} catch (SQLiteException e) {
			System.out.println("Database doesn't exist");
		}
		return checkdb;
	}

	@Override
	public synchronized void close() {
		if (sqLiteDatabase != null)
			sqLiteDatabase.close();
		super.close();
	}

	public SQLiteDatabase openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DATABASE_NAME;
		if(sqLiteDatabase== null || 
				(sqLiteDatabase != null && !sqLiteDatabase.isOpen())) {
			sqLiteDatabase = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.CREATE_IF_NECESSARY);
		}
		return sqLiteDatabase;
	}
	
	

	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = mContext.getAssets().open(DATABASE_NAME);
		// Path to the just created empty db
		String outFileName = DB_PATH + DATABASE_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// likhit
	/*
	 * public SQLiteDatabase getDatabase() { return
	 * mDatabaseOpenHelper.getReadableDatabase(); }
	 */

	// likhit
	/*
	 * private static class DatabaseOpenHelper extends SQLiteOpenHelper {
	 * private SQLiteDatabase mDatabase;
	 * 
	 * 
	 * //likhit private static String CREATE_SECTION_MASTER; private static
	 * String CREATE_ITEM_MASTER; private static String CREATE_CATEGORY_MASTER;
	 * private static String CREATE_ITEM_MODIFIERS_TABLE; private static String
	 * CREATE_SECTION_ITEM_TABLE;
	 * 
	 * public DatabaseOpenHelper(Context context) { super(context,
	 * DATABASE_NAME, null, DATABASE_VERSION); }
	 * 
	 * @Override public void onCreate(SQLiteDatabase db) { mDatabase = db;
	 * mDatabase.execSQL(CREATE_SECTION_MASTER); }
	 * 
	 * @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int
	 * newVersion) { db.execSQL("DROP TABLE IF EXISTS " + SECTION_ITEM_TABLE);
	 * onCreate(db); }
	 * 
	 * @Override public SQLiteDatabase getReadableDatabase() {
	 * DBHelper.sqLiteDatabase = SQLiteDatabase.openDatabase(DBHelper.DB_PATH +
	 * DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY); return
	 * DBHelper.sqLiteDatabase; }
	 * 
	 * @Override public SQLiteDatabase getWritableDatabase() {
	 * DBHelper.sqLiteDatabase = SQLiteDatabase.openDatabase(DBHelper.DB_PATH +
	 * DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE); return
	 * DBHelper.sqLiteDatabase; } }
	 */
}
