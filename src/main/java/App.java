import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.mindrot.jbcrypt.BCrypt;

import com.mysql.cj.jdbc.JdbcPreparedStatement;

import java.sql.PreparedStatement;

public class App {

    private static String[][] data = { { "PENELOPE", "GUINESS", "PENELOPE@mail.com.by", "12345" },
            { "NICK", "WAHLBERG", "NICK@mail.com.by", "12345" }, { "ED", "CHASE", "ED@mail.com.by", "12345" },
            { "JENNIFER", "DAVIS", "JENNIFER@mail.com.by", "12345" },
            { "JOHNNY", "LOLLOBRIGIDA", "JOHNNY@mail.com.by", "12345" },
            { "BETTE", "NICHOLSON", "BETTE@mail.com.by", "12345" }, { "GRACE", "MOSTEL", "GRACE@mail.com.by", "12345" },
            { "MATTHEW", "JOHANSSON", "MATTHEW@mail.com.by", "12345" }, { "JOE", "SWANK", "JOE@mail.com.by", "12345" },
            { "CHRISTIAN", "GABLE", "CHRISTIAN@mail.com.by", "12345" } };

    private static String[][] data2 = { { "Aruba", "Kabul", "01/01/2019" }, { "Afghanistan", "Qandahar", "01/01/2019" },
            { "Angola", "Herat", "01/01/2019" }, { "Anguilla", "Mazar-e-Sharif", "01/01/2019" },
            { "Albania", "Amsterdam", "01/01/2019" }, { "Andorra", "Rotterdam", "01/01/2019" },
            { "Netherlands Antilles", "Haag", "01/01/2019" }, { "United Arab Emirates", "Utrecht", "01/01/2019" },
            { "Argentina", "Eindhoven", "01/01/2019" }, { "Armenia", "Tilburg", "01/01/2019" }, };

    private final static String INSERT_NEW_USER_INTO_USERS = "INSERT INTO users(first_name, last_name, email, password, password_salt, last_update, user_details_id_user_details) VALUES(?,?,?,?,?,?,?)";
    private final static String INSERT_USER_DATA_INTO_USER_DELATILS = "INSERT INTO user_details(country, city, birthday) VALUES(?,?,?)";

    public static void main(String[] args) throws ClassNotFoundException, SQLException, ParseException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/jd2_hib_v2?useSSL=false", "root",
                "root");

        JdbcPreparedStatement psUsersDetails = (JdbcPreparedStatement) con.prepareStatement(INSERT_USER_DATA_INTO_USER_DELATILS, Statement.RETURN_GENERATED_KEYS);
        PreparedStatement psUsers = con.prepareStatement(INSERT_NEW_USER_INTO_USERS);

        for (int i = 0; i < 1; i++) {

            psUsersDetails.setString(2, data2[i][1]);
            psUsersDetails.setString(1, data2[i][0]);

            SimpleDateFormat birthdayFormat = new SimpleDateFormat("dd/mm/yyyy");
            Timestamp tb = new Timestamp(birthdayFormat.parse(data2[i][2]).getTime());

            psUsersDetails.setTimestamp(3, tb);

            psUsersDetails.executeUpdate();

            ResultSet autoKeyRS = psUsersDetails.getGeneratedKeys();
            int autoKey;

            if (autoKeyRS.next()) {
                autoKey = autoKeyRS.getInt(1);
            } else {
                throw new RuntimeException("no auto key");
            }

            psUsers.setString(1, data[i][0]);// first_name
            psUsers.setString(2, data[i][1]);// last_name
            psUsers.setString(3, data[i][2]);// email

            String salt = BCrypt.gensalt();
            String hashpw = BCrypt.hashpw(data[i][3], salt);

            psUsers.setString(4, hashpw);// password
            psUsers.setString(5, salt);// password_salt

            Timestamp t = new Timestamp(System.currentTimeMillis());
            psUsers.setTimestamp(6, t);

            psUsers.setInt(7, autoKey);

            psUsers.executeUpdate();

            /*
             * System.out.print(data[i][0] + ", "); System.out.print(data[i][1] + ", ");
             * System.out.print(data[i][2] + ", "); System.out.print(hashpw + ", ");
             * System.out.print(salt + ", "); System.out.println(t);
             */

        }

        psUsers.close();
        psUsersDetails.close();
        con.close();
    }
}