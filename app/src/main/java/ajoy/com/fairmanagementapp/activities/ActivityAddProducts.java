package ajoy.com.fairmanagementapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ajoy.com.fairmanagementapp.extras.Utility;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.materialtest.R;


public class ActivityAddProducts extends AppCompatActivity {


    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect;
    private ImageView ivImage;
    private String userChoosenTask;

    private static final String url = "jdbc:mysql://192.168.0.101:3306/";
    private static final String username="ajoy";
    private static final String password="ajoydas";

    private ImageView imageView;
    private EditText nameInput;
    private EditText companyInput;
    private EditText descriptionInput;
    private EditText priceInput;
    private RadioGroup radioGroup;
    private String availability;
    private String image;
    private Bitmap bitmap;
    private String name;
    private String company;
    private String description;
    private String price;

    private Uri filePath;

    private String fair_db_name;
    private String fair_stall;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fair_db_name=getIntent().getStringExtra("db_name");
        fair_stall = getIntent().getStringExtra("stall");
        System.out.println("Db: "+fair_db_name+"Stall: "+fair_stall);

        setContentView(R.layout.activity_add_products);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        imageView = (ImageView) findViewById(R.id.uploadedimage);
        nameInput = (EditText) findViewById(R.id.addproductname);
        companyInput = (EditText) findViewById(R.id.addproductcompany);
        descriptionInput = (EditText) findViewById(R.id.addproductdescription);
        priceInput = (EditText) findViewById(R.id.addproductprice);
        availability = "High";
        image=null;
        bitmap=null;
        radioGroup= (RadioGroup)findViewById(R.id.availabilityoption);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == R.id.addproducthigh) {
                    availability="High";
                } else if (checkedId == R.id.addproductmedium) {
                    availability = "Medium";
                } else if (checkedId == R.id.addproductlow) {
                    availability = "Low";
                } else if (checkedId == R.id.addproductoutofstock) {
                    availability = "Out of Stock";
                }
            }
        });


    }

    public void addProductClicked(View view) {
        name = nameInput.getText().toString();
        company = companyInput.getText().toString();
        description = descriptionInput.getText().toString();
        price = priceInput.getText().toString();

        if(name==null||price==null||name.equals("")||!isDouble(price))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddProducts.this);
            builder.setTitle("Invalid!");
            builder.setMessage("Product Name or Price is Missing or Invalid.Please Try Again.");
            builder.setCancelable(true);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else {
            upload();
        }
    }

    boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void cancelClicked(View view) {
        finish();
    }

    public void uploadImageClicked(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        selectImage();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddProducts.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(ActivityAddProducts.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }



    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image*/
/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

*/

    private static Bitmap codec(Bitmap src,
                                int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, os);

        byte[] array = os.toByteArray();
        System.out.println("Byte size:   "+array.length);
        Bitmap temp=BitmapFactory.decodeByteArray(array, 0, array.length);
        System.out.println("Byte size:   "+array.length);
        return temp;
    }


    //Handles the Activity Result and Process the requested result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE){
                filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap = codec(bitmap, 20);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == REQUEST_CAMERA) {
                bitmap = (Bitmap) data.getExtras().get("data");
                //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap = codec(bitmap, 20);
                imageView.setImageBitmap(bitmap);
            }

        }

        /*if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }


    //Bitmap to string converter
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    //process upload image request and upload to database
    private void upload(){
        class Upload extends AsyncTask<Void,Void,Integer> {

            ProgressDialog loading;
            //RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if(bitmap!=null) {
                    image = getStringImage(bitmap);
                }
                loading = ProgressDialog.show(ActivityAddProducts.this, "Saving Product", "Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(Integer value) {
                super.onPostExecute(value);
                loading.dismiss();
                if(value==1)
                {
                    L.t(getApplicationContext(),"Saved Successfully");
                    finish();
                }
                else
                {
                    L.t(getApplicationContext(),"Saving Failed!");
                }
            }

            @Override
            protected Integer doInBackground(Void... params) {

                Integer result=0;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    String Url = url + fair_db_name;
                    Connection con = DriverManager.getConnection(Url, username, password);
                    System.out.println("Connected");

                    PreparedStatement st = con.prepareStatement("INSERT INTO products" +
                            "(stall,name,company,description,price,availability,image)" +
                            "VALUES" +
                            "(?,?,?,?,?,?,?)");
                    st.setString(1, fair_stall);
                    st.setString(2,name);
                    st.setString(3,company);
                    st.setString(4,description);
                    st.setString(5,price);
                    st.setString(6,availability);
                    st.setString(7,image);

                    System.out.println("Statement");

                    ResultSet rs = null;

                    int rows = st.executeUpdate();

                    System.out.println(rows);

                    if (rows == 1) {
                        result=1;
                    }

                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
                return  result;

            }

                /*HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);

                String result = rh.sendPostRequest(UPLOAD_URL,data);
*/
            //return result;
        }

        Upload ui = new Upload();
        ui.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
