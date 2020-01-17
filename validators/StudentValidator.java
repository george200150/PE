package validators;

import domain.Student;

public class StudentValidator extends AbstractValidator<Student> {
    private static StudentValidator instance = null;

    private StudentValidator() {
    }

    @Override
    public void validate(Student entity) throws ValidationException {
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
        if(entity.getGrupa() < 100 || entity.getGrupa() > 999){
            exception += "grupa trebuie sa fie un numar natural de 3 cifre\n";
        }

        if(exception.length() > 0)
            throw new ValidationException(exception);
    }

    public static StudentValidator getInstance(){
        if(instance == null){
            instance = new StudentValidator();
        }
        return instance;
    }
}
