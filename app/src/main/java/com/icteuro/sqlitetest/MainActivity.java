package com.icteuro.sqlitetest;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Button btnAdd;
    private EditText etName,etContact;
    private DatabaseHandler db;
    private List<Contact> contacts  = new ArrayList<>();
    private ListView listContact;
    HistoryAdapter historyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db = new DatabaseHandler(this);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        etName = (EditText) findViewById(R.id.etName);
        etContact = (EditText)findViewById(R.id.etContact);
        listContact = (ListView)findViewById(R.id.listContact);

        contacts = db.getAllContacts();
        for (Contact cn : contacts) {
            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
            // Writing Contacts to log
            Log.d("Name: ", log);
            historyAdapter = new HistoryAdapter(this);
            listContact.setAdapter(historyAdapter);
            historyAdapter.notifyDataSetChanged();

        }


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertContact();
            }
        });



    }

    private void insertContact() {



        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..");
        db.addContact(new Contact(etName.getText().toString(), etContact.getText().toString()));
//        db.addContact(new Contact("Srinivas", "9199999999"));
//        db.addContact(new Contact("Tommy", "9522222222"));
//        db.addContact(new Contact("Karthik", "9533333333"));

        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");

        contacts = db.getAllContacts();
        for (Contact cn : contacts) {
            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
            // Writing Contacts to log
            Log.d("Name: ", log);

            historyAdapter = new HistoryAdapter(this);
            listContact.setAdapter(historyAdapter);
            historyAdapter.notifyDataSetChanged();
        }

    }



    private class HistoryAdapter extends ArrayAdapter<Contact> {
        Context context;

        public HistoryAdapter(Context context) {
            super(context, R.layout.raw_contact, contacts);

            this.context = context;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            position = position;
            View v = convertView;

            if (v == null) {
                final LayoutInflater vi = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.raw_contact, null);

            }

            if( position<contacts.size()){

                final Contact query = contacts.get(position);



                final TextView tvName = (TextView)v.findViewById(R.id.tvName);
                final TextView tvPhone = (TextView)v.findViewById(R.id.tvPhone);
                final TextView tvDelete = (TextView)v.findViewById(R.id.tvDelete);
                final TextView tvUpDate = (TextView)v.findViewById(R.id.tvUpDate);
                tvName.setText(query.getName());
                tvPhone.setText(query.getPhoneNumber());

                tvUpDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateDialog(context,tvName.getText().toString(),tvPhone.getText().toString(),query.getName());
                    }
                });

                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.deleteContact(query);
                        contacts.remove(query);
//                        historyAdapter = new HistoryAdapter(context);
//                        listContact.setAdapter(historyAdapter);
                        historyAdapter.notifyDataSetChanged();
                    }
                });
            }


            return v;
        }
    }

    private void updateDialog(final Context con, String name, String phone, final String upName){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_update);
        // set the custom dialog components - text, image and button
        final EditText etUpName = (EditText) dialog.findViewById(R.id.etUpName);
        etUpName.setText(name);

        final EditText etUpPhone = (EditText) dialog.findViewById(R.id.etUpPhone);
        etUpPhone.setText(phone);

        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.updateContact(new Contact(etUpName.getText().toString(), etUpPhone.getText().toString()),upName);
                contacts = db.getAllContacts();


                for (Contact cn : contacts) {
                    String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
                    // Writing Contacts to log
                    Log.d("Name: ", log);
                    historyAdapter = new HistoryAdapter(con);
                    listContact.setAdapter(historyAdapter);
                    historyAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
