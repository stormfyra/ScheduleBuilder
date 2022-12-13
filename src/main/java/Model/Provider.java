package Model;

public class Provider {
    private int providerId;
    private String providerName;
    private String specialPreference;
    private boolean isApp;
    private boolean usesScribes;

    public Provider(int providerId, String providerName, String specialPreference, boolean isApp, boolean usesScribes) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.specialPreference = specialPreference;
        this.isApp = isApp;
        this.usesScribes = usesScribes;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getSpecialPreference() {
        return specialPreference;
    }

    public void setSpecialPreference(String specialPreference) {
        this.specialPreference = specialPreference;
    }

    public boolean isApp() {
        return isApp;
    }

    public void setApp(boolean app) {
        isApp = app;
    }

    public boolean isUsesScribes() {
        return usesScribes;
    }

    public void setUsesScribes(boolean usesScribes) {
        this.usesScribes = usesScribes;
    }
}
