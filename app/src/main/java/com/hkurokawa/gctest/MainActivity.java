package com.hkurokawa.gctest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private int softRefId = 0;
    private Collection<SoftReference<Data>> softRefSet;
    private Set<Data> strongRefSet;
    private NumberPicker softSizePick;
    private NumberPicker strongSizePick;
    private TextView numSoftReference;
    private TextView numStrongReference;
    private TextView softRefContent;
    private TextView heapStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.softRefSet = new ArrayList<>();
        this.strongRefSet = new HashSet<>();
        this.softSizePick = (NumberPicker) findViewById(R.id.sizeSoftReference);
        this.strongSizePick = (NumberPicker) findViewById(R.id.sizeStrongReference);
        this.softSizePick.setMaxValue(100);
        this.strongSizePick.setMaxValue(100);
        this.softRefContent = (TextView) findViewById(R.id.softReferenceContent);
        this.numSoftReference = (TextView) this.findViewById(R.id.numSoftReferenceObject);
        this.numStrongReference = (TextView) this.findViewById(R.id.numStrongReferenceObject);
        this.heapStatus = (TextView) this.findViewById(R.id.heapStatus);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onAddSoftReferenceClick(View btn) {
        this.softRefSet.add(new SoftReference<>(new Data(Character.toString((char)('A' + this.softRefId)), this.softSizePick.getValue() * 1024 * 1024)));
        this.update();
        this.softRefId++;
    }

    public void onAddStrongReferenceClick(View btn) {
        this.strongRefSet.add(new Data("", this.strongSizePick.getValue() * 1024 * 1024));
        this.update();
    }

    public void onClearStrongReferenceClick(View btn) {
        this.strongRefSet.clear();
        this.update();
    }

    public void onRunGCClick(View btn) {
        System.gc();
        this.update();
    }

    public void onRefreshClick(View btn) {
        this.update();
    }

    private void update() {
        final Iterator<SoftReference<Data>> iter = this.softRefSet.iterator();
        while (iter.hasNext()) {
            Reference<Data> ref = iter.next();
            if (ref.get() == null) {
                iter.remove();
            }
        }
        this.softRefContent.setText(toString(this.softRefSet));
        this.numSoftReference.setText(Integer.toString(this.softRefSet.size()));
        this.numStrongReference.setText(Integer.toString(this.strongRefSet.size()));

        final DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        this.heapStatus.setText("allocated: " + df.format((double) Runtime.getRuntime().totalMemory() / 1048576) + "MB of " + df.format((double) Runtime.getRuntime().maxMemory() / 1048576) + "MB (" + df.format((double) Runtime.getRuntime().freeMemory() / 1048576) + "MB free)");
    }

    private static String toString(Collection<SoftReference<Data>> collection) {
        final StringBuffer sb = new StringBuffer();
        for (Iterator<SoftReference<Data>> iter = collection.iterator(); iter.hasNext();) {
            final SoftReference<?> s = iter.next();
            sb.append(s.get() == null ? "null" : s.get()).append(", ");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    public static class Data {
        private final byte[] data;
        private final String id;

        public Data(String id, int size) {
            this.id = id;
            data = new byte[size];
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return this.id;
        }
    }
}