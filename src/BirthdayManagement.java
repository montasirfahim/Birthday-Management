import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BirthdayManagement extends Application {

    private StackPane mainPane;
    private VBox homePane, addPane, searchPane;

    private TableView<Classmate> tableView;
    private Label todayLabel;

    private TextField tfName, tfSearch;
    private DatePicker dpBirth;
    private TableView<Classmate> resultTable;
    private VBox deletePane;
    private TextField tfDeleteId;
    private Button btnBackFromDelete, btnDeleteConfirm;
    private Button btndlt, btnAdd, btnSearch, btnRefresh, btnSave, btnBackFromAdd, btnBackFromSearch, btnSearchSubmit;

    private VBox updatePane;
    private TextField tfUpdateId, tfNewName;
    private DatePicker dpNewBirth;
    private Button btnUpdate, btnBackFromUpdate, btnUpdateConfirm;

    @Override
    public void start(Stage primaryStage) {
        mainPane = new StackPane();
        setupHomePane();
        setupAddPane();
        setupSearchPane();
        setupDeletePane();
        setupUpdatePane();

        mainPane.getChildren().add(homePane);

        primaryStage.setTitle("Birthday Management");
        primaryStage.setScene(new Scene(mainPane, 800, 500));
        primaryStage.show();
    }

    private void setupHomePane() {
        btnAdd = new Button("নতুন যোগ করুন");
        btndlt = new Button("মুছে ফেলুন");
        btnUpdate = new Button("আপডেট করুন");
        btnUpdate.setOnAction(this::actionPerformed);
        btnSearch = new Button("খুঁজুন");
        btnRefresh = new Button("তালিকা রিফ্রেশ");

        btnAdd.setOnAction(this::actionPerformed);
        btndlt.setOnAction(this::actionPerformed);
        btnSearch.setOnAction(this::actionPerformed);
        btnRefresh.setOnAction(this::actionPerformed);

        todayLabel = new Label();
        tableView = new TableView<>();

        TableColumn<Classmate, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Classmate, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Classmate, LocalDate> dateCol = new TableColumn<>("Birthdate");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("birthdate"));

        tableView.getColumns().addAll(idCol, nameCol, dateCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox topControls = new VBox(10, todayLabel, new HBox(10, btnAdd,btndlt,btnUpdate, btnSearch, btnRefresh));
        topControls.setAlignment(Pos.CENTER);

        homePane = new VBox(15, topControls, tableView);
        homePane.setAlignment(Pos.CENTER);
        homePane.setPadding(new Insets(20));

        loadAllBirthdays();
        showTodayBirthdays();
    }

    private void setupAddPane() {
        Label lblName = new Label("নাম:");
        tfName = new TextField();

        Label lblDate = new Label("জন্ম তারিখ:");
        dpBirth = new DatePicker();

        btnSave = new Button("সংরক্ষণ করুন");
        btnBackFromAdd = new Button("← পেছনে যান");

        btnSave.setOnAction(this::actionPerformed);
        btnBackFromAdd.setOnAction(this::actionPerformed);

        addPane = new VBox(10, lblName, tfName, lblDate, dpBirth, btnSave, btnBackFromAdd);
        addPane.setAlignment(Pos.CENTER);
        addPane.setPadding(new Insets(20));
    }
    private void setupDeletePane() {
        Label lbl = new Label("মুছে ফেলতে আইডি লিখুন:");
        tfDeleteId = new TextField();
        tfDeleteId.setPromptText("যেমনঃ 1");

        btnDeleteConfirm = new Button("ডিলিট করুন");
        btnBackFromDelete = new Button("← পেছনে যান");

        btnDeleteConfirm.setOnAction(this::actionPerformed);
        btnBackFromDelete.setOnAction(this::actionPerformed);

        deletePane = new VBox(15, lbl, tfDeleteId, btnDeleteConfirm, btnBackFromDelete);
        deletePane.setAlignment(Pos.CENTER);
        deletePane.setPadding(new Insets(20));
    }

    private void setupUpdatePane() {
        Label lblId = new Label("আপডেট করতে আইডি লিখুন:");
        tfUpdateId = new TextField();
        tfUpdateId.setPromptText("যেমনঃ 1");

        Label lblName = new Label("নতুন নাম (ঐচ্ছিক):");
        tfNewName = new TextField();
        tfNewName.setPromptText("নতুন নাম দিন");

        Label lblBirth = new Label("নতুন জন্মতারিখ (ঐচ্ছিক):");
        dpNewBirth = new DatePicker();

        btnUpdateConfirm = new Button("আপডেট নিশ্চিত করুন");
        btnBackFromUpdate = new Button("← পেছনে যান");

        btnUpdateConfirm.setOnAction(this::actionPerformed);
        btnBackFromUpdate.setOnAction(this::actionPerformed);

        updatePane = new VBox(15,
                lblId, tfUpdateId,
                lblName, tfNewName,
                lblBirth, dpNewBirth,
                btnUpdateConfirm, btnBackFromUpdate
        );
        updatePane.setAlignment(Pos.CENTER);
        updatePane.setPadding(new Insets(20));
    }


    private void setupSearchPane() {
        Label lblSearch = new Label("নাম অথবা মাস লিখুন:");
        tfSearch = new TextField();
        btnSearchSubmit = new Button("খুঁজুন");
        btnBackFromSearch = new Button("← পেছনে যান");

        btnSearchSubmit.setOnAction(this::actionPerformed);
        btnBackFromSearch.setOnAction(this::actionPerformed);

        resultTable = new TableView<>();

        TableColumn<Classmate, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Classmate, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Classmate, LocalDate> dateCol = new TableColumn<>("Birthdate");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("birthdate"));

        resultTable.getColumns().addAll(idCol, nameCol, dateCol);
        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        searchPane = new VBox(10, lblSearch, tfSearch, btnSearchSubmit, resultTable, btnBackFromSearch);
        searchPane.setAlignment(Pos.CENTER);
        searchPane.setPadding(new Insets(20));
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == btnAdd) {
            mainPane.getChildren().setAll(addPane);
        }if (event.getSource() == btndlt) {
            mainPane.getChildren().setAll(deletePane);
        }
        else if (event.getSource() == btnSearch) {
            mainPane.getChildren().setAll(searchPane);
        } else if (event.getSource() == btnRefresh) {
            loadAllBirthdays();
        } else if (event.getSource() == btnBackFromAdd || event.getSource() == btnBackFromSearch) {
            mainPane.getChildren().setAll(homePane);
        } else if (event.getSource() == btnSave) {
            String name = tfName.getText().trim();
            LocalDate date = dpBirth.getValue();

            if (name.isEmpty() || date == null) {
                showAlert("অনুগ্রহ করে নাম এবং তারিখ দিন!");
                return;
            }

            try (Connection conn = Database.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO classmates (name, birthdate) VALUES (?, ?)");
                stmt.setString(1, name);
                stmt.setDate(2, Date.valueOf(date));
                stmt.executeUpdate();
                showAlert("সফলভাবে যোগ হয়েছে!");
                tfName.clear();
                dpBirth.setValue(null);
                loadAllBirthdays();
            } catch (Exception ex) {
                showAlert("ত্রুটি: " + ex.getMessage());
            }
        } else if (event.getSource() == btnSearchSubmit) {
            String keyword = tfSearch.getText().trim().toLowerCase();
            resultTable.getItems().clear();

            if (keyword.isEmpty()) return;

            try (Connection conn = Database.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT * FROM classmates WHERE LOWER(name) LIKE ? OR LOWER(MONTHNAME(birthdate)) LIKE ?"
                );
                stmt.setString(1, "%" + keyword + "%");
                stmt.setString(2, "%" + keyword + "%");

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    resultTable.getItems().add(new Classmate(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDate("birthdate").toLocalDate()
                    ));
                }
            } catch (Exception ex) {
                showAlert("ত্রুটি: " + ex.getMessage());
            }
        }
        else if (event.getSource() == btnBackFromDelete) {
            mainPane.getChildren().setAll(homePane);
        } else if (event.getSource() == btnDeleteConfirm) {
            String input = tfDeleteId.getText().trim();
            if (input.isEmpty()) {
                showAlert("অনুগ্রহ করে একটি আইডি লিখুন!");
                return;
            }

            try {
                int id = Integer.parseInt(input);

                try (Connection conn = Database.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM classmates WHERE id = ?");
                    stmt.setInt(1, id);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        showAlert("সফলভাবে মুছে ফেলা হয়েছে!");
                        loadAllBirthdays();
                        tfDeleteId.clear();
                    } else {
                        showAlert("এই আইডির কোনো রেকর্ড খুঁজে পাওয়া যায়নি।");
                    }
                }

            } catch (NumberFormatException ex) {
                showAlert("বৈধ একটি সংখ্যা লিখুন!");
            } catch (Exception ex) {
                showAlert("ত্রুটি: " + ex.getMessage());
            }
        }

        else if(event.getSource() == btnUpdate) {
            System.out.println("update button pressed");
            tfUpdateId.clear();
            tfNewName.clear();
            dpNewBirth.setValue(null);
            mainPane.getChildren().setAll(updatePane);

        } else if (event.getSource() == btnBackFromUpdate) {
            mainPane.getChildren().setAll(homePane);

        } else if (event.getSource() == btnUpdateConfirm) {
            String idText = tfUpdateId.getText().trim();
            String newName = tfNewName.getText().trim();
            LocalDate newBirth = dpNewBirth.getValue();

            if (idText.isEmpty()) {
                showAlert("অনুগ্রহ করে একটি আইডি লিখুন!");
                return;
            }

            if (newName.isEmpty() && newBirth == null) {
                showAlert("কমপক্ষে একটি তথ্য (নাম বা জন্মতারিখ) দিন!");
                return;
            }

            try {
                int id = Integer.parseInt(idText);

                StringBuilder query = new StringBuilder("UPDATE classmates SET ");
                List<Object> params = new ArrayList<>();

                if (!newName.isEmpty()) {
                    query.append("name = ?");
                    params.add(newName);
                }

                if (newBirth != null) {
                    if (!params.isEmpty()) query.append(", ");
                    query.append("birthdate = ?");
                    params.add(Date.valueOf(newBirth));
                }

                query.append(" WHERE id = ?");
                params.add(id);

                try (Connection conn = Database.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement(query.toString());

                    for (int i = 0; i < params.size(); i++) {
                        stmt.setObject(i + 1, params.get(i));
                    }

                    int updated = stmt.executeUpdate();
                    if (updated > 0) {
                        showAlert("সফলভাবে আপডেট হয়েছে!");
                        loadAllBirthdays();
                    } else {
                        showAlert("এই আইডির কোনো রেকর্ড খুঁজে পাওয়া যায়নি।");
                    }

                }

            } catch (NumberFormatException ex) {
                showAlert("বৈধ একটি আইডি লিখুন!");
            } catch (Exception ex) {
                showAlert("ত্রুটি: " + ex.getMessage());
            }
        }
    }

    private void loadAllBirthdays() {
        tableView.getItems().clear();
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT * FROM classmates " +
                            "WHERE DATE_FORMAT(birthdate, '%m-%d') >= DATE_FORMAT(CURDATE(), '%m-%d') " +
                            "ORDER BY MONTH(birthdate), DAY(birthdate)"
            );

            while (rs.next()) {
                tableView.getItems().add(new Classmate(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("birthdate").toLocalDate()
                ));
            }
        } catch (Exception ex) {
            showAlert("ত্রুটি: " + ex.getMessage());
        }
    }

    private void showTodayBirthdays() {
        List<String> names = new ArrayList<>();
        LocalDate today = LocalDate.now();

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT name FROM classmates WHERE MONTH(birthdate) = ? AND DAY(birthdate) = ?"
            );
            stmt.setInt(1, today.getMonthValue());
            stmt.setInt(2, today.getDayOfMonth());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) names.add(rs.getString("name"));
        } catch (Exception ex) {
            showAlert("ত্রুটি: " + ex.getMessage());
        }

        if (!names.isEmpty()) {
            todayLabel.setText("আজ জন্মদিন: " + String.join(", ", names));
        } else {
            todayLabel.setText("আজ কারো জন্মদিন নেই");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
