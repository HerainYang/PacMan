package ghost;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigReader {
    JSONObject configFile;
    JSONParser parser;
    public ConfigReader() throws IOException, ParseException {
        FileReader reader = new FileReader("config.json");
        parser = new JSONParser();
        Object object = parser.parse(reader);
        configFile = (JSONObject) object;
    }

    public String getMapFile(){
        return (String) configFile.get("map");
    }

    public long getLives(){
        return (long) configFile.get("lives");
    }

    public long getSpeed(){
        return (Long) configFile.get("speed");
    }

    public long[] getGhostMode(){
        JSONArray jsonArray = (JSONArray) configFile.get("modeLengths");
        long[] ghostMode = new long[jsonArray.size()];
        for(int i = 0; i < jsonArray.size(); i ++){
            ghostMode[i] = (long) jsonArray.get(i);
        }
        return ghostMode;
    }

    public long getFrightenedLength(){
        return (long) configFile.get("frightenedLength");
    }
}

