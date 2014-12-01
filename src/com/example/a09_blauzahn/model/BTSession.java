package com.example.a09_blauzahn.model;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.example.a09_blauzahn.util.DBHelper;

public class BTSession {

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
	 * a comma-separated {@link String} containing
	 * the names (not the addresses!) of the sighted
	 * devices during this bluetooth session.
	 * @return {@link String}
	 */
	public final String getBTSightingsNames() {
		StringBuffer s = new StringBuffer();
		if (btSightings != null) {
			Iterator<BTSighting> iterator = btSightings.iterator();
			while (iterator.hasNext()) {
				BTSighting sighting = iterator.next();
				String name = sighting.getName();
				if (name == null) {
					s.append(DBHelper.NULL_VALUE);
				} else {
					s.append("\'")
					.append(name)
					.append("\'");
				}
				if (iterator.hasNext()) {
					s.append(", ");
				}
			}
		}
		return s.toString();
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
