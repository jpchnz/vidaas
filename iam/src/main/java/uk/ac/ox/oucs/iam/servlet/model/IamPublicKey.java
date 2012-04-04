package uk.ac.ox.oucs.iam.servlet.model;

public class IamPublicKey {
	private int id;
	private String uuid;
	private String ownerIp;
	private String actualKey;
	
	public IamPublicKey(){}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOwnerIp() {
		return ownerIp;
	}

	public void setOwnerIp(String ownerIp) {
		this.ownerIp = ownerIp;
	}

	public String getActualKey() {
		return actualKey;
	}

	public void setActualKey(String actualKey) {
		this.actualKey = actualKey;
	}
}
