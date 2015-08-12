package com.gdg.nanochat.ui.activity;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.gdg.nanochat.R;
import com.gdg.nanochat.model.ChatMessage;
import com.gdg.nanochat.ui.adapter.FirebaseListAdapter;

public class MainActivity
    extends ListActivity
{

    private Firebase firebaseRef;

    private EditText messageEditText;

    private FirebaseListAdapter listAdapter;

    private String username;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        messageEditText = (EditText) findViewById( R.id.message_text );
        configureFirebase();

        listAdapter =
            new FirebaseListAdapter<ChatMessage>( firebaseRef, ChatMessage.class, R.layout.message_layout, this )
            {
                @Override
                protected void populateView( View v, ChatMessage model )
                {
                    ( (TextView) v.findViewById( R.id.username_text_view ) ).setText( model.getName() );
                    ( (TextView) v.findViewById( R.id.message_text_view ) ).setText( model.getMessage() );
                }
            };

        setListAdapter( listAdapter );
    }

    private void configureFirebase()
    {
        //Allows Firebase client to keep its context
        Firebase.setAndroidContext( this );

        firebaseRef = new Firebase( "https://nanochat-gdg.firebaseio.com/" );

        firebaseRef.addAuthStateListener( new Firebase.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged( AuthData authData )
            {
                if ( authData != null )
                {
                    username = ( (String) authData.getProviderData().get( "email" ) );
                    findViewById( R.id.login ).setVisibility( View.GONE );
                    findViewById( R.id.logout ).setVisibility( View.VISIBLE );
                }
                else
                {
                    username = null;
                    findViewById( R.id.login ).setVisibility( View.VISIBLE );
                    findViewById( R.id.logout ).setVisibility( View.GONE );
                }
            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings )
        {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    public void onSendButtonClick( View view )
    {
        String message = messageEditText.getText().toString();
        firebaseRef.push().setValue( new ChatMessage( username, message ) );
        messageEditText.setText( "" );
    }

    public void onLoginButtonClick( View view )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );

        builder.setMessage( getString( R.string.Enter_your_email_address_and_password ) ).setTitle(
            getString( R.string.Log_in ) );

        LayoutInflater inflater = getLayoutInflater();
        builder.setView( inflater.inflate( R.layout.dialog_signin, null ) );

        builder.setPositiveButton( getString( R.string.OK ), new DialogInterface.OnClickListener()
        {
            public void onClick( DialogInterface dialog, int id )
            {
                AlertDialog alertDialog = (AlertDialog) dialog;
                final String email = ( (TextView) alertDialog.findViewById( R.id.email ) ).getText().toString();
                final String password = ( (TextView) alertDialog.findViewById( R.id.password ) ).getText().toString();

                login( email, password );
            }
        } );

        builder.setNegativeButton( getString( R.string.Cancel ), null );

        AlertDialog dialog = builder.create();

        dialog.show();
    }


    private void login( final String email, final String password )
    {
        firebaseRef.createUser( email, password, new Firebase.ResultHandler()
        {
            @Override
            public void onSuccess()
            {
                firebaseRef.authWithPassword( email, password, null );
            }

            @Override
            public void onError( FirebaseError firebaseError )
            {
                firebaseRef.authWithPassword( email, password, null );
            }
        } );
    }

    public void onLogoutButtonClick( View view )
    {
        firebaseRef.unauth();
    }
}
