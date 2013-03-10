package com.travelsmartlondon.entry;

public class CommentEntry {
	
	private String _comment; 
	private String _userName;
	
	public CommentEntry(String userName, String comment){
		this._comment = comment;
		this._userName = userName;
	}
	
	public String getUserID(){
		return this._userName;
	}
	
	public String getComment(){
		return this._comment;
	}

}
