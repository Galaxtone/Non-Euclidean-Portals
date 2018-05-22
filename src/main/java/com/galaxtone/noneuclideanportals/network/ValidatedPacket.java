package com.galaxtone.noneuclideanportals.network;

public class ValidatedPacket {

	private String reason;
	private boolean valid;

	protected void setValid() {
		this.valid = true;
	}

	protected void setInvalid(String reason, Object... arguments) {
		this.reason = String.format(reason, arguments);
		this.valid = false;
	}

	public String getReason() {
		return this.reason;
	}

	public boolean isValid() {
		return this.valid;
	}
}
