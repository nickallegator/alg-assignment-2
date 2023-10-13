package com.example.pokedex;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.bootstrapfx.scene.layout.Panel;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class PokemonApp extends Application {
    private int currentIndex = 0;

    private Pokemon pokemon;

    // Filtered Pokemon records that are currently displaying
    private OrderedDictionary currentPokemons = new OrderedDictionary();

    // Holds data for all Pokemon records
    static class PokemonData {
        static OrderedDictionary data = new OrderedDictionary();
    }


    private Image noMatchImage;
    private ImageView imageView;

    private MediaPlayer mediaPlayer;
    private Button playCryButton;

    private Button stopCryButton;

    private Button smallestButton;

    private Button largestButton;

    private Button prevButton;

    private Button nextButton;

    private Button firstButton;

    private Label flavorTextArea;

    private Label nameLabel;
    private Label typeLabel;
    private Label heightLabel;

    private Button lastButton;

    private Button deleteButton;

    private Button reloadButton;

    private TextField searchBar;
    private TextField fromHeight;
    private TextField toHeight;
    private Button findButton;
    private Button exitButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Pokemon Info");

        loadPokemonDataFromCSV("/pokemon_data.csv");

        // Initially populate filteredPokemons with all Pokemon
        for (int i = 0; i < PokemonData.data.size(); i++) {
            currentPokemons.insert(PokemonData.data.getByIndex(i));
        }
        noMatchImage = new Image(getClass().getResourceAsStream("/question.png"));

        nameLabel = new Label();
        typeLabel = new Label();
        heightLabel = new Label();

        imageView = new ImageView();
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        playCryButton = new Button("Play Cry");
        stopCryButton = new Button("Stop Cry");
        flavorTextArea = new Label();
        flavorTextArea.setWrapText(true);
        flavorTextArea.setMaxWidth(200);

        searchBar = new TextField();
        searchBar.setPromptText("Search Pokémon by name...");

        fromHeight = new TextField();
        fromHeight.setPromptText("From Height (m)");
        // Prevent non numeric input
        // https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
        fromHeight.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    fromHeight.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        toHeight = new TextField();
        toHeight.setPromptText("To Height (m)");
        toHeight.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    toHeight.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        findButton = new Button("Find");
        findButton.setOnAction(e -> updateSearchResults());
        findButton.getStyleClass().add("button");

        exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));
        exitButton.getStyleClass().add("button");


        reloadButton = new Button("Reload");
        reloadButton.setOnAction(e -> reloadCSVData());


        HBox searchBox = new HBox(10, reloadButton, searchBar, fromHeight, toHeight, findButton, exitButton);

        smallestButton = new Button("Smallest");
        smallestButton.setOnAction(e -> showSmallestPokemon());

        largestButton = new Button("Largest");
        largestButton.setOnAction(e -> showLargestPokemon());

        prevButton = new Button("Previous");
        prevButton.setOnAction(e -> showPrevPokemon());

        nextButton = new Button("Next");
        nextButton.setOnAction(e -> showNextPokemon());

        firstButton = new Button("First");
        firstButton.setOnAction(e -> showFirstPokemon());

        lastButton = new Button("Last");
        lastButton.setOnAction(e -> showLastPokemon());

        deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deletePokemon());

        //Organizing navigation buttons in one row
        HBox navigationBox = new HBox(10, firstButton, prevButton, nextButton, lastButton, smallestButton, largestButton, deleteButton);

        VBox rightBox = new VBox(10, playCryButton, stopCryButton);

        // Organizing main content
        HBox mainContent = new HBox(10, imageView, flavorTextArea, rightBox);

        VBox vbox = new VBox(10, searchBox, nameLabel, typeLabel, heightLabel, mainContent, navigationBox);
        vbox.getStyleClass().add("background_box");

        Scene scene = new Scene(vbox, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Display the first Pokémon initially
        showPokemonAtIndex(currentIndex);
    }
    private void loadPokemonDataFromCSV(String resourcePath) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream(resourcePath)))) {
            // Skip the header line
            reader.readNext();

            String[] fields;
            while ((fields = reader.readNext()) != null) {
                if (fields.length >= 5) {
                    String name = fields[0].trim();
                    String type = fields[1].trim();
                    String imagePath = fields[3].trim();
                    String cryPath = fields[4].trim();
                    String flavorTextPath = fields[5].trim();
                    int height = Integer.parseInt(fields[2].trim());

                    Pokemon pokemon = new Pokemon(name, type, imagePath, cryPath, flavorTextPath, height);
                    PokemonData.data.insert(new ObjectRecord(new DataKey(name, height), pokemon));
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load Pokémon data: " + e.getMessage());
        } catch (CsvValidationException e) {
            System.out.println("There was an error validating the CSV data: " + e.getMessage());
        }
    }

    private void reloadCSVData() {
        currentPokemons.clear();
        PokemonData.data.clear();

        loadPokemonDataFromCSV("/pokemon_data.csv");

        for (int i = 0; i < PokemonData.data.size(); i++) {
            currentPokemons.insert(PokemonData.data.getByIndex(i));
        }

        currentIndex = 0;
        showPokemonAtIndex(currentIndex);
        
        //Enable buttons if previous search had no match
        toggleButtons(false);
    }


    private void showPokemon(ObjectRecord pokemonRecord) {
        if (pokemonRecord == null) {
            return;
        }

        pokemon = (Pokemon) pokemonRecord.value;

        nameLabel.setText("Name: " + pokemon.getName().toUpperCase());
        typeLabel.setText("Type: " + String.join(", ", pokemon.type));
        heightLabel.setText("Height: " + pokemon.getHeight()/10.0 + " m");


        InputStream imageStream = getClass().getClassLoader().getResourceAsStream(pokemon.getImagePath());
        imageView.setImage(new Image(imageStream));

        playCryButton.setOnAction(e -> {
            URL cryResource = getClass().getResource("/" + pokemon.getCryPath());
            if (cryResource == null) {
                System.out.println("Resource not found: " + pokemon.getCryPath());
                return;
            }
            Media cry = new Media(cryResource.toString());

            //reset media
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            mediaPlayer = new MediaPlayer(cry);
            mediaPlayer.play();
        });

        stopCryButton.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });



        flavorTextArea.setText(readFlavorText(pokemon.getFlavorTextPath()));

    }

    // Shows filtered pokemon
    private void updateSearchResults() {
        String searchQuery = searchBar.getText().toLowerCase().trim();
        Integer minHeight = null;
        Integer maxHeight = null;

        try {
            minHeight = Integer.parseInt(fromHeight.getText().trim()) * 10;
        } catch (NumberFormatException e) {
            // Do nothing, minHeight remains null
        }

        try {
            maxHeight = Integer.parseInt(toHeight.getText().trim()) * 10;
        } catch (NumberFormatException e) {
            //Do nothing, maxHeight remains null
        }

        currentPokemons.clear();

        for (int i = 0; i < PokemonData.data.size(); i++) {
            ObjectRecord record = PokemonData.data.getByIndex(i);
            Pokemon pokemon = (Pokemon) record.value;

            boolean nameMatches = pokemon.getName().toLowerCase().contains(searchQuery);
            boolean minHeightMatches = (minHeight == null) || (pokemon.getHeight() >= minHeight);
            boolean maxHeightMatches = (maxHeight == null) || (pokemon.getHeight() <= maxHeight);

            if (nameMatches && minHeightMatches && maxHeightMatches) {
                currentPokemons.insert(record);
            }
        }

        if (currentPokemons.size() == 0) {
            handleNoMatch();
        } else {
            showPokemon(currentPokemons.getByIndex(0));
            currentIndex = 0;
            //Enable buttons if previous search had no match
            toggleButtons(false);
        }
    }




    private void showPokemonAtIndex(int index) {
        ObjectRecord pokemonRecord = currentPokemons.getByIndex(index);
        if (pokemonRecord == null) {
            return;
        }

        showPokemon(pokemonRecord);

        currentIndex = index;  // Update the current index
    }

    private void showSmallestPokemon() {
        ObjectRecord smallestRecord = currentPokemons.getSmallest();
        if (smallestRecord == null) {
            return;
        }
        showPokemon(smallestRecord);
    }


    private void showLargestPokemon() {
        ObjectRecord largestRecord = currentPokemons.getLargest();
        if (largestRecord == null) {
            return;
        }
        showPokemon(largestRecord);

    }

    private void showPrevPokemon() {
        if (currentIndex > 0) {
            currentIndex--;
            showPokemonAtIndex(currentIndex);
        } else {
            showError("Can't show previous Pokemon! Already at the beginning of the list.");
        }
    }

    private void showNextPokemon() {
        if (currentIndex < currentPokemons.size() - 1) {
            currentIndex++;
            showPokemonAtIndex(currentIndex);
        } else {
            showError("Can't show next Pokemon! Already at the end of the list.");
        }
    }


    private void showFirstPokemon() {
        showPokemonAtIndex(0);
    }

    private void showLastPokemon() {
        showPokemonAtIndex(currentPokemons.size()-1);
    }
    private void handleNoMatch() {
        imageView.setImage(noMatchImage);
        nameLabel.setText("");
        typeLabel.setText("");
        heightLabel.setText("");
        flavorTextArea.setText("No Pokémon matches the search.");
        //Disable buttons if no pokemon match

        toggleButtons(true);
    }

    private void toggleButtons(boolean status) {
        playCryButton.setDisable(status);
        stopCryButton.setDisable(status);
        prevButton.setDisable(status);
        nextButton.setDisable(status);
        firstButton.setDisable(status);
        lastButton.setDisable(status);
        smallestButton.setDisable(status);
        largestButton.setDisable(status);
        deleteButton.setDisable(status);
    }


    private String readFlavorText(String path) {
        try {
            InputStream is = getClass().getResourceAsStream("/" + path);
            if (is == null) {
                return "Resource not found: " + path;
            }
            byte[] data = is.readAllBytes();
            return new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to read flavor text.";
        }
    }

    private void deletePokemon() {
        if (pokemon == null) {
            System.out.println("No Pokémon to delete.");
            return;
        }

        DataKey pokemonKey = new DataKey(pokemon.getName(), pokemon.getHeight());

        // Delete the Pokémon from both OrderedDictionary instances
        currentPokemons.remove(pokemonKey);
        PokemonData.data.remove(pokemonKey);

        pokemon = null;

        // If the current Pokemon dictionary is now empty, display a message
        if (currentPokemons.size() == 0) {
            handleNoMatch();
            return;
        }

        // If the deleted Pokémon was the last one in the dictionary, show the previous Pokémon
        if (currentIndex == currentPokemons.size()) {
            currentIndex--;
        }
        showPokemonAtIndex(currentIndex);
    }




    static class Pokemon {
        private String name;
        private List<String> type;
        private String imagePath;
        private String cryPath;
        private String flavorTextPath;
        private int height;


        public Pokemon(String name, String type, String imagePath, String cryPath, String flavorTextPath, int height) {
            this.name = name;
            this.type = Arrays.asList(type.split("\\s+"));
            this.imagePath = imagePath;
            this.cryPath = cryPath;
            this.flavorTextPath = flavorTextPath;
            this.height = height;

        }

        public String getName() {
            return name;
        }

        public int getHeight() {
            return height;
        }
        public String getImagePath() {
            return imagePath;
        }

        public String getCryPath() {
            return cryPath;
        }

        public String getFlavorTextPath() {
            return flavorTextPath;
        }

    }


    private void showError(String msg) {
        Alert a  = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.show();
    }
}
