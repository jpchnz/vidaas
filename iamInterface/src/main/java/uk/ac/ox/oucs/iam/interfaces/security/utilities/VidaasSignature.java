package uk.ac.ox.oucs.iam.interfaces.security.utilities;



/**
 * Helper class for a digital signature. This class extends the standard signature
 * by adding a timestamp so that messages over a certain age can be thrown out.
 * @author oucs0153
 *
 */
public class VidaasSignature {
	private String signature;
	private String originalMessage;
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
			originalMessage = message + "_" + timestamp;
		}
		else {
			originalMessage = message;
		}
		return originalMessage;
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


	public String getOriginalMessage() {
		return originalMessage;
	}


	public void setOriginalMessage(String originalMessage) {
		this.originalMessage = getTimestampedMessage(originalMessage);
	}
}
