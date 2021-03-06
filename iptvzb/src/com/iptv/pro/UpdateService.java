package com.iptv.pro;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.iptv.utils.AppUpdateThread;
import com.iptv.utils.Appinfo;
import com.iptv.utils.DownLoadThread;
import com.iptv.utils.DownloadThreadListener;
import com.iptv.utils.Utils;

public class UpdateService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		AppUpdateThread thread = new AppUpdateThread(handler, "update.xml");
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1 && msg.obj != null) {
				Appinfo info = (Appinfo) msg.obj;
				if (info.getVercode() > Utils
						.getversioncode(UpdateService.this)) {
					show(info);
				} else {
				}

			} else if (msg.what == 2 && msg.obj != null) {
				showinstall((File) msg.obj);
			}else{
				Toast.makeText(UpdateService.this, "下载失败", Toast.LENGTH_SHORT).show();
				
			}
		}

	};

	private void show(final Appinfo info) {
		Builder builder = new Builder(UpdateService.this);
		builder.setTitle("更新");
		builder.setMessage(info.getMsg());
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				File file = createFile();
				Log.i("tvinfo", file + "开始下载" + info.getUrl());
				if (file != null) {

					DownLoadThread dt = new DownLoadThread(info.getUrl(), file,
							new DownloadThreadListenerImpl());
					dt.start();
				}

			}
		});
		builder.setNegativeButton("取消", null);
		AlertDialog dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}

	public File createFile() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/updateiptv.apk");
			if (file.exists()) {
				Log.i("tvinfo", "文件存在");
				file.delete();
			}
			return file;
		}
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	class DownloadThreadListenerImpl implements DownloadThreadListener {

		@Override
		public void afterPerDown(String uri, long count, long rcount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void downCompleted(String uri, long count, long rcount,
				boolean isdown, File file) {
			// TODO Auto-generated method stub
			if (file != null&&file.length()>0) {
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = file;
				handler.sendMessage(msg);

			}else{
				handler.sendEmptyMessage(3);
			}
		}

		@Override
		public void returncode(int statecode) {
			// TODO Auto-generated method stub
		}

	}

	public void showinstall(final File file) {
		Builder builder = new Builder(UpdateService.this);
		builder.setTitle("安装");
		builder.setMessage("下载完成,是否安装...");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file),
						"application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

			}
		});
		builder.setNegativeButton("取消", null);
		AlertDialog dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}

}
