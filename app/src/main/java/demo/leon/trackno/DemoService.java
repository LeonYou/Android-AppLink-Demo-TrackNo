package demo.leon.trackno;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM;
import com.ford.syncV4.proxy.rpc.AddCommandResponse;
import com.ford.syncV4.proxy.rpc.AddSubMenuResponse;
import com.ford.syncV4.proxy.rpc.AlertResponse;
import com.ford.syncV4.proxy.rpc.ChangeRegistrationResponse;
import com.ford.syncV4.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteCommandResponse;
import com.ford.syncV4.proxy.rpc.DeleteFileResponse;
import com.ford.syncV4.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteSubMenuResponse;
import com.ford.syncV4.proxy.rpc.DiagnosticMessageResponse;
import com.ford.syncV4.proxy.rpc.EndAudioPassThruResponse;
import com.ford.syncV4.proxy.rpc.GenericResponse;
import com.ford.syncV4.proxy.rpc.GetDTCsResponse;
import com.ford.syncV4.proxy.rpc.GetVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.ListFilesResponse;
import com.ford.syncV4.proxy.rpc.OnAudioPassThru;
import com.ford.syncV4.proxy.rpc.OnButtonEvent;
import com.ford.syncV4.proxy.rpc.OnButtonPress;
import com.ford.syncV4.proxy.rpc.OnCommand;
import com.ford.syncV4.proxy.rpc.OnDriverDistraction;
import com.ford.syncV4.proxy.rpc.OnHMIStatus;
import com.ford.syncV4.proxy.rpc.OnHashChange;
import com.ford.syncV4.proxy.rpc.OnKeyboardInput;
import com.ford.syncV4.proxy.rpc.OnLanguageChange;
import com.ford.syncV4.proxy.rpc.OnLockScreenStatus;
import com.ford.syncV4.proxy.rpc.OnPermissionsChange;
import com.ford.syncV4.proxy.rpc.OnSystemRequest;
import com.ford.syncV4.proxy.rpc.OnTBTClientState;
import com.ford.syncV4.proxy.rpc.OnTouchEvent;
import com.ford.syncV4.proxy.rpc.OnVehicleData;
import com.ford.syncV4.proxy.rpc.PerformAudioPassThruResponse;
import com.ford.syncV4.proxy.rpc.PerformInteractionResponse;
import com.ford.syncV4.proxy.rpc.PutFileResponse;
import com.ford.syncV4.proxy.rpc.ReadDIDResponse;
import com.ford.syncV4.proxy.rpc.ResetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.ScrollableMessageResponse;
import com.ford.syncV4.proxy.rpc.SetAppIconResponse;
import com.ford.syncV4.proxy.rpc.SetDisplayLayoutResponse;
import com.ford.syncV4.proxy.rpc.SetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.SetMediaClockTimerResponse;
import com.ford.syncV4.proxy.rpc.Show;
import com.ford.syncV4.proxy.rpc.ShowResponse;
import com.ford.syncV4.proxy.rpc.SliderResponse;
import com.ford.syncV4.proxy.rpc.SpeakResponse;
import com.ford.syncV4.proxy.rpc.SubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.SubscribeVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.SystemRequestResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.enums.ButtonName;
import com.ford.syncV4.proxy.rpc.enums.SyncDisconnectedReason;
import com.ford.syncV4.proxy.rpc.enums.TextAlignment;

// Weather service

public class DemoService extends Service implements IProxyListenerALM
{
	private SyncProxyALM mProxy = null;
	private Show mShow = null;
	private static ComponentName mComName = null;
	public static final String LOG_TAG = "APPLINK";
	public static final String SERVICE_NANE = "com.example.myapplink.WeatherService";


	public static void startService(Context ctx)
	{
		if (mComName == null)
		{
			Intent intent = new Intent(SERVICE_NANE);
			mComName = ctx.startService(intent);
		}
	}

	public static void stopService(Context ctx)
	{
		if (mComName != null)
		{
			Intent intent = new Intent(SERVICE_NANE);
			ctx.stopService(intent);
			mComName = null;
		}
	}


	private void startProxy()
	{
		if (mProxy == null)
		{
			try
			{
				mProxy = new SyncProxyALM(this, "福特测试", true, "584421907");
			}
			catch (SyncException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void disposeProxy()
	{
		if (mProxy != null)
		{
			try
			{
				mProxy.dispose();
				mProxy = null;
			}
			catch (SyncException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void resetProxy()
	{
		if (mProxy != null)
		{
			try
			{
				mProxy.resetProxy();
			}
			catch (SyncException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	// Input number consecutively
	private static int MAX_NUM = 80;
	private StringBuffer mNum = new StringBuffer();
	private Thread mInputThread = new Thread()
	{
		@Override
		public void run()
		{
			super.run();

			while(true)
			{
				String trackInfo = "";
				synchronized (mNum)
				{
					try
					{
						mNum.setLength(0);
						mNum.wait();
						mNum.wait(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}

					int curNum = Integer.parseInt(mNum.toString());
					if (curNum > MAX_NUM)
						curNum = MAX_NUM;
					trackInfo = curNum + "/" + MAX_NUM;
				}

				mShow.setMediaTrack(trackInfo);
				try
				{
					mProxy.sendRPCRequest(mShow);
				}
				catch (SyncException e)
				{
					e.printStackTrace();
				}
			}
		}
	};

	private String ButtonName2String(ButtonName name)
	{
		switch(name)
		{
		case PRESET_0:
			return "0";
		case PRESET_1:
			return "1";
		case PRESET_2:
			return "2";
		case PRESET_3:
			return "3";
		case PRESET_4:
			return "4";
		case PRESET_5:
			return "5";
		case PRESET_6:
			return "6";
		case PRESET_7:
			return "7";
		case PRESET_8:
			return "8";
		case PRESET_9:
			return "9";
		default:
			return "";
		}
	}

	public void consecutiveInput(ButtonName name)
	{
		synchronized (mNum)
		{
			mNum.append(ButtonName2String(name));
			mNum.notify();
		}
	}




	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub
		startProxy();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		disposeProxy();
		super.onDestroy();
	}


	@Override
	public void onAddCommandResponse(AddCommandResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onAlertResponse(AlertResponse arg0)
	{
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "Result=" + arg0.getResultCode().toString());
		Log.d(LOG_TAG, "Info=" + arg0.getInfo());
	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDiagnosticMessageResponse(DiagnosticMessageResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(String arg0, Exception arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGenericResponse(GenericResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onListFilesResponse(ListFilesResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnButtonEvent(OnButtonEvent arg0)
	{
		// TODO Auto-generated method stub

	}


	@Override
	public void onOnButtonPress(OnButtonPress notification)
	{
		// TODO Auto-generated method stub
		consecutiveInput(notification.getButtonName());
	}

	@Override
	public void onOnCommand(OnCommand arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnDriverDistraction(OnDriverDistraction arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnHMIStatus(OnHMIStatus status)
	{
		// TODO Auto-generated method stub
		switch (status.getSystemContext())
		{
			case SYSCTXT_MAIN:
				break;
			case SYSCTXT_VRSESSION:
				break;
			case SYSCTXT_MENU:
				break;
			default:
				return;
		}

		switch (status.getAudioStreamingState())
		{
			case AUDIBLE:
				// play audio if applicable
				break;
			case NOT_AUDIBLE:
				// pause/stop/mute audio if applicable
				break;
			default:
				return;
		}

		switch (status.getHmiLevel())
		{
			case HMI_FULL:
				int id = 0;
				if (status.getFirstRun())
				{
					LockScreenActivity.show(this, true);

					// setup app on SYNC
					// send welcome message if applicable
					try
					{
						mShow = new Show();
						mShow.setCorrelationID(id++);
						mShow.setMainField1("Demo");
						mShow.setMainField2("TrackNo");
						mProxy.sendRPCRequest(mShow);

//						mProxy.show("Initializing", null, null, null, null, null,
//									null, null, softBtns, null, null, id);


						// send addcommands
						mProxy.subscribeButton(ButtonName.OK, id++);
						mProxy.subscribeButton(ButtonName.SEEKLEFT, id++);
						mProxy.subscribeButton(ButtonName.SEEKRIGHT, id++);
						mProxy.subscribeButton(ButtonName.TUNEUP, id++);
						mProxy.subscribeButton(ButtonName.TUNEDOWN, id++);
						mProxy.subscribeButton(ButtonName.PRESET_1, id++);
						mProxy.subscribeButton(ButtonName.PRESET_2, id++);
						mProxy.subscribeButton(ButtonName.PRESET_3, id++);
						mProxy.subscribeButton(ButtonName.PRESET_4, id++);
						mProxy.subscribeButton(ButtonName.PRESET_5, id++);
						mProxy.subscribeButton(ButtonName.PRESET_6, id++);
						mProxy.subscribeButton(ButtonName.PRESET_7, id++);
						mProxy.subscribeButton(ButtonName.PRESET_8, id++);
						mProxy.subscribeButton(ButtonName.PRESET_9, id++);
						mProxy.subscribeButton(ButtonName.PRESET_0, id++);

						mInputThread.start();
					}
					catch (SyncException e)
					{
						e.printStackTrace();
					}
				}
				break;
			case HMI_LIMITED:
				break;
			case HMI_BACKGROUND:
				break;
			case HMI_NONE:
				LockScreenActivity.show(this, false);
				break;
			default:
				return;
		}
	}

	@Override
	public void onOnHashChange(OnHashChange arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnKeyboardInput(OnKeyboardInput arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnLanguageChange(OnLanguageChange arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnLockScreenNotification(OnLockScreenStatus arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnSystemRequest(OnSystemRequest arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnTBTClientState(OnTBTClientState arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnTouchEvent(OnTouchEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnVehicleData(OnVehicleData arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProxyClosed(String arg0, Exception arg1, SyncDisconnectedReason arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPutFileResponse(PutFileResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowResponse(ShowResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSliderResponse(SliderResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeakResponse(SpeakResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSystemRequestResponse(SystemRequestResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
