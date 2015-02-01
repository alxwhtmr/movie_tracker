package com.github.alxwhtmr.cinematracker.ui;

import com.github.alxwhtmr.cinematracker.Constants;
import com.github.alxwhtmr.cinematracker.Movie;
import com.github.alxwhtmr.cinematracker.TrackerApp;
import com.github.alxwhtmr.cinematracker.Utils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;

import static com.github.alxwhtmr.cinematracker.Constants.UI.*;

/**
 * The {@code UiMain} class represents the UI form
 * with a simple progress gif image and
 * {@code TableView} instance that contains
 * processed movies data
 *
 * @since 30.01.2015
 */
public class UiMain extends Application {
    private TrackerApp trackerApp;
    private Stage stage;
    private Scene scene;
    private GridPane mainPane;
    private GridPane topPane;
    private GridPane tablePane;
    private GridPane labelPane;
    private Label emptyLabel;
    private GridPane imgPane;
    private TableView table;

    public UiMain(TrackerApp trackerApp) {
        this.trackerApp = trackerApp;
    }

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        initUi(stage);
    }

    public void initUi(Stage stage) {
        mainPane = new GridPane();
        mainPane.setId("pane");
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setVgap(10);

        initTopPane();
        emptyLabel = new Label();
        emptyLabel.setMinHeight(IMAGE_HEIGHT);
        mainPane.addRow(1, emptyLabel);
        initTable();

        this.stage = stage;
        stage.setTitle(Constants.UI.APP_TITLE);
        stage.setWidth(700);
        stage.setHeight(700);
        scene = new Scene(mainPane);

        Image icon;
        try {
            icon = new Image(Constants.UI.Images.APP_ICON);
        } catch (IllegalArgumentException e) {
            Utils.logErr(e.getCause());
            icon = new Image("file:"+Constants.UI.Images.APP_ICON);
        }
        stage.getIcons().add(icon);
        stage.setScene(scene);

        System.out.println(System.getProperty("user.dir"));
        try {
            scene.getStylesheets().add("file:"+Constants.UI.CSS_FILE);
        } catch (IllegalArgumentException e) {
            Utils.logErr(e.getCause());
//            scene.getStylesheets().add("file:"+Constants.UI.CSS_FILE);
        }
        stage.show();
    }

    public void setTableLabel() {
        Label label1 = new Label(String.format(FORMAT_LABEL1, Constants.Misc.RANGE));
        label1.setFont(new Font(Constants.UI.TABLE_LABEL_FONT, 20));
        label1.setAlignment(Pos.CENTER);
        Label label2 = new Label(String.format(FORMAT_LABEL2, Constants.Movies.MIN_RATING));
        label2.setFont(new Font(Constants.UI.TABLE_LABEL_FONT, 20));
        label2.setAlignment(Pos.CENTER);
        labelPane = new GridPane();

        labelPane.setMinHeight(IMAGE_HEIGHT);
        labelPane.setAlignment(Pos.BOTTOM_CENTER);
        labelPane.add(label1, 0, 0);
        labelPane.add(label2, 0, 1);
        GridPane.setHalignment(label2, HPos.CENTER);
        mainPane.getChildren().removeAll(imgPane);
        mainPane.addRow(1, labelPane);
    }

    public void setTableData(LinkedList<Movie> allMovies) {
        ObservableList<Movie> data = FXCollections.observableArrayList(allMovies);
        table.setItems(data);
    }

    private void initTopPane() {
        topPane = new GridPane();
        topPane.setAlignment(Pos.CENTER);
        topPane.setHgap(30);
        ObservableList<Integer> days =
                FXCollections.observableArrayList(
                    7, 15, 30, 60, 90
                );
        ComboBox daysBox = new ComboBox(days);
        daysBox.setPromptText("Days range");
        daysBox.setTooltip(new Tooltip("Default: " + Constants.Misc.RANGE));
        daysBox.setId("bevel-grey");

        ObservableList<Double> ratings =
                FXCollections.observableArrayList(
                        6.0, 6.5, 7.0, 7.5, 8.0
                );
        ComboBox ratingsBox = new ComboBox(ratings);
        ratingsBox.setPromptText("Minimum rating");
        ratingsBox.setTooltip(new Tooltip("Default: " + Constants.Movies.MIN_RATING));
        ratingsBox.setId("bevel-grey");

        Button processBtn = new Button("Process");
        processBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainPane.getChildren().removeAll(labelPane, emptyLabel, imgPane);
                if (daysBox.getValue() != null && ratingsBox.getValue() != null) {
                    Constants.Misc.RANGE = Integer.parseInt(daysBox.getValue().toString());
                    Constants.Movies.MIN_RATING = Double.parseDouble(ratingsBox.getValue().toString());
                }
                setImage(Constants.UI.Images.LOAD_IMG);
                trackerApp.getTrackerService().start();
            }
        });
        processBtn.setId("glass-grey");

        topPane.addRow(0, daysBox, ratingsBox, processBtn);
        mainPane.addRow(0, topPane);
    }

    private void initTable() {
        tablePane = new GridPane();
        tablePane.setId("glass-grey");
        tablePane.setAlignment(Pos.CENTER);
        table = new TableView();
        table.setMinWidth(Constants.UI.TABLE_WIDTH);
        table.setMaxWidth(Constants.UI.TABLE_WIDTH);
        table.getColumns().addAll(initColumns());
        table.setEditable(true);
        tablePane.add(table, 0, 0);
        mainPane.addRow(2, tablePane);
    }

    private ArrayList<TableColumn> initColumns() {
        ArrayList<TableColumn> columns = new ArrayList<>();
        String[][] cols = {
                {"Eng", "titleEng"},
                {"Rus", "titleRus"},
                {"Rating", "rating"},
                {"Director", "director"},
                {"Premiere", "premiere"},
                {"IMDB", "imdbLinkShort"}
        };

        for (String[] s : cols) {
            TableColumn column = new TableColumn(s[0]);
            column.setCellValueFactory(new PropertyValueFactory<Movie, String>(s[1]));
            if (s[0].equalsIgnoreCase("Rating")) {
                column.setMinWidth(TABLE_WIDTH / cols.length * RATING_COL_MULTIPLIER);
                column.setMaxWidth(TABLE_WIDTH / cols.length * RATING_COL_MULTIPLIER);
            } else if (s[0].equalsIgnoreCase("IMDB")) {
                double cw = TABLE_WIDTH - ((cols.length-2)*(TABLE_WIDTH/cols.length)) - (TABLE_WIDTH/cols.length*RATING_COL_MULTIPLIER);
                column.setMinWidth(cw);
                column.setMaxWidth(cw);
            }
            else {
                column.setMinWidth(TABLE_WIDTH / cols.length);
            }
            columns.add(column);
        }
        return columns;
    }


    private void setImage(String imgLink) {
        Utils.logInfo(System.getProperty("user.dir"));
        Image image;
        try {
            image = new Image(imgLink, true);
        } catch (IllegalArgumentException e) {
            Utils.logErr(e);
            image = new Image("file:"+imgLink, true);
        }
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imgPane = new GridPane();
        imgPane.setAlignment(Pos.CENTER);
        imgPane.setMinHeight(IMAGE_HEIGHT);
        imgPane.add(imageView, 0, 0);
        mainPane.getChildren().removeAll(emptyLabel);
        mainPane.addRow(1, imgPane);
    }
}
