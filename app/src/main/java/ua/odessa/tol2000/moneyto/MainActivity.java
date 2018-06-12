package ua.odessa.tol2000.moneyto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private static MoneyDb dbObj = null;

    public static MoneyDb getDbObj() {
        return dbObj;
    }

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private EditText ed1;
    private EditText ed2;
    private EditText ed3;
    private TextView tvMessage;
    private TextView tvInfo;
    private Button addButton;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        ed1 = (EditText) findViewById(R.id.ed1);
        ed2 = (EditText) findViewById(R.id.ed2);
        ed3 = (EditText) findViewById(R.id.ed3);
        addButton = (Button) findViewById(R.id.addbutton);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvInfo = (TextView) findViewById(R.id.tvInfo);

        if (dbObj==null)
            try {
                dbObj = new MoneyDb(getPackageName()); //Activity may create more than one time, so dbObj may have already created )
            } catch (SQLException | ClassNotFoundException e) {
                tvInfo.append("\nError: " + e.getMessage() + "\n");
            }

        TolSettings.initSettings(this);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        addButton.setEnabled(false);
        ed2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Float dummy = Float.valueOf(s.toString());
                    addButton.setEnabled(true);
                } catch (Throwable e) {
                    addButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvMessage.setText(getResources().getString(R.string.activity_main_tvMessage_text_init));
        mainInit();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // with activity activated we do not must to see the last message (added...)
            tvMessage.setText(getResources().getString(R.string.activity_main_tvMessage_text_init));
            mainInit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ifFirst - first call of mainInit (program first start)
    private void mainInit() {
        ed1.setText(MyAndronUtils.getDateNow(true, MyAndronUtils.GETDATE_CURRENT));
        ed2.setText("");
        ed3.setText("");
        ed2.requestFocus();
        dbObj.showConnectionInfo(tvInfo);
        hideKb();
        this.setTitle(Html.fromHtml("<small>" + getResources().getString(R.string.title_activity_main) + " (всего: " + dbObj.getRowsQnty().trim() + ")"+"</small>"));
    }

    public void addbuttonDataClick(View view) {
        String locSql = "insert into money (sum_of_tra,date_of_tra,date_of_add,text_of_tra) values("+ed2.getText()+",'"+ed1.getText()+"','"+MyAndronUtils.getDateNow(true, MyAndronUtils.GETDATE_CURRENT)+"','"+ed3.getText()+"');";
        try {
            dbObj.runDmlStatement(locSql);
            String ppId = dbObj.runSqlStatement("select identity() from dual;",false,"");
            dbObj.runDmlStatement("commit;");
            String s = MyAndronUtils.getMoneyStringById(this,Integer.valueOf(ppId),getResources().getString(R.string.activity_main_tvMessage_text_added));
            tvMessage.setText(s);
        } catch (SQLException e) {
            tvMessage.setText("Ошибка добавления: "+e.getMessage());
        }
        mainInit();
    }

    public void addMenuButtonClick(View view) {
        Intent intent = new Intent(this, AddMenuActivity.class);
        startActivity(intent);
    }

    private void hideKb() { //now do not work :(
        imm.hideSoftInputFromWindow(ed1.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.hideSoftInputFromWindow(ed2.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.hideSoftInputFromWindow(ed2.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

}
