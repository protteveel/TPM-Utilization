package com.includesecurity.tpm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MlProject {
	
	// Position of the project name field, within the line of text
	private static final int NAME_IDX = 0;
	// Position of the project status field, within the line of text
	private static final int STATUS_IDX = 1;
	// Position of the project start date field, within the line of text
	private static final int START_DATE_IDX = 2;
	// Position of the project due date field, within the line of text
	private static final int DUE_DATE_IDX = 3;
	// Position of the project lead field, within the line of text
	private static final int LEAD_IDX = 6;
	
	// Workspace ID
	String workspaceId = "";
	// Project name
	String projectName = "";
	// Project status
	ProjectStatus status = null;
	// Project start date
	Date startDate = null;
	// Project due date
	Date dueDate = null;
	// Number of work days
	int workDays = 0;
	// Price of the project
	String price = "";
	// Project lead
	TpmName leadTpm = null;
	// Number of Consultants
	int consultants = 0;
	// Number of assessment days
	int assessmentDays = 0;
	// Consultant complexity factor
	double consultantFactor = 0.0;
	// Client complexity factor
	double clientFactor = 0.0;
	// Project complexity factor
	double complexityFactor = 0.0;
	
	// Tries to convert a string to a date
	private Date strToDate(String input) {
		// Default return value
		Date retVal = null;
		// Do we have an input?
		if((input != null) && (input.length() > 0)) {
			// Try to convert the string to a date
			try {
				retVal = new SimpleDateFormat("dd-MMM-yy").parse(input);
			} catch (ParseException e) {
				// If the string cannot be converted to a date, 
				//   the function returns null, which be handles by the calling function
			}  
		}
		// Return the result
		return retVal;
	}
	
	// Create ML project on actual values
	MlProject(String newWorkspaceId, String newProjectName, String newStatus, Date newStartDate, Date newDueDate, int newWorkDays, String newPrice, String newLeadTPM, int newConsultants, int newAssessmentDays, double newConsultantFactor, double newClientFactor, double newComplexityFactor) {
		// Copy workspace ID
		this.workspaceId = newWorkspaceId;
		// Copy project name
		this.projectName = newProjectName;
		// Copy project status
		this.status = ProjectStatus.typeFromString(newStatus);
		// Copy project start date
		this.startDate = newStartDate;
		// Copy project due date
		this.dueDate = newDueDate;
		// Copy number of work days
		this.workDays = newWorkDays;
		// Copy price of the project
		this.price = newPrice;
		// Copy project lead
		this.leadTpm = TpmName.typeFromString(newLeadTPM);
		// Copy number of Consultants
		this.consultants = newConsultants;
		// Copy number of assessment days
		this.assessmentDays = newAssessmentDays;
		// Copy consultant complexity factor
		this.consultantFactor = newConsultantFactor;
		// Copy client complexity factor
		this.clientFactor = newClientFactor;
		// Copy project complexity factor
		this.complexityFactor = newComplexityFactor;
	}
	
	// Create ML project based on one line of text
	MlProject(String input) {
		// Keep track we have all the info
		boolean allIsGood = false;
		// Do we have a line of text?
		if((input != null) && (input.length() > 0)) {
			// Split the fields, based on tabs
			String fields[] = input.split("\t");
			// Do we have the minimum number of fields
			if(fields.length > LEAD_IDX) {
				// Try to get a project status
				this.status = ProjectStatus.typeFromString(fields[STATUS_IDX]);
				// Do we have a project status?
				if(this.status != null) {
					// Try to get the project lead
					this.leadTpm = TpmName.typeFromString(fields[LEAD_IDX]);
					// Do we have a project lead?
					if(this.leadTpm != null) {
						// Try to get the project start date
						this.startDate = strToDate(fields[START_DATE_IDX]);
						// Do we have a project start date?
						if(this.startDate != null) {
							// Try to get the project due date
							this.dueDate = strToDate(fields[DUE_DATE_IDX]);
							// Do we have a project due date?
							if(this.dueDate != null) {
								// All is good!
								allIsGood = true;
								// Get the project name
								this.projectName = fields[NAME_IDX];
							}
						}
					}
				}
			}
		}
		// Do not we have all the info?
		if(!allIsGood) {
			// Set the project status to null
			this.status = null;
		}
	}

}
