package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StructuraSemestru {
    private int anUniversitar;
    private int semestru;
    private LocalDate startSemester;
    private LocalDate beginHolyday;
    private LocalDate endHolyday;
    private LocalDate endSemester;
    private String fileName;

    public StructuraSemestru(int an, int semestru, String filename) {
        this.anUniversitar = an;
        this.semestru = semestru;
        this.fileName = filename;
        loadData();
    }

    public StructuraSemestru(int an, int semestru, String t1, String t2, String t3, String t4) {
        this.anUniversitar = an;
        this.semestru = semestru;
        this.startSemester = LocalDate.parse(t1,Constants.DATE_TIME_FORMATTER);
        this.beginHolyday = LocalDate.parse(t2,Constants.DATE_TIME_FORMATTER);
        this.endHolyday = LocalDate.parse(t3,Constants.DATE_TIME_FORMATTER);
        this.endSemester = LocalDate.parse(t4,Constants.DATE_TIME_FORMATTER);
    }

    /**
     * Get from the semester file the principal dates for start / stop dates
     * in chronological order. Note that there is only a single holiday during semester!
     */
    private void loadData() {
        Path path = Paths.get(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            this.startSemester = LocalDate.parse(lines.get(0), Constants.DATE_TIME_FORMATTER);
            this.beginHolyday = LocalDate.parse(lines.get(1), Constants.DATE_TIME_FORMATTER);
            this.endHolyday = LocalDate.parse(lines.get(2), Constants.DATE_TIME_FORMATTER);
            this.endSemester = LocalDate.parse(lines.get(3), Constants.DATE_TIME_FORMATTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStartSemesterAsString() {
        return startSemester.format(Constants.DATE_TIME_FORMATTER);
    }

    public String getEndSemesterAsString() {
        return endSemester.format(Constants.DATE_TIME_FORMATTER);
    }

    public String getStartHolidayAsString() {
        return beginHolyday.format(Constants.DATE_TIME_FORMATTER);
    }

    public String getEndHolidayAsString() {
        return endHolyday.format(Constants.DATE_TIME_FORMATTER);
    }


    public LocalDate getStartSemesterAsLocalDate() {
        return startSemester;
    }

    public LocalDate getBeginHolydayAsLocalDate() {
        return beginHolyday;
    }

    public LocalDate getEndHolydayAsLocalDate() {
        return endHolyday;
    }

    public LocalDate getEndSemesterAsLocalDate() {
        return endSemester;
    }

    /**
     * @param date - MUST BE FORMATTED "dd/mm/yyyy"
     * @return true if the date given is in holiday and false if not.
     */
    public boolean isHoliday(String date) {
        return Constants.compareDates(this.endHolyday.format(Constants.DATE_TIME_FORMATTER), date) &&
                Constants.compareDates(date, this.beginHolyday.format(Constants.DATE_TIME_FORMATTER));
    }

    /**
     * @param date - MUST BE FORMATTED "dd/mm/yyyy"
     * @return true if date is before holiday and after semester start
     */
    public boolean isFirstPartOfSemester(String date) {
        return Constants.compareDates(this.beginHolyday.format(Constants.DATE_TIME_FORMATTER), date) &&
                Constants.compareDates(date, this.startSemester.format(Constants.DATE_TIME_FORMATTER));
    }

    /**
     * @param date - MUST BE FORMATTED "dd/mm/yyyy"
     * @return true if date is after holiday and before semester end
     */
    public boolean isLastPartOfSemester(String date) {
        return Constants.compareDates(this.endSemester.format(Constants.DATE_TIME_FORMATTER), date) &&
                Constants.compareDates(date, this.endHolyday.format(Constants.DATE_TIME_FORMATTER));
    }

    public int getAnUniversitar() {
        return anUniversitar;
    }

    public int getSemestru() {
        return semestru;
    }

    public int getSemesterWeek(LocalDateTime data) {
        LocalDateTime now = data;
        String stringNow = now.format(Constants.DATE_TIME_FORMATTER);

        String semStart = Constants.startSemester;
        String semEnd = Constants.endSemester;
        String vacStart = Constants.beginHolyday;
        String vacEnd = Constants.endHolyday;
        int start = Constants.getWeek(semStart);
        start -= 1;//because mathematics tell us that the number of weeks between j and i equals i - j + 1

        if (Constants.compareDates(semStart, stringNow)) {//semester not started yet
            //EXCEPTION : SEMESTER NOT STARTED YET
            return -1;
        } else {//semester started
            if (Constants.compareDates(vacStart, stringNow)) {//holiday not started yet
                int date = Constants.getWeek(LocalDateTime.now());
                date -= start;
                return date;
            } else {//now is after holiday start
                if (Constants.compareDates(vacEnd, stringNow)) {//now is in holiday
                    int date = Constants.getWeek(vacStart);
                    date += 1;//first task after holiday has the name counter equals to the last task name counter before holiday + 1
                    date -= start;
                    return date;
                } else {//holiday finished
                    if (Constants.compareDates(semEnd, stringNow)) {//semester ended
                        //EXCEPTION : SEMESTER FINISHED ALREADY
                        return -1;
                    } else {//in semester after holiday
                        int date = Constants.getWeek(vacStart);
                        int dif = Constants.getWeek(vacEnd) - Constants.getWeek(vacStart) + 1;//get the duration of holiday in weeks
                        date -= dif;//subtract the holiday length from the current week to get the current task name counter
                        date -= start;
                        return date;
                    }
                }
            }
        }
    }
}

        /*
        startSemester="30.09.2019"
        beginHolyday="23.12.2019"
        endHolyday="05.01.2020"
        endSemester="17.01.2020"
         */