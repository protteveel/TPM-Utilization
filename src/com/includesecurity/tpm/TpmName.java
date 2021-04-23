package com.includesecurity.tpm;

//List of TPM names to include
public enum TpmName {
	
	DESIREE("Desiree Dewysocki"),
	DEV("Gurdev Deol"),
	EDEN("Eden Ben-Benjamin"),
	ERIK("Erik Cabetas"),
	JOEL("Joel Patterson"),
	KENSINGTON("Kensington Moore");

	// The value of the enumeration
	private String value = "";

	// The constructor for the enumeration
 TpmName( String value ) {
     this.value = value;
 }

 // Returns the enumeration as a string
 public String getValue() {
     return this.value;
 }

 // Creates an enumeration from a string
 public static TpmName typeFromString( String value ) {
     if((value == null) || (value.length() == 0)) {
         return null;
     } else if( value.equals( "Desiree Dewysocki" ) ) {
         return TpmName.DESIREE;
     } else if( value.equals( "Gurdev Deol" ) ) {
         return TpmName.DEV;
     } else if( value.equals( "Eden Ben-Benjamin" ) ) {
         return TpmName.EDEN;
     } else if( value.equals( "Erik Cabetas" ) ) {
         return TpmName.ERIK;
     } else if( value.equals( "Joel Patterson" ) ) {
         return TpmName.JOEL;
     } else if( value.equals( "Kensington Moore" ) ) {
         return TpmName.KENSINGTON;
     } else {
         return null;
     }
 }
}