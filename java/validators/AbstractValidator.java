package validators;

public class AbstractValidator<E> implements Validator<E> {
    private static AbstractValidator instance = null;

    @Override
    public void validate(E entity) throws ValidationException {

    }

    public static AbstractValidator getInstance(){
        if(instance == null){
            instance = new AbstractValidator();
        }
        return instance;
    }
}
