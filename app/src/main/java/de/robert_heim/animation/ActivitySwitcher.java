/*
 * Copyright (c) 2011 Robert Heim
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.robert_heim.animation;

import android.animation.ObjectAnimator;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

/**
 * This ActivitySwitcher uses a 3D rotation to animate an activity during its
 * start or finish.
 * 
 * see: http://blog.robert-heim.de/karriere/android-startactivity-rotate-3d-animation-activityswitcher/
 * 
 * @author Robert Heim
 * 
 */
public class ActivitySwitcher {

	private final static int DURATION = 1000;
	private final static float DEPTH = 400.0f;

	/* ----------------------------------------------- */

	public interface AnimationFinishedListener {
		/**
		 * Called when the animation is finished.
		 */
		public void onAnimationFinished();
	}

	/* ----------------------------------------------- */

	public static void animationIn(View container, WindowManager windowManager) {
		animationIn(container, windowManager, null);
	}

	public static void animationIn(View container, WindowManager windowManager, AnimationFinishedListener listener) {
		apply3DRotation(-360, 0, false, container, windowManager, listener);
	}

	public static void animationOut(View container, WindowManager windowManager) {
		animationOut(container, windowManager, null);
	}

	public static void animationOut(View container, WindowManager windowManager, AnimationFinishedListener listener) {
		apply3DRotation(0, -90, true, container, windowManager, listener);
	}

	/* ----------------------------------------------- */

	private static void apply3DRotation(float fromDegree, float toDegree, boolean reverse, View container, WindowManager windowManager, final AnimationFinishedListener listener) {
		Display display = windowManager.getDefaultDisplay();
		final float centerX = display.getWidth() / 2.0f;
		final float centerY = display.getHeight() / 2.0f;

		final Rotate3dAnimation a = new Rotate3dAnimation(fromDegree, toDegree, centerX, centerY, DEPTH, reverse);
		a.reset();
		a.setDuration(DURATION);
		a.setFillAfter(true);
		a.setInterpolator(new AccelerateInterpolator());
		if (listener != null) {
			a.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					listener.onAnimationFinished();
				}
			});
		}
		container.clearAnimation();
		container.startAnimation(a);

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(container, "scaleX", 0f, 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(container, "scaleY", 0f, 1f);
        scaleUpY.setDuration(DURATION);
        scaleUpX.setDuration(DURATION);
        scaleUpX.start();
        scaleUpY.start();
	}
}
