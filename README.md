# COMP3310A1Y2024
Assignment 1 Starter code for COMP3310 - Checkers

## how to build and run this program using Gradle
Once you have this repository cloned to your local pc, you can use the command line (in the local vscode terminal for the repository)

```
./gradlew build
```

then

```
./gradlew run
```
to start playing the game. Once you've had a bit of fun with the game to see how it works / plays out, then you can start analysing the code.

## Analysing the code
The code files are located in the folowing director within the repository:

```
/app/src/main/java/assignment1
```
Within the assignment1 folder, you should see the following files

```
|
|- App.java
|- Board.java
|- Cell.java
|- CellIterator.java
|- Grid.java
|- Prompt.java
|- SQLiteConnectionManager.java
```
App.java is the starting point for the code. When the program is run, the entry point in the code is 
```
public static void main(String[] args) throws Exception{
    ....

```
From there, the App is called, which then creates the checkersGame Graphical user interface. The checkersGame class within App then calls the Board class. From there, you can trace through what Board, Grid, Cell, and Prompt do.

The Prompt class is the black bar at the bottom of the Game that displays what you have typed so far (before hitting enter).

## Working with Sonarlint
Note that SonarLint might need you to have all the .java files open to be able to do a scan of the files and present the messages with S numbers.