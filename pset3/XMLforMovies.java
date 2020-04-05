/*
 * XMLforMovies
 * 
 * A class for objects that are able to convert movie data from the 
 * relational database used in PS 1 to XML.
 * 
 * Before compiling this program, you must download the JAR file for the
 * SQLite JDBC Driver and add it to your classpath. See the JDBC-specific
 * notes in the assignment for more details.
 */

import java.util.*;     // needed for the Scanner class
import java.sql.*;      // needed for the JDBC-related classes
import java.io.*;       // needed for the PrintStream class

public class XMLforMovies {
    private Connection db;   // a connection to the database
    
    /*
     * XMLforMovies constructor - takes the name of a SQLite file containing
     * a Movie table like the one from PS 1, and creates an object that 
     * can be used to convert the data in that table to XML.
     * 
     * ** YOU SHOULD NOT CHANGE THIS METHOD **
     */
    public XMLforMovies(String dbFilename) throws SQLException {
        this.db = DriverManager.getConnection("jdbc:sqlite:" + dbFilename);
    }
    
    /*
     * idFor - takes the name of a movie and returns the id number of 
     * that movie in the database as a string. If the movie is not in the 
     * database, it returns an empty string.
     * 
     * ** YOU SHOULD NOT CHANGE THIS METHOD **
     */
    public String idFor(String name) throws SQLException {
        String query = "SELECT id FROM Movie WHERE name = '" + name + "';";
        Statement stmt = this.db.createStatement();
        ResultSet results = stmt.executeQuery(query);
        
        if (results.next()) {    
            String id = results.getString(1);
            return id;
        } else {
            return "";
        }
    }   
    
    /*
     * simpleElem - takes the name and value of an XML element and 
     * returns a string representation of that XML element
     */
    public String simpleElem(String name, String value) {

        StringBuilder xmlElement = new StringBuilder();

        xmlElement.append("<");
        xmlElement.append(name);
        xmlElement.append(">");
        xmlElement.append(value);
        xmlElement.append("</");
        xmlElement.append(name);
        xmlElement.append(">");

        return xmlElement.toString();
    }
    
    /*
     * fieldsFor - takes a string representing the id number of a movie
     * and returns a sequence of XML elements for the non-null field values
     * of that movie in the database. If there is no movie with the specified
     * id number, the method returns an empty string.
     */
    public String fieldsFor(String movieID) throws SQLException {

        String selectColumns = "SELECT * FROM Movie WHERE id='"+ movieID+"';";
        Statement statement = this.db.createStatement();
        ResultSet resultSet = statement.executeQuery(selectColumns);
        Map<String, String> resultsMap = new LinkedHashMap<>();

        if(resultSet.next()){
            for(int i = 2; i <= resultSet.getMetaData().getColumnCount(); i++) {
                resultsMap.put(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));
            }
        }

        StringBuilder xmlResult = new StringBuilder();

        for(String result : resultsMap.keySet()){
            if(resultsMap.get(result) != null){
                xmlResult.append("    ");
                xmlResult.append(simpleElem(result, resultsMap.get(result)));
                xmlResult.append("\n");
            }
        }


        return xmlResult.toString().isEmpty() ? "" : xmlResult.toString();
    }
    
    /*
     * actorsFor - takes a string representing the id number of a movie
     * and returns a single complex XML element named "actors" that contains a
     * nested child element named "actor" for each actor associated with that
     * movie in the database. If there is no movie with the specified
     * id number, the method returns an empty string.
     */
    public String actorsFor(String movieID) throws SQLException {

        String query = "SELECT Person.name FROM Person INNER JOIN Actor on Person.id = Actor.actor_id, Movie " +
                "WHERE Actor.movie_id = Movie.id AND Movie.id='"+movieID+"' " +
                "ORDER BY Person.name;";
        Statement statement = this.db.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        StringBuilder innerResult = new StringBuilder();

        while(resultSet.next()){
            innerResult.append("      ");
            innerResult.append(simpleElem("actor", resultSet.getString(1)));
            innerResult.append("\n");
        }

        StringBuilder xmlResult = new StringBuilder();

        if(innerResult.toString() != null && !innerResult.toString().isEmpty()){
            xmlResult.append("    ");
            xmlResult.append("<actors>");
            xmlResult.append("\n");
            xmlResult.append(innerResult);
            xmlResult.append("    ");
            xmlResult.append("</actors>");
            xmlResult.append("\n");
        }

        return xmlResult.toString().isEmpty() ? "" : xmlResult.toString();
    }    
    
    /*
     * directorsFor - takes a string representing the id number of a movie
     * and returns a single complex XML element named "directors" that contains a
     * nested child element named "director" for each director associated with 
     * that movie in the database. If there is no movie with the specified
     * id number, the method returns an empty string.
     */
    public String directorsFor(String movieID) throws SQLException {

        String query = "SELECT Person.name from Person INNER JOIN Director on Person.id=" +
                "Director.director_id, Movie WHERE Movie.id = Director.movie_id AND Movie.id='" +
                movieID + "' ORDER BY Person.name";
        Statement statement = this.db.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        StringBuilder innerResult = new StringBuilder();

        while(resultSet.next()){
            innerResult.append("      ");
            innerResult.append(simpleElem("director", resultSet.getString(1)));
            innerResult.append("\n");
        }

        StringBuilder xmlResult = new StringBuilder();

        if(innerResult.toString() != null && !innerResult.toString().isEmpty()){
            xmlResult.append("    ");
            xmlResult.append("<directors>");
            xmlResult.append("\n");
            xmlResult.append(innerResult);
            xmlResult.append("    ");
            xmlResult.append("</directors>");
            xmlResult.append("\n");
        }

        return xmlResult.toString().isEmpty() ? "" : xmlResult.toString();
    }    
    
    /*
     * elementFor - takes a string representing the id number of a movie
     * and returns a single complex XML element named "movie" that contains
     * nested child elements for all of the fields, actors, and directors 
     * associated with  that movie in the database. If there is no movie with 
     * the specified id number, the method returns an empty string.
     */
    public String elementFor(String movieID) throws SQLException {

        StringBuilder xmlResult = new StringBuilder();

        if(!fieldsFor(movieID).isEmpty() || !actorsFor(movieID).isEmpty() || !directorsFor(movieID).isEmpty()){
            xmlResult.append("  ");
            xmlResult.append("<movie id=\""+movieID+"\">");
            xmlResult.append("\n");
            xmlResult.append(fieldsFor(movieID));
            xmlResult.append(actorsFor(movieID));
            xmlResult.append(directorsFor(movieID));
            xmlResult.append("  ");
            xmlResult.append("</movie>");
        }

        return xmlResult.toString().isEmpty() ? "" : xmlResult.toString();
    }

    /*
     * createFile - creates a text file with the specified filename containing 
     * an XML representation of the entire Movie table.
     * 
     * ** YOU SHOULD NOT CHANGE THIS METHOD **
     */
    public void createFile(String filename) 
      throws FileNotFoundException, SQLException 
    {
        PrintStream outfile = new PrintStream(filename);    
        outfile.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>");
        outfile.println("<Movies>");
        
        // Use a query to get all of the ids from the Movie Table.
        Statement stmt = this.db.createStatement();  
        ResultSet results = stmt.executeQuery("SELECT id FROM Movie;");
        
        // Process one movie id at a time, creating its 
        // XML element and writing it to the output file.
        while (results.next()) {
            String movieID = results.getString(1);
            outfile.println(elementFor(movieID));
        }
        
        outfile.println("</Movies>");
        
        // Close the connection to the output file.
        outfile.close();
        System.out.println("movies.xml has been written.");
    }
    
    /*
     * closeDB - closes the connection to the database that was opened when 
     * the XMLforMovies object was constructed
     * 
     * ** YOU SHOULD NOT CHANGE THIS METHOD **
     */
    public void closeDB() throws SQLException {
        this.db.close();
    }
    
    public static void main(String[] args) 
        throws ClassNotFoundException, SQLException, FileNotFoundException
    {
        // Get the name of the SQLite database file from the user.
        Scanner console = new Scanner(System.in);
        System.out.print("Enter the name of the database file: ");
        String dbFilename = console.next();
        
        // Create an XMLforMovies object for the SQLite database, and
        // convert the entire database into an XML file.
        XMLforMovies xml = new XMLforMovies(dbFilename);
//        System.out.println(xml.fieldsFor(xml.idFor("Black Panther")));
//        System.out.println(xml.fieldsFor(xml.idFor("West Side Story")));
//        System.out.println(xml.fieldsFor("1234567"));

//        System.out.println(xml.actorsFor(xml.idFor("Black Panther")));
//        System.out.println(xml.actorsFor(xml.idFor("Wonder Woman")));
//        System.out.println(xml.actorsFor("1234567"));

//        System.out.println(xml.directorsFor(xml.idFor("Black Panther")));
//        System.out.println(xml.directorsFor(xml.idFor("Frozen")));
//        System.out.println(xml.directorsFor("1234567"));

        System.out.println(xml.elementFor("1234567"));

        xml.createFile("movies.xml");
        xml.closeDB();
    }
}