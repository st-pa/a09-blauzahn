package com.example.a09_blauzahn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BTSession
implements Serializable {

	/** default serial id. */
	private static final long serialVersionUID = 1L;

	private long id;
	private Date start;
	private Date stop;
	private List<BTSighting> btSightings;

	/**
	 * Constructor.
	 * @param id {@link Long}
	 * @param start {@link Date}
	 * @param stop {@link Date}
	 * @param btSightings {@link List}<Sighting> optional List of bluetooth sightings
	 */
	public BTSession(long id, Date start, Date stop, List<BTSighting> btSightings) {
		super();
		this.id = id;
		this.start = start;
		this.stop = stop;
		this.btSightings = btSightings;
	}

	/**
	 * @return the id
	 */
	public final long getId() {
		return id;
	}

	/**
	 * @return the time of start
	 */
	public final Date getStart() {
		return start;
	}

	/**
	 * @return the time of stop
	 */
	public final Date getStop() {
		return stop;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(long id) {
		this.id = id;
	}

	/**
	 * @param start the time of start to set
	 */
	public final void setStart(Date start) {
		this.start = start;
	}

	/**
	 * @param stop the time of stop to set
	 */
	public final void setStop(Date stop) {
		this.stop = stop;
	}

	/**
	 * @return the number of bluetooth sightings
	 */
	public final long getBTSightingsCount() {
		return (btSightings == null) ? 0 : btSightings.size();
	}

	/**
	 * the names (not the addresses!) of the sighted
	 * devices during this bluetooth session.
	 * @return {@link List}<{@link String}>
	 */
	public final List<String> getBTSightingsNames() {
		List<String> list = new ArrayList<String>();
		if (btSightings != null) {
			for (BTSighting sighting : btSightings) {
				list.add(sighting.getName());
			}
		}
		return list;
	}

	/**
	 * @param btSightings the bluetooth sightings to set
	 */
	public final void setBTSightings(List<BTSighting> btSightings) {
		this.btSightings = btSightings;
	}

	/** gives {@link #stop}-time minus {@link #start}-time. */
	public final long getDuration() {
		return stop.getTime() - start.getTime();
	}
}
