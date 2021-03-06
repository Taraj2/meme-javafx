package sample.controllers;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import sample.State;
import sample.controllers.pages.*;
import sample.util.AlertsFactory;
import sample.util.Page;
import sample.util.SuperPage;
import sample.util.SuperProps;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private Pane mainContainer;

    @FXML
    private VBox mainMenu;

    private List<Class<? extends SuperPage>> pages;

    private Class<? extends SuperPage> initPage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPage = MainPage.class;
        setMenuItems();
        openInitPage();
        State.getIsAuthenticated().addListener((observable, oldValue, newValue) -> Platform.runLater(this::setMenuItems));
    }

    private void setMenuItems() {
        pages = new ArrayList<>();
        pages.add(MainPage.class);
        pages.add(QueuePage.class);
        pages.add(RandomPage.class);
        if (State.getIsAuthenticated().get()) {
            pages.add(AddPage.class);
            pages.add(AccountPage.class);
            pages.add(LogoutPage.class);
        } else {
            pages.add(LoginPage.class);
        }
        initializeMenu();
    }


    private void openInitPage() {
        loadNewPage(initPage, null);
    }

    private void buttonClicked(ActionEvent event) {
        Button source = ((Button) event.getSource());

        Class<? extends SuperPage> clazz = (Class<? extends SuperPage>) source.getUserData();
        loadNewPage(clazz, null);
    }


    private void loadNewPage(Class<? extends SuperPage> clazz, SuperProps superProps) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(clazz.getAnnotation(Page.class).resource()));
            Pane pane = loader.load();
            SuperPage controller = loader.getController();
            controller.setRouter(this::loadNewPage);
            controller.setProps(superProps);
            controller.init();
            Platform.runLater(() -> mainContainer.getChildren().setAll(pane));
        } catch (IOException e) {
            AlertsFactory.unknownError(e.getMessage());
        }
    }

    private void initializeMenu() {
        mainMenu.getChildren().removeIf(node -> node instanceof Button);
        for (Class<? extends SuperPage> page : pages) {
            createMenuButton(page.getAnnotation(Page.class).name(), page);
        }
    }

    private void createMenuButton(String text, Class<? extends SuperPage> page) {
        Button button = new Button(text);
        VBox.setVgrow(button, Priority.ALWAYS);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setUserData(page);
        VBox.setMargin(button, new Insets(0, 0, 10, 0));
        button.setOnAction(this::buttonClicked);
        mainMenu.getChildren().add(button);
    }
}
