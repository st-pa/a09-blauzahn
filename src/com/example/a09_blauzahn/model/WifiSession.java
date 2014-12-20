package com.example.a09_blauzahn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.a09_blauzahn.AppBlauzahn;

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

	/** gives {@link #stop}-time minus {@link #start}-time. */
	public final long getDuration() {
		return stop.getTime() - start.getTime();
	}

	/**
	 * the names (not the addresses!) of the sighted
	 * devices during this wifi session.
	 * @return {@link List}<{@link String}>
	 */
	public final List<String> getWifiSightingsNames() {
		List<String> list = new ArrayList<String>();
		if (wifiSightings != null) {
			for (WifiSighting sighting : wifiSightings) {
				list.add(sighting.getSSID());
			}
		}
		return list;
	}

	/**
	 * @return the number of wifi sightings
	 */
	public int getWifiSightingsCount() {
		return (wifiSightings == null) ? 0 : wifiSightings.size();
	}

	@Override
	public final String toString() {
		return AppBlauzahn.getDescription(this);
	}
}
