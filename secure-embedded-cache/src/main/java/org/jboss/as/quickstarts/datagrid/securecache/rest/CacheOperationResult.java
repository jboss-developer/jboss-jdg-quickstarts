package org.jboss.as.quickstarts.datagrid.securecache.rest;

import java.util.ArrayList;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class CacheOperationResult<T> {
	
	private ArrayList<T> outputEntries;
	private Boolean failed = false;
	private String failureMessage = ""; 
	
	public ArrayList<T> getOutputEntries() {
		return outputEntries;
	}
	public void setOutputEntries(ArrayList<T> outputEntries) {
		this.outputEntries = outputEntries;
	}

	public String getFailureMessage() {
		return failureMessage;
	}
	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}
	public Boolean getFailed() {
		return failed;
	}
	public void setFailed(Boolean failed) {
		this.failed = failed;
	}
	
}