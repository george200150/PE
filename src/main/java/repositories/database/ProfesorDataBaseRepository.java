package repositories.database;


import domain.Profesor;
import repositories.CrudRepository;
import validators.ValidationException;
import validators.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfesorDataBaseRepository implements CrudRepository<String, Profesor> {
    private Connection connection;
    private Validator<Profesor> validator;

    public ProfesorDataBaseRepository(Validator<Profesor> validator) {
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
    public Profesor findOne(String id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("ID-ul NU POATE FI NULL");
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Profesori\"  WHERE id =" + "\'" +  id + "\'");
            data.next();
            return new Profesor(data.getString(1), data.getString(2), data.getString(3), data.getString(4));
        }
        catch (SQLException ignored) {
            return null;
        }
    }

    @Override
    public Iterable<Profesor> findAll() {//TODO: id nume prenume email
        List<Profesor> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Profesori\"");
            while (data.next()) {
                Profesor entity = new Profesor(data.getString(1), data.getString(2), data.getString(3), data.getString(4));
                lst.add(entity);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return lst;
    }


    @Override
    public Profesor save(Profesor entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");

        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            throw new ValidationException("DUPLICAT GASIT!");
        }

        try {
            connection.createStatement().execute("INSERT INTO \"Profesori\" VALUES (" +
                    entity.getId() + ",\'" + entity.getNume() + "\',\'" + entity.getPrenume() + "\',\'" +
                    entity.getEmail() + "\')"
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return null;
    }

    @Override
    public Profesor delete(String id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("ID-ul nu poate fi NULL!");
        Profesor entity = findOne(id);
        if (entity != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Profesori\" WHERE id = " + "\'" +  id + "\'");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    @Override
    public Profesor update(Profesor entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            Profesor old = findOne(entity.getId());
            try {
                connection.createStatement().execute("UPDATE \"Teme\" SET " +
                        "nume = \'" + entity.getNume() + "\'" +
                        ",\"prenume\" = \'" + entity.getPrenume() + "\'" +
                        ",\"email\" = \'" + entity.getEmail() + "\'" + "WHERE id =" + "\'" + entity.getId() + "\'"
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return old;
        }
        return null;
    }
}

