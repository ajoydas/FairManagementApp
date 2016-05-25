package ajoy.com.fairmanagementapp.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ajoy.com.fairmanagementapp.materialtest.R;
import ajoy.com.fairmanagementapp.pojo.Fair;

public class ActivitySellerSignin extends AppCompatActivity {

    Button loginButton;
    EditText usernameInput,passwordInput;

    private Toolbar mToolbar;

    ProgressDialog loading;
    //a layout grouping the toolbar and the tabs together
    private ViewGroup mContainerToolbar;
    private Fair fair;
    private static final String url = "jdbc:mysql://192.168.0.100:3306/logindatabase";
    private static final String username="ajoy";
    private static final String password="ajoydas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);
        fair=(Fair)getIntent().getParcelableExtra("Information");

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mContainerToolbar = (ViewGroup) findViewById(R.id.container_app_bar);
        //set the Toolbar as ActionBar
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);



        usernameInput=(EditText)findViewById(R.id.username);
        passwordInput=(EditText)findViewById(R.id.password);
        loginButton=(Button)findViewById(R.id.bsignin);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //new Mytask().execute();

            }
        });


    }



    public View getContainerToolbar() {
        return mContainerToolbar;
    }

    private class Mytask extends AsyncTask<Void,Void,Boolean>
    {
        private String user="",pass="",passrecieved="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getApplicationContext(), "Signing In", "Please wait...",true,true);
            user=usernameInput.getText().toString();
            pass=passwordInput.getText().toString();
            System.out.println(user+pass);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                String Url=url+fair.getDb_name();
                Connection con= DriverManager.getConnection(Url,username,password);

                System.out.println("Connected");

                PreparedStatement st=con.prepareStatement("Select password from  users where username=?");
                st.setString(1,user);

                System.out.println("Statement");

                ResultSet rs=null;
                //st.setString(1,user);
                rs=st.executeQuery();

                System.out.println("Executed");

                if(rs==null)
                {
                    return false;
                }
                while(rs.next()) {
                    passrecieved = rs.getString("password");
                    System.out.println("username: " + user + " password: " + passrecieved);
                }
                return true;

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            loading.dismiss();

            if(bool==true) {
                if (pass.equals(passrecieved)) {
                    Toast.makeText(getApplicationContext(), "Login Successful!!!",
                            Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "Login failed!",Toast.LENGTH_LONG).show();


                }
            }
            else
            {

            }
        }
    }

}
