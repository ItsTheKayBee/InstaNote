package com.example.instanote;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

final class SearchResults {

    private static Context context;
    private static ProgressBar resultsProgress;
    private static RelativeLayout darkLayout;
    private static SearchView searchView;
    private static RecyclerView resultsView;
    private static FloatingActionButton fab;
    private static Activity activity;
    private static Menu menu;


  /*  SearchResults(String incompleteQuery){
        new GetSuggestions().execute(incompleteQuery);
    }*/

    SearchResults(Activity activity, Context context, Menu menu, String query) {
        SearchResults.activity = activity;
        SearchResults.menu = menu;
        SearchResults.context = context;
        resultsProgress = activity.findViewById(R.id.results_progress);
        darkLayout = activity.findViewById(R.id.dim_layout);
        searchView = activity.findViewById(R.id.search_bar);
        resultsView = activity.findViewById(R.id.results_view);
        fab = activity.findViewById(R.id.fab);

        new GetLinks().execute(query);
    }

    private static class GetLinks extends AsyncTask<String, Void, Void> {
        private static final String API_KEY = BuildConfig.SEARCH_API_KEY;
        private static final String CSE_ID = BuildConfig.SEARCH_ENGINE_ID;
        ArrayList<String> links = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            resultsProgress.setProgress(0);
            resultsProgress.setVisibility(View.VISIBLE);
            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        resultsView.setVisibility(View.GONE);
                        darkLayout.setVisibility(View.VISIBLE);
                    } else {
                        searchView.clearFocus();
                        resultsView.setVisibility(View.VISIBLE);
                        darkLayout.setVisibility(View.GONE);
                    }
                }
            });
            searchView.clearFocus();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String query = strings[0];

            String url = "https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" + CSE_ID + "&q=" + query;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray items = response.getJSONArray("items");
                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject item = items.getJSONObject(i);
                                    if (item.getString("link").contains("http://"))
                                        links.add(item.getString("link").replace("http", "https"));
                                    else
                                        links.add(item.getString("link"));
                                    titles.add(item.getString("title"));
                                }
                                new GetResults(null).execute(links, titles, null, null, null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            Volley.newRequestQueue(context).add(jsonObjectRequest);
            return null;
        }
    }

    private static class GetResults extends AsyncTask<ArrayList, Integer, Integer> implements PinnedAdapter.CardClickListener, PinnedAdapter.CardLongClickListener {
        private static final String TITLE = "TITLE";
        private static final String TEXT = "TEXT";
        private static final String PINNED = "PINNED";
        private static final String LINK = "LINK";
        private static final String ID = "ID";
        private ArrayList<String> links = new ArrayList<>();
        private ArrayList<String> linksLim;
        private ArrayList<String> titles = new ArrayList<>();
        private ArrayList<String> titlesLim;
        private ArrayList<String> htmlContent;
        private ActionMode mode = null;
        private PinnedAdapter adapter;
        private ArrayList<Integer> selectedArray;
        private ActionMode.Callback actionCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.save) {
                    save();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                selectedArray.clear();
                adapter.setSelectedList(selectedArray);
                adapter.notifyDataSetChanged();
                mode = null;
            }
        };

        GetResults(PinnedAdapter adapter) {
            this.adapter = adapter;
            if (adapter == null) {
                setCount(0);
            }
        }

        @Override
        protected void onPreExecute() {
            resultsProgress.setProgress(0);
            resultsProgress.setVisibility(View.VISIBLE);
            darkLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(ArrayList... arrayLists) {
            links = arrayLists[0];
            titles = arrayLists[1];
            linksLim = arrayLists[2];
            titlesLim = arrayLists[3];
            htmlContent = arrayLists[4];
            if (linksLim == null) {
                linksLim = new ArrayList<>();
            }
            if (titlesLim == null) {
                titlesLim = new ArrayList<>();
            }
            if (htmlContent == null) {
                htmlContent = new ArrayList<>();
            }

            int i = 0;
            int count = getCount();
            if (count < 9) {
                for (int i1 = count; i1 < count + 3; i1++) {
                    String link = links.get(i1);
                    try {
                        URL url = new URL(link);

                        BoilerpipeExtractor extractor = CommonExtractors.DEFAULT_EXTRACTOR;
                        HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();
                        String data = hh.process(url, extractor);

                        htmlContent.add(data);
                        linksLim.add(links.get(i1));
                        titlesLim.add(titles.get(i1));
                        i++;
                        publishProgress((int) ((i / (float) 3) * 100));
                    } catch (BoilerpipeProcessingException | IOException | SAXException e) {
                        e.printStackTrace();
                    }
                }
                setCount(count + 3);
            }
            return getCount();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            resultsProgress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer size) {
            resultsProgress.setVisibility(View.GONE);
            darkLayout.setVisibility(View.GONE);
            final int count = getCount();
            if (count == 3) {
                resultsView.setLayoutManager(new LinearLayoutManager(context));
                adapter = new PinnedAdapter(context, titlesLim, linksLim, htmlContent, null);
                adapter.setClickListener(this);
                adapter.setLongClickListener(this);
                resultsView.setAdapter(adapter);
            } else {
                View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                adapter.notifyItemRangeInserted(count - 3, 3);
                Snackbar.make(view, "More results displayed", Snackbar.LENGTH_SHORT)
                        .show();
            }
            resultsView.setVisibility(View.VISIBLE);
            selectedArray = new ArrayList<>();
            final MenuItem clearResults = menu.findItem(R.id.clear_results);
            final View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            clearResults.setVisible(true);
            clearResults.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (clearAll()) {
                        clearResults.setVisible(false);
                        searchView.clearFocus();
                    }
                    return true;
                }
            });
            if (count < 9)
                fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (count < 9) {
                        if (count == 6) {
                            fab.setVisibility(View.GONE);
                        }
                        new GetResults(adapter).execute(links, titles, linksLim, titlesLim, htmlContent);
                    } else {
                        fab.setVisibility(View.GONE);
                    }
                }
            });
        }

        @Override
        public void onCardClick(View view, int position) {
            if (mode == null) {
                Intent intent = new Intent(context, ResultActivity.class);
                intent.putExtra(TITLE, adapter.getCardTitle(position));
                intent.putExtra(TEXT, adapter.getCardText(position));
                intent.putExtra(LINK, adapter.getCardLink(position));
                intent.putExtra(ID, adapter.getCardId(position));
                intent.putExtra(PINNED, false);
                context.startActivity(intent);
            } else {
                selectCard(view, position);
            }
        }

        @Override
        public void onCardLongClick(View view, int position) {
            if (mode != null) {
                selectCard(view, position);
            } else {
                mode = activity.startActionMode(actionCallback);
                selectCard(view, position);
            }
        }

        private void save() {
            Collections.sort(selectedArray);
            int size = selectedArray.size();
            DbManager dbManager = new DbManager(context);
            try {
                dbManager.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for (int j : selectedArray) {
                String content=htmlContent.get(j);
                String title=titlesLim.get(j);
                String link=linksLim.get(j);
                dbManager.insertData(title,link,content);
            }
            dbManager.close();
            selectedArray.clear();
            mode.finish();

            View view = activity.findViewById(R.id.save);
            String snackText;
            if (size == 1) {
                snackText = size + " note saved";
            } else {
                snackText = size + " notes saved";
            }
            Snackbar.make(view, snackText, BaseTransientBottomBar.LENGTH_SHORT)
                    .show();
        }

        private void selectCard(View view, int position) {
            if (selectedArray.contains(position)) {
                selectedArray.remove(Integer.valueOf(position));
                if (selectedArray.size() == 0) {
                    mode.finish();
                    mode = null;
                }
            } else {
                selectedArray.add(position);
            }
            if (selectedArray.size() > 0) {
                if (mode != null) {
                    if (selectedArray.size() == 1)
                        mode.setTitle(selectedArray.size() + " note selected");
                    else
                        mode.setTitle(selectedArray.size() + " notes selected");
                }
            }
            adapter.setSelectedList(selectedArray);
            adapter.notifyItemChanged(position);
        }

        private boolean clearAll() {
            new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
                    .setTitle("Are you sure?")
                    .setMessage("All results will be cleared")
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // do nothing
                        }
                    })
                    .setPositiveButton("CLEAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectedArray.clear();
                            links.clear();
                            titles.clear();
                            htmlContent.clear();
                            adapter.notifyItemRangeChanged(0, linksLim.size());
                            resultsView.setVisibility(View.GONE);
                            searchView.setQuery("", false);
                            View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                            Snackbar.make(view, "All results cleared", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return true;
        }

        private int getCount() {
            SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
            int defaultValue = 0;
            return sharedPref.getInt("RESULT_COUNT", defaultValue);
        }

        private void setCount(int count) {
            SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("RESULT_COUNT", count);
            editor.apply();
        }
    }

   /* private static class GetSuggestions extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            String query = strings[0];
            String url = "https://www.google.com/search?output=toolbar&q=" + query;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("DOCUMENT", "Response is: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("DOCUMENT", "Not working");
                }
            });
            queue.add(stringRequest);
            return null;
        }
    }*/

}
