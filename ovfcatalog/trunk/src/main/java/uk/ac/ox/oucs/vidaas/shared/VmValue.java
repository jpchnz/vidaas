package uk.ac.ox.oucs.vidaas.shared;

import java.io.Serializable;
import java.util.Collection;

public class VmValue implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer status;
	private String[] ipAddresses;
	//private String[] files;
	boolean createable = false;
	private Integer busy;
	
	public VmValue() {

	}
	
	public VmValue(String name, Integer status, Collection<String> ipAddresses, int busy) {
		this.name = name;
		this.status = status;
		if(ipAddresses != null) {
			this.ipAddresses = new String[ipAddresses.size()];
			int i = 0;
			for(String ip : ipAddresses) {
				this.ipAddresses[i] = ip;
			}
		}
		this.busy = new Integer(busy);
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
	
	/*public String[] getFiles() {
		return files;
	}*/
	
	public boolean isCreateable() {
		return createable;
	}
	
	public void setCreateable(boolean b) {
		createable = b;
	}

	public boolean isStoppable() {
		// TODO find out about status constants
		if(status==null) return false;
		return status==4;
	}
	
	public boolean isStartable() {
		// TODO find out about status constants
		if(status==null) return false;
		return status==8;
	}
	
	public Integer isBusy() {
		return busy;
	}

}
