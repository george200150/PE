package repositories.database;


import domain.Tema;
import repositories.CrudRepository;
import validators.ValidationException;
import validators.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TemaDataBaseRepository implements CrudRepository<String, Tema> {
    private Connection connection;
    private Validator<Tema> validator;

    public TemaDataBaseRepository(Validator<Tema> validator) {
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
    public Tema findOne(String id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("ID-ul NU POATE FI NULL");
        try {
            //BAD QUERY !!! =: ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Teme\"  WHERE id =" + "\"id\"");
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Teme\"  WHERE id =" + "\'" +  id + "\'");
            data.next();
            String id2 = data.getString(1);
            String nume = data.getString(2);
            String descriere = data.getString(3);
            String start = data.getString(4);
            String stop = data.getString(5);
            return new Tema(id, nume, descriere, start, stop);
        }
        catch (SQLException ignored) {
            return null;
        }
    }

    @Override
    public Iterable<Tema> findAll() {
        List<Tema> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Teme\"");
            while (data.next()) {
                Tema entity = new Tema(data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5));
                lst.add(entity);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return lst;
    }


    @Override
    public Tema save(Tema entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");

        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            throw new ValidationException("DUPLICAT GASIT!");
        }

        try {
            connection.createStatement().execute("INSERT INTO \"Teme\" VALUES (" +
                    entity.getId() + ",\'" + entity.getNume() + "\',\'" + entity.getDescriere() + "\',\'" +
                    entity.getStartWeek() + "\',\'" + entity.getDeadlineWeek() + "\')"
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return null;
    }

    @Override
    public Tema delete(String id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("ID-ul nu poate fi NULL!");
        Tema entity = findOne(id);
        if (entity != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Teme\" WHERE id = " + "\'" +  id + "\'");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    @Override
    public Tema update(Tema entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            Tema old = findOne(entity.getId());
            try {
                connection.createStatement().execute("UPDATE \"Teme\" SET " +
                        "nume = \'" + entity.getNume() + "\'" +
                        ",\"descriere\" = \'" + entity.getDescriere() + "\'" +
                        ",\"startWeek\" = \'" + entity.getStartWeek() + "\'" +
                        ",\"deadlineWeek\" = \'" + entity.getDeadlineWeek() + "\'" + "WHERE id =" + "\'" + entity.getId() + "\'"
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return old;
        }
        return null;
    }
}

