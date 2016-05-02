package com.android.jrios.mysmssender;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SendActivity extends AppCompatActivity {

    static final int REQUEST_NUMBER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        // Que hacer cuando se presiona el boton de exit del menu
        if (id == R.id.action_exit)
        {
            finishAffinity(); // Cierra todas las Activities
            System.exit(0); // Salir de la aplicacion
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        finish(); // Cierra la Activity actual
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_NUMBER)
        {
            if(resultCode == RESULT_OK) {
                Uri contact = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(contact, projection, null, null, null);
                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIndex);

                    EditText et_num = (EditText) findViewById(R.id.editText_tlf);
                    et_num.setText(number);
                }
            }
        }
    }

    public void onContact(View v)
    {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, REQUEST_NUMBER);
        }

    }

    // Boton de envio presionado
    public void onSend(View v)
    {
        TextView status = (TextView)findViewById(R.id.textView_msgStatus);
        TextView tv_tlf = (TextView) findViewById(R.id.editText_tlf);
        TextView tv_msg = (TextView) findViewById(R.id.editText_msg);

        // Obtenemos los valores introducidos en los TextBox del numero de telefono y del mensaje
        String tlf = tv_tlf.getText().toString();
        String msg = tv_msg.getText().toString();

        // Comprobamos que los valores introducidos son adecuados (hay algo introducido en ellos)
        if((!tlf.isEmpty()) && (!msg.isEmpty()))
        {
            try // Intentamos enviar el SMS
            {
                // Enviamos el SMS
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(tlf, null, msg, null, null);

                // Notificamos del estado del envio
                status.setTextColor(Color.GREEN);
                status.setText("Message Sended");
            }

            catch (Exception e) // Captura cualquier excepcion acontecida en el envio del SMS
            {
                // Notificamos del estado del envio
                status.setTextColor(Color.RED);
                status.setText("Error when trying to send the message");
            }
        }
        else // Si no se ha incluido el numero de telefono o el mensaje, se le menciona al usuario
        {
            // Notificamos del estado del envio
            status.setTextColor(Color.YELLOW);
            status.setText("Write the phone number and the message text");
        }
    }
}
