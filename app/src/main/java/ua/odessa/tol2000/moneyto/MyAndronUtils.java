package ua.odessa.tol2000.moneyto;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TableLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Tolik on 27.05.2015.
 */
public class MyAndronUtils {

    // Set of getDateNow constants
    public static final int GETDATE_CURRENT = 0;
    public static final int GETDATE_FIRST_DAY_OF_MONTH = 1;

    public static Date firstDayOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }


    public static String getDateNow(boolean withTime, int resType) {
        String fFormat;
        Date res;
        if (withTime) fFormat = "yyyy-MM-dd HH:mm:ss"; else fFormat = "yyyy-MM-dd";
        switch (resType) {
            case MyAndronUtils.GETDATE_CURRENT:
                res = Calendar.getInstance().getTime();
                break;
            case MyAndronUtils.GETDATE_FIRST_DAY_OF_MONTH:
                res = firstDayOfCurrentMonth();
                break;
            default:
                res = Calendar.getInstance().getTime();
                break;
        }
        return (new SimpleDateFormat(fFormat)).format(res);
    }

    // wCols - array of column widths (0 - autowidth of column)
    public static void arrIntoTableLayout(List<String> pArr, TableLayout pTbl, List<Integer> wCols, Boolean boldFirstRow, Boolean boldLastRow) {
        pTbl.removeAllViews();
        int pColCount = wCols.size();
        int curRow; // from 1 to qntyRows
        int qntyRows = pArr.size()/pColCount;
        for (int i=0; i<pArr.size(); i+=pColCount) {
            curRow = (i/pColCount)+1;
            TableRow tr = new TableRow(pTbl.getContext());
            for (int j=0; j<pColCount; j++) {
                TextView tv = new TextView(pTbl.getContext());
                tv.setText(pArr.get(i + j));
                if (wCols.get(j)>0) tv.setWidth(wCols.get(j));
                else tv.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                if ( ((curRow==1)&&(boldFirstRow))||((curRow==qntyRows)&&(boldLastRow)) ) {
                    Typeface tf = tv.getTypeface();
                    tv.setTypeface(tf, Typeface.BOLD);
                }
                tr.addView(tv, new TableRow.LayoutParams());
                if (j<(pColCount-1)) {  //create dummy for margin (without last column)
                    TextView tvDummy = new TextView(pTbl.getContext());
                    tvDummy.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                    tvDummy.setText(" | ");
                    tr.addView(tvDummy, new TableRow.LayoutParams());
                }
            }
            pTbl.addView(tr, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
        //pTbl.setStretchAllColumns(true);
    }

    public static String getMoneyStringById(Activity pActivity, Integer pId, String pPrefix) {
        String s = MainActivity.getDbObj().runSqlStatement("select '" + pActivity.getResources().getString(R.string.app_settings_fieldnames_money_id) + ": '||id as id, '" + pActivity.getResources().getString(R.string.app_settings_fieldnames_money_date_of_tra) + ": '||to_char(date_of_tra,'DD.MM.YY HH24:MI') as date, '" + pActivity.getResources().getString(R.string.app_settings_fieldnames_money_sum_of_tra) + ": '||sum_of_tra as sum, '" + pActivity.getResources().getString(R.string.app_settings_fieldnames_money_text_of_tra) + ": '||text_of_tra as desc from money where id=" + pId, false, "\n");
        s = pPrefix + "\n" + s;
        return s;
    }

}
