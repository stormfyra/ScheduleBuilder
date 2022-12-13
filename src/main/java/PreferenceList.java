import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferenceList {
    private Set<String> preferences = new HashSet<>();
    private String specialPreferences = null;

    public PreferenceList() {
    }

    public PreferenceList(Set<String> preferences, String specialPreferences) {
        this.preferences = preferences;
        this.specialPreferences = specialPreferences;
    }

    public Set<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(Set<String> preferences) {
        this.preferences = preferences;
    }
}
