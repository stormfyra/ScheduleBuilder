package Model;

import Model.Shift;

import java.util.Set;

public class ProviderPreference {
    private String providerName;
    private Set<Shift> shifts;
    private String specialPreference;

    public ProviderPreference(String providerName, Set<Shift> shifts, String specialPreference) {
        this.providerName = providerName;
        this.shifts = shifts;
        this.specialPreference = specialPreference;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Set<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(Set<Shift> shifts) {
        this.shifts = shifts;
    }

    public String getSpecialPreference() {
        return specialPreference;
    }

    public void setSpecialPreference(String specialPreference) {
        this.specialPreference = specialPreference;
    }
}
