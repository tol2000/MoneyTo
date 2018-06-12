package ua.odessa.tol2000.moneyto;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Report1Activity extends ActionBarActivity {

    private EditText edDateFrom;
    private EditText edDateTo;
    private GridView reportGridView;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report1);
        edDateFrom = (EditText) findViewById(R.id.edDateFrom);
        edDateTo = (EditText) findViewById(R.id.edDateTo);
        edDateFrom.setText(MyAndronUtils.getDateNow(false, MyAndronUtils.GETDATE_CURRENT));
        edDateTo.setText(MyAndronUtils.getDateNow(true, MyAndronUtils.GETDATE_CURRENT));
        reportGridView = (GridView) findViewById(R.id.reportGridView);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report1, menu);
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
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        List<String> fArr = new ArrayList<String>();
        try {
            MainActivity.getDbObj().runSqlIntoArr(fArr, "select id, to_char(date_of_tra,'DD.MM.YY HH24:MI') as Date, sum_of_tra as summary, text_of_tra as Description from money where date_of_tra>='" + edDateFrom.getText() + "' and date_of_tra<='" + edDateTo.getText() + "' order by date_of_tra desc", true);
        } catch (SQLException e) {
            fArr.add("ERROR "+e.getMessage());
        }
        ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.myrowlayout1, R.id.label, fArr);
        reportGridView.setAdapter(adapter);

/*
        List<String> fArr = new ArrayList<String>();

        class Report1Adapter extends ArrayAdapter<String> {

            public Report1Adapter(Context context, List<String> pArr) {
                super(context, android.R.layout.simple_list_item_2, pArr);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String item = getItem(position);

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext())
                            .inflate(android.R.layout.simple_list_item_2, null);
                }
                ((TextView) convertView.findViewById(android.R.id.text1))
                        .setText(cat.name);
                ((TextView) convertView.findViewById(android.R.id.text2))
                        .setText(cat.gender);
                return convertView;
            }
        }
        MainActivity.getMainActivityObj().runSqlIntoArr(fArr, "select to_char(date_of_tra,'DD.MM.YYYY HH24:MI') as Date, sum_of_tra as summary, text_of_tra as Description from money where date_of_tra>='"+edDateFrom.getText()+"' and date_of_tra<='"+edDateTo.getText()+"' order by date_of_tra desc", true);
        ListAdapter adapter = new Report1Adapter(this, fArr);
        report1GridView.setAdapter(adapter);
*/

    }

}
