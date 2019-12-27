package validators;

import domain.Profesor;

public class ProfesorValidator extends AbstractValidator<Profesor>  {

    private static ProfesorValidator instance = null;

    private ProfesorValidator() {
    }

    @Override
    public void validate(Profesor entity) throws ValidationException {
        String exception = "";
        if(!entity.getEmail().matches("[a-zA-Z0-9._]+@[a-z.]+.[a-z]+")){
            exception += "email adress is not valid\n";
        }
        if(entity.getNume().equals("")){
            exception += "surname cannot be null\n";
        }
        if(entity.getPrenume().equals("")){
            exception += "name cannot be null\n";
        }

        if(exception.length() > 0)
            throw new ValidationException(exception);
    }

    public static ProfesorValidator getInstance(){
        if(instance == null){
            instance = new ProfesorValidator();
        }
        return instance;
    }

}
