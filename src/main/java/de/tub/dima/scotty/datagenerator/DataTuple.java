package de.tub.dima.scotty.datagenerator;

/**
 * Created by philipp on 5/24/17.
 */
public class DataTuple {

	public String key;
	public String value;
	public Long eventTime;
	public Long processTime;

	public DataTuple(final String key, final String value, final Long time) {
		this.key = key;
		this.value = value;
		this.eventTime = time;
		this.processTime = time;

	}
}
