package com.thong.youtube.entity;

import java.io.Serializable;

import com.google.api.client.util.DateTime;

public class PlayListInfoEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String privacy;
	private String description;
	private DateTime postedTime;
	private String chanel;
	
	public PlayListInfoEntity() {
		super();
	}

	public PlayListInfoEntity(String id, String name, String privacy, String description, DateTime postedTime,
			String chanel) {
		super();
		this.id = id;
		this.name = name;
		this.privacy = privacy;
		this.description = description;
		this.postedTime = postedTime;
		this.chanel = chanel;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrivacy() {
		return privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DateTime getPostedTime() {
		return postedTime;
	}

	public void setPostedTime(DateTime postedTime) {
		this.postedTime = postedTime;
	}

	public String getChanel() {
		return chanel;
	}

	public void setChanel(String chanel) {
		this.chanel = chanel;
	}
	
}
