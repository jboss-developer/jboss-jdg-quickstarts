package org.infinispan.quickstart.securecache;

import java.security.Principal;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.infinispan.security.PrincipalRoleMapper;
import org.infinispan.security.PrincipalRoleMapperContext;

public class JAASPrincipalRoleMapper implements PrincipalRoleMapper {
	
	public Set<String> principalToRoles(Principal principal) {
		if(principal instanceof SimpleGroup) {
			SimpleGroup sg = (SimpleGroup) principal;
			@SuppressWarnings("rawtypes")
			Enumeration members = sg.members();
			HashSet<String> roles = new HashSet<String>();
			while(members.hasMoreElements()) {
				Object obj = members.nextElement();
				if(obj instanceof SimplePrincipal) {
					roles.add(((SimplePrincipal) obj).getName());
				}
			}
			return roles;
		} else {
			return null;
		}
	}

	public void setContext(PrincipalRoleMapperContext context) {

	}

}
