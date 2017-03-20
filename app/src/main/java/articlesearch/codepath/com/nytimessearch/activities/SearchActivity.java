package articlesearch.codepath.com.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import articlesearch.codepath.com.nytimessearch.models.Article;
import articlesearch.codepath.com.nytimessearch.adapters.ArticleArrayAdapter;
import articlesearch.codepath.com.nytimessearch.listeners.EndlessScrollListener;
import articlesearch.codepath.com.nytimessearch.R;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

public class SearchActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SearchActivity";

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    String query = null;
    String sortOrder = null;
    int beginDateDay = -1;
    int beginDateMonth = -1;
    int beginDateYear = -1;

    Boolean checkBoxArts = false;
    Boolean checkBoxFashionStyle = false;
    Boolean checkBoxSports = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.filter:
                        goToFilterActivity();
                        break;
                }
                return false;
            }
        });

        setupViews();

        getValuesFromIntent();
    }

    private void getValuesFromIntent() {
        query = getIntent().getStringExtra("query");
        if(!TextUtils.isEmpty(query)) {
            etQuery.setText(query);
        }
        sortOrder = getIntent().getStringExtra("sortOrder");

        beginDateDay = getIntent().getIntExtra("beginDateDay", -1);
        beginDateMonth= getIntent().getIntExtra("beginDateMonth", -1);
        beginDateYear = getIntent().getIntExtra("beginDateYear", -1);

        checkBoxArts = getIntent().getBooleanExtra("checkBoxArts", false);
        checkBoxFashionStyle = getIntent().getBooleanExtra("checkBoxFashionStyle", false);
        checkBoxSports = getIntent().getBooleanExtra("checkBoxSports", false);
    }

    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);

                Article article = articles.get(position);
                i.putExtra("article", article);

                startActivity(i);
            }

        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                return loadNextDataFromApi(page);
            }
        });
    }

    public boolean loadNextDataFromApi(int offset) {
        if(offset == 0) {
            adapter.clear();
        }

        String query = etQuery.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "057e305e8d884f7cb1bcadeee1e14a55");
        params.put("page", offset);
        if(query != null) {
            params.put("q", query);
        }
        if(sortOrder != null) {
            params.put("sort", sortOrder);
        }
        if(beginDateDay != -1 && beginDateMonth != -1 && beginDateYear != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(beginDateYear, beginDateMonth-1, beginDateDay);

            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
            params.put("begin_date", format1.format(calendar.getTime()));
        }
        StringBuilder sb = new StringBuilder();
        if(checkBoxArts) {
            sb.append("\"Arts\" ");
        }
        if(checkBoxFashionStyle) {
            sb.append("\"Fashion & Style\" ");
        }
        if(checkBoxSports) {
            sb.append("\"Sports\" ");
        }
        if(sb.length() != 0) {
            params.put("fq", "news_desk:(" + sb.toString() + ")");
        }

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                AsyncHttpClient.log.w(LOG_TAG, "onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)", throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                AsyncHttpClient.log.w(LOG_TAG, "onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)", throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AsyncHttpClient.log.w(LOG_TAG, "onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)", throwable);
            }
        });

        return true;
    }

    public void goToFilterActivity() {
        Intent showOtherActivityIntent = new Intent(this, FilterActivity.class);
        showOtherActivityIntent.putExtra("query", etQuery.getText().toString());
        startActivity(showOtherActivityIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        adapter.clear();

        String query = etQuery.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "057e305e8d884f7cb1bcadeee1e14a55");
        params.put("page", 0);
        if(query != null) {
            params.put("q", query);
        }
        if(sortOrder != null) {
            params.put("sort", sortOrder);
        }
        if(beginDateDay != -1 && beginDateMonth != -1 && beginDateYear != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(beginDateYear, beginDateMonth-1, beginDateDay);

            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
            params.put("begin_date", format1.format(calendar.getTime()));
        }
        StringBuilder sb = new StringBuilder();
        if(checkBoxArts) {
            sb.append("\"Arts\" ");
        }
        if(checkBoxFashionStyle) {
            sb.append("\"Fashion & Style\" ");
        }
        if(checkBoxSports) {
            sb.append("\"Sports\" ");
        }
        if(sb.length() != 0) {
            params.put("fq", "news_desk:(" + sb.toString() + ")");
        }

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                AsyncHttpClient.log.w(LOG_TAG, "onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)", throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                AsyncHttpClient.log.w(LOG_TAG, "onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)", throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AsyncHttpClient.log.w(LOG_TAG, "onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)", throwable);
            }
        });
    }
}
