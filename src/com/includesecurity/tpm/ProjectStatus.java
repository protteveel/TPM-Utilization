package com.includesecurity.tpm;

// List of project statuses to include
public enum ProjectStatus {
	
	COMPLETED("Completed"),
	DELIVERED("Delivered"),
	PROGRESS("In Progress"),
	HOLD("On Hold"),
	SETUP("In Setup");

	// The value of the enumeration
	private String value = "";

	// The constructor for the enumeration
    ProjectStatus( String value ) {
        this.value = value;
    }

    // Returns the enumeration as a string
    public String getValue() {
        return this.value;
    }

    // Creates an enumeration from a string
    public static ProjectStatus typeFromString( String value ) {
        if((value == null) || (value.length() == 0)) {
            return null;
        } else if( value.equals( "Completed" ) ) {
            return ProjectStatus.COMPLETED;
        } else if( value.equals( "Delivered" ) ) {
            return ProjectStatus.DELIVERED;
        } else if( value.equals( "In Progress" ) ) {
            return ProjectStatus.PROGRESS;
        } else if( value.equals( "On Hold" ) ) {
            return ProjectStatus.HOLD;
        } else if( value.equals( "In Setup" ) ) {
            return ProjectStatus.SETUP;
        } else {
            return null;
        }
    }
}
