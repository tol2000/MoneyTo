package ua.odessa.tol2000.moneyto;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class ExportActivity extends ActionBarActivity {

    private EditText edExpDirTo;
    private TextView tvInfo;
    private CheckBox cbToScript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        edExpDirTo = (EditText) findViewById(R.id.edExpDirTo);
        edExpDirTo.setText(TolSettings.getPrefValue("activity_export_edExpDirTo_text"));
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        cbToScript = (CheckBox) findViewById(R.id.cbToScript);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_export, menu);
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

    public void exportRunButtonClick(View view) {
        MainActivity.getDbObj().exportDb(edExpDirTo.getText().toString(), tvInfo, cbToScript.isChecked());
        TolSettings.setPrefValue("activity_export_edExpDirTo_text",edExpDirTo.getText().toString());
    }

    public void dirChooseClick(View view) {
        DirectoryChooserDialog directoryChooserDialog =
            new DirectoryChooserDialog(this,
                new DirectoryChooserDialog.ChosenDirectoryListener() {
                    @Override
                    public void onChosenDir(String chosenDir) {
                        edExpDirTo.setText(chosenDir);
                    }
                });
        directoryChooserDialog.chooseDirectory(edExpDirTo.getText().toString());
    }

}
