import Model.Shift;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

public class ExcelFileInteractor {
    private static final String[] SCRIBE_SCHEDULE_COLUMN_TITLES = new String[] {"name", "location", "position",
            "start date", "end date", "start time", "finish time", "notes", "title", "open", "remote site"};
    private static final String LOCATION = "Washington";
    private static final String POSITION = "TECP : Tacoma General Hospital";
    private static final DateTimeFormatter TIME_FORMATTER_INITIAL = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter TIME_FORMATTER_FINAL = DateTimeFormatter.ofPattern("ha");
    private static final DateTimeFormatter DATE_FORMATTER_INITIAL = DateTimeFormatter.ofPattern("y LLL d");
    private static final DateTimeFormatter DATE_FORMATTER_FINAL = DateTimeFormatter.ofPattern("MM/d/yyyy");

    public static List<String[]> getPhysicianSchedule(FileInputStream file) throws IOException {
        final int NUMBER_OF_COLUMNS = 8;
        List<String[]> schedule = new ArrayList<>();
        Workbook scheduleReaderWorkbook = new HSSFWorkbook(file);
        Sheet scheduleReaderSheet = scheduleReaderWorkbook.getSheetAt(0);
        for (Row cells : scheduleReaderSheet) {
            String[] rowArray = new String[NUMBER_OF_COLUMNS];
            for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
                Cell cell = cells.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                String value = "null";
                if (cell != null) {
                    if (cell.getCellType() == NUMERIC) {
                        value = "" + cell.getNumericCellValue();
                    } else if (cell.getCellType() == STRING) {
                        value = cell.getStringCellValue();
                    }
                }
                rowArray[i] = value;
            }
            schedule.add(rowArray);
        }
        return schedule;
    }

    public static void writeScribeSchedule(String year, String month, int monthStartsOn, List<String[]> shifts) {
        Set<String> providers = JdbcPreferenceDao.getProviders();
        Map<String, Shift> shiftsToCover = JdbcPreferenceDao.getShifts();
        Map<String, String> specialPreferences = new HashMap<>();
        for (String provider : providers) {
            String specialPreference = JdbcPreferenceDao.getSpecialPreferencesForProvider(provider);
            specialPreferences.put(provider, specialPreference);
        }

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("new sheet");

        Row row = sheet.createRow(0);
        for (int i = 0; i < SCRIBE_SCHEDULE_COLUMN_TITLES.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(SCRIBE_SCHEDULE_COLUMN_TITLES[i]);
        }

        for (int i = 0; i < shifts.size(); i++) {
            row = sheet.createRow(i + 1);
            String[] shift = shifts.get(i);
            String dayOfMonth = shift[0];
            String shiftTitle = shift[1];
            String provider = shift[2];
            Shift details = shiftsToCover.get(shiftTitle);
            String location = details.getLocation();
            LocalDate startDate = LocalDate.parse(year + " " + month + " " + dayOfMonth, DATE_FORMATTER_INITIAL);
            LocalDate endDate =
                    details.getEndTime().isBefore(details.getStartTime()) ?
                            startDate.plusDays(1) :
                            startDate;
            LocalTime startTime = details.getStartTime();
            LocalTime endTime = details.getEndTime();
            int modSevenSunday;
            int modSevenSaturday;
            if (monthStartsOn == 1) {
                modSevenSunday = 0;
                modSevenSaturday = 1;
            } else if (monthStartsOn == 2) {
                modSevenSaturday = 6;
                modSevenSunday = 0;
            } else {
                modSevenSunday = 8 - monthStartsOn;
                modSevenSaturday = 9 - monthStartsOn;
            }
            boolean isWeekend = Integer.parseInt(dayOfMonth) % 7 == modSevenSunday || Integer.parseInt(dayOfMonth) % 7 == modSevenSaturday;
            if (specialPreferences.containsKey(provider)) {
                if (specialPreferences.get(provider) != null) {
                    boolean locationIsAllenmoreOrCovingtonOrFederalWay = location.equals("AH") || location.equals("CV") || location.equals("FW");
                    if (specialPreferences.get(provider).equals("AH, CV, FW mornings 9am") &&
                            locationIsAllenmoreOrCovingtonOrFederalWay &&
                            (startTime.isBefore(LocalTime.of(9, 0)) && startTime.isAfter(LocalTime.of(1, 0)))) {
                        startTime = LocalTime.of(9, 0);
                    } else if (specialPreferences.get(provider).equals("AH, CV, FW mornings 8am") &&
                            locationIsAllenmoreOrCovingtonOrFederalWay &&
                            (startTime.isBefore(LocalTime.of(8, 0)) && startTime.isAfter(LocalTime.of(0, 0)))) {
                        startTime = LocalTime.of(8, 0);
                    } else if (specialPreferences.get(provider).equals("AH, CV mornings 8am") &&
                            (location.equals("AH") || location.equals("CV")) &&
                            (startTime.isBefore(LocalTime.of(8, 0)) && startTime.isAfter(LocalTime.of(0, 0)))) {
                        startTime = LocalTime.of(8, 0);
                    } else if (specialPreferences.get(provider).equals("AH no nights M-F") &&
                            location.equals("AH") && !isWeekend && startTime.isAfter(LocalTime.of(19, 0))) {
                        provider = "DELETE";
                    }
                }
            }
            Cell cell = row.createCell(0);
            cell.setCellValue("");
            cell = row.createCell(1);
            cell.setCellValue(LOCATION);
            cell = row.createCell(2);
            cell.setCellValue(POSITION);
            cell = row.createCell(3);
            cell.setCellValue(startDate.format(DATE_FORMATTER_FINAL));
            cell = row.createCell(4);
            cell.setCellValue(endDate.format(DATE_FORMATTER_FINAL));
            cell = row.createCell(5);
            cell.setCellValue(startTime.format(TIME_FORMATTER_INITIAL));
            cell = row.createCell(6);
            cell.setCellValue(endTime.format(TIME_FORMATTER_INITIAL));
            cell = row.createCell(7);
            cell.setCellValue("");
            cell = row.createCell(8);
            cell.setCellValue(location + " " + startTime.format(TIME_FORMATTER_FINAL)
                    .toLowerCase() + "-" + endTime.format(TIME_FORMATTER_FINAL).toLowerCase() + " " +
                    provider);
            cell = row.createCell(9);
            cell.setCellValue("1");
            cell = row.createCell(10);
            cell.setCellValue("");
            try (FileOutputStream fileOut = new FileOutputStream("scribeSchedule" + month + ".xlsx")) {
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* Workbook wb = new HSSFWorkbook();
//Workbook wb = new XSSFWorkbook();
CreationHelper createHelper = wb.getCreationHelper();
Sheet sheet = wb.createSheet("new sheet");
// Create a row and put some cells in it. Rows are 0 based.
Row row = sheet.createRow((short)0);
// Create a cell and put a value in it.
Cell cell = row.createCell(0);
cell.setCellValue(1);
// Or do it on one line.
row.createCell(1).setCellValue(1.2);
row.createCell(2).setCellValue(
createHelper.createRichTextString("This is a string"));
row.createCell(3).setCellValue(true);
// Write the output to a file
FileOutputStream fileOut = new FileOutputStream("workbook.xls");
wb.write(fileOut);
fileOut.close(); */
}