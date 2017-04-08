package com.cdx.onestepsos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by CDX on 2017/4/8.
 */

public class UiActivity extends Activity implements View.OnClickListener {
    private Button btn_add_contacts;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uilayout);
        btn_add_contacts = (Button) findViewById(R.id.btn_add_contacts);
        btn_add_contacts.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_contacts:
                AddContactDialog addContactDialog = new AddContactDialog(this);
                addContactDialog.show();
                break;

        }
    }
}
