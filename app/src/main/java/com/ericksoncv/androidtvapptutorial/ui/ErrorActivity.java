package com.ericksoncv.androidtvapptutorial.ui;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.ericksoncv.androidtvapptutorial.R;

public class ErrorActivity extends FragmentActivity {

    private static final String TAG = ErrorActivity.class.getSimpleName();

    private ErrorFragment mErrorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testError();
    }

    private void testError(){
        mErrorFragment = new ErrorFragment();
        getFragmentManager().beginTransaction().add(R.id.main_browse_fragment, mErrorFragment).commit();
    }
}
