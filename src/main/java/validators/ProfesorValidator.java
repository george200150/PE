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
            exception += "adresa de email nu este valida\n";
        }
        if(entity.getNume().equals("")){
            exception += "prenumele nu poate fi gol\n";
        }
        if(entity.getPrenume().equals("")){
            exception += "numele nu poate fi gol\n";
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
