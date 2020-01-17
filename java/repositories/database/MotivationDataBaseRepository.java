package repositories.database;

import utils.Constants;
import domain.Motivation;
import repositories.CrudRepository;
import validators.ValidationException;
import validators.Validator;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MotivationDataBaseRepository implements CrudRepository<String, Motivation> {
    private Connection connection;
    private Validator<Motivation> validator;

    public MotivationDataBaseRepository(Validator<Motivation> validator) {
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
    public Motivation findOne(String id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("ID-ul NU POATE FI NULL");
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Motivari\"  WHERE id =" + id);
            data.next();
            return new Motivation(data.getString(1), data.getString(2), data.getString(3), Integer.parseInt(data.getString(4)),data.getString(5), data.getString(6), LocalDate.parse(data.getString(7), Constants.DATE_TIME_FORMATTER), LocalDate.parse(data.getString(8), Constants.DATE_TIME_FORMATTER));
        }
        catch (SQLException ignored) {
            return null;
        }
    }

    @Override
    public Iterable<Motivation> findAll() {
        List<Motivation> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Motivari\"");
            while (data.next()) {
                Motivation entity = new Motivation(data.getString(1), data.getString(2), data.getString(3), Integer.parseInt(data.getString(4)),data.getString(5), data.getString(6), LocalDate.parse(data.getString(7), Constants.DATE_TIME_FORMATTER), LocalDate.parse(data.getString(8), Constants.DATE_TIME_FORMATTER));
                lst.add(entity);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return lst;
    }


    @Override
    public Motivation save(Motivation entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");

        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            throw new ValidationException("DUPLICAT GASIT!");
        }

        try {
            connection.createStatement().execute("INSERT INTO \"Motivari\" VALUES (" +
                    entity.getIdStudent() + ",\'" + entity.getNumeStudent() + "\',\'" + entity.getPrenumeStudent() +
                    "\',\'" + entity.getGrupaStudent() + "\',\'" + entity.getEmailStudent() + "\',\'" +
                    entity.getCadruDidacticIndrumatorLabStudent() +
                    "\',\'" + entity.getInterval().getStart() + "\',\'" + entity.getInterval().getEnd() + "\')"
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return null;
    }

    @Override
    public Motivation delete(String id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("ID-ul nu poate fi NULL!");
        Motivation entity = findOne(id);
        if (entity != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Motivari\" WHERE id = " + id);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    @Override
    public Motivation update(Motivation entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            Motivation old = findOne(entity.getId());
            try {
                connection.createStatement().execute("UPDATE \"Motivari\" SET " +
                        ",nume = \'" + entity.getNumeStudent() + "\'" +
                        ",\"prenume\" = \'" + entity.getPrenumeStudent() + "\'" +
                        ",\"email\" = \'" + entity.getEmailStudent() + "\'" +
                        ",\"grupa\" = \'" + entity.getGrupaStudent() + "\'" +
                        ",\"cadruDidacticIndrumatorLab\" = \'" + entity.getCadruDidacticIndrumatorLabStudent() + "\'" +
                        ",\"startMotivare\" = \'" + entity.getInterval().getStart() + "\'" +
                        ",\"stopMotivare\" = \'" + entity.getInterval().getEnd() + "\'" + "WHERE id =" + entity.getId()
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return old;
        }
        return null;
    }
}

