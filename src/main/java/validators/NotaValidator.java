package validators;

import domain.Nota;

public class NotaValidator extends AbstractValidator<Nota> {
    private static NotaValidator instance = null;

    private NotaValidator() {
    }

    @Override
    public void validate(Nota entity) throws ValidationException {
        String exception = "";

        if(entity.getProfesor().equals("")){
            exception += "Profesorul nu poate sa lipseasca\n";
        }
        if(entity.getValoare() > 10 || entity.getValoare() < 0 ){
            exception += "Nota este incorect\n";
        }

        String[] id_s = entity.getId().split(":");
        if(id_s[0].equals("") || id_s[1].equals("")){
            exception += "One of the id-s are missing\n";
        }

        if(exception.length() > 0)
            throw new ValidationException(exception);
    }

    public static NotaValidator getInstance(){
        if(instance == null){
            instance = new NotaValidator();
        }
        return instance;
    }
}
