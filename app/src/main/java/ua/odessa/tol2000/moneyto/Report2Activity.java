package ua.odessa.tol2000.moneyto;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Проверка русских букв
public class Report2Activity extends ActionBarActivity {

    private Spinner spSort;
    private EditText edDesc;
    private EditText edDateFrom;
    private EditText edDateTo;
    private EditText edDel;
    private Button delButton;
    private TableLayout tbl1;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report2);
        spSort = (Spinner) findViewById(R.id.spSort);
        edDesc = (EditText) findViewById(R.id.edDesc);
        edDateFrom = (EditText) findViewById(R.id.edDateFrom);
        edDateTo = (EditText) findViewById(R.id.edDateTo);
        edDateFrom.setText(MyAndronUtils.getDateNow(false, MyAndronUtils.GETDATE_FIRST_DAY_OF_MONTH));
        edDateTo.setText(MyAndronUtils.getDateNow(false, MyAndronUtils.GETDATE_CURRENT));
        edDel = (EditText) findViewById(R.id.edDel);
        edDel.setText("");
        delButton = (Button) findViewById(R.id.delButton);
        tbl1 = (TableLayout) findViewById(R.id.tbl1);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        ArrayAdapter sortAdapter = ArrayAdapter.createFromResource(this, R.array.activity_report2_spSort_values, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(sortAdapter);

        delButton.setEnabled(false);
        edDel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    delButton.setEnabled(Integer.valueOf(s.toString()) > 0);
                } catch (Throwable e) {
                    delButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report2, menu);
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

    public void reportButtonRunClick(View view) {
        List<String> fArr = new ArrayList<String>();
        try {
            // using trunc for upper comparing dates without time in <=
            // sum_of_tra etc - for column aliases, else (if column without expression) h2db do not use alias... hm...
            String qWhere = "where date_of_tra>='" + edDateFrom.getText() + "' and trunc(date_of_tra)<='" + edDateTo.getText() + "'";
            String likeStr = edDesc.getText().toString().trim();
            String orderStr = "";
            switch (spSort.getSelectedItemPosition()) {
                case 0: orderStr = "date_of_tra";
                    break;
                case 1: orderStr = "upper(text_of_tra), date_of_tra";
                    break;
                case 2: orderStr = "id";
                    break;
                default: orderStr = "date_of_tra";
                    break;
            }
            if (!("".equals(likeStr))) qWhere+=" and upper(text_of_tra) like '%"+likeStr.trim().toUpperCase()+"%'";
            String qStr = "select * from ( select id as \""+getResources().getString(R.string.app_settings_fieldnames_money_id)+
                "\", to_char(date_of_tra,'DD.MM.YY HH24:MI') as \""+getResources().getString(R.string.app_settings_fieldnames_money_date_of_tra)+
                "\", sum_of_tra+0 as \""+getResources().getString(R.string.app_settings_fieldnames_money_sum_of_tra)+
                "\", text_of_tra||'' as \""+getResources().getString(R.string.app_settings_fieldnames_money_text_of_tra)+
                "\" from money "+qWhere+" order by "+orderStr+" )";
            qStr += " union all select null as \""+getResources().getString(R.string.app_settings_fieldnames_money_id)+
                "\", '"+getResources().getString(R.string.app_settings_report_totally_string)+
                "' as \""+getResources().getString(R.string.app_settings_fieldnames_money_date_of_tra)+
                "\", sum(sum_of_tra+0) as \""+getResources().getString(R.string.app_settings_fieldnames_money_sum_of_tra)+
                "\", '"+getResources().getString(R.string.app_settings_report_on_period_string)+
                "' as \""+getResources().getString(R.string.app_settings_fieldnames_money_text_of_tra)+"\" from money "+qWhere;
            MainActivity.getDbObj().runSqlIntoArr(fArr, qStr, true);
            MyAndronUtils.arrIntoTableLayout(fArr, tbl1, new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0)), true, true);  //(60, 130, 100, 160)
        } catch (SQLException e) {
            outMess("ERROR "+e.getMessage());
        }
        hideKb();
    }

    public void delButtonRunClick(View view) {
        try {
            String s = MyAndronUtils.getMoneyStringById(this, Integer.valueOf(edDel.getText().toString()), getResources().getString(R.string.activity_report2_deleted_string_message));
            MainActivity.getDbObj().runDmlStatement("delete from money where id=" + edDel.getText() + ";");
            MainActivity.getDbObj().runDmlStatement("commit;");
            outMess(s);
            edDel.setText("");
            hideKb();
        } catch (Throwable e) {
            outMess(getResources().getString(R.string.activity_report2_deleted_string_error_message)+" "+edDel.getText()+" "+e.getMessage());
        }
    }

    private void outMess(String mess) {
        tbl1.removeAllViews();
        TableRow tr = new TableRow(tbl1.getContext());
        TextView tv = new TextView(tbl1.getContext());
        tv.setText(mess);
        tv.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(tv, new TableRow.LayoutParams());
        tbl1.addView(tr, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    private void hideKb() { //now do not work :(
        imm.hideSoftInputFromWindow(edDel.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.hideSoftInputFromWindow(edDateFrom.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.hideSoftInputFromWindow(edDateTo.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

}
