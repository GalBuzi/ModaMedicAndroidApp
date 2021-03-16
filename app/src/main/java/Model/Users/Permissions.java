package Model.Users;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;

import static com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT;
import static com.google.android.gms.fitness.data.DataType.TYPE_CALORIES_EXPENDED;
import static com.google.android.gms.fitness.data.DataType.TYPE_DISTANCE_DELTA;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class Permissions {

    private AppCompatActivity app;
    final private int ALL_PERMISSIONS = 101;

    public Permissions(AppCompatActivity app) {
        this.app = app;
    }

    public void requestPermissions() throws PackageManager.NameNotFoundException, InterruptedException {
        Thread t1 = new Thread() {
            public void run()  {
                PackageInfo info = null;
                try {
                    info = app.getPackageManager().getPackageInfo(app.getPackageName(), PackageManager.GET_PERMISSIONS);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String[] permissions = info.requestedPermissions;//This array contains the requested permissions.
                Log.i("PERMISSIONS", "************PERMISSIONS LIST*************");
                ActivityCompat.requestPermissions(app, permissions, ALL_PERMISSIONS);
            }
        };

        t1.start();
        t1.join();

        Thread t2 = new Thread() {
            public void run()  {
                Log.i("FITNESS", "************FITNESS PERMISSIONS*************");
                GoogleSignInOptionsExtension fitnessOptions =
                        FitnessOptions.builder()
                                .addDataType(TYPE_STEP_COUNT_DELTA,FitnessOptions.ACCESS_READ)
                                .addDataType(TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                                .addDataType(TYPE_CALORIES_EXPENDED,FitnessOptions.ACCESS_READ)
                                .addDataType(TYPE_ACTIVITY_SEGMENT,FitnessOptions.ACCESS_READ)
                                .build();

//        GoogleSignIn.requestPermissions(app,1,GoogleSignIn.getLastSignedInAccount(app),fitnessOptions);
                boolean hasGooglePermissions = GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(app), fitnessOptions);

                if (!hasGooglePermissions){
                    GoogleSignIn.requestPermissions(
                            app, // your activity
                            1,
                            GoogleSignIn.getLastSignedInAccount(app),
                            fitnessOptions);
                }
            }
        };

        t2.start();
        t2.join();



//        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(app), fitnessOptions)) {
//            GoogleSignIn.requestPermissions(
//                    app, // your activity
//                    1,
//                    GoogleSignIn.getLastSignedInAccount(app),
//                    fitnessOptions);
//        }
//        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(app), fitnessOptions))
//            Log.i("PERMISSIONS", "************PERMISSIONS REQUESTED*************");
    }


}
