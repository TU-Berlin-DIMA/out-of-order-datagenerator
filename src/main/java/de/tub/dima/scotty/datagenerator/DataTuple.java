package de.tub.dima.scotty.datagenerator;

/**
 * Created by philipp on 5/24/17.
 */
public class DataTuple {

	public String key;
	public String[] values;
	public Long eventTime;
	public Long processTime;

	public DataTuple(final String key, final String[] values, final Long time) {
		this.key = key;
		this.values = values;
		this.eventTime = time;
		this.processTime = time;

	}
}
