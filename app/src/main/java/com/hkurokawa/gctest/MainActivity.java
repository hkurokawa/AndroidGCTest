package com.hkurokawa.gctest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private static int MB = 1024 * 1024;
    private int softRefId = 0;
    private Collection<WeakReference<Data>> weakRefSet;
    private Collection<SoftReference<Data>> softRefSet;
    private Collection<Data> weakRefAnchorSet;
    private Collection<Data> softRefAnchorSet;
    private Collection<Data> strongRefSet;
    private EditText weakSizePick;
    private EditText softSizePick;
    private EditText strongSizePick;
    private TextView numWeakReference;
    private TextView numSoftReference;
    private TextView numStrongReference;
    private TextView softRefContent;
    private TextView heapStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.weakRefSet = new HashSet<>();
        this.weakRefAnchorSet = new HashSet<>();
        this.softRefSet = new ArrayList<>();
        this.softRefAnchorSet = new HashSet<>();
        this.strongRefSet = new HashSet<>();
        this.weakSizePick = (EditText) findViewById(R.id.sizeWeakReference);
        this.softSizePick = (EditText) findViewById(R.id.sizeSoftReference);
        this.strongSizePick = (EditText) findViewById(R.id.sizeStrongReference);
        this.softRefContent = (TextView) findViewById(R.id.softReferenceContent);
        this.numWeakReference = (TextView) this.findViewById(R.id.numWeakReferenceObject);
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

    public void onAddWeakReferenceClick(View btn) {
        final Data data = new Data("", parseEditText(this.weakSizePick) * MB);
        this.weakRefSet.add(new WeakReference<>(data));
        this.weakRefAnchorSet.add(data);
        this.update();
    }

    public void onClearWeakReferenceClick(View btn) {
        this.weakRefAnchorSet.clear();
        this.update();
    }

    public void onAddSoftReferenceClick(View btn) {
        final Data data = new Data(Integer.toString(this.softRefId),parseEditText(this.softSizePick) * MB);
        this.softRefSet.add(new SoftReference<>(data));
        this.softRefAnchorSet.add(data);
        this.update();
        this.softRefId++;
    }

    public void onClearSoftReferenceClick(View btn) {
        this.softRefAnchorSet.clear();
        this.update();
    }

    public void onAddStrongReferenceClick(View btn) {
        this.strongRefSet.add(new Data("", parseEditText(this.strongSizePick) * MB));
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
        cleanUp(this.weakRefSet);
        this.numWeakReference.setText(Integer.toString(this.weakRefSet.size()));
        cleanUp(this.softRefSet);
        this.softRefContent.setText(toString(this.softRefSet));
        this.numSoftReference.setText(Integer.toString(this.softRefSet.size()));
        this.numStrongReference.setText(Integer.toString(this.strongRefSet.size()));

        final DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        this.heapStatus.setText("allocated: " + df.format((double) Runtime.getRuntime().totalMemory() / 1048576) + "MB of " + df.format((double) Runtime.getRuntime().maxMemory() / 1048576) + "MB (" + df.format((double) Runtime.getRuntime().freeMemory() / 1048576) + "MB free)");
    }

    private static void cleanUp(Collection<? extends Reference<Data>> refs) {
        for (Iterator<? extends Reference<Data>> iter = refs.iterator(); iter.hasNext();) {
            Reference<Data> ref = iter.next();
            if (ref.get() == null) {
                iter.remove();
            }
        }
    }

    private static int parseEditText(EditText text) {
        try {
            return Integer.parseInt(text.getText().toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String toString(Collection<? extends Reference<Data>> collection) {
        final StringBuilder sb = new StringBuilder();
        for (final Reference<Data> s : collection) {
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
