package org.monarchinitiative.loinc2hpogui.github;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class GitHubLabelRetriever {

    private List<String> labels;

    public GitHubLabelRetriever() {
        labels=new ArrayList<>();
        retrieveLabels();
    }



    public List<String> getLabels(){ return labels; }



    private void parseLabelElement(Object obj) {
        JSONObject jsonObject = (JSONObject) obj;
        String name = jsonObject.get("name").toString();
        //System.out.println(jsonObject.toString() +"\n\t"+name);
        labels.add(name);

    }


    private void decodeJSON(String s) {
        Object obj= JSONValue.parse(s);
        JSONArray jsonArray = (JSONArray) obj;
        Iterator<String> iterator = jsonArray.iterator();
        jsonArray.forEach(label -> parseLabelElement(label) );
    }




    private void retrieveLabels()  {
        HttpURLConnection httpconnection=null;
        try {
            URL url = new URL("https://api.github.com/repos/obophenotype/human-phenotype-ontology/labels");
            httpconnection= (HttpURLConnection) url.openConnection();
            httpconnection.setDoOutput(true);
            Scanner scanner = new Scanner(url.openStream());
            String response = scanner.useDelimiter("\\Z").next();
            scanner.close();
            decodeJSON(response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(httpconnection != null)
            {
                httpconnection.disconnect();
            }
        }
    }
}