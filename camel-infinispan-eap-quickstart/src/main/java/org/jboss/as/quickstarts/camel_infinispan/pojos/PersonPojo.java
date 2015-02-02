package org.jboss.as.quickstarts.camel_infinispan.pojos;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class PersonPojo {
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	private String id;
	private String firstName; 
	private String lastName;
}
