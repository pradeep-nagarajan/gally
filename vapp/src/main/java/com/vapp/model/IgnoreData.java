package com.vapp.model;

public class IgnoreData {
	private String ledger;
	private String newLedger;
	private String mode;
	public String getLedger() {
		return ledger;
	}
	public void setLedger(String ledger) {
		this.ledger = ledger;
	}
	public String getNewLedger() {
		return newLedger;
	}
	public void setNewLedger(String newLedger) {
		this.newLedger = newLedger;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	@Override
	public String toString() {
		return "IgnoreData [ledger=" + ledger + ", newLedger=" + newLedger
				+ ", mode=" + mode + "]";
	}
}
