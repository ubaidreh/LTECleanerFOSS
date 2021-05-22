/*
 * Copyright 2020 Hunter J Drum
 */

package theredspy15.ltecleanerfoss;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class WhitelistActivity extends AppCompatActivity {

    ListView listView;
    BaseAdapter adapter;
    static SharedPreferences prefs;
    private static List<String> whiteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);
        listView = findViewById(R.id.whitelistView);

        adapter = new ArrayAdapter<>(this, R.layout.custom_textview, getWhiteList());
        listView.setAdapter(adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Clears the whitelist, then sets it up again without loading saved one from stash
     * @param view the view that is clicked
     */
    public final void emptyWhitelist(View view) {

        new AlertDialog.Builder(WhitelistActivity.this,R.style.MyAlertDialogTheme)
                .setTitle(R.string.reset_whitelist)
                .setMessage(R.string.are_you_reset_whitelist)
                .setPositiveButton(R.string.reset, (dialog, whichButton) -> {
                    whiteList.clear();
                    prefs.edit().putString("whiteList", whiteList.toString()).apply();
                    refreshListView();
                })
                .setNegativeButton(R.string.cancel, (dialog, whichButton) -> { }).show();
    }

    public void addRecommended(View view) {
        File externalDir = Environment.getExternalStorageDirectory();

        if (!whiteList.contains(new File(externalDir, "Music").getPath())) {
            whiteList.add(new File(externalDir, "Music").getPath());
            whiteList.add(new File(externalDir, "Podcasts").getPath());
            whiteList.add(new File(externalDir, "Ringtones").getPath());
            whiteList.add(new File(externalDir, "Alarms").getPath());
            whiteList.add(new File(externalDir, "Notifications").getPath());
            whiteList.add(new File(externalDir, "Pictures").getPath());
            whiteList.add(new File(externalDir, "Movies").getPath());
            whiteList.add(new File(externalDir, "Download").getPath());
            whiteList.add(new File(externalDir, "DCIM").getPath());
            whiteList.add(new File(externalDir, "Documents").getPath());
            prefs.edit().putString("whiteList", whiteList.toString()).apply();
            refreshListView();

        } else
            Toast.makeText(this, "Already added",
                    Toast.LENGTH_LONG).show();
    }

    /**
     * Creates a dialog asking for a file/folder name to add to the whitelist
     * @param view the view that is clicked
     */
    public final void addToWhiteList(View view) {

        final EditText input = new EditText(WhitelistActivity.this);

        new AlertDialog.Builder(WhitelistActivity.this,R.style.MyAlertDialogTheme)
                .setTitle(R.string.add_to_whitelist)
                .setMessage(R.string.enter_file_name)
                .setView(input)
                .setPositiveButton(R.string.add, (dialog, whichButton) -> {
                    whiteList.add(String.valueOf(input.getText()));
                    prefs.edit().putString("whiteList", whiteList.toString()).apply();
                    refreshListView();
                })
                .setNegativeButton(R.string.cancel, (dialog, whichButton) -> { }).show();
    }

    public void refreshListView() {
        runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            listView.invalidateViews();
            listView.refreshDrawableState();
        });
    }

    public static synchronized List<String> getWhiteList() {
        if (whiteList == null) {
            String whiteListStrings = prefs.getString("whiteList","no whitelist");
            assert whiteListStrings != null;
            String[] whitelistString = whiteListStrings.split(", ");
            whiteList = Arrays.asList(whitelistString.clone());
        }
        return whiteList;
    }
}
