# Assignment 2 - Dictionaries

CS8050 Group 3

Nick Allegretti, Jingsong Tan, Keven Xue

## Dictionary Population:
The dictionary populates with Pokémon by parsing a CSV file. The CSV follows the format: Pokémon Name, Type(s), Height, Image Path, Cry Path, and Flavor Text Path. The program is intended to be used with the included `pokemon_data.csv` file. This file was generated using a series of Python scripts we created to scrape websites containing images and sounds of all the Pokémon. Additionally, we utilized the Pokémon API to gather information regarding Pokémon height, type, and description. Because of these scripts, which automated the compilation of our data, we were able to include over 800 Pokémon in our program.

## Running the Program:
To start the program, run the `PokemonApp.java` file. This program will populate the BST with the entries from the `pokemon_data.csv` at the start. To repopulate the program, pressing the 'reload' button will reset the BST and repopulate it with the `pokemon_data.csv`. This action will undo any deletes performed on the tree.

## Searching:
Pokémon can be searched by full or partial name, as well as by a height range. A search string present in the name of multiple Pokémon will return all Pokémon present in the BST whose name contains the string. Height range can be searched with name or separately. Searching a max height without specifying a min height will return all Pokémon shorter than the max height. Conversely, a search specifying a min height without a max will return all Pokémon taller than the minimum height. To perform the search, press the "Find" button.

## Play Cry/Stop Cry:
These buttons play and stop the cry of the Pokémon. Each Pokémon has a unique cry.

## Delete:
This button removes the active Pokémon currently being displayed from the BST. A deleted Pokémon will no longer appear in search results or navigation until the "reload" button is used.

## Navigation Buttons:
Pokémon are sorted in alphabetical order. The buttons navigate the BST. When a search result is active, the buttons will only navigate Pokémon meeting the search criteria. If there are no Pokémon to navigate, either due to the search or removal, the buttons will be disabled, and a message will display stating there are no available Pokémon to display.