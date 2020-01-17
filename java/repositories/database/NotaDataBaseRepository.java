package repositories.database;


import domain.Nota;
import repositories.CrudRepository;
import validators.ValidationException;
import validators.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotaDataBaseRepository implements CrudRepository<String, Nota> {
    private Connection connection;
    private Validator<Nota> validator;

    public NotaDataBaseRepository(Validator<Nota> validator) {
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
    public Nota findOne(String id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("ID-ul NU POATE FI NULL");
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Note\"  WHERE id =" + id);
            data.next();
            return new Nota(data.getString(1), Integer.parseInt(data.getString(2)), data.getString(3), data.getString(4), data.getString(5));
        }
        catch (SQLException ignored) {
            return null;
        }
    }

    @Override
    public Iterable<Nota> findAll() {
        List<Nota> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Note\"");
            while (data.next()) {
                Nota entity = new Nota(data.getString(1), Integer.parseInt(data.getString(2)), data.getString(3), data.getString(4), data.getString(5));
                lst.add(entity);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return lst;
    }


    @Override
    public Nota save(Nota entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");

        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            throw new ValidationException("DUPLICAT GASIT!");
        }

        try {
            connection.createStatement().execute("INSERT INTO \"Note\" VALUES (" +
                    entity.getId() + ",\'" + entity.getValoare() + "\',\'" + entity.getProfesor() + "\',\'" +
                    entity.getData() + "\',\'" + entity.getFeedback() + "\')"
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return null;
    }

    @Override
    public Nota delete(String id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("ID-ul nu poate fi NULL!");
        Nota entity = findOne(id);
        if (entity != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Note\" WHERE id = " + id);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    @Override
    public Nota update(Nota entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            Nota old = findOne(entity.getId());
            try {
                connection.createStatement().execute("UPDATE \"Note\" SET " +
                        "id = \'" + entity.getId() + "\'" +
                        ",valoare = \'" + entity.getValoare() + "\'" +
                        ",\"profesor\" = \'" + entity.getProfesor() + "\'" +
                        ",\"data\" = \'" + entity.getData() + "\'" +
                        ",\"feedback\" = \'" + entity.getFeedback() + "\'" + "WHERE id =" + entity.getId()
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return old;
        }
        return null;
    }
}
