import Model.Shift;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.*;

public class App {
    public static void main(String[] args) throws IOException {
        final int NUMBER_OF_COLUMNS = 8;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please provide the absolute path of the provider schedule Excel file: ");
        String path = scanner.nextLine();
        List<String[]> schedule = ExcelFileInteractor.getPhysicianSchedule(new FileInputStream(path));

        String scheduleName = schedule.get(0)[0];
        String[] scheduleNameWords = scheduleName.split(" ");
        String month = scheduleNameWords[scheduleNameWords.length - 2].substring(0, 3);
        String year = scheduleNameWords[scheduleNameWords.length - 1];
        int rowsPerWeekListed = getRowsPerWeekListed(schedule);
        int weeksListed = (schedule.size() - 2) / rowsPerWeekListed;

        Set<String> providers = JdbcPreferenceDao.getProviders();
        Map<String, Shift> shiftsToCover = JdbcPreferenceDao.getShifts();
        Map<String, Set<String>> providersPreferences = new HashMap<>();
        for (String provider : providers) {
            Set<String> providerPreferences = JdbcPreferenceDao.getShiftsForProvider(provider);
            providersPreferences.put(provider, providerPreferences);
        }

        List<String[]> monthSchedule = new ArrayList<>();
        List<String[]> shifts = new ArrayList<>();
        String[] line;
        String[] monthNumbers = null;

        for (int i = 1; i <= rowsPerWeekListed; i++) {
            line = new String[NUMBER_OF_COLUMNS * weeksListed];
            line[0] = schedule.get(i + 1)[0];
            for (int j = 0; j < weeksListed; j++) {
                System.arraycopy(schedule.get(i + 1 + rowsPerWeekListed * j), 1, line,
                        1 + (NUMBER_OF_COLUMNS - 1) * j, NUMBER_OF_COLUMNS - 1);
            }
            if (i == 1) {
                monthSchedule.add(line);
                monthNumbers = line;
            } else if (shiftsToCover.containsKey(line[0])) {
                String shiftTitle = line[0];
                for (int j = 1; j < line.length; j++) {
                    String providerName = line[j];
                    if (providersPreferences.containsKey(providerName)) {
                        if (providersPreferences.get(providerName).contains(shiftTitle)) {
                            String[] scribeShift = new String[] {monthNumbers[j], shiftTitle, providerName};
                            shifts.add(scribeShift);
                        }
                    }
                }
                deleteUnnecessaryShifts(line, providersPreferences);
                monthSchedule.add(line);
            }
        }

        int monthStartsOn = 0;
        for (int i = 1; i < Objects.requireNonNull(monthNumbers).length; i++) {
            if (monthNumbers[i] != null) {
                monthStartsOn = i;
                break;
            }
        }

        ExcelFileInteractor.writeScribeSchedule(year, month, monthStartsOn, shifts);

        System.out.println(scheduleName);
        for (String[] strings : monthSchedule) {
            System.out.println();
            for (String string : strings) {
                System.out.print(string + "\t");
            }
        }
        for (String[] shift : shifts) {
            System.out.println(shift[0] + " " + shift[1] + " " + shift[2]);
        }
    }

    private static int getRowsPerWeekListed(List<String[]> schedule) {
        for (int i = 3; i < schedule.size(); i++) {
            if (schedule.get(i)[0].equals("null")) {
                return i - 2;
            }
        }
        return 1;
    }

    public static void deleteUnnecessaryShifts(String[] line, Map<String, Set<String>> providersPreferences) {
        String shiftTitle = line[0];
        for (int i = 1; i < line.length; i++) {
            String providerName = line[i];
            if (providersPreferences.containsKey(providerName)) {
                if (providersPreferences.get(providerName).contains(shiftTitle)) {
                    continue;
                }
            }
            line[i] = "null";
        }
    }
}
