package com.tomdoesburg.kooktijden;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;
import com.viewpagerindicator.CirclePageIndicator;

;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MAIN_ACTIVITY";
    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.swipert);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final ImageButton lockButton = (ImageButton) findViewById(R.id.lockButton);
        final CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);

        //Set the pager with an adapter
        final ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));

        //Bind the indicator dots to the adapter
        indicator.setViewPager(pager);


        //clicking the lock icon toggles the ability to swipe between fragments
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton lockButton = (ImageButton) findViewById(R.id.lockButton);
                if(MyViewPager.swipingEnabled){
                    MyViewPager.swipingEnabled = false;
                    lockButton.setImageResource(R.drawable.lock_locked);
                    Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
                    indicator.startAnimation(anim);

                    //save locked position in preferences (in case of restart)
                    sharedPrefs.edit().putInt("kookplaatViewPos",pager.getCurrentItem()).commit();
                } else {
                    MyViewPager.swipingEnabled = true;
                    lockButton.setImageResource(R.drawable.lock_unlocked);
                    Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                    indicator.startAnimation(anim);

                    //remove locked position from preferences
                    sharedPrefs.edit().remove("kookplaatViewPos").commit();
                }
            }
        });


        //if user has locked a view the last time, set that as the default
        if(sharedPrefs.contains("kookplaatViewPos")){
            pager.setCurrentItem(sharedPrefs.getInt("kookplaatViewPos",0));
            MyViewPager.swipingEnabled = false;
            lockButton.setImageResource(R.drawable.lock_locked);
            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
            indicator.startAnimation(anim);
        }

        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        MySQLiteHelper db = new MySQLiteHelper(this);

        Log.d("database", "Created the database connection");
        Log.d("database", "First start: " + (sharedPrefs.getBoolean("databaseLoaded",false)));

        // Add vegetables only on first launch
        if(!sharedPrefs.getBoolean("databaseLoaded",false)) {
            //TODO work with db versions

            Log.d("database", "adding stuff to the db");
            addVegetablesToDb(db);

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("databaseLoaded", true);
            editor.commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Call Back method to get the cooking time from the vegetable Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("Joost","onActivityResult started");
        super.onActivityResult(requestCode, resultCode, data);


        // check if the request code is same as what is passed here it is 9001
        if(requestCode==9001){
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                // The user picked a vegetable
                int vegId = data.getIntExtra("vegId",0);
                String kookplaatID = data.getStringExtra("kookPlaatID");
                Log.v(TAG, kookplaatID + " was selected");
                //MySQLiteHelper db = new MySQLiteHelper(this);
                //Vegetable veg = db.getVegetable(vegId);
            }
        }

    }

    /////////////////////////////////////////////////////////////////////////
    ///////////////////Service related, do not touch!////////////////////////
    /////////////////////////////////////////////////////////////////////////
    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(br, new IntentFilter(TimerService.TIMER_SERVICE));
        Log.i(TAG, "Registered broacast receiver");
    }

    //Service related

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    //Service related

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    /*

    //Service related
     @Override
     public void onDestroy() {
         //getActivity().stopService(new Intent(getActivity(), TimerService.class));
        // Log.i(TAG, "Stopped service");
        // super.onDestroy();
     }
     */
    //Service related: br receives ticks from service
    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    //Service related: processes ticks and updates GUI
    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getIntExtra("countdown", 0);
            Log.i(TAG, "received tick!");
            //To do: forward tick action to all TimerHelper instances

        }
    }

    /////////////////////////////////////////////////////////////////////////
    ////////////End of service related stuff. You may now touch!/////////////
    /////////////////////////////////////////////////////////////////////////

    public void addVegetablesToDb (MySQLiteHelper db) {
        db.addVegetable(new Vegetable("Artichoke", "Artisjok", 25, 25, "Artichokes look more like a Flower than a vegetable. To prepare an artichoke you have to cut of the stem and remove the tougher leaves until only the soft remain.", "Artisjokken lijken meer op een bloem dan een groente. Om een artisjok te bereiden moet je de steel er af snijden en verwijder je de harde bladeren totdat er alleen nog zachte over zijn."));
        db.addVegetable(new Vegetable("Asparagus (whole)", "Asperges (heel)", 15, 15, "Before cooking asparagus you have to peel them. This easiest way is to leave them for 30 minutes in a bit of water before peeling them. To peel them grab the head and move your knife from the top downwards. Next cut a small piece from the bottom. Cook the asparagus with the peels and the bottom ens for more flavour.", "Voordat asperges in de pan kunnen, moeten ze geschild worden. Dit gaat het makkelijkst wanneer je ze een half uur laat weken in koud water voordat je ze schilt. Pak vervolgens een asperge bij de kop en schil van boven naar beneden en verwijder een stukje van de onderkant. Kook de asperges vervolgens met de schillen en de uiteinden voor meer smaak."));
        db.addVegetable(new Vegetable("Beans", "Sperziebonen", 8, 8, "Remove both ends of the beans and place them in boiling water. After 8 minutes rinse the beans with cold water to preserve the green color and prevent overcooking", "Snij de uiteinden van de sperziebonen af en plaats ze in kokend water. Giet de sperziebonen na 8 minuten af en spoel ze kort af met koud water om de mooie groene kleur te behouden en te voorkomen dat ze nog verder garen."));
        db.addVegetable(new Vegetable("Beetroot", "Bieten", 40, 60, "If there's a stem remove it. Put it in water and heat it. Boil them with the lid on the pan until there done. Use a fork to test if the beetroot is ready. If it's soft, it's probably done.", "Als er nog een stengel aan de bieten zit, kun je deze er af snijden. Maak de bieten schoon, breng ze in een pan aan de kook en kook ze gaar. Check of ze gaar zijn door er met een vork in te prikken. Als de bieten zacht zijn, zijn ze klaar."));
        db.addVegetable(new Vegetable("Bok choy", "Paksoi / Chinese kool", 10, 10, "Cut the bok choy in large strokes and wash is thoroughly. You may then cook it, however you could also eat it raw", "Snij de chinese kool in stroken en was deze goed. Vervolgens kun je ze koken, maar je kunt ze ook rauw eten"));
        db.addVegetable(new Vegetable("Broccoli", "Broccoli", 8, 8, "Before placing the broccoli in the pan, cut the bottom of the broccoli and cut it into small florets", "Voordat je broccoli in de pan doet, verwijder je de onderkant en snijd je de broccoli in roosjes."));
        db.addVegetable(new Vegetable("Brussels sprouts", "Spruitjes", 10, 10, "Prepare Brussels sprouts by removing the outer leaves and a small piece from the bottom. Place them in water and bring it to boil.", "Ter voorbereiding van het koken van spruitjes verwijder je de onderkant en de buitenste blaadjes. Doe ze vervolgens in de pan en breng het water aan de kook."));
        db.addVegetable(new Vegetable("Cabbage", "Witte kool", 10, 15, "", ""));
        db.addVegetable(new Vegetable("Carrots", "Wortels", 10, 10, "", ""));
        db.addVegetable(new Vegetable("Cauliflower", "Bloemkool", 12, 12, "", ""));
        db.addVegetable(new Vegetable("Corn on the cob", "Maiskolf", 20, 20, "", ""));
        db.addVegetable(new Vegetable("Endive", "Witlof", 12, 15, "", ""));
        db.addVegetable(new Vegetable("Leek", "Prei", 15, 15, "", ""));
        db.addVegetable(new Vegetable("Peas", "Erwten", 5, 5, "", ""));
        db.addVegetable(new Vegetable("Potatoes (halves)", "Aardappelen (halven)", 10, 15, "", ""));
        db.addVegetable(new Vegetable("Potatoes (whole)", "Aardappelen (heel)", 15, 20, "", ""));
        db.addVegetable(new Vegetable("Red cabbage (shredded)", "Rodekool (gesneden)", 15, 20, "", ""));
        db.addVegetable(new Vegetable("Spinach", "Spinazie", 4, 5, "", ""));
        db.addVegetable(new Vegetable("Sweet potatoes (whole)", "Zoete aardappelen (heel)", 20, 20, "", ""));
    }

}