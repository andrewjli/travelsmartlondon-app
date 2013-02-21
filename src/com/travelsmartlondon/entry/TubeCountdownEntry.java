package com.travelsmartlondon.entry;

/**
 * TubeCountdownEntryrepresenting an entry in a Tube station count down table.
 * Immutable entry class providing getters for each property.
 * @author kamilprzekwas
 *
 */
public class TubeCountdownEntry {
	private String _destination;
	private String _timeRemaining;
	private String _line;
	
	public TubeCountdownEntry(String destination_, String timeRemaining_, String line_) {
		this._destination = destination_;
		this._timeRemaining = timeRemaining_;
		this._line = line_;
	}
	
	public String getDestination() {
		return this._destination;
	}
	
	public String getTimeRemaining() {
		return this._timeRemaining;
	}
	
	public String getLine() {
		return this._line;
	}
}
