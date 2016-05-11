package com.oink.walkingwithpug;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.oink.walkingwithpug.PugGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//This option removes on-screen buttons
		config.useImmersiveMode = true;
		//This option locks the screen to turn off
		config.useWakelock = true;

		initialize(new PugGame(), config);
	}
}
