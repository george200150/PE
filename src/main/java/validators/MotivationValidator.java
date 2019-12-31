package validators;

import domain.Motivation;

public class MotivationValidator extends AbstractValidator<Motivation> {
    private static MotivationValidator instance = null;

    private MotivationValidator() {
    }

    @Override
    public void validate(Motivation entity) throws ValidationException {
    }

    public static MotivationValidator getInstance(){
        if(instance == null){
            instance = new MotivationValidator();
        }
        return instance;
    }
}
