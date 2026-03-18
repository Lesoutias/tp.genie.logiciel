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

    opens com.groupe2.project to javafx.graphics;
    exports com.groupe2.project;
}