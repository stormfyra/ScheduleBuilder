import Model.Shift;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.*;

public class JdbcPreferenceDao {
    private final static JdbcTemplate jdbcTemplate = getJdbcTemplate();

    private static JdbcTemplate getJdbcTemplate() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/ProviderPreferences");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        return new JdbcTemplate(dataSource);
    }

    public static Set<String> getProviders() {
        Set<String> providers = new HashSet<>();
        String sqlQuery = "SELECT provider_name\n" +
                "FROM provider;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery);
        while (results.next()) {
            providers.add(results.getString("provider_name"));
        }
        return providers;
    }

    public static Set<String> getShiftsForProvider(String name) {
        Set<String> shifts = new HashSet<>();
        String sqlQuery = "SELECT shift_title\n" +
                "FROM provider_shift\n" +
                "JOIN provider ON provider_shift.provider_id = provider.provider_id\n" +
                "JOIN shift ON provider_shift.shift_id = shift.shift_id\n" +
                "WHERE provider_name = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, name);
        while (results.next()) {
            shifts.add(results.getString("shift_title"));
        }
        return shifts;
    }

    public static String getSpecialPreferencesForProvider(String name) {
        String sqlQuery = "SELECT special_preference\n" +
                "FROM provider\n" +
                "WHERE provider_name = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sqlQuery, name);
        if (result.next()) {
            return result.getString("special_preference");
        }
        return null;
    }

    public static Map<String, Shift> getShifts() {
        Map<String, Shift> shiftsWithDetails = new HashMap<>();
        String sqlQuery = "SELECT * FROM shift";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery);
        while (results.next()) {
            Shift shift = mapRowToShift(results);
            shiftsWithDetails.put(results.getString("shift_title"), shift);
        }
        return shiftsWithDetails;
    }

    private static Shift mapRowToShift(SqlRowSet results) {
        Shift shift = new Shift();
        shift.setShiftId(results.getInt("shift_id"));
        shift.setShiftTitle(results.getString("shift_title"));
        shift.setAppShift(results.getBoolean("is_app_shift"));
        shift.setLocation(results.getString("hospital"));
        shift.setStartTime(Objects.requireNonNull(results.getTime("start_time")).toLocalTime());
        shift.setEndTime();
        return shift;
    }
}
