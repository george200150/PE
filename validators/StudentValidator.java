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
            exception += "email adress is not valid\n";
        }
        if(entity.getNume().equals("")){
            exception += "surname cannot be null\n";
        }
        if(entity.getPrenume().equals("")){
            exception += "name cannot be null\n";
        }
        if(entity.getGrupa() < 100 || entity.getGrupa() > 999){
            exception += "group must be a 3 digit positive number\n";
        }
        //we should later check if the lab professor exists in the app...

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
