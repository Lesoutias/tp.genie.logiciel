package com.groupe2.project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class GestionPersonneUI extends Application {

    private BorderPane root;

    // PERSONNE UI
    private TableView<Personne> tablePersonne;
    private TableView<Telephone> tableTelephonesPersonne;

    private TextField txtId;
    private TextField txtNom;
    private TextField txtPostNom;
    private TextField txtPrenom;
    private ComboBox<Sexe> cbSex;

    // TELEPHONE UI
    private TableView<Telephone> tableTelephone;
    private TextField txtTelId;
    private TextField txtTelType;
    private TextField txtTelNumero;
    private TextField txtTelPersonneId;

    @Override
    public void start(Stage stage) {

        // ---------- INITIALIZE DATABASE CONNECTION ----------
        Connexion config = new Connexion("localhost", "gestion_personne", "root", "Lesoutils@1907", 3306);
        IConnexion factory = new ImplementeConnexion();
        DatabaseConnection.getInstance(config, ConnectionType.MYSQL, factory);

        // ---------- ROOT LAYOUT ----------
        root = new BorderPane();
        root.setTop(buildMenuBar());
        root.setCenter(buildPersonneUI());

        Scene scene = new Scene(root, 1100, 650);
        stage.setTitle("Gestion des Entités");
        stage.setScene(scene);
        stage.show();
    }

    // ---------------------------------------------------------
    // MENU BAR
    // ---------------------------------------------------------
    private MenuBar buildMenuBar() {
        Menu menu = new Menu("Navigation");

        MenuItem personneItem = new MenuItem("Personnes");
        MenuItem telephoneItem = new MenuItem("Téléphones");
        MenuItem adresseItem = new MenuItem("Adresses");
        MenuItem personneAdresseItem = new MenuItem("Personne Adresse");

        personneItem.setOnAction(e -> root.setCenter(buildPersonneUI()));
        telephoneItem.setOnAction(e -> root.setCenter(buildTelephoneUI()));
        adresseItem.setOnAction(e -> root.setCenter(buildAdresseUI()));
        personneAdresseItem.setOnAction(e -> root.setCenter(buildPersonneAdresseUI()));


        menu.getItems().addAll(personneItem, telephoneItem, adresseItem, personneAdresseItem);

        return new MenuBar(menu);
    }

    // ---------------------------------------------------------
    // PERSONNE UI
    // ---------------------------------------------------------
    private VBox buildPersonneUI() {

        txtId = new TextField();
        txtNom = new TextField();
        txtPostNom = new TextField();
        txtPrenom = new TextField();
        cbSex = new ComboBox<>();
        cbSex.getItems().addAll(Sexe.MASCULIN, Sexe.FEMININ);

        txtId.setPromptText("ID");
        txtNom.setPromptText("Nom");
        txtPostNom.setPromptText("Postnom");
        txtPrenom.setPromptText("Prénom");
        cbSex.setPromptText("Sexe");

        HBox form = new HBox(10, txtNom, txtPostNom, txtPrenom, cbSex);
        form.setPadding(new Insets(10));
        form.setAlignment(Pos.CENTER);

        Button btnSave = new Button("Enregistrer");
        Button btnDelete = new Button("Supprimer");
        Button btnRefresh = new Button("Actualiser");

        btnSave.setOnAction(e -> savePersonne());
        btnDelete.setOnAction(e -> deletePersonne());
        btnRefresh.setOnAction(e -> loadPersonnes());

        HBox buttons = new HBox(10, btnSave, btnDelete, btnRefresh);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));

        tablePersonne = new TableView<>();

        TableColumn<Personne, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Personne, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Personne, String> colPostNom = new TableColumn<>("Postnom");
        colPostNom.setCellValueFactory(new PropertyValueFactory<>("postNom"));

        TableColumn<Personne, String> colPrenom = new TableColumn<>("Prenom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableColumn<Personne, Sexe> colSex = new TableColumn<>("Sexe");
        colSex.setCellValueFactory(new PropertyValueFactory<>("sex"));

        tablePersonne.getColumns().addAll(colId, colNom, colPostNom, colPrenom, colSex);

        // TABLE DES TELEPHONES DE LA PERSONNE
        tableTelephonesPersonne = new TableView<>();

        TableColumn<Telephone, String> colNum = new TableColumn<>("Numéro");
        colNum.setCellValueFactory(new PropertyValueFactory<>("numero"));

        TableColumn<Telephone, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("initial"));

        tableTelephonesPersonne.getColumns().addAll(colNum, colType);

        // Listener : quand on clique une personne → charger ses téléphones
        tablePersonne.setOnMouseClicked(e -> {
            Personne selected = tablePersonne.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadTelephonesForPersonne(selected.getId());
            }
        });

        loadPersonnes();

        VBox layout = new VBox(10,
                form,
                buttons,
                new Label("Liste des personnes"),
                tablePersonne,
                new Label("Téléphones de la personne sélectionnée"),
                tableTelephonesPersonne
        );

        layout.setPadding(new Insets(15));
        return layout;
    }

    // ---------------------------------------------------------
    // TELEPHONE UI
    // ---------------------------------------------------------
    
    private ComboBox<Personne> cbTelPersonne;

    private VBox buildTelephoneUI() {

        txtTelId = new TextField();
        txtTelType = new TextField();
        txtTelNumero = new TextField();
        txtTelPersonneId = new TextField();
        
        cbTelPersonne = new ComboBox<>();
        cbTelPersonne.setPromptText("Sélectionner une personne");

        // Charger les personnes
        Personne p = new Personne();
        List<IPersonne> personnes = p.Personnes();

        for (IPersonne pers : personnes) {
            cbTelPersonne.getItems().add((Personne) pers);
        }

        // Affichage lisible
        cbTelPersonne.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Personne item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom() + " " + item.getPostNom());
            }
        });

        cbTelPersonne.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Personne item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom() + " " + item.getPostNom());
            }
        });

        

        txtTelId.setPromptText("ID");
        txtTelType.setPromptText("Type");
        txtTelNumero.setPromptText("Numéro");
        txtTelPersonneId.setPromptText("ID Personne");

        HBox form = new HBox(10, txtTelType, txtTelNumero, cbTelPersonne);
        form.setPadding(new Insets(10));
        form.setAlignment(Pos.CENTER);

        Button btnSave = new Button("Enregistrer");
        Button btnDelete = new Button("Supprimer");
        Button btnRefresh = new Button("Actualiser");

        btnSave.setOnAction(e -> saveTelephone());
        btnDelete.setOnAction(e -> deleteTelephone());
        btnRefresh.setOnAction(e -> loadTelephones());

        HBox buttons = new HBox(10, btnSave, btnDelete, btnRefresh);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));

        tableTelephone = new TableView<>();

        TableColumn<Telephone, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Telephone, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("initial"));

        TableColumn<Telephone, String> colNumero = new TableColumn<>("Numéro");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));

        TableColumn<Telephone, Integer> colPersonne = new TableColumn<>("Personne ID");
        colPersonne.setCellValueFactory(new PropertyValueFactory<>("idProprietaire"));

        tableTelephone.getColumns().addAll(colId, colNumero, colType, colPersonne);

        loadTelephones();

        VBox layout = new VBox(10, form, buttons, tableTelephone);
        layout.setPadding(new Insets(15));

        return layout;
    }

    // ---------------------------------------------------------
    // CRUD PERSONNE
    // ---------------------------------------------------------
    private void savePersonne() {
        try {
        	
        	Personne p = new Personne();

       	 	int newId = p.getLastId() + 1;
            Personne pers = new Personne(
                    newId,
                    txtNom.getText(),
                    txtPostNom.getText(),
                    txtPrenom.getText(),
                    cbSex.getValue()
            );

            pers.Enregistrer(pers);
            loadPersonnes();

        } catch (Exception ex) {
            showError("Erreur lors de l'enregistrement : " + ex.getMessage());
        }
    }

    private void deletePersonne() {
	    try {
	        Personne selected = tablePersonne.getSelectionModel().getSelectedItem();

	        if (selected == null) {
	            showError("Veuillez sélectionner une adresse dans la liste.");
	            return;
	        }

	        Personne p = new Personne();
	        p.Supprimer(selected.getId());
	        loadAdresses();

	    } catch (Exception ex) {
	        showError("Erreur lors de la suppression : " + ex.getMessage());
	    }
	}

    private void loadPersonnes() {
        try {
            Personne p = new Personne();
            List<IPersonne> list = p.Personnes();

            tablePersonne.getItems().clear();
            for (IPersonne pers : list) {
                tablePersonne.getItems().add((Personne) pers);
            }

        } catch (Exception ex) {
            showError("Erreur lors du chargement : " + ex.getMessage());
        }
    }

    private void loadTelephonesForPersonne(int personneId) {
        try {
            Telephone t = new Telephone();
            List<ITelephone> list = t.TelephonePersonne(personneId);

            tableTelephonesPersonne.getItems().clear();
            for (ITelephone tel : list) {
                tableTelephonesPersonne.getItems().add((Telephone) tel);
            }

        } catch (Exception ex) {
            showError("Erreur lors du chargement des téléphones : " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    // CRUD TELEPHONE
    // ---------------------------------------------------------
    private void saveTelephone() {
        try {
            // Récupérer la personne sélectionnée dans le ComboBox
            Personne selectedPerson = cbTelPersonne.getValue();

            if (selectedPerson == null) {
                showError("Veuillez sélectionner une personne.");
                return;
            }

            // Générer le nouvel ID
            Telephone t = new Telephone();
            int newId = t.getLastId() + 1;

            // Créer le téléphone
            Telephone tel = new Telephone(
                    newId,
                    txtTelType.getText(),      // initial
                    txtTelNumero.getText(),    // numero
                    selectedPerson.getId()     // idProprietaire
            );

            tel.Enregistrer(tel);
            loadTelephones();

        } catch (Exception ex) {
            showError("Erreur lors de l'enregistrement : " + ex.getMessage());
        }
    }


    private void deleteTelephone() {
    	try {
	        Telephone selected = tableTelephone.getSelectionModel().getSelectedItem();

	        if (selected == null) {
	            showError("Veuillez sélectionner une adresse dans la liste.");
	            return;
	        }

	        Telephone t = new Telephone();
	        t.Supprimer(selected.getId());
	        loadAdresses();

	    } catch (Exception ex) {
	        showError("Erreur lors de la suppression : " + ex.getMessage());
	    }
    }

    private void loadTelephones() {
        try {
            Telephone t = new Telephone();
            List<ITelephone> list = t.Telephones();

            tableTelephone.getItems().clear();
            for (ITelephone tel : list) {
                tableTelephone.getItems().add((Telephone) tel);
            }

        } catch (Exception ex) {
            showError("Erreur lors du chargement : " + ex.getMessage());
        }
    }
    
 // ---------------------------------------------------------
 // ADRESSE UI
 // ---------------------------------------------------------
 private TableView<Adresse> tableAdresse;
 private TextField txtAdrQuartier;
 private TextField txtAdrCommune;
 private TextField txtAdrVille;
 private TextField txtAdrPays;
 private TextField txtAdrId;

 private VBox buildAdresseUI() {

     txtAdrQuartier = new TextField();
     txtAdrCommune = new TextField();
     txtAdrVille = new TextField();
     txtAdrPays = new TextField();
     txtAdrId = new TextField();

     txtAdrQuartier.setPromptText("Quartier");
     txtAdrCommune.setPromptText("Commune");
     txtAdrVille.setPromptText("Ville");
     txtAdrPays.setPromptText("Pays");

     HBox form = new HBox(10, txtAdrQuartier, txtAdrCommune, txtAdrVille, txtAdrPays);
     form.setPadding(new Insets(10));
     form.setAlignment(Pos.CENTER);

     Button btnSave = new Button("Enregistrer");
     Button btnDelete = new Button("Supprimer");
     Button btnRefresh = new Button("Actualiser");

     btnSave.setOnAction(e -> saveAdresse());
     btnDelete.setOnAction(e -> deleteAdresse());
     btnRefresh.setOnAction(e -> loadAdresses());

     HBox buttons = new HBox(10, btnSave, btnDelete, btnRefresh);
     buttons.setAlignment(Pos.CENTER);
     buttons.setPadding(new Insets(10));

     tableAdresse = new TableView<>();

     TableColumn<Adresse, Integer> colId = new TableColumn<>("ID");
     colId.setCellValueFactory(new PropertyValueFactory<>("id"));

     TableColumn<Adresse, String> colQuartier = new TableColumn<>("Quartier");
     colQuartier.setCellValueFactory(new PropertyValueFactory<>("quartier"));

     TableColumn<Adresse, String> colCommune = new TableColumn<>("Commune");
     colCommune.setCellValueFactory(new PropertyValueFactory<>("commune"));

     TableColumn<Adresse, String> colVille = new TableColumn<>("Ville");
     colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));

     TableColumn<Adresse, String> colPays = new TableColumn<>("Pays");
     colPays.setCellValueFactory(new PropertyValueFactory<>("pays"));

     tableAdresse.getColumns().addAll(colId, colQuartier, colCommune, colVille, colPays);

     loadAdresses();

     VBox layout = new VBox(10, form, buttons, tableAdresse);
     layout.setPadding(new Insets(15));

     return layout;
 }

 // CRUD Adresse
 private void saveAdresse() {
     try {
    	 Adresse a = new Adresse();

    	 int newId = a.getLastId() + 1;
    	 
         Adresse adr = new Adresse(
                 newId,
                 txtAdrQuartier.getText(),
                 txtAdrCommune.getText(),
                 txtAdrVille.getText(),
                 txtAdrPays.getText()
         );

         adr.Enregistrer(adr);
         loadAdresses();

     } catch (Exception ex) {
    	 ex.printStackTrace();
         showError("Erreur lors de l'enregistreme : " + ex.getMessage());
     }
 }

 private void deleteAdresse() {
	    try {
	        Adresse selected = tableAdresse.getSelectionModel().getSelectedItem();

	        if (selected == null) {
	            showError("Veuillez sélectionner une adresse dans la liste.");
	            return;
	        }

	        Adresse a = new Adresse();
	        a.Supprimer(selected.getId());
	        loadAdresses();

	    } catch (Exception ex) {
	        showError("Erreur lors de la suppression : " + ex.getMessage());
	    }
	}


 private void loadAdresses() {
     try {
         Adresse a = new Adresse();
         List<Adresse> list = a.Adresses();

         tableAdresse.getItems().clear();
         tableAdresse.getItems().addAll(list);

     } catch (Exception ex) {
         showError("Erreur lors du chargement : " + ex.getMessage());
     }
 }

//---------------------------------------------------------
//PERSONNE-ADRESSE UI (DOMICILE)
//---------------------------------------------------------
private TableView<PersonneAdresse> tablePersonneAdresse;
private TextField txtPAId;
private TextField txtPAIdPersonne;
private TextField txtPAIdAdresse;
private TextField txtPAAvenue;
private TextField txtPANumero;
private ComboBox<Adresse> cbTelPersonneAdresse;

private VBox buildPersonneAdresseUI() {

  txtPAId = new TextField();
  txtPAIdPersonne = new TextField();
  txtPAIdAdresse = new TextField();
  txtPAAvenue = new TextField();
  txtPANumero = new TextField();
  
  cbTelPersonne = new ComboBox<>();
  cbTelPersonne.setPromptText("Sélectionner une personne");

  // Charger les personnes
  Personne p = new Personne();
  List<IPersonne> personnes = p.Personnes();

  for (IPersonne pers : personnes) {
      cbTelPersonne.getItems().add((Personne) pers);
  }

  // Affichage lisible
  cbTelPersonne.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(Personne item, boolean empty) {
          super.updateItem(item, empty);
          setText(empty || item == null ? "" : item.getNom() + " " + item.getPostNom());
      }
  });

  cbTelPersonne.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(Personne item, boolean empty) {
          super.updateItem(item, empty);
          setText(empty || item == null ? "" : item.getNom() + " " + item.getPostNom());
      }
  });

  txtPAId.setPromptText("ID");
  txtPAIdPersonne.setPromptText("ID Personne");
  txtPAIdAdresse.setPromptText("ID Adresse");
  txtPAAvenue.setPromptText("Avenue");
  txtPANumero.setPromptText("Numéro");
  cbTelPersonneAdresse = new ComboBox<>();
  cbTelPersonneAdresse.setPromptText("Sélectionner l'adresse d'une personne");

  // Charger les adresses
  Adresse pa = new Adresse();
  List<Adresse> personnesAdresse = pa.Adresses();

  for (Adresse pers : personnesAdresse) {
      cbTelPersonneAdresse.getItems().add((Adresse) pers);
  }

  // Affichage lisible
  cbTelPersonneAdresse.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(Adresse item, boolean empty) {
          super.updateItem(item, empty);
          setText(empty || item == null ? "" : item.getQuartier());
      }
  });

  cbTelPersonneAdresse.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(Adresse item, boolean empty) {
          super.updateItem(item, empty);
          setText(empty || item == null ? "" : item.getQuartier());
      }
  });

  HBox form = new HBox(10, cbTelPersonne, cbTelPersonneAdresse, txtPAAvenue, txtPANumero);
  form.setPadding(new Insets(10));
  form.setAlignment(Pos.CENTER);

  Button btnSave = new Button("Enregistrer");
  Button btnDelete = new Button("Supprimer");

  btnSave.setOnAction(e -> savePersonneAdresse());
  btnDelete.setOnAction(e -> deletePersonneAdresse());

  HBox buttons = new HBox(10, btnSave, btnDelete);
  buttons.setAlignment(Pos.CENTER);
  buttons.setPadding(new Insets(10));

  tablePersonneAdresse = new TableView<>();

  TableColumn<PersonneAdresse, Integer> colId = new TableColumn<>("ID");
  colId.setCellValueFactory(new PropertyValueFactory<>("id"));

  TableColumn<PersonneAdresse, Integer> colPers = new TableColumn<>("ID Personne");
  colPers.setCellValueFactory(new PropertyValueFactory<>("idPersonne"));

  TableColumn<PersonneAdresse, Integer> colAdr = new TableColumn<>("ID Adresse");
  colAdr.setCellValueFactory(new PropertyValueFactory<>("idAdresse"));

  TableColumn<PersonneAdresse, String> colAvenue = new TableColumn<>("Avenue");
  colAvenue.setCellValueFactory(new PropertyValueFactory<>("avenue"));

  TableColumn<PersonneAdresse, Integer> colNum = new TableColumn<>("Numéro");
  colNum.setCellValueFactory(new PropertyValueFactory<>("numeroAvenue"));

  tablePersonneAdresse.getColumns().addAll(colId, colPers, colAdr, colAvenue, colNum);

  VBox layout = new VBox(10, form, buttons, tablePersonneAdresse);
  layout.setPadding(new Insets(15));

  loadPersonneAdresse();
  
  return layout;
}

//CRUD PersonneAdresse


private void savePersonneAdresse() {
  try {
	// Récupérer la personne sélectionnée dans le ComboBox
      Personne selectedPerson = cbTelPersonne.getValue();

      if (selectedPerson == null) {
          showError("Veuillez sélectionner une personne.");
          return;
      }

   // Récupérer l'adresse sélectionnée dans le ComboBox
      Adresse selectedPersonAdresse = cbTelPersonneAdresse.getValue();

      if (selectedPersonAdresse == null) {
          showError("Veuillez sélectionner une adresse d'une personne.");
          return;
      }
      
      // Générer le nouvel ID
      PersonneAdresse p = new PersonneAdresse();
      int newId = p.getLastId() + 1;
      PersonneAdresse pa = new PersonneAdresse(
              newId,
              selectedPerson.getId(),
              selectedPersonAdresse.getId(),
              txtPAAvenue.getText(),
              Integer.parseInt(txtPANumero.getText())
      );

      pa.Enregistrer(pa);

  } catch (Exception ex) {
	  ex.printStackTrace();
      showError("Erreur lors de l'enregistrement du domicile : " + ex.getMessage());
  }
}

private void deletePersonneAdresse() {
	try{
		PersonneAdresse selected = tablePersonneAdresse.getSelectionModel().getSelectedItem();

    if (selected == null) {
        showError("Veuillez sélectionner une adresse dans la liste.");
        return;
    }

    PersonneAdresse a = new PersonneAdresse();
    a.Supprimer(selected.getId());
    loadAdresses();

} catch (Exception ex) {
    showError("Erreur lors de la suppression : " + ex.getMessage());
}
}

private void loadPersonneAdresse() {
    try {
        PersonneAdresse pa = new PersonneAdresse();
        List<PersonneAdresse> list = pa.PersonnesAdresse();

        tablePersonneAdresse.getItems().clear();
        tablePersonneAdresse.getItems().addAll(list);

    } catch (Exception ex) {
        showError("Erreur lors du chargement : " + ex.getMessage());
    }
}

    // ---------------------------------------------------------
    // ERROR POPUP
    // ---------------------------------------------------------
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
