package com.charles.myroadsideassistant.Components;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.charles.myroadsideassistant.Credentials.Signin;
import com.charles.myroadsideassistant.CustomAlertDialog.ContactsUpdateDialog;
import com.charles.myroadsideassistant.CustomAlertDialog.PanicDialog;
import com.charles.myroadsideassistant.CustomAlertDialog.PlaceChooserDialog;
import com.charles.myroadsideassistant.CustomAlertDialog.ProfileDialog;
import com.charles.myroadsideassistant.CustomAlertDialog.ShowCreatedProfileDialog;
import com.charles.myroadsideassistant.Database.ContactsDb;
import com.charles.myroadsideassistant.Database.Profile;
import com.charles.myroadsideassistant.GoogleMap.NearbyPlacesOfCurrentLocation;
import com.charles.myroadsideassistant.R;
import com.charles.myroadsideassistant.Service.Constants;
import com.charles.myroadsideassistant.Service.LocationService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ProfileDialog.ProfileCreateListener, ShowCreatedProfileDialog.ShowProfileCreateListener, ContactsUpdateDialog.ContactsUpdateDialogListener, PanicDialog.ContactsUpdateDialogListener, PlaceChooserDialog.ContactsUpdateDialogListener {

    ImageView setProfileImg, emergencyImg, updateProfileImg, deleteProfileImg, onlineentertainment_img, checkNearbyPlaces;

    TextView setProfile, emergency, updateProfile, deleteProfile, onlineentertainment_text, checkNearbyPlaces_text;

    private static final int PROXIMITY_RADIUS = 25;
    private static final String TAG = "MainActivity";
    ImageView create_ten_contacts_img, check_ten_contacts_img;


    TextView create_ten_contacts_text, check_ten_concats;

    FirebaseStorage storage;
    StorageReference storageReference;

    String uriPath= "";

    Profile pf;

    ContactsDb cdb;

    private static final int CONTACT_PERMISSION_CODE= 1;
    private static final int CONTACT_PICK_CODE= 2;

    private static final int REQUEST_CODE_LOCATION_PERMISSION= 1;

    private static final int PERMISSION_REQUEST_SEND_SMS = 0;

    ArrayList<String> ContactNumber;
    ArrayList<String> ContactName;

    Set<String> ContactsSet;

    private GoogleMap map;
    UiSettings mapSettings;

    private static final int REQUEST_CODE_LOCATION_PERMISSISON = 1;

    public static final int REQUEST_CODE_PERMISSION_RESULT = 5;

    String mobileNumber="";

    MediaPlayer mp;

    LocationService ls;

    String concatNumber="", contactName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        mp= new MediaPlayer();

        mp= MediaPlayer.create(this, R.raw.alarm_sound);

        storage= FirebaseStorage.getInstance();
        storageReference= storage.getReference();

        pf= new Profile(MainActivity.this);

        cdb= new ContactsDb(MainActivity.this);

        ContactNumber= new ArrayList<>();
        ContactName= new ArrayList<>();
        ContactsSet= new LinkedHashSet<>();

        setProfile= (TextView) findViewById(R.id.user_text_id);
        setProfileImg= (ImageView) findViewById(R.id.user_img_id);

        emergencyImg= (ImageView) findViewById(R.id.emmergency_img_id);
        emergency= (TextView) findViewById(R.id.emmergency_text_id);

        updateProfileImg= (ImageView) findViewById(R.id.update_img_id);
        updateProfile= (TextView) findViewById(R.id.updateprofile_text_id);

        deleteProfile= (TextView) findViewById(R.id.delete_text_id);
        deleteProfileImg= (ImageView) findViewById(R.id.delete_img_id);

        create_ten_contacts_img= (ImageView) findViewById(R.id.take_contacts_from_phone_book_img_id);
        create_ten_contacts_text= (TextView) findViewById(R.id.take_contacts_from_phonebook_text_id);

        check_ten_contacts_img= (ImageView) findViewById(R.id.check_contacts);
        check_ten_concats= (TextView) findViewById(R.id.check_contact_id);

        onlineentertainment_img= (ImageView) findViewById(R.id.online_etertainment_img_id);
        onlineentertainment_text= (TextView) findViewById(R.id.online_entertainment_text_id);

        checkNearbyPlaces= (ImageView) findViewById(R.id.check_nearby_places_img_id);
        checkNearbyPlaces_text= (TextView) findViewById(R.id.check_nearby_places_text_id);

        checkNearbyPlaces.setOnClickListener(this);
        checkNearbyPlaces_text.setOnClickListener(this);

        setProfileImg.setOnClickListener(this);
        setProfile.setOnClickListener(this);

        emergencyImg.setOnClickListener(this);
        emergency.setOnClickListener(this);

        updateProfile.setOnClickListener(this);
        updateProfileImg.setOnClickListener(this);

        deleteProfile.setOnClickListener(this);
        deleteProfileImg.setOnClickListener(this);

        create_ten_contacts_img.setOnClickListener(this);
        create_ten_contacts_text.setOnClickListener(this);

        check_ten_concats.setOnClickListener(this);
        check_ten_contacts_img.setOnClickListener(this);

        onlineentertainment_img.setOnClickListener(this);
        onlineentertainment_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_text_id:
                profileAlert();
                break;
            case R.id.user_img_id:
                profileAlert();
                break;
            case R.id.emmergency_text_id:
                play();
                panic();
                break;
            case R.id.emmergency_img_id:
                play();
                panic();
                break;
            case R.id.updateprofile_text_id:
                updateMembers();
                break;
            case R.id.update_img_id:
                updateMembers();
                break;
            case R.id.delete_img_id:
                stopLocationService();
                break;
            case R.id.delete_text_id:
                stopLocationService();
                break;
            case R.id.take_contacts_from_phone_book_img_id:
                inputTenContacts();
                displayStoredContactsAndUpload();
                break;
            case R.id.take_contacts_from_phonebook_text_id:
                inputTenContacts();
                displayStoredContactsAndUpload();
                break;
            case R.id.check_contact_id:
                checkContacts();
                break;
            case R.id.check_contacts:
                checkContacts();
                break;
            case R.id.online_entertainment_text_id:
                onlineEntertainmentAlert();
                break;
            case R.id.online_etertainment_img_id:
                onlineEntertainmentAlert();
                break;
            case R.id.check_nearby_places_img_id:
                checkNearbyPlaces();
                break;
            case R.id.check_nearby_places_text_id:
                checkNearbyPlaces();
                break;
        }
    }

    private void checkNearbyPlaces() {
        PlaceChooserDialog pcd= new PlaceChooserDialog();
        pcd.show(getSupportFragmentManager(), "PlaceChooserDialog");
       // startActivity(new Intent(MainActivity.this, NearbyPlacesOfCurrentLocation.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,Menu.FIRST,Menu.NONE,"Logout");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Signin.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onlineEntertainmentAlert() {
        AlertDialog.Builder a11= new AlertDialog.Builder(MainActivity.this);
        a11.setTitle("Online Entertainment");
        a11.setMessage("Choose among live weather forecasting and live news updates\n\nChoose your option");
        a11.setPositiveButton("Live Weather", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(), Weather.class));
            }
        });
        a11.setNegativeButton("Live News", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(), News1.class));
            }
        });

        AlertDialog a1= a11.create();
        a1.show();
    }

    private void updateMembers() {
        AlertDialog.Builder a11= new AlertDialog.Builder(MainActivity.this);
        a11.setTitle("Update Members");
        a11.setMessage("You can either update member details or entirely delete member details and create a fresh new member details from the begining.\n\nPlease choose appropriate options");
        a11.setPositiveButton("Update a single member", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openContactsUpdateDialog();
            }
        });

        a11.setNegativeButton("Delete all member details", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cdb.delete();
                recreate();
            }
        });

        a11.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog a1= a11.create();
        a1.show();
    }

    private void openContactsUpdateDialog() {
        ContactsUpdateDialog cud= new ContactsUpdateDialog();
        cud.show(getSupportFragmentManager(), "Update Contacts");
    }

    private void play() {
        if (mp== null) {
         //   mp= MediaPlayer.create(this, R.raw.alarm_sound);
         //   mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
           //     @Override
          //      public void onCompletion(MediaPlayer mp) {
          //          play();
          //      }
         //   });
        }
       // mp.start();
    }

    private void profileAlert() {
        AlertDialog.Builder a11= new AlertDialog.Builder(MainActivity.this);
        a11.setTitle("Profile Section");
        a11.setMessage("Choose appropriate option\n\n");
        a11.setPositiveButton("Crete Profile", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openDialog();
            }
        });

        a11.setNegativeButton("Check Profile", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkProfile();
            }
        });

        a11.setNeutralButton("Update profile", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog a1= a11.create();
        a1.show();
    }

    private void checkProfile() {
        SQLiteDatabase db= pf.getWritableDatabase();

        String query = "select * from profile";
        Cursor c1 = db.rawQuery(query, null);

        if (c1.getCount() == 0) {
            Toast.makeText(this, "Create profile first!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (c1!=null && c1.getCount() > 0) {
                if (c1.moveToFirst()) {
                    openShowProfileDialog();
                }
            }
        }
    }

    private void openShowProfileDialog() {
        ShowCreatedProfileDialog spd= new ShowCreatedProfileDialog();
        spd.show(getSupportFragmentManager(), "Show Created Profile");
    }

    private void openDialog() {
        ProfileDialog pd= new ProfileDialog();
        pd.show(getSupportFragmentManager(), "Profile Creation");
    }

    @Override
    public void applyShowProfileCreateFields(String name1, String mobile1, Uri imageUri) {

    }

    @Override
    public void applyUpdateContactsFields(String newContactNumber1, String name11) {
        System.out.println("From applyUpdateContactsFields() name11 is: " + name11);
        String newContactNumber= newContactNumber1, oldName= name11;
        cdb.delete(oldName);
        cdb.insertData(oldName, newContactNumber);
        Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show();
        recreate();
    }

    private void panic() {

        openPanicDialog();
     /*   AlertDialog.Builder a11= new AlertDialog.Builder(MainActivity.this);
        a11.setTitle("Panic");
        a11.setMessage("From panic choose appropriate option\n\n");
        a11.setPositiveButton("Call 911", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                call_Nine_One_One();
            }
        });

        a11.setNegativeButton("Send Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermission();
                GoToLocation();
            }
        });

        AlertDialog a1= a11.create();
        a1.show();   */
    }

    private void openPanicDialog() {
        PanicDialog pd= new PanicDialog();
        pd.show(getSupportFragmentManager(), "Panic Dialog");
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    private void GoToLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            startLocationService();
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager!=null) {
            for (ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Toast.makeText(this, "Location service started...", Toast.LENGTH_SHORT).show();
            Intent intent= new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent= new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service stopped...", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        PERMISSION_REQUEST_SEND_SMS);
            }
        }
    }

    private void call_Nine_One_One(String mobilenumber) {

        if (Build.VERSION.SDK_INT < 23) {
            phoneCall(mobileNumber);
        }else {

            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                phoneCall(mobilenumber);
            }else {
                final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                //Asking request Permissions
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, 9);
            }
        }
    }

    private void phoneCall(String mobilenumber){
        String Nine_One_one= "+91 " + mobilenumber;
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+Nine_One_one));
            startActivity(callIntent);
        }else{
            Toast.makeText(getApplicationContext(), "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayStoredContactsAndUpload() {
        System.out.println("From displayStoredContactsAndUpload() Contact Name: " + ContactName + " And Contact Number: " + ContactNumber);
        Toast.makeText(this, "Contact Name: " + ContactName + " Contact Number: " + ContactNumber, Toast.LENGTH_SHORT).show();
        SQLiteDatabase db= cdb.getWritableDatabase();
        String query = "select * from contact";
        Cursor c1 = db.rawQuery(query, null);
        if (ContactName!=null && ContactNumber!=null) {
            //  if (!ContactName.isEmpty() && !ContactNumber.isEmpty()) {
            for (int i = 0, i1 = 0; (i < ContactName.size() && i1 < ContactNumber.size()); i++, i1++) {
                cdb.insertData(ContactName.get(i), ContactNumber.get(i1));
            }
            //  }
        }
    }

    private void inputTenContacts() {
        Toast.makeText(this, "Clicked on inputTenContacts()", Toast.LENGTH_SHORT).show();
        if (checkContactPermission()) {
            pickContactIntent();
            recreate();
            // displayStoredContactsAndUpload();
        } else {
            requestContactPermission();
        }
    }

    private void checkContacts() {
        //  displayStoredContactsAndUpload();
        SQLiteDatabase db= cdb.getWritableDatabase();
        String query = "select * from contact";
        Cursor c1 = db.rawQuery(query, null);
        StringBuilder sb1= new StringBuilder();
        if (c1!= null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    ContactsSet.add(c1.getString(0) + "\t" + c1.getString(1) + "\n\n\n\n");
                } while (c1.moveToNext());
            }
        }

        String str= ContactsSet + "";

        str= str.replace("[","").replace("]","").replace(",","").trim();

        AlertDialog.Builder a100= new AlertDialog.Builder(MainActivity.this);

        //  recreate();

        a100.setMessage(str);

        a100.setTitle("Check 10 Contacts");

        a100.setIcon(R.drawable.contacts_ic);

        a100.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        a100.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog a111= a100.create();
        a111.show();
    }

    private boolean checkContactPermission() {
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)== (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestContactPermission() {
        String permission[]= {Manifest.permission.READ_CONTACTS};
        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE);
    }

    private void pickContactIntent() {
        Intent intent= new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            }
        }

        if (requestCode== PERMISSION_REQUEST_SEND_SMS && grantResults.length > 0) {
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            }
        }

        if (requestCode==CONTACT_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                pickContactIntent();
            }
            else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }

        boolean permissionGranted = false;
        if (requestCode==9) {
            permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;

        }
        if(permissionGranted){
            System.out.println("From onRequestPermissionResult() mobileNumber is: " + mobileNumber);
            phoneCall(mobileNumber);
        }else {
            Toast.makeText(MainActivity.this, "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== RESULT_OK) {
            if (requestCode == CONTACT_PICK_CODE) {
                Cursor c1, c2;
                Uri uri= data.getData();
                c1= getContentResolver().query(uri, null, null, null, null);
                if (c1.moveToFirst()) {
                    String contactId= c1.getString(c1.getColumnIndex(ContactsContract.Contacts._ID));
                    contactName= c1.getString(c1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String idResults= c1.getString(c1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    int idResultHold= Integer.parseInt(idResults);
                    ContactName.add(contactName);
                    System.out.println("contactName is: " + contactName);
                    if (idResultHold==1) {
                        c2= getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = " + contactId, null, null);
                        while (c2.moveToNext()) {
                            concatNumber= c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            ContactNumber.add(concatNumber);
                         //   System.out.println("contactNumber is: " + concatNumber);
                        }
                      //  storeContactDetails(contactName, concatNumber);
                        System.out.println("ContactName is: " + ContactName + "\tContactNumber is: " + ContactNumber);
                        displayStoredContactsAndUpload();
                        c2.close();
                    }
                    c1.close();
                }
            }
        }
    }

    private void storeContactDetails(String contactName, String concatNumber) {
        ContactNumber.add(concatNumber);
        ContactName.add(contactName);
    }

    @Override
    public void applyProfileCreateFields(String name1, String mobile1, Uri imageUri) {
        System.out.println("From MainActivity applyProfileCreateFields(): " + name1 + " " + mobile1);
        System.out.println("imageUri==null? " + imageUri==null);

        saveProfileData(name1, mobile1, imageUri);
    }

    private void saveProfileData(final String name1, final String mobile1, Uri imageUri) {
        if (imageUri != null) {
            final StorageReference ref = storageReference.child("Profile Pics/" + UUID.randomUUID().toString());

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Please wait...");
            pd.show();

            try {

                ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Profile pic uploaded successfully", Toast.LENGTH_SHORT).show();

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(MainActivity.this, "Uploaded Image url: " + uri+"", Toast.LENGTH_SHORT).show();
                                uriPath= uri + "";
                                System.out.println("uriPath is: " + uriPath);

                                SQLiteDatabase db= pf.getWritableDatabase();

                                String query = "select * from profile";
                                Cursor c1 = db.rawQuery(query, null);

                                if (c1!= null && c1.getCount() > 0) {
                                    Toast.makeText(MainActivity.this, "User cannot create multiple profiles!", Toast.LENGTH_SHORT).show();
                                }

                                else {
                                    pf.insertData(name1,mobile1, uriPath);
                                    Toast.makeText(MainActivity.this, "Data successfully saved", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Error while uploading profile pic", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Uploaded: " + progress + "%");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void applyUpdateContactsFields(final String number1) {
        System.out.println("From PanicDialog applyUpdateContactsFields() number1 is: " + number1);
        mobileNumber= number1;

        AlertDialog.Builder a11= new AlertDialog.Builder(MainActivity.this);
        a11.setTitle("Panic");
        a11.setMessage("From panic choose appropriate option\n\n");
        a11.setPositiveButton("Call " + mobileNumber, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                call_Nine_One_One(number1);
            }
        });

     /*   a11.setNeutralButton("Stop Alarm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopPlayer();
            }
        });   */

        a11.setNegativeButton("Send Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermission();
                GoToLocation();
            }
        });

        AlertDialog a1= a11.create();
        a1.show();
    }

    @Override
    public void applyPlaceChooserFields(String places) {
        System.out.println("From MainActivity applylaceChooser() places: " + places);
        Intent sendData= new Intent(MainActivity.this, NearbyPlacesOfCurrentLocation.class);
        sendData.putExtra("place", places);
       // startActivity(new Intent(MainActivity.this, NearbyPlacesOfCurrentLocation.class));
        startActivity(sendData);
    }
}