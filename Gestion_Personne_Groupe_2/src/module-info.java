/**
 * 
 */
/**
 * 
 */
module Gestion_Personne_Groupe_2 {
	requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires java.sql;
	requires org.apache.poi.poi;
	requires org.apache.poi.ooxml;

    opens com.groupe2.project to javafx.graphics;
    exports com.groupe2.project;
}