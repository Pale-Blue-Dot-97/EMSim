import java.util.*;
import java.io.*;
/**
 * Name: DataOutput
 * Purpose: To allow strings to be written to text files
 *
 * @author Harry Baker
 * @version 1.0
 * Date: 01.12.2017
 *
 */
public class DataOutput
  {
    /**
     * Writes a string to a file
     *
     * @param data String of data to written to a file
     * @param filename String representing the name of the file
     *
     */
    public static void dataToFile(String data, String filename)
      {
        try
          {
            // Creates a new instance of the file in Java
            File file = new File(filename);

            if(! file.exists())
              {
                // Creates a new file with the name of filename
                file.createNewFile();
              }

            // Creates a filewriter to write data to the file
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close(); // Closes the file after data has been written to it

          }catch(IOException e)
            {
              // Catches and prints the error if it occurs, preventing a crash
              System.out.println("Error: " + e);
              e.printStackTrace();
            }
      }
  }
