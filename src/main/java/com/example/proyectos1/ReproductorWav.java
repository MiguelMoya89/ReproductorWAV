package com.example.proyectos1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
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
import java.util.Random;

public class ReproductorWav extends Application {

    private MediaPlayer mediaPlayer;
    private final List<String> audioFiles = new ArrayList<>();
    private final ListView<String> audioListView = new ListView<>();
    private boolean isPaused = false;
    private double pauseTime = 0;
    private int currentSongIndex = -1;
    private boolean isRandomMode = false;
    private final TextArea songNameTextArea = new TextArea();

    private final Slider velocidadSlider = new Slider(0.5, 2, 1);

    private final Slider volumenSlider = new Slider(0, 1, 0.5);


    @Override
    public void start(Stage primaryStage) {
        // Configurar el título de la ventana principal
        primaryStage.setTitle("Mi Aplicación");

        // Crear botones
        Button btnSeleccionar = new Button("Seleccionar archivo .wav");
        Button btnReproducir = new Button("▶");
        Button btnDetener = new Button("⏹");
        Button btnPausa = new Button("⏸");
        Button btnSiguiente = new Button("⏭");
        Button btnAleatorio = new Button("🔀");

        // Configurar los eventos para los botones
        btnSeleccionar.setOnAction(e -> seleccionarArchivo(primaryStage));
        btnReproducir.setOnAction(e -> reproducirAudio());
        btnDetener.setOnAction(e -> detenerAudio());
        btnPausa.setOnAction(e -> pausaAudio());
        btnSiguiente.setOnAction(e -> reproducirSiguiente());
        btnAleatorio.setOnAction(e -> reproducirAleatorio());

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

        // Configurar el área de texto para mostrar el nombre de la canción actual
        songNameTextArea.setEditable(false);
        songNameTextArea.setStyle("-fx-font-family: Arial; -fx-font-size: 14px;");

        // Configurar los sliders

        velocidadSlider.setPrefWidth(200);

        volumenSlider.setPrefWidth(200);



        velocidadSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setRate(newValue.doubleValue());
            }
        });



        volumenSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });

        // Crear un VBox para organizar los elementos verticalmente
        VBox vbox = new VBox(10); // Espacio de 10 píxeles entre los elementos
        vbox.setPadding(new Insets(10)); // Espacio de 10 píxeles alrededor del VBox
        vbox.getChildren().addAll(btnSeleccionar, audioListView);

        // Crear un HBox para organizar los botones horizontalmente
        HBox hbox = new HBox(6); // Espacio de 10 píxeles entre los botones
        hbox.setPadding(new Insets(0)); // Espacio de 10 píxeles alrededor del HBox
        hbox.getChildren().addAll(btnReproducir, btnDetener, btnPausa, btnSiguiente, btnAleatorio);

        // Crear un VBox para organizar los sliders verticalmente
        VBox slidersVBox = new VBox(10);
        slidersVBox.getChildren().addAll( velocidadSlider,  volumenSlider);

        // Crear un HBox para organizar el HBox de botones y el VBox de sliders
        HBox bottomHBox = new HBox(10);
        bottomHBox.getChildren().addAll(hbox);

        // Crear un VBox para organizar el HBox inferior y el songNameTextArea
        VBox bottomVBox = new VBox(10);
        bottomVBox.setPadding(new Insets(0, 10, 10, 10)); // Espacio de 10 píxeles alrededor del VBox en la parte inferior
        bottomVBox.getChildren().addAll(bottomHBox, songNameTextArea, slidersVBox);

        // Agregar el VBox de botones y sliders y el VBox inferior al VBox principal
        vbox.getChildren().add(bottomVBox);

        // Configurar la escena principal con el VBox
        primaryStage.setScene(new Scene(vbox, 300, 400));

        // Mostrar la ventana principal
        primaryStage.show();
    }

    private void reproducirAleatorio() {
        if (audioFiles.isEmpty()) {
            return;
        }
        isRandomMode = true;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPaused = false;
        }
        currentSongIndex = new Random().nextInt(audioFiles.size());
        String filePath = audioFiles.get(currentSongIndex);
        Media media = new Media(filePath);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        songNameTextArea.setText(audioListView.getItems().get(currentSongIndex));
        configurarMediaProperties();
    }

    private void reproducirSiguiente() {
        if (audioFiles.isEmpty()) {
            return;
        }
        if (isRandomMode) {
            reproducirAleatorio();
            return;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPaused = false;
        }
        if (currentSongIndex == audioFiles.size() - 1) {
            currentSongIndex = 0;
        } else {
            currentSongIndex++;
        }
        String filePath = audioFiles.get(currentSongIndex);
        Media media = new Media(filePath);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        songNameTextArea.setText(audioListView.getItems().get(currentSongIndex));
        configurarMediaProperties();
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
            if (isPaused) {
                mediaPlayer.setStartTime(Duration.millis(pauseTime));
                mediaPlayer.play();
                isPaused = false;
            } else {
                mediaPlayer.play();
            }
            songNameTextArea.setText(selectedAudio);
            configurarMediaProperties();
        }
    }

    private void detenerAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPaused = false;
        }
        songNameTextArea.setText("");
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

    private void configurarMediaProperties() {
        mediaPlayer.setRate(velocidadSlider.getValue());
        mediaPlayer.setVolume(volumenSlider.getValue());
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
