package com.groupe2.project;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Interface utilisateur pour la gestion des personnes, téléphones, adresses
 * et leurs relations.
 * 
 * @author Groupe2
 * @version 1.0
 */
public class GestionPersonneUI extends Application {

    // ============================================================
    // CONSTANTES
    // ============================================================
    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 650;
    private static final int SPACING_SMALL = 10;
    private static final int PADDING_STANDARD = 15;

    // ============================================================
    // COMPOSANTS PRINCIPAUX
    // ============================================================
    private BorderPane root;
    
    // ============================================================
    // COMPOSANTS PERSONNE
    // ============================================================
    private TableView<Personne> tablePersonne;
    private TableView<Telephone> tableTelephonesPersonne;
    private TextField txtId;
    private TextField txtNom;
    private TextField txtPostNom;
    private TextField txtPrenom;
    private ComboBox<Sexe> cbSex;

    // ============================================================
    // COMPOSANTS TELEPHONE
    // ============================================================
    private TableView<Telephone> tableTelephone;
    private TextField txtTelId;
    private TextField txtTelType;
    private TextField txtTelNumero;
    private TextField txtTelPersonneId;
    private ComboBox<Personne> cbTelPersonne;

    // ============================================================
    // COMPOSANTS ADRESSE
    // ============================================================
    private TableView<Adresse> tableAdresse;
    private TextField txtAdrQuartier;
    private TextField txtAdrCommune;
    private TextField txtAdrVille;
    private TextField txtAdrPays;
    private TextField txtAdrId;

    // ============================================================
    // COMPOSANTS PERSONNE-ADRESSE
    // ============================================================
    private TableView<PersonneAdresse> tablePersonneAdresse;
    private TextField txtPAId;
    private TextField txtPAIdPersonne;
    private TextField txtPAIdAdresse;
    private TextField txtPAAvenue;
    private TextField txtPANumero;
    private ComboBox<Adresse> cbAdressePersonne;

    // ============================================================
    // COMPOSANTS RAPPORT
    // ============================================================
    private TableView<RapportPersonne> tableRapport;

    // ============================================================
    // METHODES PRINCIPALES
    // ============================================================
    
    @Override
    public void start(Stage stage) {
        // Initialisation de la connexion à la base de données
        initializeDatabaseConnection();
        
        // Configuration de l'interface principale
        root = new BorderPane();
        root.setTop(buildMenuBar());
        root.setCenter(buildPersonneUI());

        // Configuration de la scène et affichage
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Gestion des Entités");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialise la connexion à la base de données MySQL.
     */
    private void initializeDatabaseConnection() {
        Connexion config = new Connexion("localhost", "gestion_personne", "root", "Lesoutils@1907", 3306);
        IConnexion factory = new ImplementeConnexion();
        DatabaseConnection.getInstance(config, ConnectionType.MYSQL, factory);
    }

    // ============================================================
    // CONSTRUCTION DE LA BARRE DE MENU
    // ============================================================
    
    /**
     * Construit la barre de menu de navigation.
     * 
     * @return MenuBar configurée
     */
    private MenuBar buildMenuBar() {
        Menu menu = new Menu("Navigation");

        // Création des éléments de menu
        MenuItem personneItem = createMenuItem("Personnes", e -> root.setCenter(buildPersonneUI()));
        MenuItem telephoneItem = createMenuItem("Téléphones", e -> root.setCenter(buildTelephoneUI()));
        MenuItem adresseItem = createMenuItem("Adresses", e -> root.setCenter(buildAdresseUI()));
        MenuItem personneAdresseItem = createMenuItem("Personne Adresse", e -> root.setCenter(buildPersonneAdresseUI()));
        MenuItem rapportItem = createMenuItem("Rapport", e -> root.setCenter(buildRapportUI()));

        menu.getItems().addAll(personneItem, telephoneItem, adresseItem, personneAdresseItem, rapportItem);
        return new MenuBar(menu);
    }

    /**
     * Crée un élément de menu avec son gestionnaire d'événement.
     * 
     * @param title Titre du menu
     * @param handler Gestionnaire d'événement
     * @return MenuItem configuré
     */
    private MenuItem createMenuItem(String title, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        MenuItem item = new MenuItem(title);
        item.setOnAction(handler);
        return item;
    }

    // ============================================================
    // INTERFACE PERSONNE
    // ============================================================
    
    /**
     * Construit l'interface de gestion des personnes.
     * 
     * @return VBox contenant l'interface personne
     */
    private VBox buildPersonneUI() {
        // Initialisation des champs de saisie
        initializePersonneFields();
        
        // Construction du formulaire
        HBox form = createPersonneForm();
        
        // Construction des boutons
        HBox buttons = createPersonneButtons();
        
        // Construction des tableaux
        initializePersonneTable();
        initializeTelephonesPersonneTable();
        
        // Chargement des données
        loadPersonnes();

        // Assemblage final
        return createPersonneLayout(form, buttons);
    }

    /**
     * Initialise les champs de saisie pour une personne.
     */
    private void initializePersonneFields() {
        txtId = new TextField();
        txtNom = createTextField("Nom");
        txtPostNom = createTextField("Postnom");
        txtPrenom = createTextField("Prénom");
        
        cbSex = new ComboBox<>();
        cbSex.getItems().addAll(Sexe.MASCULIN, Sexe.FEMININ);
        cbSex.setPromptText("Sexe");
    }

    /**
     * Crée un champ de texte avec un prompt.
     */
    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        return field;
    }

    /**
     * Crée le formulaire de saisie pour une personne.
     */
    private HBox createPersonneForm() {
        HBox form = new HBox(SPACING_SMALL, txtNom, txtPostNom, txtPrenom, cbSex);
        form.setPadding(new Insets(PADDING_STANDARD));
        form.setAlignment(Pos.CENTER);
        return form;
    }

    /**
     * Crée les boutons d'action pour la gestion des personnes.
     */
    private HBox createPersonneButtons() {
        Button btnSave = new Button("Enregistrer");
        Button btnDelete = new Button("Supprimer");
        Button btnRefresh = new Button("Actualiser");

        btnSave.setOnAction(e -> savePersonne());
        btnDelete.setOnAction(e -> deletePersonne());
        btnRefresh.setOnAction(e -> loadPersonnes());

        HBox buttons = new HBox(SPACING_SMALL, btnSave, btnDelete, btnRefresh);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(PADDING_STANDARD));
        return buttons;
    }

    /**
     * Initialise le tableau des personnes.
     */
    private void initializePersonneTable() {
        tablePersonne = new TableView<>();

        TableColumn<Personne, Integer> colId = createTableColumn("ID", "id", Integer.class);
        TableColumn<Personne, String> colNom = createTableColumn("Nom", "nom", String.class);
        TableColumn<Personne, String> colPostNom = createTableColumn("Postnom", "postNom", String.class);
        TableColumn<Personne, String> colPrenom = createTableColumn("Prenom", "prenom", String.class);
        TableColumn<Personne, Sexe> colSex = createTableColumn("Sexe", "sex", Sexe.class);

        tablePersonne.getColumns().addAll(colId, colNom, colPostNom, colPrenom, colSex);

        // Listener pour charger les téléphones de la personne sélectionnée
        tablePersonne.setOnMouseClicked(e -> {
            Personne selected = tablePersonne.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadTelephonesForPersonne(selected.getId());
            }
        });
    }

    /**
     * Initialise le tableau des téléphones d'une personne.
     */
    private void initializeTelephonesPersonneTable() {
        tableTelephonesPersonne = new TableView<>();

        TableColumn<Telephone, String> colNum = createTableColumn("Numéro", "numero", String.class);
        TableColumn<Telephone, String> colType = createTableColumn("Type", "initial", String.class);

        tableTelephonesPersonne.getColumns().addAll(colNum, colType);
    }

    /**
     * Crée une colonne de tableau typée.
     */
    private <S, T> TableColumn<S, T> createTableColumn(String title, String property, Class<T> type) {
        TableColumn<S, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    /**
     * Crée la disposition complète pour l'interface personne.
     */
    private VBox createPersonneLayout(HBox form, HBox buttons) {
        VBox layout = new VBox(SPACING_SMALL,
                form,
                buttons,
                new Label("Liste des personnes"),
                tablePersonne,
                new Label("Téléphones de la personne sélectionnée"),
                tableTelephonesPersonne
        );

        layout.setPadding(new Insets(PADDING_STANDARD));
        return layout;
    }

    // ============================================================
    // INTERFACE TELEPHONE
    // ============================================================
    
    /**
     * Construit l'interface de gestion des téléphones.
     */
    private VBox buildTelephoneUI() {
        initializeTelephoneFields();
        initializePersonneComboBox();
        
        HBox form = createTelephoneForm();
        HBox buttons = createTelephoneButtons();
        
        initializeTelephoneTable();
        loadTelephones();

        return createTelephoneLayout(form, buttons);
    }

    /**
     * Initialise les champs de saisie pour un téléphone.
     */
    private void initializeTelephoneFields() {
        txtTelId = new TextField();
        txtTelType = createTextField("Type");
        txtTelNumero = createTextField("Numéro");
        txtTelPersonneId = createTextField("ID Personne");
    }

    /**
     * Initialise la combobox des personnes.
     */
    private void initializePersonneComboBox() {
        cbTelPersonne = new ComboBox<>();
        cbTelPersonne.setPromptText("Sélectionner une personne");

        // Chargement des personnes
        Personne p = new Personne();
        List<IPersonne> personnes = p.Personnes();
        for (IPersonne pers : personnes) {
            cbTelPersonne.getItems().add((Personne) pers);
        }

        // Configuration de l'affichage
        configurePersonneComboBoxDisplay();
    }

    /**
     * Configure l'affichage de la combobox des personnes.
     */
    private void configurePersonneComboBoxDisplay() {
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
    }

    /**
     * Crée le formulaire de saisie pour un téléphone.
     */
    private HBox createTelephoneForm() {
        HBox form = new HBox(SPACING_SMALL, txtTelType, txtTelNumero, cbTelPersonne);
        form.setPadding(new Insets(PADDING_STANDARD));
        form.setAlignment(Pos.CENTER);
        return form;
    }

    /**
     * Crée les boutons d'action pour la gestion des téléphones.
     */
    private HBox createTelephoneButtons() {
        Button btnSave = new Button("Enregistrer");
        Button btnDelete = new Button("Supprimer");
        Button btnRefresh = new Button("Actualiser");

        btnSave.setOnAction(e -> saveTelephone());
        btnDelete.setOnAction(e -> deleteTelephone());
        btnRefresh.setOnAction(e -> loadTelephones());

        HBox buttons = new HBox(SPACING_SMALL, btnSave, btnDelete, btnRefresh);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(PADDING_STANDARD));
        return buttons;
    }

    /**
     * Initialise le tableau des téléphones.
     */
    private void initializeTelephoneTable() {
        tableTelephone = new TableView<>();

        TableColumn<Telephone, Integer> colId = createTableColumn("ID", "id", Integer.class);
        TableColumn<Telephone, String> colType = createTableColumn("Type", "initial", String.class);
        TableColumn<Telephone, String> colNumero = createTableColumn("Numéro", "numero", String.class);
        TableColumn<Telephone, Integer> colPersonne = createTableColumn("Personne ID", "idProprietaire", Integer.class);

        tableTelephone.getColumns().addAll(colId, colNumero, colType, colPersonne);
    }

    /**
     * Crée la disposition pour l'interface téléphone.
     */
    private VBox createTelephoneLayout(HBox form, HBox buttons) {
        VBox layout = new VBox(SPACING_SMALL, form, buttons, tableTelephone);
        layout.setPadding(new Insets(PADDING_STANDARD));
        return layout;
    }

    // ============================================================
    // INTERFACE ADRESSE
    // ============================================================
    
    /**
     * Construit l'interface de gestion des adresses.
     */
    private VBox buildAdresseUI() {
        initializeAdresseFields();
        
        HBox form = createAdresseForm();
        HBox buttons = createAdresseButtons();
        
        initializeAdresseTable();
        loadAdresses();

        return createAdresseLayout(form, buttons);
    }

    /**
     * Initialise les champs de saisie pour une adresse.
     */
    private void initializeAdresseFields() {
        txtAdrQuartier = createTextField("Quartier");
        txtAdrCommune = createTextField("Commune");
        txtAdrVille = createTextField("Ville");
        txtAdrPays = createTextField("Pays");
        txtAdrId = new TextField();
    }

    /**
     * Crée le formulaire de saisie pour une adresse.
     */
    private HBox createAdresseForm() {
        HBox form = new HBox(SPACING_SMALL, txtAdrQuartier, txtAdrCommune, txtAdrVille, txtAdrPays);
        form.setPadding(new Insets(PADDING_STANDARD));
        form.setAlignment(Pos.CENTER);
        return form;
    }

    /**
     * Crée les boutons d'action pour la gestion des adresses.
     */
    private HBox createAdresseButtons() {
        Button btnSave = new Button("Enregistrer");
        Button btnDelete = new Button("Supprimer");
        Button btnRefresh = new Button("Actualiser");

        btnSave.setOnAction(e -> saveAdresse());
        btnDelete.setOnAction(e -> deleteAdresse());
        btnRefresh.setOnAction(e -> loadAdresses());

        HBox buttons = new HBox(SPACING_SMALL, btnSave, btnDelete, btnRefresh);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(PADDING_STANDARD));
        return buttons;
    }

    /**
     * Initialise le tableau des adresses.
     */
    private void initializeAdresseTable() {
        tableAdresse = new TableView<>();

        TableColumn<Adresse, Integer> colId = createTableColumn("ID", "id", Integer.class);
        TableColumn<Adresse, String> colQuartier = createTableColumn("Quartier", "quartier", String.class);
        TableColumn<Adresse, String> colCommune = createTableColumn("Commune", "commune", String.class);
        TableColumn<Adresse, String> colVille = createTableColumn("Ville", "ville", String.class);
        TableColumn<Adresse, String> colPays = createTableColumn("Pays", "pays", String.class);

        tableAdresse.getColumns().addAll(colId, colQuartier, colCommune, colVille, colPays);
    }

    /**
     * Crée la disposition pour l'interface adresse.
     */
    private VBox createAdresseLayout(HBox form, HBox buttons) {
        VBox layout = new VBox(SPACING_SMALL, form, buttons, tableAdresse);
        layout.setPadding(new Insets(PADDING_STANDARD));
        return layout;
    }

    // ============================================================
    // INTERFACE PERSONNE-ADRESSE (DOMICILE)
    // ============================================================
    
    /**
     * Construit l'interface de gestion des domiciles (personne-adresse).
     */
    private VBox buildPersonneAdresseUI() {
        initializePersonneAdresseFields();
        initializeAdresseComboBox();
        
        HBox form = createPersonneAdresseForm();
        HBox buttons = createPersonneAdresseButtons();
        
        initializePersonneAdresseTable();
        loadPersonneAdresse();

        return createPersonneAdresseLayout(form, buttons);
    }

    /**
     * Initialise les champs de saisie pour un domicile.
     */
    private void initializePersonneAdresseFields() {
        txtPAId = new TextField();
        txtPAIdPersonne = createTextField("ID Personne");
        txtPAIdAdresse = createTextField("ID Adresse");
        txtPAAvenue = createTextField("Avenue");
        txtPANumero = createTextField("Numéro");
        
        // Réutilisation de la combobox personne
        initializePersonneComboBox();
    }

    /**
     * Initialise la combobox des adresses.
     */
    private void initializeAdresseComboBox() {
        cbAdressePersonne = new ComboBox<>();
        cbAdressePersonne.setPromptText("Sélectionner l'adresse");

        // Chargement des adresses
        Adresse a = new Adresse();
        List<Adresse> adresses = a.Adresses();
        cbAdressePersonne.getItems().addAll(adresses);

        // Configuration de l'affichage
        configureAdresseComboBoxDisplay();
    }

    /**
     * Configure l'affichage de la combobox des adresses.
     */
    private void configureAdresseComboBoxDisplay() {
        cbAdressePersonne.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Adresse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getQuartier() + ", " + item.getVille());
            }
        });

        cbAdressePersonne.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Adresse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getQuartier() + ", " + item.getVille());
            }
        });
    }

    /**
     * Crée le formulaire de saisie pour un domicile.
     */
    private HBox createPersonneAdresseForm() {
        HBox form = new HBox(SPACING_SMALL, cbTelPersonne, cbAdressePersonne, txtPAAvenue, txtPANumero);
        form.setPadding(new Insets(PADDING_STANDARD));
        form.setAlignment(Pos.CENTER);
        return form;
    }

    /**
     * Crée les boutons d'action pour la gestion des domiciles.
     */
    private HBox createPersonneAdresseButtons() {
        Button btnSave = new Button("Enregistrer");
        Button btnDelete = new Button("Supprimer");

        btnSave.setOnAction(e -> savePersonneAdresse());
        btnDelete.setOnAction(e -> deletePersonneAdresse());

        HBox buttons = new HBox(SPACING_SMALL, btnSave, btnDelete);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(PADDING_STANDARD));
        return buttons;
    }

    /**
     * Initialise le tableau des domiciles.
     */
    private void initializePersonneAdresseTable() {
        tablePersonneAdresse = new TableView<>();

        TableColumn<PersonneAdresse, Integer> colId = createTableColumn("ID", "id", Integer.class);
        TableColumn<PersonneAdresse, Integer> colPers = createTableColumn("ID Personne", "idPersonne", Integer.class);
        TableColumn<PersonneAdresse, Integer> colAdr = createTableColumn("ID Adresse", "idAdresse", Integer.class);
        TableColumn<PersonneAdresse, String> colAvenue = createTableColumn("Avenue", "avenue", String.class);
        TableColumn<PersonneAdresse, Integer> colNum = createTableColumn("Numéro", "numeroAvenue", Integer.class);

        tablePersonneAdresse.getColumns().addAll(colId, colPers, colAdr, colAvenue, colNum);
    }

    /**
     * Crée la disposition pour l'interface domicile.
     */
    private VBox createPersonneAdresseLayout(HBox form, HBox buttons) {
        VBox layout = new VBox(SPACING_SMALL, form, buttons, tablePersonneAdresse);
        layout.setPadding(new Insets(PADDING_STANDARD));
        return layout;
    }

    // ============================================================
    // INTERFACE RAPPORT
    // ============================================================
    
    /**
     * Construit l'interface du rapport des personnes.
     */
    private VBox buildRapportUI() {
        initializeRapportTable();
        
        VBox layout = new VBox(SPACING_SMALL, new Label("Rapport des personnes"), tableRapport);
        layout.setPadding(new Insets(PADDING_STANDARD));
        
        return layout;
    }

    /**
     * Initialise le tableau du rapport.
     */
    private void initializeRapportTable() {
        tableRapport = new TableView<>();

        TableColumn<RapportPersonne, String> colNom = createTableColumn("Personne", "nomComplet", String.class);
        TableColumn<RapportPersonne, String> colNum = createTableColumn("Numéros", "numeros", String.class);
        TableColumn<RapportPersonne, String> colAdr = createTableColumn("Adresses", "adresses", String.class);

        tableRapport.getColumns().addAll(colNom, colNum, colAdr);
        tableRapport.getItems().addAll(loadRapport());
    }

    // ============================================================
    // OPERATIONS CRUD - PERSONNE
    // ============================================================
    
    /**
     * Sauvegarde une nouvelle personne.
     */
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
            clearPersonneFields();

        } catch (Exception ex) {
            showError("Erreur lors de l'enregistrement : " + ex.getMessage());
        }
    }

    /**
     * Supprime la personne sélectionnée.
     */
    private void deletePersonne() {
        try {
            Personne selected = tablePersonne.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Veuillez sélectionner une personne dans la liste.");
                return;
            }

            if (confirmDelete("personne")) {
                Personne p = new Personne();
                p.Supprimer(selected.getId());
                loadPersonnes();
                tableTelephonesPersonne.getItems().clear();
            }

        } catch (Exception ex) {
            showError("Erreur lors de la suppression : " + ex.getMessage());
        }
    }

    /**
     * Charge la liste des personnes.
     */
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

    /**
     * Efface les champs de saisie d'une personne.
     */
    private void clearPersonneFields() {
        txtNom.clear();
        txtPostNom.clear();
        txtPrenom.clear();
        cbSex.setValue(null);
    }

    // ============================================================
    // OPERATIONS CRUD - TELEPHONE
    // ============================================================
    
    /**
     * Sauvegarde un nouveau téléphone.
     */
    private void saveTelephone() {
        try {
            Personne selectedPerson = cbTelPersonne.getValue();

            if (selectedPerson == null) {
                showError("Veuillez sélectionner une personne.");
                return;
            }

            Telephone t = new Telephone();
            int newId = t.getLastId() + 1;

            Telephone tel = new Telephone(
                    newId,
                    txtTelType.getText(),
                    txtTelNumero.getText(),
                    selectedPerson.getId()
            );

            tel.Enregistrer(tel);
            loadTelephones();
            clearTelephoneFields();

        } catch (Exception ex) {
            showError("Erreur lors de l'enregistrement : " + ex.getMessage());
        }
    }

    /**
     * Supprime le téléphone sélectionné.
     */
    private void deleteTelephone() {
        try {
            Telephone selected = tableTelephone.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Veuillez sélectionner un téléphone dans la liste.");
                return;
            }

            if (confirmDelete("téléphone")) {
                Telephone t = new Telephone();
                t.Supprimer(selected.getId());
                loadTelephones();
            }

        } catch (Exception ex) {
            showError("Erreur lors de la suppression : " + ex.getMessage());
        }
    }

    /**
     * Charge la liste des téléphones.
     */
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

    /**
     * Charge les téléphones d'une personne spécifique.
     * 
     * @param personneId Identifiant de la personne
     */
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

    /**
     * Efface les champs de saisie d'un téléphone.
     */
    private void clearTelephoneFields() {
        txtTelType.clear();
        txtTelNumero.clear();
        cbTelPersonne.setValue(null);
    }

    // ============================================================
    // OPERATIONS CRUD - ADRESSE
    // ============================================================
    
    /**
     * Sauvegarde une nouvelle adresse.
     */
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
            clearAdresseFields();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Erreur lors de l'enregistrement : " + ex.getMessage());
        }
    }

    /**
     * Supprime l'adresse sélectionnée.
     */
    private void deleteAdresse() {
        try {
            Adresse selected = tableAdresse.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Veuillez sélectionner une adresse dans la liste.");
                return;
            }

            if (confirmDelete("adresse")) {
                Adresse a = new Adresse();
                a.Supprimer(selected.getId());
                loadAdresses();
            }

        } catch (Exception ex) {
            showError("Erreur lors de la suppression : " + ex.getMessage());
        }
    }

    /**
     * Charge la liste des adresses.
     */
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

    /**
     * Efface les champs de saisie d'une adresse.
     */
    private void clearAdresseFields() {
        txtAdrQuartier.clear();
        txtAdrCommune.clear();
        txtAdrVille.clear();
        txtAdrPays.clear();
    }

    // ============================================================
    // OPERATIONS CRUD - PERSONNE-ADRESSE
    // ============================================================
    
    /**
     * Sauvegarde un nouveau domicile (relation personne-adresse).
     */
    private void savePersonneAdresse() {
        try {
            Personne selectedPerson = cbTelPersonne.getValue();
            Adresse selectedAdresse = cbAdressePersonne.getValue();

            if (selectedPerson == null) {
                showError("Veuillez sélectionner une personne.");
                return;
            }

            if (selectedAdresse == null) {
                showError("Veuillez sélectionner une adresse.");
                return;
            }

            PersonneAdresse p = new PersonneAdresse();
            int newId = p.getLastId() + 1;
            
            PersonneAdresse pa = new PersonneAdresse(
                    newId,
                    selectedPerson.getId(),
                    selectedAdresse.getId(),
                    txtPAAvenue.getText(),
                    Integer.parseInt(txtPANumero.getText())
            );

            pa.Enregistrer(pa);
            loadPersonneAdresse();
            clearPersonneAdresseFields();

        } catch (NumberFormatException ex) {
            showError("Le numéro doit être un nombre valide.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Erreur lors de l'enregistrement du domicile : " + ex.getMessage());
        }
    }

    /**
     * Supprime le domicile sélectionné.
     */
    private void deletePersonneAdresse() {
        try {
            PersonneAdresse selected = tablePersonneAdresse.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Veuillez sélectionner un domicile dans la liste.");
                return;
            }

            if (confirmDelete("domicile")) {
                PersonneAdresse a = new PersonneAdresse();
                a.Supprimer(selected.getId());
                loadPersonneAdresse();
            }

        } catch (Exception ex) {
            showError("Erreur lors de la suppression : " + ex.getMessage());
        }
    }

    /**
     * Charge la liste des domiciles.
     */
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

    /**
     * Efface les champs de saisie d'un domicile.
     */
    private void clearPersonneAdresseFields() {
        cbTelPersonne.setValue(null);
        cbAdressePersonne.setValue(null);
        txtPAAvenue.clear();
        txtPANumero.clear();
    }

    // ============================================================
    // OPERATIONS RAPPORT
    // ============================================================
    
    /**
     * Charge les données du rapport à partir de la procédure stockée.
     * 
     * @return Liste des rapports
     */
    private List<RapportPersonne> loadRapport() {
        List<RapportPersonne> list = new ArrayList<>();
        String sql = "{CALL sp_rapport_personnes()}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new RapportPersonne(
                        rs.getString("nomComplet"),
                        rs.getString("numeros"),
                        rs.getString("adresses")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du rapport : " + e.getMessage());
        }

        return list;
    }

    // ============================================================
    // METHODES UTILITAIRES
    // ============================================================
    
    /**
     * Affiche une boîte de dialogue d'erreur.
     * 
     * @param msg Message d'erreur à afficher
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue de confirmation.
     * 
     * @param itemType Type d'élément à supprimer
     * @return true si l'utilisateur confirme, false sinon
     */
    private boolean confirmDelete(String itemType) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer ce " + itemType + " ?",
                ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    // ============================================================
    // POINT D'ENTREE PRINCIPAL
    // ============================================================
    
    public static void main(String[] args) {
        launch(args);
    }
}