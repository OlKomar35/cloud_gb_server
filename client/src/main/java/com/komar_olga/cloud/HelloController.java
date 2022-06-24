package com.komar_olga.cloud;

import com.komar_olga.cloud.model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private HBox cloudBox;
    @FXML
    private VBox clientBox, serverBox, loginPanel;
    @FXML
    private RadioButton downloadRadio, uploadRadio, deleteRadio, renameRadio;
    @FXML
    private TableView<FileData> filesListClient, filesListServer;
    @FXML
    private TableColumn<FileData, String> filesNameClient, filesTypeClient, filesSizeClient, filesNameServer, filesTypeServer, filesSizeServer;
    @FXML
    private Label textSelectedRadio, folderServer;
    @FXML
    private Button buttonRadio;
    @FXML
    private TextField addressBarClient, addressBarServer, userLogin;
    @FXML
    private PasswordField userPass;

    private ObservableList<FileData> clientData = FXCollections.observableArrayList();
    private ObservableList<FileData> serverData = FXCollections.observableArrayList();
    private String indicator = "client";
    String fileSeparator = File.separator;


    @Override

    public void initialize(URL location, ResourceBundle resources) {

        try {
            Network.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //todo Проблемы с остановкой сервера
            // Network.stop();
        }
        filesListClient.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FileData>() {
            @Override
            public void changed(ObservableValue<? extends FileData> observable, FileData oldValue, FileData newValue) {
                if (filesListClient.getSelectionModel().getSelectedItem() != null) {
                    String address = filesListClient.getSelectionModel().getSelectedItem().getName() + "." + filesListClient.getSelectionModel().getSelectedItem().getType();
                    addressBarClient.setText(address);
                }
            }
        });
        filesListServer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FileData>() {
            @Override
            public void changed(ObservableValue<? extends FileData> observable, FileData oldValue, FileData newValue) {
                if (filesListServer.getSelectionModel().getSelectedItem() != null) {
                    String address = filesListServer.getSelectionModel().getSelectedItem().getName() + "." + filesListServer.getSelectionModel().getSelectedItem().getType();
                    addressBarServer.setText(address);
                }
            }
        });

    }

    private void refreshServerData(FileList fl) {
        folderServer.setText(fl.getDirectory());
        for (int i = 0; i < fl.getFileName().size(); i++) {
            serverData.add(new FileData(fl.getFileName().get(i), fl.getFileType().get(i), fl.getFileSize().get(i) + " Byte"));
        }
        filesNameServer.setCellValueFactory(new PropertyValueFactory<FileData, String>("name"));
        filesTypeServer.setCellValueFactory(new PropertyValueFactory<FileData, String>("type"));
        filesSizeServer.setCellValueFactory(new PropertyValueFactory<FileData, String>("size"));

        filesListServer.setItems(serverData);

    }

    private void refreshClientData() throws IOException {
        FileList fl = new FileList("client_storage/");
        for (int i = 0; i < fl.getFileName().size(); i++) {
            clientData.add(new FileData(fl.getFileName().get(i), fl.getFileType().get(i), fl.getFileSize().get(i) + " Byte"));
        }
        filesNameClient.setCellValueFactory(new PropertyValueFactory<FileData, String>("name"));
        filesTypeClient.setCellValueFactory(new PropertyValueFactory<FileData, String>("type"));
        filesSizeClient.setCellValueFactory(new PropertyValueFactory<FileData, String>("size"));

        filesListClient.setItems(clientData);

    }

    public void onClickRadioButton() {
        if (downloadRadio.isSelected()) {
            textSelectedRadio.setText("Скачать файл из облака?");
            buttonRadio.setText("Download");
        }
        if (uploadRadio.isSelected()) {
            textSelectedRadio.setText("Загрузить файл в облоко?");
            buttonRadio.setText("Upload");
        }
        if (deleteRadio.isSelected()) {
            textSelectedRadio.setText("Удалить файл?");
            buttonRadio.setText("Delete");
            Font font = new Font("Cambria Bold", 16);
            Label deleteLabel = new Label("    Вы хотите удалить файл на Клиенте или на Сервере?");
            deleteLabel.setFont(font);
            deleteLabel.setWrapText(true);
            Button buttonClient = new Button("На клиенте");
            buttonClient.setFont(font);
            Button buttonServer = new Button("На сервере");
            buttonServer.setFont(font);
            StackPane deletePane = new StackPane();
            deletePane.getChildren().addAll(deleteLabel, buttonClient, buttonServer);
            deletePane.setAlignment(deleteLabel, Pos.TOP_CENTER);
            deletePane.setMargin(deleteLabel, new Insets(20, 10, 0, 10));
            deletePane.setAlignment(buttonClient, Pos.CENTER_LEFT);
            deletePane.setMargin(buttonClient, new Insets(30, 0, 0, 65));
            deletePane.setAlignment(buttonServer, Pos.CENTER_RIGHT);
            deletePane.setMargin(buttonServer, new Insets(30, 65, 0, 0));
            Scene deleteScene = new Scene(deletePane, 400, 200);
            Stage deleteWindow = new Stage();
            deleteWindow.setTitle("Что удаляем?");
            deleteWindow.setResizable(false);
            deleteWindow.setScene(deleteScene);
            deleteWindow.initModality(Modality.WINDOW_MODAL);
            buttonClient.setOnAction(event -> {
                indicator = "client";
                deleteWindow.close();
            });
            buttonServer.setOnAction(event -> {
                indicator = "server";
                deleteWindow.close();
            });
            deleteWindow.show();
        }
        if (renameRadio.isSelected()) {
            textSelectedRadio.setText("Переименовать файл?");
            buttonRadio.setText("Rename");
            Font font = new Font("Cambria Bold", 16);
            Label renameLabel = new Label("    Вы хотите переименовать файл на Клиенте или на Сервере?");
            renameLabel.setFont(font);
            renameLabel.setWrapText(true);
            Button buttonClient = new Button("На клиенте");
            buttonClient.setFont(font);
            Button buttonServer = new Button("На сервере");
            buttonServer.setFont(font);
            StackPane renamePane = new StackPane();
            renamePane.getChildren().addAll(renameLabel, buttonClient, buttonServer);
            renamePane.setAlignment(renameLabel, Pos.TOP_CENTER);
            renamePane.setMargin(renameLabel, new Insets(20, 10, 0, 10));
            renamePane.setAlignment(buttonClient, Pos.CENTER_LEFT);
            renamePane.setMargin(buttonClient, new Insets(30, 0, 0, 65));
            renamePane.setAlignment(buttonServer, Pos.CENTER_RIGHT);
            renamePane.setMargin(buttonServer, new Insets(30, 65, 0, 0));
            Scene renameScene = new Scene(renamePane, 400, 200);
            Stage renameWindow = new Stage();
            renameWindow.setTitle("Что переименовываем?");
            renameWindow.setResizable(false);
            renameWindow.setScene(renameScene);
            renameWindow.initModality(Modality.WINDOW_MODAL);
            buttonClient.setOnAction(event -> {
                indicator = "client";
                renameWindow.close();
            });
            buttonServer.setOnAction(event -> {
                indicator = "server";
                renameWindow.close();
            });
            renameWindow.show();
        }
    }


    public void onClickButton() {
        if (downloadRadio.isSelected()) {
            if (!addressBarServer.getText().equals(null)) {
                Network.sendMsg(new FileRequest(addressBarServer.getText(), "download"));
                filesListClient.getItems().clear();
                try {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        if (fm.getActionPoint().equals("download")) {

                            Files.write(Paths.get("client_storage/" + fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);

                        }
                    }
                    refreshClientData();
                } catch (ClassNotFoundException | IOException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файл для скачивания не выбран");
                    alert.showAndWait();

                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файл для скачивания не выбран");
                alert.showAndWait();
            }
        }
        if (uploadRadio.isSelected()) {
            if (!addressBarClient.getText().equals(null)) {
                try {
                    Network.sendMsg(new FileMessage(Paths.get("client_storage/" + addressBarClient.getText()), "upload"));
                    System.out.println(addressBarClient.getText());
                    System.out.println("up");
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Что-то пошло не так при скачивании файла");
                    alert.showAndWait();
                    e.printStackTrace();
                }
                filesListServer.getItems().clear();
               // Network.sendMsg(new FileRequest(null, "list"));
                try {
                    AbstractMessage listRefresh = Network.readObject();
                    if (listRefresh instanceof FileList) {
                        FileList fl = (FileList) listRefresh;
                        refreshServerData(fl);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                addressBarClient.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файла с таким именем нет");
                alert.showAndWait();
            }

        }
        if (deleteRadio.isSelected()) {
            if (indicator.equals("client")) {
                try {
                    Path path = Paths.get("client_storage/" + addressBarClient.getText());
                    Files.delete(path);
                    filesListClient.getItems().clear();
                    refreshClientData();

                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файла с таким именем нет");
                    alert.showAndWait();
                    addressBarClient.clear();
                }

                addressBarClient.clear();
            } else {
                System.out.println(addressBarServer.getText());
                Network.sendMsg(new FileRequest(addressBarServer.getText(), "delete"));
                addressBarServer.clear();
                filesListServer.getItems().clear();
                System.out.println("del");
                try {
                    AbstractMessage listRefresh = Network.readObject();
                    if (listRefresh instanceof FileList) {
                        FileList fl = (FileList) listRefresh;
                        refreshServerData(fl);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

            }
        }
        if (renameRadio.isSelected()) {
            if (indicator.equals("client")) {
                try {
                    String address = filesListClient.getSelectionModel().getSelectedItem().getName() + "."
                            + filesListClient.getSelectionModel().getSelectedItem().getType();
                    Path sourcePath = Paths.get("client_storage/" + address);
                    Path destinationPath = Paths.get("client_storage/" + addressBarClient.getText());
                    System.out.println(sourcePath.getFileName() + "->" + destinationPath.getFileName());
                    Files.move(sourcePath, destinationPath);
                    filesListClient.getItems().clear();
                    refreshClientData();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Не верное действие");
                    alert.showAndWait();
                    addressBarClient.clear();

                }
                addressBarClient.clear();
            } else {
                String address = filesListServer.getSelectionModel().getSelectedItem().getName() + "."
                        + filesListServer.getSelectionModel().getSelectedItem().getType();
                Network.sendMsg(new FileRename(address, addressBarServer.getText()));
                filesListServer.getItems().clear();
                try {
                    AbstractMessage listRefresh = Network.readObject();
                    if (listRefresh instanceof FileList) {
                        FileList fl = (FileList) listRefresh;
                        refreshServerData(fl);
                    }
                } catch (ClassNotFoundException | IOException e) {
                   e.printStackTrace();
                }
            }
        }
    }

    public void login() {
        if (!userLogin.getText().trim().isEmpty()) {
            if (!userPass.getText().trim().isEmpty()) {
                String msg = String.format("%s %s %s", userLogin.getText().trim(), userPass.getText().trim(), 0);
                Network.sendMsg(new FileRequest(msg, "/auth"));
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Не верный логин или пароль");
                alert.showAndWait();
            }
        }
        try {
            AbstractMessage am = Network.readObject();
            if (am instanceof FileRequest) {
                FileRequest fr = (FileRequest) am;
                System.out.println(fr.getFileName() + "  " + fr.getActionPoint());
                if (fr.getFileName().startsWith("_auth")) {
                    String[] parts = fr.getFileName().split("\\s");
                    boolean authIndex = Boolean.parseBoolean(parts[1]);
                    if (authIndex) {
                        try {
                            refreshClientData();
                            Network.sendMsg(new FileRequest(null, "list"));
                            AbstractMessage listRefresh = Network.readObject();
                            if (listRefresh instanceof FileList) {
                                FileList fl = (FileList) listRefresh;
                                refreshServerData(fl);
                            }
                            System.out.println("log");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        loginPanel.setVisible(false);
                        loginPanel.setManaged(false);
                        cloudBox.setVisible(true);
                        cloudBox.setManaged(true);

                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "не верный логин или пароль");
                        alert.showAndWait();
                    }
                }
            }

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickHyperlink() {
        Font font = new Font("Cambria Bold", 16);
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 20, 20, 20));

        Label labelLogin = new Label("Login");
        labelLogin.setFont(font);
        TextField singUpLogin = new TextField();

        Label labelPass = new Label("Password");
        labelPass.setFont(font);
        PasswordField singUpPass = new PasswordField();

        Button btnEnter = new Button("Enter");
        btnEnter.setFont(font);

        BorderPane.setMargin(btnEnter, new Insets(35, 0, 0, 0));
        vBox.getChildren().addAll(labelLogin, singUpLogin, labelPass, singUpPass, btnEnter);
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(vBox);
        Scene secondScene = new Scene(secondaryLayout, 400, 200);
        Stage newWindow = new Stage();
        newWindow.setTitle("Window for sing up");
        newWindow.setScene(secondScene);
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.show();


        btnEnter.setOnAction(event -> {
            if (!singUpLogin.getText().trim().isEmpty()) {
                if (!singUpPass.getText().trim().isEmpty()) {
                    String msg = String.format("%s %s %s", singUpLogin.getText().trim(), singUpPass.getText().trim(), 1);
                    Network.sendMsg(new FileRequest(msg, "/auth"));
                    System.out.println(singUpLogin.getText().trim() + " " + singUpPass.getText().trim());
                }
            }
            try {
                AbstractMessage am = Network.readObject();
                if (am instanceof FileRequest) {
                    FileRequest fr = (FileRequest) am;
                    System.out.println(fr.getFileName() + "  " + fr.getActionPoint());
                    if (fr.getFileName().startsWith("_auth")) {
                        String[] parts = fr.getFileName().split("\\s");
                        boolean authIndex = Boolean.parseBoolean(parts[1]);
                        if (authIndex) {
                            try {
                                refreshClientData();
                                Network.sendMsg(new FileRequest(null, "list"));
                                AbstractMessage nm = Network.readObject();
                                if (am instanceof FileList) {
                                    FileList fl = (FileList) nm;
                                    refreshServerData(fl);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            newWindow.close();
                            loginPanel.setVisible(false);
                            loginPanel.setManaged(false);
                            cloudBox.setVisible(true);
                            cloudBox.setManaged(true);

                        } else {
                            //todo  ник не обновляется
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Клиент с таким логином уже существует");
                            alert.showAndWait();
                        }
                    }
                }

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

        });

    }
}
