package articlesearch.codepath.com.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import articlesearch.codepath.com.nytimessearch.R;

public class FilterActivity extends AppCompatActivity {
    Spinner sortOrder;
    DatePicker beginDate;
    CheckBox checkBoxArts;
    CheckBox checkBoxFashionStyle;
    CheckBox checkBoxSports;
    Button btnSetFilter;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sortOrder = (Spinner) findViewById(R.id.sortOrderSpinner);
        beginDate = (DatePicker) findViewById(R.id.dpResult);
        checkBoxArts = (CheckBox) findViewById(R.id.checkbox_arts);
        checkBoxFashionStyle = (CheckBox) findViewById(R.id.checkbox_fashion_style);
        checkBoxSports = (CheckBox) findViewById(R.id.checkbox_sports);
        btnSetFilter = (Button) findViewById(R.id.btnSearch);

        // Access intent
        query = getIntent().getStringExtra("query");
    }

    public void onSetFilter(View view) {
        Intent showOtherActivityIntent = new Intent(this, SearchActivity.class);
        showOtherActivityIntent.putExtra("sortOrder", sortOrder.getSelectedItem().toString());

        showOtherActivityIntent.putExtra("beginDateDay", beginDate.getDayOfMonth());
        showOtherActivityIntent.putExtra("beginDateMonth", beginDate.getMonth() + 1);
        showOtherActivityIntent.putExtra("beginDateYear", beginDate.getYear());

        showOtherActivityIntent.putExtra("checkBoxArts", checkBoxArts.isChecked());
        showOtherActivityIntent.putExtra("checkBoxFashionStyle", checkBoxFashionStyle.isChecked());
        showOtherActivityIntent.putExtra("checkBoxSports", checkBoxSports.isChecked());
        showOtherActivityIntent.putExtra("query", query);

        startActivity(showOtherActivityIntent);
    }
}