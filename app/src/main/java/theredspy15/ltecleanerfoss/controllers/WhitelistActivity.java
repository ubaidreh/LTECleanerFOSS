/*
 * Copyright 2021 Hunter J Drum
 */

package theredspy15.ltecleanerfoss.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import theredspy15.ltecleanerfoss.R;
import theredspy15.ltecleanerfoss.databinding.ActivityWhitelistBinding;

public class WhitelistActivity extends AppCompatActivity {

    BaseAdapter adapter;
    private static ArrayList<String> whiteList;

    ActivityWhitelistBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);

        binding = ActivityWhitelistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.resetWhiteList.setOnClickListener(this::emptyWhitelist);
        binding.recommendedButton.setOnClickListener(this::addRecommended);
        binding.addWhiteList.setOnClickListener(this::addToWhiteList);

        adapter = new ArrayAdapter<>(this, R.layout.custom_textview, getWhiteList());
        binding.whitelistView.setAdapter(adapter);
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
                    MainActivity.prefs.edit().putString("whiteList", whiteList.toString()).apply();
                    refreshListView();
                })
                .setNegativeButton(R.string.cancel, (dialog, whichButton) -> { }).show();
    }

    public void addRecommended(View view) {
        String externalDir = this.getExternalFilesDir(null).getPath();
        externalDir = externalDir.substring(0, externalDir.indexOf("Android"));

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
            MainActivity.prefs.edit().putString("whiteList", whiteList.toString()).apply();
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
                    MainActivity.prefs.edit().putString("whiteList", whiteList.toString()).apply();
                    refreshListView();
                })
                .setNegativeButton(R.string.cancel, (dialog, whichButton) -> { }).show();
    }

    public void refreshListView() {
        runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            binding.whitelistView.invalidateViews();
            binding.whitelistView.refreshDrawableState();
        });
    }

    public static synchronized List<String> getWhiteList() {
        if (whiteList == null) {
            String whiteListString = MainActivity.prefs.getString("whiteList","no whitelist");
            String[] whitelistStrings = whiteListString.split(", ");
            whiteList = new ArrayList<>(Arrays.asList(whitelistStrings));
            whiteList.remove("[");
            whiteList.remove("]");
        }
        return whiteList;
    }
}
