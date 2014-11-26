package com.example.a09_blauzahn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.a09_blauzahn.model.Session;
import com.example.a09_blauzahn.model.Sighting;

public class DBHelper
extends SQLiteOpenHelper {

	private static final String DB_NAME = "blauzahn";
	private static final int DB_VERSION = 2;

	protected static final String TAB_SESSION = "session";
	protected static final String KEY_SESSION_ID = "id";
	protected static final String KEY_SESSION_START = "start";
	protected static final String KEY_SESSION_STOP = "stop";

	protected static final String TAB_SIGHTING = "sighting";
	protected static final String KEY_SIGHTING_ID = "id";
	protected static final String KEY_SIGHTING_SESSION_ID = "sessionId";
	protected static final String KEY_SIGHTING_TIME = "time";
	protected static final String KEY_SIGHTING_ADDRESS = "address";
	protected static final String KEY_SIGHTING_NAME = "name";
	protected static final String KEY_SIGHTING_RSSI = "rssi";

	private Context context;
	private SQLiteDatabase db;
	private ContentValues vals = new ContentValues(); // Behälter zum Datenschreiben

	/** Constructor. */
	public DBHelper(
		Context context, String name,
		CursorFactory factory, int version
	) {
		super(context, name, factory, version);
		this.context = context;
	}

	/** Constructor. */
	public DBHelper(Context context) {
		this(context,DB_NAME,null,DB_VERSION);
	}

	public void close() {
		if (this.db != null) {
			this.db.close();
		}
		super.close();
	}

	private void toast(String text) {
		Toast.makeText(
			context,
			text,
			Toast.LENGTH_SHORT
		).show();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		toast("DBHelper.onCreate");
		db.execSQL(
			new StringBuffer()
			.append("CREATE TABLE ")
			.append(TAB_SESSION)
			.append(" (\n")
			.append(KEY_SESSION_ID)    .append(" INTEGER PRIMARY KEY AUTOINCREMENT,\n")
			.append(KEY_SESSION_START) .append(" INTEGER,\n")
			.append(KEY_SESSION_STOP)  .append(" INTEGER\n")
			.append(")")
			.toString()
		);
		db.execSQL(
			new StringBuffer()
			.append("CREATE TABLE ")
			.append(TAB_SIGHTING)
			.append(" (\n")
			.append(KEY_SIGHTING_ID)        .append(" INTEGER PRIMARY KEY AUTOINCREMENT,\n")
			.append(KEY_SIGHTING_SESSION_ID).append(" INTEGER,\n")
			.append(KEY_SIGHTING_TIME)      .append(" INTEGER,\n")
			.append(KEY_SIGHTING_ADDRESS)   .append(" TEXT,\n")
			.append(KEY_SIGHTING_NAME)      .append(" TEXT,\n")
			.append(KEY_SIGHTING_RSSI)      .append(" INTEGER\n")
			.append(")")
			.toString()
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		toast("DBHelper.onUpdate");
		if (oldVersion == 1 && newVersion == 2)
		db.execSQL(
			new StringBuffer()
			.append("ALTER TABLE ")
			.append(TAB_SIGHTING)
			.append(" ADD COLUMN ")
			.append(KEY_SIGHTING_SESSION_ID)
			.append(" INTEGER")
			.toString()
		);
		db.execSQL(
			new StringBuffer()
			.append("ALTER TABLE ")
			.append(TAB_SIGHTING)
			.append(" ADD COLUMN ")
			.append(KEY_SIGHTING_TIME)
			.append(" INTEGER")
			.toString()
		);
	}

	private void init() {
		if (db == null) {
			db = this.getWritableDatabase();
			vals.clear();
		}
	}

	protected int getMaxSessionId() {
		init();
		int result = -1;
		try {
			Cursor c = db.rawQuery(
				new StringBuffer()
				.append("SELECT max(\"")
				.append(KEY_SESSION_ID)
				.append("\") FROM \"")
				.append(TAB_SESSION)
				.append("\"")
				.toString(),
				null
			);
			if (c.moveToFirst()) {
				result = c.getInt(0);
			};
			c.close();
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	protected long insertSession(Session session) {
		init();
		long result = -1;
		try {
			vals.clear();
			vals.put(KEY_SESSION_START,session.getStart().getTime());
			vals.put(KEY_SESSION_STOP,session.getStop().getTime());
			result = db.insert(TAB_SESSION,null,vals);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	public long insertSighting(Sighting s) {
		init();
		long result = -1;
		try {
			vals.clear();
			vals.put(KEY_SIGHTING_ADDRESS,s.getAddress());
			vals.put(KEY_SIGHTING_NAME,s.getName());
			vals.put(KEY_SIGHTING_RSSI,s.getRssi());
			vals.put(KEY_SIGHTING_SESSION_ID,s.getSessionId());
			vals.put(KEY_SIGHTING_TIME,s.getTime().getTime());
			result = db.insert(TAB_SIGHTING,null,vals);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	public void updateSession(Session session) {
		init();
		try {
			db.execSQL(
				new StringBuffer()
				.append("UPDATE \"")
				.append(TAB_SESSION)
				.append("\" SET \"")
				.append(KEY_SESSION_STOP)
				.append("\" = ")
				.append(Long.toString(session.getStop().getTime()))
				.append(" WHERE \"")
				.append(KEY_SESSION_ID)
				.append("\" = ")
				.append(Long.toString(session.getId()))
				.toString()
			);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
	}

	/** deletes all data. */
	protected void reset() {
		try {
			db.execSQL("DELETE FROM " + TAB_SESSION);
			db.execSQL("DELETE FROM " + TAB_SIGHTING);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
	}
}