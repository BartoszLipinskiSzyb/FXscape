#!/usr/bin/bash
javac --module-path ~/Library/java/javafx-sdk-21.0.7/lib --add-modules javafx.controls,javafx.fxml ./*.java --class-path . && \
java --module-path ~/Library/java/javafx-sdk-21.0.7/lib --add-modules javafx.controls,javafx.fxml App
