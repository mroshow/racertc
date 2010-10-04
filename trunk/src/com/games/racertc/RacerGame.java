package com.games.racertc;

import com.games.racertc.gamestate.GameState;
import com.games.racertc.gamestate.GameStateChangeListener;
import com.games.racertc.gamestate.StateMachine;
import com.games.racertc.ui.JoystickSingleTouchUI;
import com.games.racertc.ui.UIManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.Window;

/**
 * Activity odpowiadajace prowadzenie rozgrywki.
 */
public class RacerGame extends Activity implements Callback, GameStateChangeListener {
	
	/** Watek obslugujacy glowna petle gry. */
	private RacerThread racerThread;
	
	/** View rozgrywki. */
	private RacerGameView racerView;
	
	/** ID wybranego samochodu */
	private int carId;
	
	/** ID wybranej trasy */
	private int trackId;
	
/*--------------------------------------*/
/*-            Obsluga menu:           -*/
/*--------------------------------------*/

//TODO: jakas enumeracja itemkow w menu
//public static final int...
	
	/**
	 * Przygotowuje menu.
	 * @param menu
	 * @return zawsze true! :P
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		super.onCreateOptionsMenu( menu );
		
		//TODO: kod przygotowujacy menu
		
		return true;
	}
	
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch( item.getItemId() ) {
			
			//TODO: kod obslugujacy menu
		
		}
		return false;
	}
	
/*--------------------------------------*/
/*-          Zarzadzanie UI:           -*/
/*--------------------------------------*/
	
	/** UIManager. */
	private UIManager uiManager;
	
	/**
	 * 
	 * @param uiManager
	 */
	private void dispatchUIManager( UIManager uiManager ) {
		//if( uiManager != null ) ???;
		this.uiManager = uiManager;
		racerView.setOnTouchListener( uiManager );
		racerThread.setUIManager( uiManager );
	}
	
/*--------------------------------------*/
/*-     Obsluga zdarzen activity:      -*/
/*--------------------------------------*/
	
	/**
	 * Wolane po uruchomieniu aplikacji. Przygotowuje grunt pod dzialanie
	 * programu.
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		//wyrzuca zbedne rzeczy z ekranu:
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		setContentView(R.layout.game);
		
		//tworzy View aplikacji:
		racerView = (RacerGameView) findViewById( R.id.racergameview );
		//tworzy obiekt zarzadzajacy glowna petla gry:
		racerThread = new RacerThread(
			racerView.getHolder(),
			racerView.getContext(),
			getResources()
		);
		//pozwala przygotowac sie do pracy RacerGameView
		racerView.initialise();
		//tworzy mendzera UI:
		dispatchUIManager( new JoystickSingleTouchUI( getResources() ) );
		
		//Ustawiamy stan gry na INTRO:
		StateMachine.getInstance().setGameState( GameState.INTRO );
		gameState = GameState.INTRO;
		
		// Pobiera informacje o wybranym samochodzie i trasie
		Intent i = getIntent();
		carId = i.getIntExtra("_car", 0);
		trackId = i.getIntExtra("_track", 0);
		
		//-uruchamianie rozgrywki "na chama" :P-//
		racerThread.setupGame(carId, trackId);
		gameState = GameState.GAME_ACTIVE;
		//--------------------------------------//
		
		//rejestruje sie jako obserwator zmian stanu gry:
		StateMachine.getInstance().addListener( this );
		
		//rejestruje sie jako sluchacz zmian w Surface programu
		racerView.getHolder().addCallback( this );
		
		if( null == savedInstanceState ) {
			//wyscigi po uruchomieniu
						
		} else {
			//wyscigi wznawiaja dzialanie
			
		}
	}
	
	/**
	 * Wolane, kiedy aplikacja znajdzie sie w tle.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		//TODO: pauzuje rozgrywle
		//zagadka w LunarLander to samo jest wolane
		//zarowno w Activity.onPause(), jak i w 
		//View.onWindowFocusChanged. Czemu?
	}
	
	/**
	 * Wolane aby umozliwic wyscigom zapisanie swojego stanu
	 * w celu pozniejszego odtworzenia.
	 * 
	 * Chyba, ze nie implementujemy tej funkcjonalnosci, ale
	 * mozna by.
	 */
	@Override
	protected void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		//TODO: zapisujemy stan programu. Chyba tylku w singlu:
		//      w multi raczej rozlaczamy (chyba, ze to multi w
		//      trybie hot seat! ;D).
	}
	
/*-----------------------------------------*/
/*- Implementacja SurfaceHolder.Callback: -*/
/*-----------------------------------------*/	
		
	/**
	 * Wolane po zmianie rozdzieczosci Surface'u.
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		racerThread.setResolution( width, height );
		uiManager.setResolution( width, height );
	}

	/**
	 * Wolane po utworzeniu Surface'u. Kiedy Surface jest gotowy, mozemy odpalic watek
	 * gry.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		racerThread.start();
	}

	/**
	 * Wolane po zniszczeniu Surface'u. Blokuje dostep do
	 * rysowania - bo jak ktos bedzie rysowal po zniszczeniu,
	 * to moze stac sie cos zlego. Konczy dzialanie programu.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//TODO jakas blokada dalszego rzsowania
		//konczy prace watku
		//racerThread.join(); //czeka na zkonczenie pracy watku
	}

/*-----------------------------------------*/
/*-Implementacja GameStateChangeListener: -*/
/*-----------------------------------------*/			
		
	private int gameState;
		
	@Override
	public void onGameStateChange( int gameState ) {
		this.gameState = gameState;
		//TODO: reakcja na zmiany stanu.
	}	
	
}
