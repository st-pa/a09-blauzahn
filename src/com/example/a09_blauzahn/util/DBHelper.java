package com.example.a09_blauzahn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.a09_blauzahn.AppBlauzahn;
import com.example.a09_blauzahn.model.Device;
import com.example.a09_blauzahn.model.Session;
import com.example.a09_blauzahn.model.Sighting;

/**
 * @author stpa
 */
public class DBHelper
extends SQLiteOpenHelper {

	////////////////////////////////////////////
	// global constants
	////////////////////////////////////////////

	public static final String NULL_VALUE = "--";
	public static final String SEPARATOR = System.getProperty("file.separator");

	////////////////////////////////////////////
	// local constants
	////////////////////////////////////////////

	/** file name the database is stored in. */
	private static final String DB_NAME = "blauzahn";
	/** internal version of the data model. */
	private static final int DB_VERSION = 3;

	/**
	 * utility class containing sql table and column names for version 1.
	 * deprecated because of preparations to upgrade to version three.
	 * @author stpa
	 * @Deprecated use {@link V3} instead.
	 */
	@Deprecated
	protected static class V1 {
		// table for bluetooth sessions
		protected static final String TAB_SESSION = "\"session\"";
		protected static final String KEY_SESSION_ID = "\"id\"";
		protected static final String KEY_SESSION_START = "\"start\"";
		protected static final String KEY_SESSION_STOP = "\"stop\"";
		// table for bluetooth sightings
		protected static final String TAB_SIGHTING = "\"sighting\"";
		protected static final String KEY_SIGHTING_ID = "\"id\"";
		protected static final String KEY_SIGHTING_SESSION_ID = "\"sessionId\"";
		protected static final String KEY_SIGHTING_TIME = "\"time\"";
		protected static final String KEY_SIGHTING_ADDRESS = "\"address\"";
		protected static final String KEY_SIGHTING_NAME = "\"name\"";
		protected static final String KEY_SIGHTING_RSSI = "\"rssi\"";
	}

	/**
	 * utility class with sql table and column names for version 3.
	 * @author stpa
	 */
	protected static class V3 {
		// table for user defined settings of the application
		protected static final String TAB_SETTINGS = "\"settings\"";
		protected static final String KEY_SETTINGS_ID = "\"id\"";
		protected static final String KEY_SETTINGS_VALID_FROM = "\"validFrom\"";
		protected static final String KEY_SETTINGS_BT_ON = "\"btOn\"";
		protected static final String KEY_SETTINGS_BT_NAME = "\"btName\"";
		protected static final String KEY_SETTINGS_BT_AUTO = "\"btAuto\"";
		protected static final String KEY_SETTINGS_BT_INTERVAL = "\"btInterval\"";
		protected static final String KEY_SETTINGS_BT_DISABLE = "\"btDisable\"";
		protected static final String KEY_SETTINGS_WIFI_ON = "\"wifiOn\"";
		protected static final String KEY_SETTINGS_WIFI_NAME = "\"wifiName\"";
		protected static final String KEY_SETTINGS_WIFI_AUTO = "\"wifiAuto\"";
		protected static final String KEY_SETTINGS_WIFI_INTERVAL = "\"wifiInterval\"";
		protected static final String KEY_SETTINGS_WIFI_DISABLE = "\"wifiDisable\"";
		// table for bluetooth sessions
		protected static final String TAB_BTSESSION = "\"btSession\"";
		protected static final String KEY_BTSESSION_ID = "\"id\"";
		protected static final String KEY_BTSESSION_START = "\"start\"";
		protected static final String KEY_BTSESSION_STOP = "\"stop\"";
		// table for bluetooth sightings
		protected static final String TAB_BTSIGHTING = "\"btSighting\"";
		protected static final String KEY_BTSIGHTING_ID = "\"id\"";
		protected static final String KEY_BTSIGHTING_SESSION_ID = "\"sessionId\"";
		protected static final String KEY_BTSIGHTING_TIME = "\"time\"";
		protected static final String KEY_BTSIGHTING_ADDRESS = "\"address\"";
		protected static final String KEY_BTSIGHTING_NAME = "\"name\"";
		protected static final String KEY_BTSIGHTING_RSSI = "\"rssi\"";
		// prepared create statements for sql.
		protected static final String CREATE_TAB_BTSESSIONS = new StringBuffer()
			.append("CREATE TABLE ")
			.append(V3.TAB_BTSESSION)
			.append(" (\n")
			.append(V3.KEY_BTSESSION_ID)   .append(" INTEGER PRIMARY KEY AUTOINCREMENT,\n")
			.append(V3.KEY_BTSESSION_START).append(" INTEGER,\n")
			.append(V3.KEY_BTSESSION_STOP) .append(" INTEGER\n")
			.append(")")
			.toString()
		;
		protected static final String CREATE_TAB_BTSIGHTINGS = new StringBuffer()
			.append("CREATE TABLE ")
			.append(V3.TAB_BTSIGHTING)
			.append(" (\n")
			.append(V3.KEY_BTSIGHTING_ID)        .append(" INTEGER PRIMARY KEY AUTOINCREMENT,\n")
			.append(V3.KEY_BTSIGHTING_SESSION_ID).append(" INTEGER,\n")
			.append(V3.KEY_BTSIGHTING_TIME)      .append(" INTEGER,\n")
			.append(V3.KEY_BTSIGHTING_ADDRESS)   .append(" TEXT,\n")
			.append(V3.KEY_BTSIGHTING_NAME)      .append(" TEXT,\n")
			.append(V3.KEY_BTSIGHTING_RSSI)      .append(" INTEGER\n")
			.append(")")
			.toString()
		;
		protected static final String CREATE_TAB_SETTINGS = new StringBuffer()
			.append("CREATE TABLE ").append(V3.TAB_SETTINGS).append(" (\n")
			.append(V3.KEY_SETTINGS_ID)         .append(" INTEGER PRIMARY KEY AUTOINCREMENT,\n")
			.append(V3.KEY_SETTINGS_VALID_FROM) .append(" INTEGER,\n")
			.append(V3.KEY_SETTINGS_BT_ON)      .append(" INTEGER,\n")
			.append(V3.KEY_SETTINGS_BT_AUTO)    .append(" INTEGER,\n")
			.append(V3.KEY_SETTINGS_BT_DISABLE) .append(" INTEGER,\n")
			.append(V3.KEY_SETTINGS_BT_INTERVAL).append(" INTEGER,\n")
			.append(V3.KEY_SETTINGS_BT_NAME)    .append(" TEXT,\n")
			.append(V3.KEY_SETTINGS_WIFI_ON)    .append(" INTEGER,\n")
			.append(V3.KEY_SETTINGS_WIFI_AUTO)  .append(" INTEGER,\n")
			.append(V3.KEY_SETTINGS_WIFI_DISABLE).append(" INTEGER,\n")
			.append(V3.KEY_SETTINGS_WIFI_INTERVAL).append(" INTEGER,\n")
			.append(V3.KEY_SETTINGS_WIFI_NAME)   .append(" TEXT\n")
			.append(")")
			.toString()
		;
	}
	protected class V4 {
		// table for wifi sessions
		protected static final String TAB_WIFISESSION = "\"wifiSession\"";
		protected static final String KEY_WIFISESSION_ID = "\"id\"";
		protected static final String KEY_WIFISESSION_START = "\"start\"";
		protected static final String KEY_WIFISESSION_STOP = "\"stop\"";
		// table for wifi sightings
		protected static final String TAB_WIFISIGHTING = "\"wifiSighting\"";
		protected static final String KEY_WIFISIGHTING_ID = "\"id\"";
		protected static final String KEY_WIFISIGHTING_SESSION_ID = "\"sessionId\"";
		protected static final String KEY_WIFISIGHTING_TIME = "\"time\"";
		// TODO add columns appropriate to wifi measurements
	}

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
		if (DB_VERSION <= 2) {
			db.execSQL(
				new StringBuffer()
				.append("CREATE TABLE ")
				.append(V1.TAB_SESSION)
				.append(" (\n")
				.append(V1.KEY_SESSION_ID)   .append(" INTEGER PRIMARY KEY AUTOINCREMENT,\n")
				.append(V1.KEY_SESSION_START).append(" INTEGER,\n")
				.append(V1.KEY_SESSION_STOP) .append(" INTEGER\n")
				.append(")")
				.toString()
			);
			db.execSQL(
				new StringBuffer()
				.append("CREATE TABLE ")
				.append(V1.TAB_SIGHTING)
				.append(" (\n")
				.append(V1.KEY_SIGHTING_ID)        .append(" INTEGER PRIMARY KEY AUTOINCREMENT,\n")
				.append(V1.KEY_SIGHTING_SESSION_ID).append(" INTEGER,\n")
				.append(V1.KEY_SIGHTING_TIME)      .append(" INTEGER,\n")
				.append(V1.KEY_SIGHTING_ADDRESS)   .append(" TEXT,\n")
				.append(V1.KEY_SIGHTING_NAME)      .append(" TEXT,\n")
				.append(V1.KEY_SIGHTING_RSSI)      .append(" INTEGER\n")
				.append(")")
				.toString()
			);
		}
		// prepare for upgrade to version three
		if (DB_VERSION >= 3) {
			db.execSQL(V3.CREATE_TAB_BTSESSIONS);
			db.execSQL(V3.CREATE_TAB_BTSIGHTINGS);
			db.execSQL(V3.CREATE_TAB_SETTINGS);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		toast("DBHelper.onUpdate");
		if (oldVersion == 1 && newVersion == 2) {
			db.execSQL(
				new StringBuffer()
				.append("ALTER TABLE ")
				.append(V1.TAB_SIGHTING)
				.append(" ADD COLUMN ")
				.append(V1.KEY_SIGHTING_SESSION_ID)
				.append(" INTEGER")
				.toString()
			);
			db.execSQL(
				new StringBuffer()
				.append("ALTER TABLE ")
				.append(V1.TAB_SIGHTING)
				.append(" ADD COLUMN ")
				.append(V1.KEY_SIGHTING_TIME)
				.append(" INTEGER")
				.toString()
			);
		} else if (oldVersion == 2 && newVersion == 3) {
			// create new table for user defined settings
			db.execSQL(V3.CREATE_TAB_SETTINGS);
			// rename bluetooth sessions table
			db.execSQL(
				new StringBuffer()
				.append("ALTER TABLE ").append(V1.TAB_SESSION).append("\n")
				.append("RENAME TO ").append(V3.TAB_BTSESSION)
				.toString()
			);
			// rename bluetooth sightings table
			db.execSQL(
				new StringBuffer()
				.append("ALTER TABLE ").append(V1.TAB_SIGHTING).append("\n")
				.append("RENAME TO ").append(V3.TAB_BTSIGHTING)
				.toString()
			);
		}
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

	/** convenience function for displaying <code>null</code> values. */
	public static final String nullValue(String text) {
		return text == null ? NULL_VALUE : text;
	}

	/** delete all data by dropping all tables and calling {@link DBHelper#onCreate(SQLiteDatabase)}. */
	public void reset() {
		try {
			db.execSQL("DROP TABLE " + V3.TAB_BTSESSION);
			db.execSQL("DROP TABLE " + V3.TAB_BTSIGHTING);
			db.execSQL("DROP TABLE " + V3.TAB_SETTINGS);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		onCreate(db);
	}

	/**
	 * gets the maximum id value in the bluetooth session table,
	 * which should be identical to the row count.
	 * @return {@link Integer}
	 */
	public int getMaxBTSessionId() {
		init();
		int result = -1;
		try {
			Cursor c = db.rawQuery(
				new StringBuffer()
				.append("SELECT max(")
				.append(V3.KEY_BTSESSION_ID)
				.append(") FROM ")
				.append(V3.TAB_BTSESSION)
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
	 * gets the maximum id value in the bluetooth sighting table,
	 * which should be identical to the row count.
	 * @return {@link Integer}
	 */
	public int getMaxBTSightingId() {
		init();
		int result = -1;
		try {
			Cursor c = db.rawQuery(
				new StringBuffer()
				.append("SELECT max(")
				.append(V3.KEY_BTSIGHTING_ID)
				.append(") FROM ")
				.append(V3.TAB_BTSIGHTING)
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
	 * adds a new row to the bluetooth session-table
	 * using only the start time value and returns the autoinced id.
	 * @param session {@link Session}
	 * @return {@link Long}
	 */
	public long addBTSession(Session session) {
		init();
		long result = -1;
		try {
			vals.clear();
			vals.put(V3.KEY_BTSESSION_START,session.getStart().getTime());
			vals.put(V3.KEY_BTSESSION_STOP,-1);
			result = db.insert(V3.TAB_BTSESSION,null,vals);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	/**
	 * adds a new row to the bluetooth sightings-table and returns the autoinced id.
	 * @param sighting {@link Sighting}
	 * @return {@link Long}
	 */
	public long addBTSighting(Sighting sighting) {
		init();
		long result = -1;
		try {
			vals.clear();
			vals.put(V3.KEY_BTSIGHTING_ADDRESS,sighting.getAddress());
			vals.put(V3.KEY_BTSIGHTING_NAME,sighting.getName());
			vals.put(V3.KEY_BTSIGHTING_RSSI,sighting.getRssi());
			vals.put(V3.KEY_BTSIGHTING_SESSION_ID,sighting.getSessionId());
			vals.put(V3.KEY_BTSIGHTING_TIME,sighting.getTime().getTime());
			result = db.insert(V3.TAB_BTSIGHTING,null,vals);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	/**
	 * updates the stop time of the given bluetooth session.
	 * @param sighting {@link Session}
	 */
	public void setBTSession(Session session) {
		init();
		try {
			db.execSQL(
				new StringBuffer()
				.append("UPDATE ")
				.append(V3.TAB_BTSESSION)
				.append(" SET ")
				.append(V3.KEY_BTSESSION_STOP)
				.append(" = ")
				.append(Long.toString(session.getStop().getTime()))
				.append(" WHERE ")
				.append(V3.KEY_BTSESSION_ID)
				.append(" = ")
				.append(Long.toString(session.getId()))
				.toString()
			);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
	}

	/**
	 * gets a list of all bluetooth device sightings.
	 * warning! this can be very long.
	 * @param limit {@link Integer} limit number of rows to be retrieved
	 * @return {@link List}<{@link Sighting}>
	 */
	public List<Sighting> getListBTSightings(int limit) {
		init();
		List<Sighting> result = new ArrayList<Sighting>();
		try {
			StringBuffer s = new StringBuffer()
			.append("SELECT\n\t")
			.append(V3.KEY_BTSIGHTING_ID).append(",\n\t")
			.append(V3.KEY_BTSIGHTING_SESSION_ID).append(",\n\t")
			.append(V3.KEY_BTSIGHTING_TIME).append(",\n\t")
			.append(V3.KEY_BTSIGHTING_NAME).append(",\n\t")
			.append(V3.KEY_BTSIGHTING_ADDRESS).append(",\n\t")
			.append(V3.KEY_BTSIGHTING_RSSI).append("\n")
			.append("FROM ").append(V3.TAB_BTSIGHTING).append("\n")
			.append("ORDER BY ").append(V3.KEY_BTSIGHTING_ID).append(" DESC\n")
			.append("LIMIT ").append(Integer.toString(limit))
			;
			Cursor c = db.rawQuery(
				s.toString(),
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

	/**
	 * list all names seen for the given address.
	 * @param address {@link String}
	 * @return {@link List}<{@link String}>
	 */
	public List<String> getListBTDeviceNames(String address) {
		init();
		List<String> result = new ArrayList<String>();
		try {
			StringBuffer s = new StringBuffer()
			.append("SELECT DISTINCT ").append(V3.KEY_BTSIGHTING_NAME).append("\n")
			.append("FROM ").append(V3.TAB_BTSIGHTING).append("\n")
			.append("WHERE ").append(V3.KEY_BTSIGHTING_ADDRESS)
			.append(" = \"").append(address).append("\"")
			;
			Cursor c = db.rawQuery(
				s.toString(),
				null
			);
			while (c.moveToNext()) {
				result.add(c.getString(0));
			};
			c.close();
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	/**
	 * gets a list of all unique sighted bluetooth devices (addresses).
	 * warning! this can be very long.
	 * @param limit {@link Integer} limit number of rows to be retrieved
	 * @return {@link List}<{@link Sighting}>
	 */
	public List<Device> getListBTDevices(int limit) {
		init();
		List<Device> result = new ArrayList<Device>();
		try {
			StringBuffer s = new StringBuffer()
			.append("SELECT\n\t")
			.append(V3.KEY_BTSIGHTING_ADDRESS).append(",\n\t")
			.append("min(").append(V3.KEY_BTSIGHTING_TIME).append("),\n\t")
			.append("max(").append(V3.KEY_BTSIGHTING_TIME).append("),\n\t")
			.append("count(DISTINCT ").append(V3.KEY_BTSIGHTING_SESSION_ID).append("),\n\t")
			.append("avg(").append(V3.KEY_BTSIGHTING_RSSI).append(")\n")
			.append("FROM ").append(V3.TAB_BTSIGHTING).append("\n")
			.append("GROUP BY ").append(V3.KEY_BTSIGHTING_ADDRESS).append("\n")
			.append("ORDER BY 4 DESC\n")
			.append("LIMIT ").append(Integer.toString(limit))
			;
			Cursor c = db.rawQuery(
				s.toString(),
				null
			);
			while (c.moveToNext()) {
				String address = c.getString(0);
				result.add(
					new Device(
						address,
						getListBTDeviceNames(address),
						new Date(c.getLong(1)),
						new Date(c.getLong(2)),
						c.getLong(3),
						c.getDouble(4)
					)
				);
			};
			c.close();
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	/**
	 * gets a list of all bluetooth sessions.
	 * warning! this can be very long.
	 * @param limit {@link Integer} limit number of rows to be retrieved
	 * @return {@link List}<{@link Session}>
	 */
	public List<Session> getListBTSessions(int limit) {
		init();
		List<Session> result = new ArrayList<Session>();
		try {
			StringBuffer s = new StringBuffer()
			.append("SELECT\n\t")
			.append(V3.KEY_BTSESSION_ID).append(",\n\t")
			.append(V3.KEY_BTSESSION_START).append(",\n\t")
			.append(V3.KEY_BTSESSION_STOP).append("\n")
			.append("FROM ").append(V3.TAB_BTSESSION).append("\n")
			.append("ORDER BY 1 DESC\n")
			.append("LIMIT ").append(Integer.toString(limit))
			;
			Cursor c = db.rawQuery(
				s.toString(),
				null
			);
			while (c.moveToNext()) {
				long sessionId = c.getLong(0);
				List<Sighting> sightings = getBTSightingsInSession(sessionId);
				result.add(
					new Session(
						sessionId,
						new Date(c.getLong(1)),
						new Date(c.getLong(2)),
						sightings
					)
				);
			};
			c.close();
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	/**
	 * count the bluetooth sightings in the given bluetooth session.
	 * @param sessionId {@loink Long}
	 * @return {@link List}<{@link Sighting}>
	 */
	public List<Sighting> getBTSightingsInSession(long sessionId) {
		init();
		List<Sighting> result = new ArrayList<Sighting>();
		try {
			Cursor c = db.rawQuery(
				new StringBuffer()
				.append("SELECT\n\t")
				.append(V3.KEY_BTSIGHTING_ID).append(",\n\t")
				.append(V3.KEY_BTSIGHTING_TIME).append(",\n\t")
				.append(V3.KEY_BTSIGHTING_NAME).append(",\n\t")
				.append(V3.KEY_BTSIGHTING_ADDRESS).append(",\n\t")
				.append(V3.KEY_BTSIGHTING_RSSI).append("\n")
				.append("FROM ").append(V3.TAB_BTSIGHTING).append("\n")
				.append("WHERE ").append(V3.KEY_BTSIGHTING_SESSION_ID)
				.append(" = ").append(Long.toString(sessionId)).append("\n")
				.append("ORDER BY 2 ASC")
				.toString(),
				null
			);
			while (c.moveToNext()) {
				result.add(
					new Sighting(
						c.getLong(0),
						sessionId,
						new Date(c.getLong(1)),
						c.getString(2),
						c.getString(3),
						c.getLong(4)
					)
				);
			};
			c.close();
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	/**
	 * get the currently valid settings, i.e. the settings with
	 * the highest id. if there is none to be found, the default
	 * settinga apply.
	 * @return {@link Settings}
	 */
	public Settings getSettings() {
		init();
		Settings result = new Settings();
		try {
			Cursor c = db.rawQuery(
				new StringBuffer()
				.append("SELECT\n\t")
				.append(V3.KEY_SETTINGS_ID).append(",\n\t")
				.append(V3.KEY_SETTINGS_VALID_FROM).append(",\n\t")
				.append(V3.KEY_SETTINGS_BT_ON).append(",\n\t")
				.append(V3.KEY_SETTINGS_BT_NAME).append(",\n\t")
				.append(V3.KEY_SETTINGS_BT_AUTO).append(",\n\t")
				.append(V3.KEY_SETTINGS_BT_INTERVAL).append(",\n\t")
				.append(V3.KEY_SETTINGS_BT_DISABLE).append(",\n\t")
				.append(V3.KEY_SETTINGS_WIFI_ON).append(",\n\t")
				.append(V3.KEY_SETTINGS_WIFI_NAME).append(",\n\t")
				.append(V3.KEY_SETTINGS_WIFI_AUTO).append(",\n\t")
				.append(V3.KEY_SETTINGS_WIFI_INTERVAL).append(",\n\t")
				.append(V3.KEY_SETTINGS_WIFI_DISABLE).append("\n")
				.append("FROM ").append(V3.TAB_SETTINGS).append("\n")
				.append("WHERE ").append(V3.KEY_SETTINGS_ID)
				.append(" = (\n\t")
				.append("SELECT max(").append(V3.KEY_SETTINGS_ID).append(")\n\t")
				.append("FROM ").append(V3.TAB_SETTINGS).append("\n")
				.append(")")
				.toString(),
				null
			);
			if (c.moveToFirst()) {
				result = new Settings(
					c.getLong(0),
					new Date(c.getLong(1)),
					c.getInt(2) == 0,
					c.getString(3),
					c.getInt(4) == 0,
					c.getInt(5),
					c.getInt(6) == 0,
					c.getInt(7) == 0,
					c.getString(8),
					c.getInt(9) == 0,
					c.getInt(10),
					c.getInt(11) == 0
				);
			}
			c.close();
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	/**
	 * adds a new row to the settings-table
	 * and returns the autoinced id.
	 * @param settings {@link Settings}
	 * @return {@link Long}
	 */
	public long addSettings(Settings settings) {
		init();
		long result = -1;
		try {
			vals.clear();
			vals.put(V3.KEY_SETTINGS_BT_ON,        settings.isBtOn() ? 0 : 1);
			vals.put(V3.KEY_SETTINGS_BT_AUTO,      settings.isBtAuto() ? 0 : 1);
			vals.put(V3.KEY_SETTINGS_BT_DISABLE,   settings.isBtDisable() ? 0 : 1);
			vals.put(V3.KEY_SETTINGS_BT_INTERVAL,  settings.getBtInterval());
			vals.put(V3.KEY_SETTINGS_BT_NAME,      settings.getBtName());
			vals.put(V3.KEY_SETTINGS_VALID_FROM,   settings.getValidFrom().getTime());
			vals.put(V3.KEY_SETTINGS_WIFI_ON,      settings.isWifiAuto() ? 0 : 1);
			vals.put(V3.KEY_SETTINGS_WIFI_AUTO,    settings.isWifiAuto() ? 0 : 1);
			vals.put(V3.KEY_SETTINGS_WIFI_DISABLE, settings.isWifiDisable() ? 0 : 1);
			vals.put(V3.KEY_SETTINGS_WIFI_INTERVAL,settings.getWifiInterval());
			vals.put(V3.KEY_SETTINGS_WIFI_NAME,    settings.getWifiName());
			result = db.insert(V3.TAB_SETTINGS,null,vals);
		} catch (SQLiteException e) {
			Log.e("SQL",e.toString());
		}
		return result;
	}

	/**
	 * TODO set a new system time and change all timestamps accordingly.
	 * @param c {@link Calendar} with the new date/time to set systemwide
	 */
	public void setSystemTime(Calendar c) {
//		Calendar c = Calendar.getInstance();
//		c.set(2010, 1, 1, 12, 00, 00);
//		AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//		am.setTime(c.getTimeInMillis());
	}

	/**
	 * export the entire sqlite-database to external storage.
	 * @see http://stackoverflow.com/questions/6540906/android-simple-export-and-import-of-sqlite-database
	 */
	public void exportDB() {
		StringBuffer targetFolder = new StringBuffer()
		.append(Environment.getExternalStorageDirectory().getAbsolutePath())
		.append(SEPARATOR)
		.append(DB_NAME);
		StringBuffer target = new StringBuffer()
		.append(targetFolder)
		.append(SEPARATOR)
		.append(AppBlauzahn.datetimestamp())
		.append("-")
		.append(DB_NAME);
		StringBuffer source = getDBSourcePath();
		System.out.println(
			"trying to copy database from\n" +
			source.toString() + " to\n" +
			target.toString()
		);
		try {
			File folder = new File(targetFolder.toString());
			if (!folder.exists()) {
				folder.mkdirs();
			}
			FileInputStream fis = new FileInputStream(new File(source.toString()));
			FileOutputStream fos = new FileOutputStream(new File(target.toString()));
			FileChannel src = fis.getChannel();
			FileChannel dst = fos.getChannel();
			dst.transferFrom(src, 0, src.size());
			fis.close();
			fos.close();
			src.close();
			dst.close();
			toast("database exported successfully.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private StringBuffer getDBSourcePath() {
		return new StringBuffer()
		.append(Environment.getDataDirectory().getAbsolutePath())
		.append(SEPARATOR)
		.append("data")
		.append(SEPARATOR)
		.append(AppBlauzahn.class.getPackage().getName())
		.append(SEPARATOR)
		.append("databases")
		.append(SEPARATOR)
		.append(DB_NAME);
	}
}