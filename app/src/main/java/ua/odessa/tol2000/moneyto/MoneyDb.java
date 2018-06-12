package ua.odessa.tol2000.moneyto;

import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Tolik on 09.06.2015.
 */

// It is recommended to call getMoneyDbObj instead of constructor!
// This object is one for all MainActivity and other activities reincarnations.

public class MoneyDb {

    private String fPackageName;
    private Date whenConnected = null;

    private Connection h2Conn = null;

    public MoneyDb(String packageName) throws SQLException, ClassNotFoundException {
        fPackageName = packageName;
        h2Conn = null; // (yes, i know, in private declaration too :)

        Class.forName("org.h2.Driver");

        doOpenDbIfNotOpen();

        runSqlStatement("create table if not exists money(sum_of_tra number(15,2),date_of_tra timestamp,date_of_add timestamp,text_of_tra varchar2(100), id identity)", false, null);
        runDmlStatement("commit;");

    }

    public String getDbShortName() {
        return "moneyto";
    }

    public String getDbDirectory() {
        return "/data/data/"+fPackageName+"/data";
    }

    public String getDbName() {
        return getDbDirectory() + "/" +getDbShortName();
    }

    public String getDbUrl() {
        return "jdbc:h2:" + getDbName() + ";FILE_LOCK=FS" + ";PAGE_SIZE=1024" + ";CACHE_SIZE=8192" ;  // this options recommended in example in h2db site
    }

    public String runSqlStatement(String pSqlText, boolean withHeaders, String delim) {
        String fRes = "";
        java.sql.ResultSet rs = null;
        try {
            Statement fStmt = h2Conn.createStatement();
            boolean rset = true;
            fStmt.execute(pSqlText);
            while (rset) {
                rs = fStmt.getResultSet();
                if (rs!=null) {
                    fRes += ProcessRS(rs, withHeaders, delim)+"\n";
                    rs.close();
                } else {
                    fRes += "Update count: "+fStmt.getUpdateCount() + "\n";
                }
                rset = fStmt.getMoreResults();
            }
            fStmt.close();
        } catch (Throwable e) {
            fRes += "Error: "+e.getMessage()+"\n";
        }
        int locPos;
        boolean cont;
        do {
            locPos = fRes.lastIndexOf("\n");
            cont = (locPos == fRes.length()-"\n".length());
            if (cont) fRes = fRes.substring(0, locPos);
        } while (cont);
        return fRes;
    }

    // returns updatecount
    public int runDmlStatement(String pSql) throws SQLException {
        int fResult;
        Statement fStmt;
        java.sql.ResultSet rs = null;
        fStmt = h2Conn.createStatement();
        fResult = fStmt.executeUpdate(pSql);
        fStmt.close();
        return fResult;
    }

    public String getRowsQnty() {
        return runSqlStatement("select count(*) from money", false, null);
    }

    private String ProcessRS (ResultSet rs, boolean withHeaders, String delim) throws SQLException {
        int cols = rs.getMetaData().getColumnCount();
        String str = "";
        String locDelim;
        if (delim==null) locDelim = "\t"; else locDelim = delim;
        if (withHeaders) {
            for (int i = 1; i <= cols; i++) {
                str += rs.getMetaData().getColumnName(i) + locDelim;
            }
            str += "\n";
        }
        while ( rs.next() ) {
            for (int i = 1; i <= cols; i++) {
                str+=rs.getString(i)+locDelim;
            }
            str+="\n";
        }
        return str;
    }

    public void runSqlIntoArr(List<String> fRes, String pSqlText, boolean withHeaders) throws SQLException {
        java.sql.ResultSet rs = null;
        Statement fStmt = h2Conn.createStatement();
        boolean rset = true;
        fStmt.execute(pSqlText);
        while (rset) {
            rs = fStmt.getResultSet();
            if (rs!=null) {
                ProcessRSArr(fRes, rs, withHeaders);
                rs.close();
            } else {
                fRes.add("Update count: "+fStmt.getUpdateCount());
            }
            rset = fStmt.getMoreResults();
        }
        fStmt.close();
    }

    private void ProcessRSArr (List<String> pArr, ResultSet rs, boolean withHeaders) throws SQLException {
        int cols = rs.getMetaData().getColumnCount();
        if (withHeaders) {
            for (int i = 1; i <= cols; i++) {
                pArr.add(rs.getMetaData().getColumnName(i));
            }
        }
        while ( rs.next() ) {
            for (int i = 1; i <= cols; i++) {
                pArr.add(rs.getString(i));
            }
        }
    }

    public void exportDb(String outPath, TextView tvUtilsInfo, Boolean toScript) {
        try {
            tvUtilsInfo.setText("");
            tvUtilsInfo.append("Exporting...\n");
            String fileNameTo = outPath + "/" + getDbShortName();
            runDmlStatement("commit;");
            if (toScript) {
                fileNameTo += ".sql";
                runSqlStatement("SCRIPT TO '" + fileNameTo + "';", true, "\t");
                runDmlStatement("commit;");
            } else {
                fileNameTo += ".zip";
                runDmlStatement("BACKUP TO '" + fileNameTo + "';");
                runDmlStatement("commit;");
            }
            tvUtilsInfo.append("Exported to " + fileNameTo + "\n");
        } catch (Throwable e) {
            tvUtilsInfo.append("ERROR! " + e.getMessage() + "\n");
            try {
                doOpenDbIfNotOpen();
            } catch (SQLException e1) {
                tvUtilsInfo.append("\nReConnection to DB Error: " + e.getMessage() + "\n");
            }
        }
    }

    public void importDbAndReconnect(String inPath, TextView tvUtilsInfo) {
        try {
            tvUtilsInfo.setText("");
            tvUtilsInfo.append("Importing...\n");
            runDmlStatement("commit;");
            doCloseAndShutdownDb();
            String zipFrom = inPath + "/" + getDbShortName()+".zip";
            tvUtilsInfo.append("From "+zipFrom+"\n");
            tvUtilsInfo.append("To " + getDbName() + "\n");
            org.h2.tools.Restore.execute(zipFrom, getDbDirectory(), getDbShortName());
            tvUtilsInfo.append("Import done!\n");
            doOpenDbIfNotOpen();
            tvUtilsInfo.append("Reconnected.\n");
        } catch (Throwable e) {
            tvUtilsInfo.append("ERROR! " + e.getMessage() + "\n");
            try {
                doOpenDbIfNotOpen();
            } catch (SQLException e1) {
                tvUtilsInfo.append("\nReConnection to DB Error: " + e.getMessage() + "\n");
            }
        }
    }

    private void doOpenDbIfNotOpen() throws SQLException {
        if (h2Conn == null) {
            h2Conn = DriverManager.getConnection(getDbUrl(), "admin", "admin");
            // initializes connection date+time to display connection time later
            whenConnected = Calendar.getInstance().getTime();
        }
    }

    private void doCloseAndShutdownDb() throws SQLException {
        runDmlStatement("shutdown;"); // shutdown closes all sessions and return only after command executed, so we can start import for example
        h2Conn.close(); // shutdown closes all sessions, but i want to close explicitly :)
        h2Conn = null;
        whenConnected = null;
    }

    public void showConnectionInfo(TextView pInfo) {
        pInfo.setText("Connected "+(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).format(whenConnected)+" to " + getDbUrl());
    }

}
