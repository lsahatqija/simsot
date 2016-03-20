package simsot.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;

import java.util.Calendar;

import simsot.game.R;

public final class MusicManager {

    private static volatile MusicManager instance = null;

    private static MediaPlayer mp;
    private static boolean isPlaying = false;
    private static boolean appHasFocus = true;
    private static int lastCloseTime = 0;

    public final static MusicManager getInstance() {
        if (MusicManager.instance == null) {
            synchronized (MusicManager.class) {
                if (MusicManager.instance == null) {
                    MusicManager.instance = new MusicManager();
                }
            }
        }
        return MusicManager.instance;
    }

    public static void start(Context context) {
        appHasFocus = true;
        lastCloseTime = 0;
        if (!isPlaying) {
            mp = MediaPlayer.create(context, R.raw.menutheme);
            mp.setLooping(true);
            mp.start();
            isPlaying = true;
        }
    }

    public static void pause() {
        Calendar c = Calendar.getInstance();
        lastCloseTime = c.get(Calendar.SECOND);

        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (appHasFocus) {
                    stop();
                }
            }
        }.start();
    }

    private static void stop() {
        if (lastCloseTime != 0) {
            mp.stop();
            isPlaying = false;
        }
    }

    public static void release() {
        appHasFocus = false;
        mp.stop();
        isPlaying = false;
        mp.release();
    }
}
