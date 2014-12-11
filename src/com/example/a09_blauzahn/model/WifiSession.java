package com.example.a09_blauzahn.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class WifiSession
implements Serializable {

	/** default serial id. */
	private static final long serialVersionUID = 1L;

	private long id;
	private Date start;
	private Date stop;
	private List<WifiSighting> wifiSightings;

	/* Constructor. */
	public WifiSession(
		long id, Date start, Date stop,
		List<WifiSighting> wifiSightings
	) {
		super();
		this.id = id;
		this.start = start;
		this.stop = stop;
		this.wifiSightings = wifiSightings;
	}

	/**
	 * @return the serialversionuid
	 */
	public static final long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the id
	 */
	public final long getId() {
		return id;
	}

	/**
	 * @return the start
	 */
	public final Date getStart() {
		return start;
	}

	/**
	 * @return the stop
	 */
	public final Date getStop() {
		return stop;
	}

	/**
	 * @return the wifiSightings
	 */
	public final List<WifiSighting> getWifiSightings() {
		return wifiSightings;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(long id) {
		this.id = id;
	}

	/**
	 * @param start the start to set
	 */
	public final void setStart(Date start) {
		this.start = start;
	}

	/**
	 * @param stop the stop to set
	 */
	public final void setStop(Date stop) {
		this.stop = stop;
	}

	/**
	 * @param wifiSightings the wifiSightings to set
	 */
	public final void setWifiSightings(List<WifiSighting> wifiSightings) {
		this.wifiSightings = wifiSightings;
	}
}
