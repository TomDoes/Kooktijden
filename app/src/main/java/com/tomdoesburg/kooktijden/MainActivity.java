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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat1pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat2pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat4pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat5pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat6pits;
import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;
import com.viewpagerindicator.CirclePageIndicator;

import org.codechimp.apprater.AppRater;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private ViewPager pager;
    private MyFragmentPagerAdapter pagerAdapter;
    private MySQLiteHelper db;
    private ImageButton lockButton;
    private CirclePageIndicator indicator;

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apprater
        AppRater.setLightTheme();
        AppRater.setNumDaysForRemindLater(7);
        AppRater.app_launched(this);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        Log.v("firstStart", "Is this the first time the main is launched? - "+sharedPrefs.getBoolean("firstStart_pt1",false));
        if (!sharedPrefs.contains("firstStart_pt1")) {
            //the app is being launched for first time, show introduction

            //create empty frame
            final FrameLayout layout = new FrameLayout(this);
            setContentView(layout);

            //inflate our regular layout in the frame
            View regular = getLayoutInflater().inflate(R.layout.swipert,null);
            layout.addView(regular);

            //inflate and add instructional overlay
            final View instructional = getLayoutInflater().inflate(R.layout.instructional_overlay_activity_main,null);
            layout.addView(instructional);
            Button instructions_close = (Button) instructional.findViewById(R.id.instructions_close);
            instructions_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout.removeView(instructional);
                    sharedPrefs.edit().putBoolean("firstStart_pt1", false).commit();
                }
            });

        } else {
            setContentView(R.layout.swipert);
        }


        lockButton = (ImageButton) findViewById(R.id.lockButton);
        indicator = (CirclePageIndicator)findViewById(R.id.indicator);

        //Set the pager with an adapter
        pager = (ViewPager)findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        //Bind the indicator dots to the adapter
        indicator.setViewPager(pager);


        //clicking the lock icon toggles the ability to swipe between fragments
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               lockButton = (ImageButton) findViewById(R.id.lockButton);
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


        db = new MySQLiteHelper(this);

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
        if (id == R.id.action_rate) {
            AppRater.rateNow(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Call Back method to get the cooking time from the vegetable Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);


        // check if the request code is same as what is passed here it is 9001
        if(requestCode==9001){
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                // The user picked a vegetable
                int vegId = data.getIntExtra("vegId",0);
                String kookPlaatID = data.getStringExtra("kookPlaatID");
                Log.v(TAG, kookPlaatID + " was selected");
                setVegetable(vegId,kookPlaatID);
                //MySQLiteHelper db = new MySQLiteHelper(this);
                //Vegetable veg = db.getVegetable(vegId);
            }
        }

    }

    public void setVegetable(int vegID, String kookPlaatID){
        int ID = 0;
        if(kookPlaatID.equals("kookPlaat1")){
            ID = 1;
        }else if(kookPlaatID.equals("kookPlaat2")){
            ID = 2;
        }else if(kookPlaatID.equals("kookPlaat3")){
            ID = 3;
        }else if(kookPlaatID.equals("kookPlaat4")){
            ID = 4;
        }else if(kookPlaatID.equals("kookPlaat5")){
            ID = 5;
        }else if(kookPlaatID.equals("kookPlaat6")){
            ID = 6;
        }

        Vegetable veg = db.getVegetable(vegID);

        int curItem = pager.getCurrentItem();
        switch(curItem){
            case 0: FragmentKookplaat1pits k1 = (FragmentKookplaat1pits) pagerAdapter.getActiveFragment(pager.getCurrentItem());
                k1.setVegetable(ID, veg);
                break;
            case 1: FragmentKookplaat2pits k2 = (FragmentKookplaat2pits) pagerAdapter.getActiveFragment(pager.getCurrentItem());
                k2.setVegetable(ID, veg);
                break;
            case 2: FragmentKookplaat4pits k4 = (FragmentKookplaat4pits) pagerAdapter.getActiveFragment(pager.getCurrentItem());
                k4.setVegetable(ID, veg);
                break;
            case 3: FragmentKookplaat5pits k5 = (FragmentKookplaat5pits) pagerAdapter.getActiveFragment(pager.getCurrentItem());
                k5.setVegetable(ID, veg);
                break;
            case 4: FragmentKookplaat6pits k6 = (FragmentKookplaat6pits) pagerAdapter.getActiveFragment(pager.getCurrentItem());
                k6.setVegetable(ID, veg);
                break;
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
         getActivity().stopService(new Intent(getActivity(), TimerService.class));
        // Log.i(TAG, "Stopped service");
         super.onDestroy();
     }
     */
    //Service related: br receives ticks from service
    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    public void lock(){
        if(MyViewPager.swipingEnabled) {
            MyViewPager.swipingEnabled = false;
            lockButton.setImageResource(R.drawable.lock_locked);
            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
            indicator.startAnimation(anim);
            //save locked position in preferences (in case of restart)
            sharedPrefs.edit().putInt("kookplaatViewPos", pager.getCurrentItem()).commit();
        }
    }

    //Service related: processes ticks and updates GUI
    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getIntExtra("countdown", 0);
            Log.i(TAG, "received tick!");
            //To do: forward tick action to all TimerHelper instances

            int curItem = pager.getCurrentItem();
            switch(curItem){
                case 0: FragmentKookplaat1pits k1 = (FragmentKookplaat1pits) pagerAdapter.getActiveFragment(pager.getCurrentItem());
                        k1.tick();
                        break;
                case 1: FragmentKookplaat2pits k2 = (FragmentKookplaat2pits) pagerAdapter.getActiveFragment(pager.getCurrentItem());
                        k2.tick();
                        break;
                case 2: FragmentKookplaat4pits k4 = (FragmentKookplaat4pits) pagerAdapter.getActiveFragment(pager.getCurrentItem());
                        k4.tick();
                        break;
                case 3: FragmentKookplaat5pits k5 = (FragmentKookplaat5pits) pagerAdapter.getActiveFragment(pager.getCurrentItem());
                        k5.tick();
                        break;
                case 4: FragmentKookplaat6pits k6 = (FragmentKookplaat6pits) pagerAdapter.getActiveFragment(pager.getCurrentItem());
                        k6.tick();
                        break;
            }

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
        db.addVegetable(new Vegetable("Peas", "Erwten", 5, 5, "Peas exist in loads of variants. The pea is a vegetable that grows inside a legume (a carpel which is folded around the peas). Peas are round or oval shaped. Most types don't need a lot of cooking, a good warm-up of approximately 5 minutes is enough.", "Erwten bestaan in veel verschillende varianten. Het is een groente die groeit binnenin een peul (een langwerpig vruchtblad dat dichtgevouwen om de erwten zit). Erwten zijn rond of ovaal van vorm. De meeste soorten hoeven niet lang te koken, vaak zo'n 5 minuten goed opwarmen al voldoende."));
        db.addVegetable(new Vegetable("Potatoes (halves)", "Aardappelen (halven)", 10, 15, "Peal or wash the potatoes before cooking. Cutting the potatoes in halves decreases the cooking time substantially. Make sure you cut the potatoes in equally sized parts, else the parts will not cook through simultaneously. Submerge the potatoes in a pan filled with water. If desired, add a pinch of salt to the water.", "Schil of was de aardappelen voor je ze gaat koken. Door de aardappelen doormidden te snijden voor je ze gaat koken verkort je de kooktijd aanzienlijk. Zorg er hierbij wel voor dat je de aardappelen in delen van gelijke grootte snijd, anders zijn de delen niet gelijktijdig gaar. Doe de aardappelen in een pan met water en voeg eventueel een snufje zout toe."));
        db.addVegetable(new Vegetable("Potatoes (whole)", "Aardappelen (heel)", 15, 20, "Peal or wash the potatoes before cooking. Submerge the potatoes in a pan filled with water. If desired, add a pinch of salt to the water.", "Schil of was de aardappelen voor je ze gaat koken. Doe de aardappelen in een pan met water en voeg eventueel een snufje zout toe."));
        db.addVegetable(new Vegetable("Red cabbage (shredded)", "Rodekool (gesneden)", 15, 20, "Remove the outer leaves of the cabbage. Cut the cabbage into quarters and remove the hard stalk. Scrape or cut the leaves into thin strips and place them in a pan. Add a little water and salt to taste and bring to the boil.", "Verwijder de buitenste bladen van de kool. Snijd vervolgens de kool in vieren en verwijder de harde stronk. Schaaf of snijd dan de bladeren in dunne sliertjes en doe deze in de pan. Voeg een beetje water en zout toe en breng aan de kook."));
        db.addVegetable(new Vegetable("Spinach", "Spinazie", 4, 5, "Fresh spinach is very healthy and versatile. Rinse the spinach before use to remove any left-over grains of sand. Remove the thick petioles from the leaves. Place the spinach in a large pan with a small amount of water and a pinch of salt, and bring to the boil. Note: spinach shrinks tremendously during cooking!", "Verse spinazie is bijzonder gezond en veelzijdig. Spoel voor gebruik de spinazie goed af om eventuele zandkorrels te verwijderen. Haal de dikke bladstengels van de bladeren. Plaats de spinazie in een ruime pan met een klein laagje water en een snufje zout en breng het aan de kook. Let op: spinazie slinkt enorm tijdens het koken!"));
        db.addVegetable(new Vegetable("Sweet potatoes (whole)", "Zoete aardappelen (heel)", 15, 20, "Boiling sweet potatoes is almost the same process as that of the normal potato. Peel and wash the potatoes and cut them into uniform pieces. Place the pieces in a pan and add water so that the majority of the potatoes are submerged. Add a pinch of salt and bring to a boil. Make sure that the potatoes are cooked with the aid of a fork. If you can easily pierce the potatoes then they are done.", "Het koken van zoete aardappelen is bijna hetzelfde proces als dat van de normale aardappel. Schil of was de aardappels en snijd ze in gelijkmatige stukken. Plaats de delen in een pan en zet ze voor het grootste deel onder water. Doe hier een snufje zout bij en breng aan de kook. Controleer of de aardappels gaar zijn met behulp van een vork. De aardappels zijn gaar als je gemakkelijk in de aardappels kunt prikken."));
    }

}