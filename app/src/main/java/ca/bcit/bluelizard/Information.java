package ca.bcit.bluelizard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import io.transmogrifier.DatasetDownloader;
import io.transmogrifier.Pair;
import io.transmogrifier.android.AndroidDatasetDownloader;
import io.transmogrifier.android.LocalAndroidManifestDownloader;
import io.transmogrifier.Manifest;
import io.transmogrifier.ManifestDownloader;
import io.transmogrifier.generic.SerialDatasetDownloader;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.TransmogrifierDelegate;

import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        ListView list = (ListView) findViewById(R.id.list);
        String[] values = new String[]{"Park", "Off Leash Area", "Athletics", "Playground"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                if (position == 0) {
                    Intent intent = new Intent(v.getContext(), MapsLocation.class);
                    startActivityForResult(intent, 0);
                }
                if (position == 1) {
                    Intent intent = new Intent(v.getContext(), MapsLocation.class);
                    startActivityForResult(intent, 0);
                }
            }
        });

        final ManifestDownloader<Integer> manifestDownloader;
        final DatasetDownloader<Integer> datasetDownloader;

        manifestDownloader = new LocalAndroidManifestDownloader(this);
        datasetDownloader  = new AndroidDatasetDownloader();

        try
        {
            final Transmogrifier<Integer> transmogrifier;

            transmogrifier = new Transmogrifier<Integer>(getFilesDir());
            transmogrifier.setTransmogrifierDelegate(new Delegate());
            transmogrifier.processManifest(R.raw.manifest, manifestDownloader, datasetDownloader);
        }
        catch(final IOException ex)
        {
            ex.printStackTrace();
        }

    }


}
