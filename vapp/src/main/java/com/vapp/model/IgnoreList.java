package com.vapp.model;

import java.io.Serializable;
import java.util.List;

public class IgnoreList implements Serializable {
	private List<String> ignoreList;

	public List<String> getIgnoreList() {
		return ignoreList;
	}

	public void setIgnoreList(List<String> ignoreList) {
		this.ignoreList = ignoreList;
	}
	
}
