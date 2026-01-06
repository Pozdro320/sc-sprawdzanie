package pl.pozdro320.utils;

import lombok.AllArgsConstructor;
import pl.pozdro320.utils.EnhancedRequest.EnhancedResponse;

@AllArgsConstructor public class GithubUpdater {
    private final String user;
    private final String repo;


    public boolean hasUpdate(SemanticVersion version) {
        try {
            EnhancedResponse response = EnhancedRequest.builder()
                .url(
                    "https://api.github.com/repos/" + this.user + "/" + this.repo + "/releases/latest")
                .build()
                .send();
            SemanticVersion latest = new SemanticVersion(response.getAsJson()
                .get("tag_name")
                .getAsString());
            return latest.isNewerThan(version);
        } catch (Exception e) {
            return false;
        }
    }


    public String getDownloadLink() {
        return "https://github.com/" + this.user + "/" + this.repo + "/releases";
    }
}
