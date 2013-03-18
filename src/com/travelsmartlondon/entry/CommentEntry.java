package com.travelsmartlondon.entry;

public class CommentEntry {
	
	private String _comment; 
	private String _userName;
	private String _type;
	
	public CommentEntry(String comment_, String type_){
		this._comment = comment_;
		this._type = type_;
	}
	
	public void setUserName(String userName_){
		this._userName = userName_;
	}
	
	public String getUserID(){
		return this._userName;
	}
	
	public String getComment(){
		return this._comment;
	}
	
	public String getType(){
		return this._type;
	}

}
