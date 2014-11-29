package com.example.a09_blauzahn.util;

import java.util.Date;

public class Settings {

	private static final String NAME = "Blauzahn";
	private static final boolean AUTO = false;
	private static final int INTERVAL = 300;
	private static final boolean DISABLE = true;

	/** autoincremented id for these settings. */
	private long id;
	/** date/time from when on these settings are/were valid. */
	private Date validFrom;
	/** name of the wifi device (e.g. "GT-Ixxxx"). */
	private String wifiName;
	/** enable wifi automatic discovery. */
	private boolean wifiAuto;
	/** how often to scan wifi. */
	private int wifiInterval;
	/** disable wifi when not in use. */
	private boolean wifiDisable;
	/** name of the bluetooth device (e.g. "GT-Ixxxx"). */
	private String btName;
	/** enable bluetooth automatic discovery. */
	private boolean btAuto;
	/** how often to scan bluetooth. */
	private int btInterval;
	/** disable bluetooth when not in use. */
	private boolean btDisable;

	/** Constructor, using all fields. */
	public Settings(
		long id, Date validFrom, String wifiName, boolean wifiAuto,
		int wifiInterval, boolean wifiDisable, String btName,
		boolean btAuto, int btInterval, boolean btDisable
	) {
		super();
		this.id = id;
		this.validFrom = validFrom;
		this.wifiName = wifiName;
		this.wifiAuto = wifiAuto;
		this.wifiInterval = wifiInterval;
		this.wifiDisable = wifiDisable;
		this.btName = btName;
		this.btAuto = btAuto;
		this.btInterval = btInterval;
		this.btDisable = btDisable;
	}

	/**
	 * Constructor, will set default settings.
	 * @see #NAME
	 * @see #AUTO
	 * @see #INTERVAL
	 * @see #DISABLE
	 */
	public Settings() {
		this(
			-1,new Date(),
			NAME,AUTO,INTERVAL,DISABLE,
			NAME,AUTO,INTERVAL,DISABLE
		);
	}

	/**
	 * @return the id
	 */
	public final long getId() {
		return id;
	}

	/**
	 * @return the validFrom
	 */
	public final Date getValidFrom() {
		return validFrom;
	}

	/**
	 * @return the wifiName
	 */
	public final String getWifiName() {
		return wifiName;
	}

	/**
	 * @return the wifiAuto
	 */
	public final boolean isWifiAuto() {
		return wifiAuto;
	}

	/**
	 * @return the wifiInterval
	 */
	public final int getWifiInterval() {
		return wifiInterval;
	}

	/**
	 * @return the wifiDisable
	 */
	public final boolean isWifiDisable() {
		return wifiDisable;
	}

	/**
	 * @return the btName
	 */
	public final String getBtName() {
		return btName;
	}

	/**
	 * @return the btAuto
	 */
	public final boolean isBtAuto() {
		return btAuto;
	}

	/**
	 * @return the btInterval
	 */
	public final int getBtInterval() {
		return btInterval;
	}

	/**
	 * @return the btDisable
	 */
	public final boolean isBtDisable() {
		return btDisable;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(long id) {
		this.id = id;
	}

	/**
	 * @param validFrom the validFrom to set
	 */
	public final void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	/**
	 * @param wifiName the wifiName to set
	 */
	public final void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}

	/**
	 * @param wifiAuto the wifiAuto to set
	 */
	public final void setWifiAuto(boolean wifiAuto) {
		this.wifiAuto = wifiAuto;
	}

	/**
	 * @param wifiInterval the wifiInterval to set
	 */
	public final void setWifiInterval(int wifiInterval) {
		this.wifiInterval = wifiInterval;
	}

	/**
	 * @param wifiDisable the wifiDisable to set
	 */
	public final void setWifiDisable(boolean wifiDisable) {
		this.wifiDisable = wifiDisable;
	}

	/**
	 * @param btName the btName to set
	 */
	public final void setBtName(String btName) {
		this.btName = btName;
	}

	/**
	 * @param btAuto the btAuto to set
	 */
	public final void setBtAuto(boolean btAuto) {
		this.btAuto = btAuto;
	}

	/**
	 * @param btInterval the btInterval to set
	 */
	public final void setBtInterval(int btInterval) {
		this.btInterval = btInterval;
	}

	/**
	 * @param btDisable the btDisable to set
	 */
	public final void setBtDisable(boolean btDisable) {
		this.btDisable = btDisable;
	}

}
