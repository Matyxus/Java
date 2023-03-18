package file_system;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.Iterator;

public class JsonFile {

    public static void main(String[] args) {
        load_game("test");
    }

    public static void load_game(String fileName) {
        // Check file existence
        String filePath = String.format(FilePath.SAVES, fileName);
        if (!FilePath.fileExists(filePath)) {
            System.err.println("File: " + filePath + " does not exist!");
            return;
        }
        JSONParser parser = new JSONParser();
        // Try to load file
        try (Reader reader = new FileReader(filePath)) {

            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            System.out.println(jsonObject);
            if (!jsonObject.containsKey("options")) {
                System.err.println("Missing 'options' in saved game!");
                return;

            } else if (!jsonObject.containsKey("history")) {
                System.err.println("Missing 'history' in saved game!");
                return;
            }
            
            /* 
            String name = (String) jsonObject.get("name");
            System.out.println(name);

            long age = (Long) jsonObject.get("age");
            System.out.println(age);
            
            // loop array
            JSONArray msg = (JSONArray) jsonObject.get("messages");
            Iterator<String> iterator = msg.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
            */

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
}
