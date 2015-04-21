package demo.leon.trackno;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DemoService.startService(this);
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		DemoService.stopService(this);
		super.onDestroy();

	}

}
