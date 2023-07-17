module com.example.proyectos1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.proyectos1 to javafx.fxml;
    exports com.example.proyectos1;
}