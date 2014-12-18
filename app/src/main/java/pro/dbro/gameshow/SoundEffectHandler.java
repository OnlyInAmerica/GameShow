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
        FILL_BOARD  (R.raw.fill_board),
        OUT_OF_TIME (R.raw.out_of_time),
        DAILY_DOUBLE(R.raw.daily_double);

        public final int resId;
        public int poolId;

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
        if (mPool == null) return;
        
        mPool.play(type.poolId, mVolume, mVolume, 1, 0, 1);
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

        for (SoundType type : SoundType.values()) {
            type.poolId = mPool.load(mContext, type.resId, 1);
        }
    }

    // </editor-fold desc="Private API">

}
