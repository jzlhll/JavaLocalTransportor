package com.allan.localnetworktransport;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HelloApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent v = fxmlLoader.load();
        HelloController controller = fxmlLoader.getController();
        controller.init();
        Scene scene = new Scene(v, 500, 360);
        stage.setTitle("局域网传输器");
        stage.setScene(scene);
        stage.show();

        EventHandler<DragEvent> dragOver = event-> {
            if (event.getDragboard() != null && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        };
        EventHandler<DragEvent> dragDrop = event -> {
            System.out.println("drag dropped!");
            List<File> currentDropped = event.getDragboard().getFiles();
            if (currentDropped != null && currentDropped.size() >= 1) {
                for (var file : currentDropped) {
                    controller.setFile(file.getAbsolutePath());
                }
            }
        };
        //因为我们默认它显示；直接上来直接设置tabPane即可。
        controller.mainBox.setOnDragOver(dragOver);
        controller.mainBox.setOnDragDropped(dragDrop);
    }

    public static void main(String[] args) {
        launch();
    }
}