package com.github.alxwhtmr.cinematracker;

import java.io.IOException;

/**
 * The {@code TrackerApp} class represents main app class.
 * The TrackerService creates task for processing movies,
 * and after succeed, shows processed data in the UI
 *
 * @since 30.01.2015
 */

import com.github.alxwhtmr.cinematracker.ui.UiMain;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class TrackerApp extends Application {
    AllMovies allMovies;
    TrackerService trackerService;

    @Override
    public void start(Stage stage) throws Exception {
        trackerService = new TrackerService();
        UiMain uiMain = new UiMain(this);
        uiMain.initUi(stage);
        trackerService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("\n\nDone:\n\n" + t.getSource().getValue());
                uiMain.setTableLabel();
                uiMain.setTableData(allMovies.getAllMovies());
                trackerService.reset();
            }
        });
    }

    public TrackerService getTrackerService() {
        return trackerService;
    }

    public class TrackerService extends Service<String> {
        protected Task<String> createTask() {
            return new Task<String>() {
                protected String call()
                        throws IOException {
                    String result = null;
                    try {
                        allMovies = new AllMovies();
                        allMovies.setAllMovies();
                        allMovies.sort();
                        result = allMovies.toString();
                    }
                    finally {}
                    return result;
                }
            };
        }
    }

    public static void main(String[] args) {
        launch();
    }
}