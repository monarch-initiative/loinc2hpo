package org.monarchinitiative.loinc2hpo.github;

import org.junit.Test;
import org.monarchinitiative.loinc2hpo.gui.GitHubPopup;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

import static org.junit.Assert.*;

public class GitHubPosterTest {
    @Test
    public void debugLabelsArray4Json1() throws Exception {

        //GitHubPoster poster = new GitHubPoster("userName", "password", "Loinc123", "content");
        GitHubPoster poster = new GitHubPoster(null, null, null, null);
        List<String> labels = new ArrayList<>();
        labels.add("loinc");
        labels.add("immunology");
        poster.setLabel(labels);
        System.out.println(poster.debugLabelsArray4Json());
        assertEquals("\"loinc\", \"immunology\"", poster.debugLabelsArray4Json());
    }


    @Test
    public void debugLabelsArray4Json2() throws Exception {

        //GitHubPoster poster = new GitHubPoster("userName", "password", "Loinc123", "content");
        GitHubPoster poster = new GitHubPoster(null, null, null, null);
        List<String> labels = new ArrayList<>();
        labels.add("lo\tin\tc");
        labels.add("immunology");
        poster.setLabel(labels);
        //System.out.println(poster.debugLabelsArray4Json());
        assertEquals("\"lo\\tin\\tc\", \"immunology\"", poster.debugLabelsArray4Json());
    }
    @Test
    public void debugReformatpayloadWithLabel1() throws Exception {

        GitHubPoster poster = new GitHubPoster(null, null, null, null);
        List<String> labels = new ArrayList<>();
        labels.add("loinc");
        labels.add("immunology");
        poster.setLabel(labels);
        String payload = "{\n" +
                "\"title\": \"null\",\n" +
                "\"body\": \"null\",\n" +
                "\"labels\": [ \"loinc\", \"immunology\"] }";
        assertEquals(payload, poster.debugReformatpayloadWithLabel());
    }

    @Test
    public void debugReformatpayloadWithLabel2() throws Exception {

        GitHubPoster poster = new GitHubPoster(null, null, null, null);
        List<String> labels = new ArrayList<>();
        labels.add("lo\"in\tc");
        labels.add("immunology");
        poster.setLabel(labels);
        String payload = "{\n" +
                "\"title\": \"null\",\n" +
                "\"body\": \"null\",\n" +
                "\"labels\": [ \"lo\\\"in\\tc\", \"immunology\"] }";
        assertEquals(payload, poster.debugReformatpayloadWithLabel());
    }

    @Test
    public void testPayload() throws Exception{
        GitHubPoster poster = new GitHubPoster(null, null, null, null);
        List<String> labels = new ArrayList<>();
        labels.add("loinc");
        labels.add("immunology");
        poster.setLabel(labels);
        System.out.println(poster.debugReformatpayloadWithLabel());
    }

    @Test
    public void testColor() throws Exception {
        Color color = Color.PINK;
        System.out.println("#" + color.toString().substring(2,8).toUpperCase());
        System.out.println(String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) ));
    }
}