package com.example.proyectos1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Reproductor extends Application {

    private MediaPlayer mediaPlayer;
    private final List<String> audioFiles = new ArrayList<>();
    private final ListView<String> audioListView = new ListView<>();
    private boolean isPaused = false;
    private double pauseTime = 0;

    @Override
    public void start(Stage primaryStage) {
        // Configurar el título de la ventana principal
        primaryStage.setTitle("Mi Aplicación");

        // Crear botones
        Button btnSeleccionar = new Button("Seleccionar archivo .wav");
        Button btnReproducir = new Button("Reproducir");
        Button btnDetener = new Button("Detener");
        Button btnPausa = new Button("Pausa");

        // Configurar los eventos para los botones
        btnSeleccionar.setOnAction(e -> seleccionarArchivo(primaryStage));
        btnReproducir.setOnAction(e -> reproducirAudio());
        btnDetener.setOnAction(e -> detenerAudio());
        btnPausa.setOnAction(e -> pausaAudio());

        // Configurar el ListView para mostrar los archivos de audio
        audioListView.setPrefHeight(150);
        audioListView.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        audioListView.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(".wav")) {
                    audioFiles.add(file.toURI().toString());
                    audioListView.getItems().add(file.getName());
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });

        // Crear un VBox para organizar los elementos verticalmente
        VBox vbox = new VBox(10); // Espacio de 10 píxeles entre los elementos
        vbox.setPadding(new Insets(10)); // Espacio de 10 píxeles alrededor del VBox
        vbox.getChildren().addAll(btnSeleccionar, audioListView);

        // Crear un HBox para organizar los botones horizontalmente
        HBox hbox = new HBox(6); // Espacio de 10 píxeles entre los botones
        hbox.setPadding(new Insets(0)); // Espacio de 10 píxeles alrededor del HBox
        hbox.getChildren().addAll(btnReproducir, btnDetener, btnPausa);

        // Agregar el HBox al VBox
        vbox.getChildren().add(hbox);

        // Configurar la escena principal con el VBox
        primaryStage.setScene(new Scene(vbox, 300, 300));

        // Mostrar la ventana principal
        primaryStage.show();
    }

    private void seleccionarArchivo(Stage owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos WAV", "*.wav"));
        File selectedFile = fileChooser.showOpenDialog(owner);
        if (selectedFile != null) {
            String filePath = selectedFile.toURI().toString();
            audioFiles.add(filePath);
            audioListView.getItems().add(selectedFile.getName());
        }
    }

    private void reproducirAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        String selectedAudio = audioListView.getSelectionModel().getSelectedItem();
        if (selectedAudio != null) {
            String filePath = audioFiles.get(audioListView.getSelectionModel().getSelectedIndex());
            Media media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }
    }

    private void detenerAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void pausaAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                pauseTime = mediaPlayer.getCurrentTime().toMillis();
                mediaPlayer.pause();
                isPaused = true;
            } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED && isPaused) {
                mediaPlayer.setStartTime(mediaPlayer.getStartTime().add(Duration.millis(pauseTime)));
                mediaPlayer.play();
                isPaused = false;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

