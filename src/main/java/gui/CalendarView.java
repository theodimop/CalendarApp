package gui;

import com.jfoenix.controls.JFXDatePicker;
import com.sun.javafx.scene.control.skin.DatePickerSkin;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The GUI class.
 */
public class CalendarView extends Application {

    private static final int FRAME_WIDTH = 720;
    private static final int FRAME_HEIGHT = 480;
    private static final int POPUP_WIDTH = 400;
    private static final int POPUP_HEIGHT = 550;
    private static String username;

    private BorderPane mainPane;            //Applications main Frame
    private GridPane addEventGridPane;
    private Stage primaryStage;             //Applications primaryStage
    private Stage addEventStage;
    private Button newEventButton;
    private GridPane addLocationGridPane;
    private Stage addLocationStage;
    private Button newLocationButton;
    private GridPane addUserGridPane;
    private Stage addUserStage;
    private Button newUserButton;
    private TableView<EventTableView> table;
    private CalendarController controller;  //Application Controller
    private ObservableList<EventTableView> data;

    private boolean loggedOn;

    /**
     * Runner method for the GUI.
     * @param args arguments array.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Method to start the application.
     * @see Application#start(Stage)
     */
    @Override public void start(Stage primaryStage) throws Exception {
        initializeComponents(primaryStage);

        while (!(loggedOn = loginPopup())) {
                //If user press cancel in login popup application exits.
        }

        if (loggedOn) {
            setupComponents();
            primaryStage.setScene(new Scene(mainPane, FRAME_WIDTH, FRAME_HEIGHT));
            primaryStage.setTitle("CS5031-CALENDAR-" + username);
            primaryStage.show();
        }
        else {
            System.exit(0);
        }
    }

    /**
     * Pops up authentication window.
     */
    private boolean loginPopup() {
        TextInputDialog dialog = new TextInputDialog("admin@admin.com");
        dialog.setTitle("AUTHENTICATION");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter your email:");
        Optional<String> result = dialog.showAndWait();
        username = result.isPresent() ? result.get() : "";
        return controller.authenticateUser(result);
    }

    /**
     * Initialize application key components.
     * Key components are the instance variables.
     * @param primaryStage
     */
    private void initializeComponents(Stage primaryStage) {
        this.primaryStage = primaryStage;
        controller = new CalendarController();
        mainPane = new BorderPane();
        addEventGridPane = new GridPane();
        addLocationGridPane = new GridPane();
        addUserGridPane = new GridPane();
    }

    /**
     * Setup application components.
     */
    private void setupComponents() {

        HBox hBox = new HBox();
        HBox leftHBox = new HBox();
        VBox centerBox = new VBox();
        //centerBox.setStyle("-fx-background-color: green");
        //centerBox.setPrefSize(300, 300);
        //leftHBox.setStyle("-fx-background-color: blue");
        leftHBox.setMaxHeight(220);
        //centerHBox.setMaxWidth(400);

        DatePicker datePickerCalendar = new DatePicker(LocalDate.now());
        datePickerCalendar.setOnAction(e -> {
            data.removeAll(data);
            EventTableView[] eventTableViews = controller.getEventsByDate(datePickerCalendar.getValue());
            //System.out.println(eventTableViews[0].description);
            data.addAll(eventTableViews);
        });
        DatePickerSkin datePickerSkin = new DatePickerSkin(datePickerCalendar);    //calendar Element
        Node calendarView = datePickerSkin.getPopupContent();

        leftHBox.getChildren().add(calendarView);

        setupPopupAddNewEvent();
        setupPopupAddNewLocation();
        setupPopupAddNewUser();
        setupEventsTableView();

        Button displayMyEvents = new Button("My Events");
        displayMyEvents.setOnAction(e -> {
            data.removeAll(data);
            EventTableView[] eventTableViews = controller.getAllUserEvents();
            data.addAll(eventTableViews);
        });

        newEventButton = new Button("New event");
        newEventButton.setOnAction(event -> buttonClicked(event));

        newLocationButton = new Button("New location");
        newLocationButton.setOnAction(location -> buttonClicked(location));

        newUserButton = new Button("New user");
        newUserButton.setOnAction(user -> buttonClicked(user));

        centerBox.getChildren().add(table);

        hBox.getChildren().addAll(displayMyEvents, newEventButton);
        if (isAdmin()) {
            hBox.getChildren().addAll(newLocationButton, newUserButton);
        }
        mainPane.setTop(hBox);
        mainPane.setLeft(leftHBox);
        mainPane.setCenter(centerBox);

    }

    /**
     * Setup the table view in the center of the main frame.
     */
    private void setupEventsTableView() {
        table = new TableView<>();
        table.setPrefHeight(500);
        TableColumn eventName = new TableColumn("EVENT");                             //Create column
        eventName.setCellValueFactory(new PropertyValueFactory<>("description"));    //bind property

        TableColumn eventDate = new TableColumn("DATE");
        eventDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn eventTime = new TableColumn("START TIME");
        eventTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn eventLocation = new TableColumn("LOCATION");
        eventLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

        data = FXCollections.observableArrayList();
        data.addAll(controller.getAllUserEvents());
        table.setItems(data);
        table.getColumns().addAll(eventName, eventDate, eventTime, eventLocation);
    }

    /**
     * Builds the add New event popup window.
     */
    private void setupPopupAddNewEvent() {
        addEventGridPane.setPadding(new Insets(10, 10, 10, 10));
        addEventGridPane.setHgap(10);
        addEventGridPane.setVgap(10);
        addEventGridPane.setPrefWidth(350);
        addEventStage = new Stage();

        List<Label> addEventLabels = new ArrayList<>();                     //Create Labels
        addEventLabels.add(new Label("*DATE :"));
        addEventLabels.add(new Label("*START TIME :"));
        addEventLabels.add(new Label("*END TIME :"));
        addEventLabels.add(new Label("*LOCATION :"));
        addEventLabels.add(new Label("*DESCRIPTION : "));
        addEventLabels.add(new Label("INVITATIONS :"));
        addEventLabels.add(new Label("CHECK AVAILABILITY : "));
        addEventLabels.forEach(label -> label.setMinWidth(120));

        List<Control> controls = new ArrayList<>();                         //Create Controls
        TextArea descriptionTextArea = new TextArea();                      //Customize description area
        descriptionTextArea.setWrapText(true);
        descriptionTextArea.setMaxHeight(POPUP_HEIGHT * 0.2);
        descriptionTextArea.setMaxWidth(POPUP_WIDTH * 0.7);

        TextArea invitationsTextArea = new TextArea();                      //Customize invitations area
        invitationsTextArea.setPromptText("example@ex.com, example1@ex.gr");
        invitationsTextArea.setWrapText(true);
        invitationsTextArea.setMaxHeight(POPUP_HEIGHT * 0.2);
        invitationsTextArea.setMaxWidth(POPUP_WIDTH * 0.7);

        TextField locationTextField = new TextField();
        TextField userAvailabilityText = new TextField();
        userAvailabilityText.setPromptText("ex1@example.com");

        JFXDatePicker datePicker = new JFXDatePicker();
        datePicker.setValue(LocalDate.now());
        datePicker.setTime(LocalTime.now());

        JFXDatePicker timeStartPicker = new JFXDatePicker();
        timeStartPicker.setShowTime(true);
        timeStartPicker.setTime(LocalTime.now().plusHours(1));

        JFXDatePicker timeEndPicker = new JFXDatePicker();
        timeEndPicker.setShowTime(true);
        timeEndPicker.setTime(LocalTime.now().plusHours(2));

        controls.add(datePicker);                               //DATE
        controls.add(timeStartPicker);                          //START TIME
        controls.add(timeEndPicker);                            //END TIME
        controls.add(locationTextField);                        //LOCATION
        controls.add(descriptionTextArea);                      //DESCRIPTION
        controls.add(invitationsTextArea);                      //INVITATIONS
        controls.add(userAvailabilityText);                     //AVAILABILITY

        controls.forEach(control -> control.setMaxWidth(200));

        int i;
        for (i = 0; i < controls.size(); i++) {             //Add components to popup
            addEventGridPane.add(addEventLabels.get(i), 0, i);
            addEventGridPane.add(controls.get(i), 1, i);
        }

        ToggleButton weeklyToggle = new ToggleButton("WEEKLY");
        ToggleButton monthlyToggle = new ToggleButton("MONTHLY");
        ToggleButton annuallyToggle = new ToggleButton("ANNUALLY");

        weeklyToggle.setOnAction(e -> {
            if (weeklyToggle.isSelected()) {
                monthlyToggle.setSelected(false);
                annuallyToggle.setSelected(false);
            }
        });
        monthlyToggle.setOnAction(e -> {
            if (monthlyToggle.isSelected()) {
                weeklyToggle.setSelected(false);
                annuallyToggle.setSelected(false);
            }
        });
        annuallyToggle.setOnAction(e -> {
            if (annuallyToggle.isSelected()) {
                weeklyToggle.setSelected(false);
                monthlyToggle.setSelected(false);
            }
        });

        VBox vToggleBox = new VBox();
        vToggleBox.setSpacing(10);
        vToggleBox.setMinWidth(200);
        vToggleBox.setAlignment(Pos.CENTER);
        vToggleBox.getChildren().addAll(weeklyToggle, monthlyToggle, annuallyToggle);

        Button addNewEventButton = new Button("ADD EVENT");
        Button cancelButton = new Button("CANCEL");
        Button checkIfAvailableButton = new Button("CHECK");

        HBox addCancelHBox = new HBox();                                  //Add buttons ADD, CANCEL
        addCancelHBox.setSpacing(10);
        addCancelHBox.setPrefWidth(200);
        addCancelHBox.setAlignment(Pos.BASELINE_RIGHT);
        addCancelHBox.getChildren().addAll(addNewEventButton, cancelButton);

        addEventGridPane.add(checkIfAvailableButton, 0, i++);
        addEventGridPane.add(new Label("RECURRING EVENT : "), 0, i);
        addEventGridPane.add(vToggleBox, 1, i++);

        TextField recNumberText = new TextField();
        recNumberText.setPromptText("0");

        addEventGridPane.add(recNumberText, 1, i++);
        addEventGridPane.add(new Label("Fields * are compulsory."), 0, ++i);
        addEventGridPane.add(addCancelHBox, 1, i++);

        addNewEventButton.setOnAction(event -> {
            try {
                String rec = getRecurringToggle(weeklyToggle, monthlyToggle, annuallyToggle);

                handleNewEventButton(controls, rec, (rec != null && !rec.isEmpty()) ? Integer.parseInt(recNumberText.getText()) : 0);
            }
            catch (NumberFormatException e) {
                displayFillFieldsAlert("Event");
            }
        });
        //check availability button
        checkIfAvailableButton.setOnAction(event -> handleAvailabilityButton(userAvailabilityText.getText(), datePicker.getValue()));
        cancelButton.setOnAction(event -> addEventStage.close());

        addEventStage.setScene(new Scene(addEventGridPane, POPUP_WIDTH, POPUP_HEIGHT));
        addEventStage.initModality(Modality.APPLICATION_MODAL);
        addEventStage.setTitle("NEW EVENT");
    }

    /**
     * Builds the add New location popup window.
     */
    private void setupPopupAddNewLocation() {
        addLocationGridPane.setPadding(new Insets(10, 10, 10, 10));
        addLocationGridPane.setHgap(10);
        addLocationGridPane.setVgap(10);
        addLocationGridPane.setPrefWidth(350);
        addLocationStage = new Stage();

        List<Label> addLocationLabels = new ArrayList<>();                     //Create Labels
        addLocationLabels.add(new Label("LOCATION NAME :"));
        addLocationLabels.add(new Label("CAPACITY :"));
        addLocationLabels.forEach(label -> label.setAlignment(Pos.TOP_RIGHT)); //Set Alignment
        addLocationLabels.forEach(label -> label.setMinWidth(110));

        List<Control> controls = new ArrayList<>();                         //Create Controls
        TextArea descriptionTextArea = new TextArea();                      //Customize description area
        descriptionTextArea.setWrapText(true);
        descriptionTextArea.setMaxHeight(POPUP_HEIGHT * 0.2);
        descriptionTextArea.setMaxWidth(POPUP_WIDTH * 0.7);

        TextField locationTextField = new TextField();
        TextField capacityTextField = new TextField();

        controls.add(locationTextField);                          //LOCATION
        controls.add(capacityTextField);                          //CAPACITY

        controls.forEach(control -> control.setMaxWidth(200));

        for (int i = 0; i < controls.size(); i++) {             //Add components to popup
            addLocationGridPane.add(addLocationLabels.get(i), 0, i);
            addLocationGridPane.add(controls.get(i), 1, i);
        }

        Button addNewLocationButton = new Button("ADD");
        Button cancelButton = new Button("CANCEL");

        HBox hBox = new HBox();                                  //Add buttons
        hBox.setSpacing(10);
        hBox.setPrefWidth(200);
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        hBox.getChildren().addAll(addNewLocationButton, cancelButton);
        addLocationGridPane.add(hBox, 1, 5);

        addNewLocationButton.setOnAction(location -> handleNewLocationButton(controls));

        cancelButton.setOnAction(location -> {
            addLocationStage.close();
        });

        addLocationStage.setScene(new Scene(addLocationGridPane, POPUP_WIDTH, POPUP_HEIGHT));
        addLocationStage.initModality(Modality.APPLICATION_MODAL);
        addLocationStage.setTitle("Pop up window");
    }

    /**
     * Builds the add New user popup window.
     */
    private void setupPopupAddNewUser() {
        addUserGridPane.setPadding(new Insets(10, 10, 10, 10));
        addUserGridPane.setHgap(10);
        addUserGridPane.setVgap(10);
        addUserGridPane.setPrefWidth(350);
        addUserStage = new Stage();

        List<Label> addUserLabels = new ArrayList<>();                     //Create Labels
        addUserLabels.add(new Label("NAME :"));
        addUserLabels.add(new Label("EMAIL :"));
        addUserLabels.forEach(label -> label.setAlignment(Pos.TOP_RIGHT)); //Set Alignment
        addUserLabels.forEach(label -> label.setMinWidth(110));

        List<Control> controls = new ArrayList<>();                         //Create Controls
        TextArea descriptionTextArea = new TextArea();                      //Customize description area
        descriptionTextArea.setWrapText(true);
        descriptionTextArea.setMaxHeight(POPUP_HEIGHT * 0.2);
        descriptionTextArea.setMaxWidth(POPUP_WIDTH * 0.7);

        TextField nameTextField = new TextField();
        TextField emailTextArea = new TextField();

        controls.add(nameTextField);                          //USER NAME
        controls.add(emailTextArea);                      //USER EMAIL
        controls.forEach(control -> control.setMaxWidth(200));

        for (int i = 0; i < controls.size(); i++) {             //Add components to popup
            addUserGridPane.add(addUserLabels.get(i), 0, i);
            addUserGridPane.add(controls.get(i), 1, i);
        }

        Button addNewUserButton = new Button("ADD");
        Button cancelButton = new Button("CANCEL");

        HBox hBox = new HBox();                                  //Add buttons
        hBox.setSpacing(10);
        hBox.setPrefWidth(200);
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        hBox.getChildren().addAll(addNewUserButton, cancelButton);
        addUserGridPane.add(hBox, 1, 5);

        addNewUserButton.setOnAction(user -> handleNewUserButton(controls)

        );

        cancelButton.setOnAction(user -> {
            addUserStage.close();
        });

        addUserStage.setScene(new Scene(addUserGridPane, POPUP_WIDTH, POPUP_HEIGHT));
        addUserStage.initModality(Modality.APPLICATION_MODAL);
        addUserStage.setTitle("Pop up window");
    }

    /**
     * Handle add new user Event.
     */
    private void handleNewUserButton(List<Control> controls) {
        String type = "User";
        if (((TextField) (controls.get(0))).getText().isEmpty() || ((TextField) (controls.get(1))).getText().isEmpty()) {

            displayFillFieldsAlert(type);
        }
        else {
            if (controller.addUser(controls)) {
                displaySuccessAlert(addLocationStage, type);
            }
            else {
                displayErrorAlert(type);
            }
        }
    }

    /**
     * Handle add new Location.
     */
    private void handleNewLocationButton(List<Control> controls) {
        String type = "Location";
        String capacity = ((TextField) (controls.get(1))).getText();

        if (((TextField) (controls.get(0))).getText().isEmpty() || capacity.isEmpty() || !NumberUtils.isNumber(capacity)) {

            displayFillFieldsAlert(type);
        }
        else {
            if (controller.addLocation(controls)) {
                displaySuccessAlert(addLocationStage, type);
            }
            else {
                displayErrorAlert(type);
            }
        }
    }

    /**
     * Get a string week,month,year based on the selected toggles.
     */
    private String getRecurringToggle(ToggleButton weeklyToggle, ToggleButton monthlyToggle, ToggleButton annuallyToggle) {
        if (weeklyToggle.isSelected()) {
            return "week";
        }
        else if (monthlyToggle.isSelected()) {
            return "month";
        }
        else if (annuallyToggle.isSelected()) {
            return "year";
        }
        else {
            return null;
        }
    }

    /**
     * Check input fields
     */
    private void handleNewEventButton(List<Control> controls, String week, int i) {
        String type = "Event";
        if (((TextField) (controls.get(3))).getText().isEmpty() || ((TextArea) (controls.get(4))).getText().isEmpty()) {
            displayFillFieldsAlert(type);
        }
        else {
            int res = controller.addEvent(controls, week, i);
            if (res == CalendarController.SUCCESS) {
                displaySuccessAlert(addEventStage, type);
            }
            else if (res == CalendarController.INSERTION_TO_DB_ERROR) {
                displayErrorInFieldsAlert(type);
            }
            else if (res == CalendarController.FORM_CONTENTS_ERROR) {
                displayErrorInFieldsAlert(type);
            }
        }
    }

    /**
     * Handle the check availability button.
     * @param email User email.
     * @param date  Date to check availability.
     */
    private void handleAvailabilityButton(String email, LocalDate date) {
        String result = controller.checkIfAvailable(email, date);

        if (result == null || !EmailValidator.getInstance().isValid(email) || result.toLowerCase().contains("error")) {
            displayUserDoesNotExist();
        }
        else {
            displayUserAvailability(result);
        }
    }

    /**
     * Handle popup up windows.
     */
    private void buttonClicked(ActionEvent e) {
        if (e.getSource() == newEventButton) {
            addEventStage.showAndWait();
        }
        else if (e.getSource() == newLocationButton) {
            addLocationStage.showAndWait();
        }
        else if (e.getSource() == newUserButton) {
            addUserStage.showAndWait();
        }
        else {
            addLocationStage.close();
            addEventStage.close();
            addUserStage.close();
        }
    }

    /**
     * Check if user is an admin
     */
    private boolean isAdmin() {
        return username.endsWith("@admin.com");
    }

    /**
     * Alert for filling out all the fields.
     * @param type Type of the insertion object. EVENT/LOCATION/USER.
     */
    private void displayFillFieldsAlert(String type) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("EMPTY FIELDS");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText("Fill out all the " + type + " details correctly!");
        errorAlert.showAndWait();
    }

    /**
     * Alert for successful insertions in database.
     * @param type Type of the insertion object. EVENT/LOCATION/USER.
     */
    private void displaySuccessAlert(Stage stage, String type) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("SUCCESS");
        successAlert.setHeaderText(null);
        successAlert.setContentText(type + " added successfully!");
        successAlert.showAndWait();
        stage.close();
    }

    /**
     * Alert for the unsuccessful shot to add data.
     * @param type Type of the insertion object. EVENT/LOCATION/USER.
     */
    private void displayErrorAlert(String type) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("ERROR!");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(type + " cannot be added!");
        errorAlert.showAndWait();
    }

    /**
     * Alert for filling out all the fields.
     * @param type Type of the insertion object. EVENT/LOCATION/USER.
     */
    private void displayErrorInFieldsAlert(String type) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("NOT VALID EVENT DETAILS");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(
                "Ensure that the room exists and is available with enough capacity." + " Check also that time is set correctly and invitations are valid member emails!");
        errorAlert.getDialogPane().setMinHeight(POPUP_HEIGHT * 0.25);
        errorAlert.showAndWait();
    }

    /**
     * Alert to display user schedule hours.
     * @param availability User unavailable hours
     */
    private void displayUserAvailability(String availability) {

        Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
        errorAlert.setTitle("USER SCHEDULE");
        errorAlert.setHeaderText("User schedule is illustrated below:");
        String[] availabilityHours = availability.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : availabilityHours) {
            stringBuilder.append("\t\t\t" + s + "\n");
        }

        errorAlert.setContentText(stringBuilder.toString());
        errorAlert.showAndWait();
    }

    /**
     * Invalid user email has been given. Inform the user.
     */
    private void displayUserDoesNotExist() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("USER DOES NOT EXIST!");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText("This email does not belong to a member.");
        errorAlert.showAndWait();
    }

    /**
     * Important class for the Table view UI component. This class is
     * observed by the the table view columns. It represents an event
     * in table row.
     */
    public static class EventTableView {

        private final SimpleStringProperty description;
        private final SimpleStringProperty date;
        private final SimpleStringProperty startTime;
        private final SimpleStringProperty location;

        public EventTableView(String description, String date, String startTime, String location) {
            this.description = new SimpleStringProperty(description);
            this.date = new SimpleStringProperty(date);
            this.startTime = new SimpleStringProperty(startTime);
            this.location = new SimpleStringProperty(location);
        }

        public String getDescription() {
            return description.get();
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }

        public String getDate() {
            return date.get();
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public String getStartTime() {
            return startTime.get();
        }

        public SimpleStringProperty startTimeProperty() {
            return startTime;
        }

        public String getLocation() {
            return location.get();
        }

        public SimpleStringProperty locationProperty() {
            return location;
        }
    }
}
