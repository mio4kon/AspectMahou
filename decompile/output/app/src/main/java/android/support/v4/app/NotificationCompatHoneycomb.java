package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

class NotificationCompatHoneycomb
{
  NotificationCompatHoneycomb() {}
  
  static Notification add(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews, int paramInt, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap)
  {
    Notification.Builder localBuilder1 = new Notification.Builder(paramContext).setWhen(when).setSmallIcon(icon, iconLevel).setContent(contentView).setTicker(tickerText, paramRemoteViews).setSound(sound, audioStreamType).setVibrate(vibrate).setLights(ledARGB, ledOnMS, ledOffMS);
    boolean bool1;
    boolean bool2;
    label111:
    boolean bool3;
    label133:
    Notification.Builder localBuilder4;
    if ((0x2 & flags) != 0)
    {
      bool1 = true;
      Notification.Builder localBuilder2 = localBuilder1.setOngoing(bool1);
      if ((0x8 & flags) == 0) {
        break label217;
      }
      bool2 = true;
      Notification.Builder localBuilder3 = localBuilder2.setOnlyAlertOnce(bool2);
      if ((0x10 & flags) == 0) {
        break label223;
      }
      bool3 = true;
      localBuilder4 = localBuilder3.setAutoCancel(bool3).setDefaults(defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(deleteIntent);
      if ((0x80 & flags) == 0) {
        break label229;
      }
    }
    label217:
    label223:
    label229:
    for (boolean bool4 = true;; bool4 = false)
    {
      return localBuilder4.setFullScreenIntent(paramPendingIntent2, bool4).setLargeIcon(paramBitmap).setNumber(paramInt).getNotification();
      bool1 = false;
      break;
      bool2 = false;
      break label111;
      bool3 = false;
      break label133;
    }
  }
}
