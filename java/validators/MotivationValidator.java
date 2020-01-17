package validators;

import domain.Motivation;
import utils.Constants;

public class MotivationValidator extends AbstractValidator<Motivation> {
    private static MotivationValidator instance = null;

    private MotivationValidator() {
    }

    @Override
    public void validate(Motivation entity) throws ValidationException {
        String exceptions = "";
        if(Constants.compareDates(entity.getInterval().getStart(), entity.getInterval().getEnd())){
            exceptions += "INTERVALUL DE MOTIVARE ESTE GRESIT!\n";
        }

        if(exceptions.length() > 0){
            throw new ValidationException(exceptions);
        }
    }

    public static MotivationValidator getInstance(){
        if(instance == null){
            instance = new MotivationValidator();
        }
        return instance;
    }
}
