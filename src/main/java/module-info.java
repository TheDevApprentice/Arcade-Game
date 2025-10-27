module org.example.snakegame {
    // Modules JavaFX requis
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    // Exporter tous nos packages pour JavaFX
    exports org.example.snakegame;
    exports org.example.snakegame.common;
    exports org.example.snakegame.snake;
    exports org.example.snakegame.pong;

    // Permettre à JavaFX d'accéder à nos classes via réflexion
    opens org.example.snakegame to javafx.fxml;
    opens org.example.snakegame.snake to javafx.fxml;
    opens org.example.snakegame.pong to javafx.fxml;
    opens org.example.snakegame.common to javafx.fxml;
}