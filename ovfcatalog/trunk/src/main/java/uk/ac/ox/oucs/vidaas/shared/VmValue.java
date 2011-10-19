package uk.ac.ox.oucs.vidaas.shared;

import java.io.Serializable;
import java.util.Collection;

public class VmValue implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer status;
	private String[] ipAddresses;
	private String[] files;
	boolean createable = false;
	
	public VmValue() {

	}
	
	public VmValue(String name, Integer status, Collection<String> ipAddresses, Collection<String> files) {
		this.name = name;
		this.status = status;
		if(ipAddresses != null) {
			this.ipAddresses = new String[ipAddresses.size()];
			int i = 0;
			for(String ip : ipAddresses) {
				this.ipAddresses[i] = ip;
			}
		}
		if(files != null) {
			this.files = new String[files.size()];
			int i = 0;
			for(String file : files) {
				this.files[i] = file;
			}
		}
	}

	public String getName() {
		return name;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public String[] getIpAddresses() {
		return ipAddresses;
	}
	
	public String[] getFiles() {
		return files;
	}
	
	public boolean isCreateable() {
		return createable;
	}
	
	public void setCreateable(boolean b) {
		createable = b;
	}

}
