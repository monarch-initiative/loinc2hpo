package org.monarchinitiative.loinc2hpogui.gui;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitHubPopupTest {
    @Test
    public void retrieveSuggestedTerm() throws Exception {

        String issue = "Suggest creating a new child term of %s [%s] for Loinc %s [%s]\n" +
                "New term label:\n" +
                "New term comment (if any):\n" +
                "Your biocurator ID for loinc2hpo (if desired): %s";
        GitHubPopup popup = new GitHubPopup(null);
        popup.setGithubIssueText(issue);
        assertEquals("UNKNOWN", popup.retrieveSuggestedTerm());
    }

    @Test
    public void retrieveSuggestedTerm2() throws Exception {

        String issue = "Suggest creating a new child term of %s [%s] for Loinc %s [%s]\n" +
                "New term label:   Increased basophil count   \n" +
                "New term comment (if any):\n" +
                "Your biocurator ID for loinc2hpo (if desired): %s";
        GitHubPopup popup = new GitHubPopup(null);
        popup.setGithubIssueText(issue);
        assertEquals("Increased basophil count", popup.retrieveSuggestedTerm());
    }

    @Test
    public void retrieveSuggestedTerm3() throws Exception {

        String issue = "Suggest creating a new child term of %s [%s] for Loinc %s [%s]\n" +
                "New term label   Increased basophil count   \n" +
                "New term comment (if any):\n" +
                "Your biocurator ID for loinc2hpo (if desired): %s";
        GitHubPopup popup = new GitHubPopup(null);
        popup.setGithubIssueText(issue);
        assertEquals("UNKNOWN", popup.retrieveSuggestedTerm());
    }
}