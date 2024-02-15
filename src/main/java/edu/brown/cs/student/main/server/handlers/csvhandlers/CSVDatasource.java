package edu.brown.cs.student.main.server.handlers.csvhandlers;

import java.util.ArrayList;
import java.util.List;

public class CSVDatasource {

  List<List<String>> data;
  String filepath;
  boolean fileLoaded;

  public CSVDatasource() {
    this.data = new ArrayList<>();
    this.filepath = "";
    this.fileLoaded = false;
  }
}
