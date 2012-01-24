package uk.ac.ox.oucs.iam.security.utilities;



/**
 * Helper class for a digital signature. This class extends the standard signature
 * by adding a timestamp so that messages over a certain age can be thrown out.
 * @author oucs0153
 *
 */
public class VidaasSignature {
	private String signature;
	private long timestamp = 0;
	private boolean timeStampInUse = false;
	
	
	public VidaasSignature(String signature, long timestamp) {
		this.signature = signature;
		this.timestamp = timestamp;
		if (timestamp > 0) {
			timeStampInUse = true;
		}
	}

	
	public String getTimestampedMessage(String message) {
		if (timeStampInUse) {
			return message + "_" + timestamp;
		}
		else {
			return message;
		}
	}
	

	public String getSignature() {
		return signature;
	}


	public boolean isTimeStampInUse() {
		return timeStampInUse;
	}


	public long getTimestamp() {
		return timestamp;
	}
}
