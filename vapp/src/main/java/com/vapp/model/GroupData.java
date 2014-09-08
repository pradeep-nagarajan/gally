package com.vapp.model;

public class GroupData {
	private String mainGroup;
	private String misGroup;
	private String ledger;
	private String mode;
	public String getMainGroup() {
		return mainGroup;
	}
	public void setMainGroup(String mainGroup) {
		this.mainGroup = mainGroup;
	}
	public String getMisGroup() {
		return misGroup;
	}
	public void setMisGroup(String misGroup) {
		this.misGroup = misGroup;
	}
	public String getLedger() {
		return ledger;
	}
	public void setLedger(String ledger) {
		this.ledger = ledger;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	@Override
	public String toString() {
		return "GroupData [mainGroup=" + mainGroup + ", misGroup=" + misGroup
				+ ", ledger=" + ledger + ", mode=" + mode + "]";
	}
	
}
