package com.example.a09_blauzahn.util;

import java.util.Date;

public class Settings {

	private static final String NAME = "Blauzahn";
	private static final boolean AUTO = false;
	private static final int INTERVAL = 300000;
	private static final boolean DISABLE = true;

	/** autoincremented id for these settings. */
	private long id;
	/** date/time from when on these settings are/were valid. */
	private Date validFrom;
	/** whether or not to scan for wifi. */
	private boolean wifiOn;
	/** name of the wifi device (e.g. "GT-Ixxxx"). */
	private String wifiName;
	/** enable wifi automatic discovery. */
	private boolean wifiAuto;
	/** how often to scan wifi. */
	private long wifiInterval;
	/** disable wifi when not in use. */
	private boolean wifiDisable;
	/** whether or not to scan for bluetooth. */
	private boolean btOn;
	/** name of the bluetooth device (e.g. "GT-Ixxxx"). */
	private String btName;
	/** enable bluetooth automatic discovery. */
	private boolean btAuto;
	/** how often to scan bluetooth. */
	private long btInterval;
	/** disable bluetooth when not in use. */
	private boolean btDisable;

	/** Constructor, using all fields. */
	public Settings(
		long id, Date validFrom,
		boolean wifiOn, String wifiName, boolean wifiAuto,
		int wifiInterval, boolean wifiDisable,
		boolean btOn, String btName,
		boolean btAuto, int btInterval, boolean btDisable
	) {
		super();
		this.id = id;
		this.validFrom = validFrom;
		this.wifiOn = wifiOn;
		this.wifiName = wifiName;
		this.wifiAuto = wifiAuto;
		this.wifiInterval = wifiInterval;
		this.wifiDisable = wifiDisable;
		this.btOn = btOn;
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
			false,NAME,AUTO,INTERVAL,DISABLE,
			true,NAME,AUTO,INTERVAL,DISABLE
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
	public final long getWifiInterval() {
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
	public final long getBtInterval() {
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
	public final void setWifiInterval(long wifiInterval) {
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
	public final void setBtInterval(long btInterval) {
		this.btInterval = btInterval;
	}

	/**
	 * @param btDisable the btDisable to set
	 */
	public final void setBtDisable(boolean btDisable) {
		this.btDisable = btDisable;
	}

	/**
	 * @return the wifiOn
	 */
	public final boolean isWifiOn() {
		return wifiOn;
	}

	/**
	 * @return the btOn
	 */
	public final boolean isBtOn() {
		return btOn;
	}

	/**
	 * @param wifiOn the wifiOn to set
	 */
	public final void setWifiOn(boolean wifiOn) {
		this.wifiOn = wifiOn;
	}

	/**
	 * @param btOn the btOn to set
	 */
	public final void setBtOn(boolean btOn) {
		this.btOn = btOn;
	}

}
