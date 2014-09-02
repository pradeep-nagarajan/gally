package com.vapp.model;

import java.math.BigDecimal;

public class MasterData {
	private String ledger;
	private String crDr;
	private BigDecimal amount;
	public String getLedger() {
		return ledger;
	}
	public void setLedger(String ledger) {
		this.ledger = ledger;
	}
	public String getCrDr() {
		return crDr;
	}
	public void setCrDr(String crDr) {
		this.crDr = crDr;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "MasterData [ledger=" + ledger + ", crDr=" + crDr + ", amount="
				+ amount + "]";
	}
}
