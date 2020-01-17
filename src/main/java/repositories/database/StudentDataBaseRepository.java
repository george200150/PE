package repositories.database;


import domain.Student;
import repositories.CrudRepository;
import validators.ValidationException;
import validators.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDataBaseRepository implements CrudRepository<String, Student> {
    private Connection connection;
    private Validator<Student> validator;

    public StudentDataBaseRepository(Validator<Student> validator) {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/PE", "postgres", "admin");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.validator = validator;
    }

    @Override
    public Student findOne(String id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("ID-ul NU POATE FI NULL");
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Studenti\"  WHERE id =" + "\'" +  id + "\'");
            data.next();
            //String id = data.getString(1);
            String nume = data.getString(2);
            String prenume = data.getString(3);
            String mail = data.getString(4);
            String prof = data.getString(5);
            int grupa = Integer.parseInt(data.getString(6));
            return new Student(id,nume,prenume,grupa,mail,prof);
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public Iterable<Student> findAll() {
        List<Student> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Studenti\"");
            while (data.next()) {
                String id = data.getString(1);
                String nume = data.getString(2);
                String prenume = data.getString(3);
                String mail = data.getString(4);
                String prof = data.getString(5);
                int grupa = Integer.parseInt(data.getString(6));
                Student student = new Student(id,nume,prenume,grupa,mail,prof);
                lst.add(student);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return lst;
    }


    @Override
    public Student save(Student entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");

        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            throw new ValidationException("DUPLICAT GASIT!");
        }

        try {
/*            INSERT INTO public."Studenti"(
                    id, nume, prenume, email, "cadruDidacticIndrumatorLab", grupa)
            VALUES (?, ?, ?, ?, ?, ?)*/
            connection.createStatement().execute("INSERT INTO \"Studenti\" VALUES (" +
                    entity.getId() + ",\'" + entity.getNume() + "\',\'" + entity.getPrenume() + "\',\'" +
                    entity.getEmail() + "\',\'" + entity.getCadruDidacticIndrumatorLab() + "\',\'" + entity.getGrupa() + "\')"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Student delete(String id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("ID-ul nu poate fi NULL!");
        Student student = findOne(id);
        if (student != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Studenti\" WHERE id = " + "\'" +  id + "\'");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return student;
    }

    @Override
    public Student update(Student entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            Student old = findOne(entity.getId());
            try {
                connection.createStatement().execute("UPDATE \"Studenti\" SET " +
                        "nume = \'" + entity.getNume() + "\'" +
                        ",prenume = \'" + entity.getPrenume() + "\'" +
                        ",\"grupa\" = \'" + entity.getGrupa() + "\'" +
                        ",email = \'" + entity.getEmail() + "\'" +
                        ",\"cadruDidacticIndrumatorLab\" = \'" + entity.getCadruDidacticIndrumatorLab() + "\'" + "WHERE id =" + "\'" + entity.getId() + "\'"
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return old;
        }
        return null;
    }
}
