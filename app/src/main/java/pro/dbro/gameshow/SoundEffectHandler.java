package pro.dbro.gameshow;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton class for playing sound effects.
 * Currently only supports one effect at a time.
 *
 * Created by davidbrodsky on 11/25/14.
 */
public class SoundEffectHandler {
    public static final String TAG = "SoundEffectHandler";

    public static enum SoundType {
        SUCCESS     (R.raw.correct),
        FAILURE     (0),
        OUT_OF_TIME (R.raw.out_of_time);

        public final int resId;

        private SoundType(final int resId) {
            this.resId = resId;
        }
    }

    private Context mContext;
    private SoundPool mPool;

    private float mVolume = 0.7f;

    private static SoundEffectHandler sInstance;
    private static AtomicInteger sHolds = new AtomicInteger();

    // <editor-fold desc="Public API">

    public static SoundEffectHandler getInstance(Context context) {
        if (sInstance == null) sInstance = new SoundEffectHandler(context);

        sHolds.incrementAndGet();
        return sInstance;
    }

    public void playSound(SoundType type) {
        mPool.play(type.resId, mVolume, mVolume, 1, 0, 1);
    }

    public void release() {
        if (sHolds.decrementAndGet() == 0 && mPool != null) {
            mPool.release();
            mPool = null;
            Log.i(TAG, "releasing SoundPool");
        }
    }

    // </editor-fold desc="Public API">

    // <editor-fold desc="Private API">

    private SoundEffectHandler(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        AudioAttributes.Builder audioBuilder = new AudioAttributes.Builder();
        audioBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
        audioBuilder.setUsage(AudioAttributes.USAGE_GAME);

        SoundPool.Builder poolBuilder = new SoundPool.Builder();
        poolBuilder.setMaxStreams(2);
        poolBuilder.setAudioAttributes(audioBuilder.build());
        mPool = poolBuilder.build();

        mPool.load(mContext, R.raw.out_of_time, 1);
        mPool.load(mContext, R.raw.correct, 1);

    }

    // </editor-fold desc="Private API">

}
