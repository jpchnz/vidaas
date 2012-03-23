package uk.ac.ox.oucs.vidaasBilling.model;

import java.util.Date;



public class Invoice implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String recipientEmail;
	private Date invoiceDate;
	private int projectId;
	private int invoicedAmount;
	
	
	public Invoice(){}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getRecipientEmail() {
		return recipientEmail;
	}


	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}


	public Date getInvoiceDate() {
		return invoiceDate;
	}


	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}


	public int getProjectId() {
		return projectId;
	}


	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}


	public int getInvoicedAmount() {
		return invoicedAmount;
	}


	public void setInvoicedAmount(int invoicedAmount) {
		this.invoicedAmount = invoicedAmount;
	}
}
