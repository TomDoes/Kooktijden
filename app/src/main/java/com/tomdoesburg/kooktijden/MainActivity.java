package com.tomdoesburg.kooktijden;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat1pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat2pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat4pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat5pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat6pits;
import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;
import org.codechimp.apprater.AppRater;

import java.lang.ref.WeakReference;

public class MainActivity extends FragmentActivity implements KooktijdenDialogTwoButtons.ActivityCommunicator{

    private static final String TAG = "MAIN_ACTIVITY";
    private MySQLiteHelper db;
    private StateSaver stateSaver;
   // private static AdView mAdView = null;
    //private AdRequest adRequest;
    private SharedPreferences sharedPrefs;
    private WeakReference<Context> weakContext;
    //create empty frame, needed for overlays
    private static FrameLayout layout;
    private int curFragment; //0,1,2,3 or 4 depending on FragmentKookplaat type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weakContext = new WeakReference<Context>(this);

        // Apprater
        AppRater.setLightTheme();
        AppRater.setNumDaysForRemindLater(7);
        AppRater.app_launched(weakContext.get());

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        //create empty frame, needed for overlays
        layout = new FrameLayout(weakContext.get());
        //inflate our regular layout in the frame
        View regular = getLayoutInflater().inflate(R.layout.activity_main,null);
        layout.addView(regular);


        if (!sharedPrefs.contains("firstStart_pt1")) {
            //the app is being launched for first time

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
        }

        //set view to the one set through settings
        int kookplaat = sharedPrefs.getInt("numberOfStoveTops",4);
        switch(kookplaat){
            case 1: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentKookplaat1pits()).commit();
                    break;
            case 2: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentKookplaat2pits()).commit();
                    break;
            //case 3 does not exist, since we don't have a stove top layout for this
            case 4: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentKookplaat4pits()).commit();
                    break;
            case 5: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentKookplaat5pits()).commit();
                    break;
            case 6: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentKookplaat6pits()).commit();
                    break;
            default: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentKookplaat4pits()).commit();
                    break;

        }

        //show the view to the user
        setContentView(layout);

        /*
        if(mAdView == null) {
            mAdView = (AdView) findViewById(R.id.adView);
            adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
        */

        db = new MySQLiteHelper(weakContext.get());

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
        //close any "timer ready" notifications
        hideNotification(TimerService.timerReadyID);

    }

    public boolean serviceActive(){
        return (TimerService.timer1Running || TimerService.timer2Running || TimerService.timer3Running
                ||TimerService.timer4Running||TimerService.timer5Running || TimerService.timer6Running);
    }

    public boolean timerRunning(){ //true is there is any running timer or any on pause
        if(TimerService.timer1Running || TimerService.timer2Running || TimerService.timer3Running
                ||TimerService.timer4Running||TimerService.timer5Running || TimerService.timer6Running){
            return true;
        }else if (TimerService.deadline1 > 0 || TimerService.deadline2 > 0 || TimerService.deadline3 > 0
                || TimerService.deadline4 > 0 || TimerService.deadline5 > 0 || TimerService.deadline6 > 0){
            return true;
        }

        return false;
    }

    public boolean allTimersDone(){ //true if there isn't any running timer
        if(TimerService.timer1Running || TimerService.timer2Running || TimerService.timer3Running
                ||TimerService.timer4Running||TimerService.timer5Running || TimerService.timer6Running){
            return false;
        }else if (TimerService.deadline1 == 0 && TimerService.deadline2 ==0 && TimerService.deadline3 == 0
                || TimerService.deadline4 == 0 && TimerService.deadline5 == 0 && TimerService.deadline6 == 0){
            return true;
        }
        return false;
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
            AppRater.rateNow(weakContext.get());
            return true;
        }else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }else if (id == R.id.action_reset) {
            KooktijdenDialogTwoButtons dialog = new KooktijdenDialogTwoButtons(this,getString(R.string.dialog_reset_title),getString(R.string.dialog_reset_message));
            dialog.show();
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

        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (frag instanceof FragmentKookplaat1pits){
            ((FragmentKookplaat1pits) frag).setVegetable(ID,veg);
        }else if(frag instanceof FragmentKookplaat2pits){
            ((FragmentKookplaat2pits) frag).setVegetable(ID,veg);
        }else if(frag instanceof FragmentKookplaat4pits){
            ((FragmentKookplaat4pits) frag).setVegetable(ID,veg);
        }else if(frag instanceof FragmentKookplaat5pits){
            ((FragmentKookplaat5pits) frag).setVegetable(ID,veg);
        }else if(frag instanceof FragmentKookplaat6pits){
            ((FragmentKookplaat6pits) frag).setVegetable(ID,veg);
        }

    }


    public Vegetable getVegetableFromDB(int ID){
        return db.getVegetable(ID);
    }

    public void reset(){
        //kill service
        TimerService.deadline1 = 0;
        TimerService.deadline2 = 0;
        TimerService.deadline3 = 0;
        TimerService.deadline4 = 0;
        TimerService.deadline5 = 0;
        TimerService.deadline6 = 0;

        TimerService.timer1Running = false;
        TimerService.timer2Running = false;
        TimerService.timer3Running = false;
        TimerService.timer4Running = false;
        TimerService.timer5Running = false;
        TimerService.timer6Running = false;

        TimerService.timer1Finished = false;
        TimerService.timer2Finished = false;
        TimerService.timer3Finished = false;
        TimerService.timer4Finished = false;
        TimerService.timer5Finished = false;
        TimerService.timer6Finished = false;

        TimerService.deadline1Add = 0;
        TimerService.deadline2Add = 0;
        TimerService.deadline3Add = 0;
        TimerService.deadline4Add = 0;
        TimerService.deadline5Add = 0;
        TimerService.deadline6Add = 0;

        Intent intent = new Intent(getApplicationContext(), TimerService.class);
        intent.putExtra(TimerService.KILL_SERVICE_IMMEDIATELY,true);
        startService(intent);

        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (frag instanceof FragmentKookplaat1pits){
            ((FragmentKookplaat1pits) frag).reset();
        }else if(frag instanceof FragmentKookplaat2pits){
            ((FragmentKookplaat2pits) frag).reset();
        }else if(frag instanceof FragmentKookplaat4pits){
            ((FragmentKookplaat4pits) frag).reset();
        }else if(frag instanceof FragmentKookplaat5pits){
            ((FragmentKookplaat5pits) frag).reset();
        }else if(frag instanceof FragmentKookplaat6pits){
            ((FragmentKookplaat6pits) frag).reset();
        }

    }

    /////////////////////////////////////////////////////////////////////////
    ///////////////////Service related, do not touch!////////////////////////
    /////////////////////////////////////////////////////////////////////////
    @Override
    public void onResume() {

        this.registerReceiver(br, new IntentFilter(TimerService.TIMER_SERVICE));
        Log.i(TAG, "Registered broacast receiver");
        TimerService.runningOnForeground = true;

        Intent intent = new Intent(getApplicationContext(), TimerService.class);
        startService(intent);

        //retrieve states only if timer not active
        if(!serviceActive()){
            Log.d(TAG,"MainActivity retrieving state");
            stateSaver = new StateSaver(getApplicationContext());
            stateSaver.retrieveStates();
        }else{
            Log.d(TAG,"MainActivity NOT retrieving state");
        }

        //if timer service still exists, but there are no running alarms: kill service
        if(allTimersDone()){
            intent = new Intent(getApplicationContext(), TimerService.class);
            intent.putExtra(TimerService.KILL_SERVICE,true);
            startService(intent);
        }

        //load adds
        /*
        if(mAdView == null) {
            mAdView = (AdView) findViewById(R.id.adView);
            adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
        mAdView.resume();
        */
        super.onResume();
    }

    //Service related

    @Override
    public void onPause() {

        //mAdView.pause();

        Log.d(TAG,"MainActivity onPause() saving state");
        stateSaver = new StateSaver(getApplicationContext());
        stateSaver.saveStates();

        unregisterReceiver(br);
        Log.i(TAG, "Unregistered broacast receiver");
        TimerService.runningOnForeground = false;
        super.onPause();
    }

    //Service related

    @Override
    public void onBackPressed(){
        Log.d(TAG,"MainActivity onBackPressed() saving state");
        stateSaver = new StateSaver(getApplicationContext());
        stateSaver.saveStates();
        super.onBackPressed();
    }

    @Override
    public void onStop() {
        /*
        mAdView.destroy();
        layout.removeView(mAdView);
        mAdView = null;
        */

        try {
            unregisterReceiver(br);
            TimerService.runningOnForeground = false;
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }

        super.onStop();
    }


    //Service related
     @Override
     public void onDestroy() {
        layout.removeAllViews();

        try {
            db.close();
        }catch(Exception E){
            Log.d(TAG,"error when closing database db: " + E.toString());
        }
         super.onDestroy();
     }

    //Service related: br receives ticks from service
    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra(TimerService.TIMER_DONE)){
                //get vegetable name that is done
                String vegName = intent.getStringExtra(TimerService.TIMER_DONE);
                //open dialog
                String alarmText = getString(R.string.the_alarm_for) + " " + vegName + " " + getString(R.string.has_finished);
                new KooktijdenDialog(MainActivity.this,getString(R.string.timer_ready),alarmText).show();
            }else if(intent.hasExtra(TimerService.TIMER_TICK)){
                //update screen
                updateGUI();
            }

        }
    };

    public void lock(){
            //save locked position in preferences (in case of restart)
            //sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            //sharedPrefs.edit().putInt("kookplaatViewPos", pager.getCurrentItem()).commit();
    }

    //Service related: processes ticks and updates GUI
    private void updateGUI() {
            //Log.d(TAG, "received tick!");
            //To do: forward tick action to all TimerHelper instances
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (frag instanceof FragmentKookplaat1pits){
            ((FragmentKookplaat1pits) frag).tick();
        }else if(frag instanceof FragmentKookplaat2pits){
            ((FragmentKookplaat2pits) frag).tick();
        }else if(frag instanceof FragmentKookplaat4pits){
            ((FragmentKookplaat4pits) frag).tick();
        }else if(frag instanceof FragmentKookplaat5pits){
            ((FragmentKookplaat5pits) frag).tick();
        }else if(frag instanceof FragmentKookplaat6pits){
            ((FragmentKookplaat6pits) frag).tick();
        }
    }

    public void hideNotification(int ID){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(ID);
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
        db.addVegetable(new Vegetable("Cabbage", "Witte kool", 10, 15, "Cabbage is rich in vitamin C and is often used in stews and salads. Remove the thick outer leaves and cut the cabbage into pieces. Clean the pieces using running water before cooking.", "Witte kool is rijk aan vitamine C en bovendien een goedkope groente die zich uitstekend leent voor bijvoorbeeld stamppot, ovenschotels en salades. Haal de buitenste bladen weg en snijd de kool in stukken. Spoel de stukken kool af voor het koken"));
        db.addVegetable(new Vegetable("Carrots", "Wortels", 10, 10, "Gently scrub the carrots under cold running water to remove the dirt on the surface. Trim both ends of each carrots. Use a kitchen knife or vegetable peeler to peel the top layer. Submerge the carrots in salted water and let it boil for roughly 10 minutes.", "Snijd de boven- en onderkant van de wortels af. Schraap vervolgens met een keukenmes alle vuil en onregelmatigheden weg. Grotere en grovere wortels kun je beter helemaal schillen. Hierna kunnen de wortels in de pan. Vul deze met koud water tot de wortels er net onder staan en voeg wat zout toe. Laat het water ongeveer 10 minuten koken."));
        db.addVegetable(new Vegetable("Cauliflower", "Bloemkool", 12, 12, "Remove the leaves and stem of the cauliflower, then wash under running water. Submerge in salted water and boil the cauliflower.", "Verwijder de bladeren van de bloemkool en een stuk van de stronk. Was de bloemkool in zijn geheel met de stronk naar beneden gehouden. Zet de bloemkool in een pan met water met zout en breng aan de kook."));
        db.addVegetable(new Vegetable("Corn on the cob", "Maiskolf", 15, 25, "Remove the husks, clean the cob and cook in water without salt. The cooking time depends on the size and it's best not to overcook the corn. The corn is done if you can pop it from the cob using a fork. Serve with butter and salt.", "Verwijder het groene omhulsel, maak de maiskolf schoon en kook in ruim water zonder zout. Kleinere kolven zijn sneller gaar dan grotere en te lang koken gaat ten koste van de smaak. Controleer daarom regelmatig of de mais gaar is. De mais is gaar als je de korrels zonder veel moeite van de kolf kunt wippen met een vork. Serveer met een klontje boter en wat zout."));
        db.addVegetable(new Vegetable("Endive", "Witlof", 12, 15, "Pull of any brown outer leaves and clean the endive with water. The green tip and heart are quite bitter and is many people prefer to remove it. Remove the stem, then cut the endive up and boil in water. ", "Trek eventuele bruine bladeren van de witlof af en spoel de stronk af. Daarna kan je een klein plakje van het bruine voetje weghalen. Verwijder het hart van de witlof aan de binnenkant, die is namelijk erg bitter. Snijd vervolgens de witlof in reepjes en leg deze in onder een laagje water in de pan, breng ze vervolgens aan de kook."));
        db.addVegetable(new Vegetable("Leek", "Prei", 15, 15, "To prepare leeks, cut off and discard the dark green parts that are tough. Trim the leek's beard at the bottom. You'll often find dirt on the leek. The easiest way to clean leeks is to cut them into rings, and swirl them in water to remove the grit, then drain them well. Cook the rings in water with a pinch of salt.", "Voor je begint met prei koken, is het belangrijk dat je de prei goed wast. Vooral tussen de bladeren van de stengels zit vaak aarde. Snijd ook de wortel en de donkergroene stukken van de prei af. Vervolgens snijd je de prei in ringetjes die je ook goed moet wassen. Kook de ringetjes in een pan met een snufje zout."));
        db.addVegetable(new Vegetable("Peas", "Erwten", 5, 5, "Peas exist in loads of variants. The pea is a vegetable that grows inside a legume (a carpel which is folded around the peas). Peas are round or oval shaped. Most types don't need a lot of cooking, a good warm-up of approximately 5 minutes is enough.", "Erwten bestaan in veel verschillende varianten. Het is een groente die groeit binnenin een peul (een langwerpig vruchtblad dat dichtgevouwen om de erwten zit). Erwten zijn rond of ovaal van vorm. De meeste soorten hoeven niet lang te koken, vaak zo'n 5 minuten goed opwarmen al voldoende."));
        db.addVegetable(new Vegetable("Potatoes (halves)", "Aardappelen (halven)", 10, 15, "Peal or wash the potatoes before cooking. Cutting the potatoes in halves decreases the cooking time substantially. Make sure you cut the potatoes in equally sized parts, else the parts will not cook through simultaneously. Submerge the potatoes in a pan filled with water. If desired, add a pinch of salt to the water.", "Schil of was de aardappelen voor je ze gaat koken. Door de aardappelen doormidden te snijden voor je ze gaat koken verkort je de kooktijd aanzienlijk. Zorg er hierbij wel voor dat je de aardappelen in delen van gelijke grootte snijd, anders zijn de delen niet gelijktijdig gaar. Doe de aardappelen in een pan met water en voeg eventueel een snufje zout toe."));
        db.addVegetable(new Vegetable("Potatoes (whole)", "Aardappelen (heel)", 15, 20, "Peal or wash the potatoes before cooking. Submerge the potatoes in a pan filled with water. If desired, add a pinch of salt to the water.", "Schil of was de aardappelen voor je ze gaat koken. Doe de aardappelen in een pan met water en voeg eventueel een snufje zout toe."));
        db.addVegetable(new Vegetable("Red cabbage (shredded)", "Rodekool (gesneden)", 15, 20, "Remove the outer leaves of the cabbage. Cut the cabbage into quarters and remove the hard stalk. Scrape or cut the leaves into thin strips and place them in a pan. Add a little water and salt to taste and bring to the boil.", "Verwijder de buitenste bladen van de kool. Snijd vervolgens de kool in vieren en verwijder de harde stronk. Schaaf of snijd dan de bladeren in dunne sliertjes en doe deze in de pan. Voeg een beetje water en zout toe en breng aan de kook."));
        db.addVegetable(new Vegetable("Spinach", "Spinazie", 4, 5, "Fresh spinach is very healthy and versatile. Rinse the spinach before use to remove any left-over grains of sand. Remove the thick petioles from the leaves. Place the spinach in a large pan with a small amount of water and a pinch of salt, and bring to the boil. Note: spinach shrinks tremendously during cooking!", "Verse spinazie is bijzonder gezond en veelzijdig. Spoel voor gebruik de spinazie goed af om eventuele zandkorrels te verwijderen. Haal de dikke bladstengels van de bladeren. Plaats de spinazie in een ruime pan met een klein laagje water en een snufje zout en breng het aan de kook. Let op: spinazie slinkt enorm tijdens het koken!"));
        db.addVegetable(new Vegetable("Sweet potatoes (whole)", "Zoete aardappelen (heel)", 15, 20, "Boiling sweet potatoes is almost the same process as that of the normal potato. Peel and wash the potatoes and cut them into uniform pieces. Place the pieces in a pan and add water so that the majority of the potatoes are submerged. Add a pinch of salt and bring to a boil. Make sure that the potatoes are cooked with the aid of a fork. If you can easily pierce the potatoes then they are done.", "Het koken van zoete aardappelen is bijna hetzelfde proces als dat van de normale aardappel. Schil of was de aardappels en snijd ze in gelijkmatige stukken. Plaats de delen in een pan en zet ze voor het grootste deel onder water. Doe hier een snufje zout bij en breng aan de kook. Controleer of de aardappels gaar zijn met behulp van een vork. De aardappels zijn gaar als je gemakkelijk in de aardappels kunt prikken."));
    }


    @Override
    public void resetDialogYesClicked() {
        reset();
    }
}