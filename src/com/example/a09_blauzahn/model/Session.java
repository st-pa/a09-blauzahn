package com.example.a09_blauzahn.model;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.example.a09_blauzahn.util.DBHelper;

public class Session {

	private long id;
	private Date start;
	private Date stop;
	private List<Sighting> sightings;

	/**
	 * Constructor.
	 * @param id {@link Long}
	 * @param start {@link Date}
	 * @param stop {@link Date}
	 * @param sightings {@link List}<Sighting> optional List of sightings
	 */
	public Session(long id, Date start, Date stop, List<Sighting> sightings) {
		super();
		this.id = id;
		this.start = start;
		this.stop = stop;
		this.sightings = sightings;
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
	 * @return the countSightings
	 */
	public final long getSightingsCount() {
		return (sightings == null) ? 0 : sightings.size();
	}

	/**
	 * a comma-separated {@link String} containing
	 * the names (not the addresses!) of the sighted
	 * devices during this session.
	 * @return {@link String}
	 */
	public final String getSightingsNames() {
		StringBuffer s = new StringBuffer();
		if (sightings != null) {
			Iterator<Sighting> iterator = sightings.iterator();
			while (iterator.hasNext()) {
				Sighting sighting = iterator.next();
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
	 * @param sightings the sightings to set
	 */
	public final void setSightings(List<Sighting> sightings) {
		this.sightings = sightings;
	}

	/** gives {@link #stop}-time minus {@link #start}-time. */
	public final long getDuration() {
		return stop.getTime() - start.getTime();
	}
}
