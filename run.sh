#!/usr/bin/bash
javac --module-path ~/Library/java/javafx-sdk-21.0.7/lib --add-modules javafx.controls,javafx.fxml ./*.java && \
java --module-path ~/Library/java/javafx-sdk-21.0.7/lib --add-modules javafx.controls,javafx.fxml App
