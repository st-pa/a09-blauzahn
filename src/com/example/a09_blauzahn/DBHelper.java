package com.example.a09_blauzahn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

/**
 * @author stpa
 */
public class DBHelper
extends SQLiteOpenHelper {

	////////////////////////////////////////////
	// local constants
	////////////////////////////////////////////

	private static final String DB_NAME = "blauzahn";
	private static final int DB_VERSION = 2;

	protected static final String TAB_SESSION = "\"session\"";
	protected static final String KEY_SESSION_ID = "\"id\"";
	protected static final String KEY_SESSION_START = "\"start\"";
	protected static final String KEY_SESSION_STOP = "\"stop\"";

	protected static final String TAB_SIGHTING = "\"sighting\"";
	protected static final String KEY_SIGHTING_ID = "\"id\"";
	protected static final String KEY_SIGHTING_SESSION_ID = "\"sessionId\"";
	protected static final String KEY_SIGHTING_TIME = "\"time\"";
	protected static final String KEY_SIGHTING_ADDRESS = "\"address\"";
	protected static final String KEY_SIGHTING_NAME = "\"name\"";
	protected static final String KEY_SIGHTING_RSSI = "\"rssi\"";

	////////////////////////////////////////////
	// local fields
	////////////////////////////////////////////

	/** used in {@link #toast(String)}. */
	private Context context;
	/** the actual database. */
	private SQLiteDatabase db;
	/** container for writing values to sql. */
	private ContentValues vals = new ContentValues();

	////////////////////////////////////////////
	// methods and functions
	////////////////////////////////////////////

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

	@Override
	public void close() {
		if (this.db != null) {
			this.db.close();
		}
		super.close();
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

	/**
	 * convenience method that shows a short toast-message.
	 * @param text {@link String}
	 */
	private void toast(String text) {
		Toast.makeText(
			context,
			text,
			Toast.LENGTH_SHORT
		).show();
	}

	/** try to initialize the database for writing access if neccessary. */
	private void init() {
		if (db == null) {
			try {
				db = this.getWritableDatabase();
			} catch (SQLiteException e) {
				e.printStackTrace();
			}
			vals.clear();
		}
	}

	/** delete all data by dropping all tables and calling {@link #onCreate(SQLiteDatabase)}. */
	protected void reset() {
		try {
			db.execSQL("DROP TABLE " + TAB_SESSION);
			db.execSQL("DROP TABLE " + TAB_SIGHTING);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		onCreate(db);
	}

	/**
	 * gets the maximum id value in the session table,
	 * which should be identical to the row count.
	 * @return {@link Integer}
	 */
	protected int getMaxSessionId() {
		init();
		int result = -1;
		try {
			Cursor c = db.rawQuery(
				new StringBuffer()
				.append("SELECT max(")
				.append(KEY_SESSION_ID)
				.append(") FROM ")
				.append(TAB_SESSION)
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

	/**
	 * gets the maximum id value in the sighting table,
	 * which should be identical to the row count.
	 * @return {@link Integer}
	 */
	protected int getMaxSightingId() {
		init();
		int result = -1;
		try {
			Cursor c = db.rawQuery(
				new StringBuffer()
				.append("SELECT max(")
				.append(KEY_SIGHTING_ID)
				.append(") FROM ")
				.append(TAB_SIGHTING)
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

	/**
	 * adds a new row to the session-table using only the start time value
	 * and returns the autoinced id.
	 * @param session {@link Session}
	 * @return {@link Long}
	 */
	protected long insertSession(Session session) {
		init();
		long result = -1;
		try {
			vals.clear();
			vals.put(KEY_SESSION_START,session.getStart().getTime());
			vals.put(KEY_SESSION_STOP,-1);
			result = db.insert(TAB_SESSION,null,vals);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	/**
	 * adds a new row to the sightings-table and returns the autoinced id.
	 * @param sighting {@link Sighting}
	 * @return {@link Long}
	 */
	public long insertSighting(Sighting sighting) {
		init();
		long result = -1;
		try {
			vals.clear();
			vals.put(KEY_SIGHTING_ADDRESS,sighting.getAddress());
			vals.put(KEY_SIGHTING_NAME,sighting.getName());
			vals.put(KEY_SIGHTING_RSSI,sighting.getRssi());
			vals.put(KEY_SIGHTING_SESSION_ID,sighting.getSessionId());
			vals.put(KEY_SIGHTING_TIME,sighting.getTime().getTime());
			result = db.insert(TAB_SIGHTING,null,vals);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	/**
	 * updates the stop time of the given session.
	 * @param sighting {@link Session}
	 */
	public void updateSession(Session session) {
		init();
		try {
			db.execSQL(
				new StringBuffer()
				.append("UPDATE ")
				.append(TAB_SESSION)
				.append(" SET ")
				.append(KEY_SESSION_STOP)
				.append(" = ")
				.append(Long.toString(session.getStop().getTime()))
				.append(" WHERE ")
				.append(KEY_SESSION_ID)
				.append(" = ")
				.append(Long.toString(session.getId()))
				.toString()
			);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
	}

	/**
	 * gets a list of all sighted devices.
	 * warning! this can be very long.
	 * @return {@link List}<{@link Sighting}>
	 */
	public List<Sighting> getListSightingComplete(int limit) {
		init();
		List<Sighting> result = new ArrayList<Sighting>();
		try {
			Cursor c = db.rawQuery(
				new StringBuffer()
				.append("SELECT\n\t")
				.append(KEY_SIGHTING_ID).append(",\n\t")
				.append(KEY_SIGHTING_SESSION_ID).append(",\n\t")
				.append(KEY_SIGHTING_TIME).append(",\n\t")
				.append(KEY_SIGHTING_ADDRESS).append(",\n\t")
				.append(KEY_SIGHTING_NAME).append(",\n\t")
				.append(KEY_SIGHTING_RSSI).append("\n\t")
				.append("FROM ").append(TAB_SIGHTING).append("\n")
				.append("ORDER BY ").append(KEY_SIGHTING_ID).append(" DESC\n")
				.append("LIMIT ").append(Integer.toString(limit))
				.toString(),
				null
			);
			while (c.moveToNext()) {
				result.add(
					new Sighting(
						c.getLong(0),
						c.getLong(1),
						new Date(c.getLong(2)),
						c.getString(3),
						c.getString(4),
						c.getLong(5)
					)
				);
			};
			c.close();
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}
}