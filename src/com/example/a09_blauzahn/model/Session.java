package com.example.a09_blauzahn.model;

import java.util.Date;

public class Session {

	private long id;
	private Date start;
	private Date stop;

	/** Constructor. */
	public Session(long id, Date start, Date stop) {
		super();
		this.id = id;
		this.start = start;
		this.stop = stop;
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

	
}
