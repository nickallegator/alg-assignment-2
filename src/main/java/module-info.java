module com.example.pokedex {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires javafx.media;
    requires com.opencsv;

    opens com.example.pokedex to javafx.fxml;
    exports com.example.pokedex;
}