package validators;


import domain.Tema;

public class TemaValidator extends AbstractValidator<Tema> {
    private static TemaValidator instance = null;
    //private static StructuraSemestru structuraSemestru1 = new StructuraSemestru(Constants.SEM_1_STRUCTURE);
    //private static StructuraSemestru structuraSemestru2 = new StructuraSemestru(Constants.SEM_2_STRUCTURE);

    @Override
    public void validate(Tema entity) throws ValidationException {
        String exception = "";

        /*if(entity.getStartWeek() > entity.getDeadlineWeek()){
            exception += "deadline is earlier than beginning";
        }*///TODO: refactor this validation ASAP

        if (entity.getNume().equals("")){
            exception += "task name cannot be null\n";
        }
        if (entity.getDescriere().equals("")){
            exception += "task description cannot be null\n";
        }
        if (entity.getId().equals("")){
            exception += "ID cannot be null\n";
        }

        /*if ( structuraSemestru1.isNotHoliday(entity.getStartWeek()) && structuraSemestru2.isNotHoliday(entity.getDeadlineWeek()) ||
                structuraSemestru2.isNotHoliday(entity.getStartWeek()) && structuraSemestru1.isNotHoliday(entity.getDeadlineWeek() ) ){
            exception += "starting and deadline weeks belong to different semesters";
        }

        if (!structuraSemestru1.isNotHoliday(entity.getStartWeek()) && !structuraSemestru2.isNotHoliday(entity.getStartWeek()) ){
            exception += "starting week is in a holiday";
        }

        if (!structuraSemestru1.isNotHoliday(entity.getDeadlineWeek()) && !structuraSemestru2.isNotHoliday(entity.getDeadlineWeek()) ){
            exception += "deadline week is in a holiday";
        }*/




        if (exception.length() > 0){
            throw new ValidationException(exception);
        }
    }

    public static TemaValidator getInstance(){
        if(instance == null){
            instance = new TemaValidator();
        }
        return instance;
    }
}
