package validators;


import domain.Tema;

public class TemaValidator extends AbstractValidator<Tema> {
    private static TemaValidator instance = null;

    @Override
    public void validate(Tema entity) throws ValidationException {
        String exception = "";

        if (entity.getNume().equals("")){
            exception += "numele temei nu poate fi gol\n";
        }
        if (entity.getDescriere().equals("")){
            exception += "descrierea temei nu poate fi goala\n";
        }
        if (entity.getId().equals("")){
            exception += "ID-ul nu poate fi gol\n";
        }

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
