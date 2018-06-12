package ua.odessa.tol2000.moneyto;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class ImportActivity extends ActionBarActivity {

    private EditText edImpDirTo;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        edImpDirTo = (EditText) findViewById(R.id.edImpDirTo);
        edImpDirTo.setText(TolSettings.getPrefValue("activity_import_edImpDirTo_text"));
        tvInfo = (TextView) findViewById(R.id.tvInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_import, menu);
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

    public void importRunButtonClick(View view) {
        MainActivity.getDbObj().importDbAndReconnect(edImpDirTo.getText().toString(), tvInfo);
        TolSettings.setPrefValue("activity_import_edImpDirTo_text", edImpDirTo.getText().toString());
    }

    public void dirChooseClick(View view) {
        DirectoryChooserDialog directoryChooserDialog =
            new DirectoryChooserDialog(this,
                new DirectoryChooserDialog.ChosenDirectoryListener() {
                    @Override
                    public void onChosenDir(String chosenDir) {
                        edImpDirTo.setText(chosenDir);
                    }
                });
        directoryChooserDialog.chooseDirectory(edImpDirTo.getText().toString());
    }

}
