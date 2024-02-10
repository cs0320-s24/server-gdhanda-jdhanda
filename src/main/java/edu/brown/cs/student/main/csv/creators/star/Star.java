package edu.brown.cs.student.main.csv.creators.star;

/**
 * A very basic class used in conjunction with StarCreator (which implements CreatorFromRow) to test
 * various type inputs to the CreatorFromRow parameter in the CSVParser class.
 */
public class Star {
  private String id;
  private String name;
  private String[] coords;

  /**
   * Basic constructor for a Star.
   *
   * @param id The star's unique integer id.
   * @param name The star's proper name.
   * @param coords The star's xyz coords.
   */
  public Star(String id, String name, String[] coords) {
    this.id = id;
    this.name = name;
    this.coords = coords;
  }

  /** Trivial getter for ID. */
  public String getID() {
    return this.id;
  }

  /** Trivial getter for ID. */
  public String getName() {
    return this.name;
  }

  /** Trivial getter for Coordinates. */
  public String[] getCoords() {
    return this.coords;
  }
}
