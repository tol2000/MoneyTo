package ua.odessa.tol2000.moneyto;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class AddMenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_menu, menu);
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

    public void report1ButtonClick(View view) {
        Intent intent = new Intent(this, Report1Activity.class);
        startActivity(intent);
    }

    public void report2ButtonClick(View view) {
        Intent intent = new Intent(this, Report2Activity.class);
        startActivity(intent);
    }

    public void exportDbButtonClick(View view) {
        Intent intent = new Intent(this, ExportActivity.class);
        startActivity(intent);
    }

    public void importDbButtonClick(View view) {
        Intent intent = new Intent(this, ImportActivity.class);
        startActivity(intent);
    }

}
