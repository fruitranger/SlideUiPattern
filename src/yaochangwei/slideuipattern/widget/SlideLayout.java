package yaochangwei.slideuipattern.widget;

import yaochangwei.slideuipattern.R;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 
 * @author Yao Changwei(yaochangwei@gmail.com)
 * 
 *         Slide UI Pattern Layout.
 */

public class SlideLayout extends ViewGroup {

	private boolean mIsUnableToDrag = false;
	private boolean mIsBeingDragged = false;

	private static final int INVALID_POINTER_ID = -1;

	/*
	 * The max distance to slide left.
	 */
	private static final int MAX_SLIDE_LEFT_DP = 100;

	private static final int MAX_SETTLE_DURATION = 600; // ms;

	private int mMaxDistance;
	private int mMaxSlideLeft;

	private static final float RATIO = 1.5f;

	/* Layout scroll utility */
	private Scroller mScroller;

	/* Track the user finger velocity */
	private VelocityTracker mVelocityTracker;

	/* The minimum distance to judge the horizontal or vertical gesture. */
	private int mTouchSlop;
	private int mMaximumVelocity;

	private float mBaseLineFlingVelocity;
	private float mFlingVelocityInflunce;

	/* Last touched position x and y active pointer id */
	private float mLastX, mLastY;
	private int mActivePointerId;

	/* indicate the layout is open */
	private boolean mIsOpen;

	private boolean mSwitchFragment;

	private static final Interpolator sInterpolator = new Interpolator() {

		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t + 1.0f;
		}

	};

	public SlideLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		final Context context = getContext();
		mScroller = new Scroller(context, sInterpolator);
		final Resources res = context.getResources();
		final float density = context.getResources().getDisplayMetrics().density;
		mMaxDistance = res.getDimensionPixelSize(R.dimen.menu_width);
		mMaxSlideLeft = (int) (density * MAX_SLIDE_LEFT_DP + 0.5f);

		mBaseLineFlingVelocity = 2500.0f * density;
		mFlingVelocityInflunce = 0.4f;

		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
		if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_UP) {
			mActivePointerId = INVALID_POINTER_ID;
			mIsBeingDragged = false;
			mIsUnableToDrag = false;
		}

		if (action != MotionEvent.ACTION_DOWN) {
			if (mIsBeingDragged) {
				return true;
			}

			if (mIsUnableToDrag) {
				return false;
			}
		}

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mActivePointerId = MotionEventCompat.getPointerId(event, 0);
			mLastX = event.getX();
			mLastY = event.getY();
			mIsUnableToDrag = false;
			mIsBeingDragged = false;
			break;
		case MotionEvent.ACTION_MOVE:
			final int activePointerId = mActivePointerId;
			if (activePointerId == INVALID_POINTER_ID) {
				break;
			}

			final int pointerIndex = MotionEventCompat.findPointerIndex(event,
					activePointerId);
			final float x = MotionEventCompat.getX(event, pointerIndex);
			final float y = MotionEventCompat.getY(event, pointerIndex);
			final float deltaX = Math.abs(x - mLastX);
			final float deltaY = Math.abs(y - mLastY);

			if (deltaX > RATIO * deltaY && deltaX > mTouchSlop) {
				mIsBeingDragged = true;
				mLastX = x;
			} else {
				if (deltaY > mTouchSlop) {
					mIsUnableToDrag = true;
				}
			}
			break;
		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(event);
			break;
		}
		return mIsBeingDragged;
	};

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastX = MotionEventCompat.getX(ev, newPointerIndex);
			mActivePointerId = MotionEventCompat.getPointerId(ev,
					newPointerIndex);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}

		mVelocityTracker.addMovement(event);
		final int action = event.getAction() & MotionEvent.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mActivePointerId = MotionEventCompat.getPointerId(event, 0);
			mLastX = event.getX();
			if (mIsOpen) {
				final float x = MotionEventCompat.getX(event, mActivePointerId);
				final int scrollX = Math.abs(getScrollX());
				if (x < scrollX) {
					return false;
				} else {
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (!mIsBeingDragged) {
				final int pointerIndex = MotionEventCompat.findPointerIndex(
						event, mActivePointerId);
				final float x = MotionEventCompat.getX(event, pointerIndex);
				final float diffX = Math.abs(x - mLastX);
				final float y = MotionEventCompat.getY(event, pointerIndex);
				final float diffY = Math.abs(y - mLastY);

				if (diffX > diffY * RATIO && diffX > mTouchSlop) {
					mIsBeingDragged = true;
					mLastX = x;
				}
			}

			if (mIsBeingDragged) {
				final int activePointerIndex = MotionEventCompat
						.findPointerIndex(event, mActivePointerId);
				final float x = MotionEventCompat.getX(event,
						activePointerIndex);
				float deltaX = mLastX - x;
				mLastX = x;

				if ((getScrollX() + deltaX) < -mMaxDistance) {
					deltaX = -mMaxDistance - getScrollX();
				} else if (getScrollX() + deltaX > mMaxSlideLeft) {
					deltaX = mMaxSlideLeft - getScrollX();
				}
				scrollBy((int) deltaX, getScrollY());
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mIsBeingDragged) {
				mActivePointerId = INVALID_POINTER_ID;
				endDrag();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsBeingDragged) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(
						velocityTracker, mActivePointerId);
				final int scrollX = getScrollX();
				mActivePointerId = INVALID_POINTER_ID;
				endDrag();
				mIsOpen = true;

				if (scrollX < 0) {
					if (initialVelocity > 0) {
						smoothScrollTo(-mMaxDistance, getScrollY(),
								initialVelocity);
					} else {
						smoothScrollTo(0, getScrollY(), initialVelocity);
					}
				} else {
					smoothScrollTo(0, getScrollY(), initialVelocity);
				}

			}
			break;
		case MotionEventCompat.ACTION_POINTER_DOWN:
			final int index = MotionEventCompat.getActionIndex(event);
			final float x = MotionEventCompat.getX(event, index);
			mLastX = x;
			mActivePointerId = MotionEventCompat.getPointerId(event, index);
			break;
		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(event);
			break;

		}
		return true;
	}

	void smoothScrollTo(int x, int y, int velocity) {
		int sx = getScrollX();
		int sy = getScrollY();
		int dx = x - sx;
		int dy = y - sy;
		if (dx == 0 && dy == 0) {
			return;
		}

		final float pageDelta = (float) Math.abs(dx) / mMaxDistance;
		int duration = (int) (pageDelta * 100);
		velocity = Math.abs(velocity);
		if (velocity > 0) {
			duration += (duration / (velocity / mBaseLineFlingVelocity))
					* mFlingVelocityInflunce;
		} else {
			duration += 150;
		}

		duration = Math.min(duration, MAX_SETTLE_DURATION);
		mScroller.startScroll(sx, sy, dx, dy, duration);
		invalidate();
	}

	private void endDrag() {
		mIsBeingDragged = false;
		mIsUnableToDrag = false;

		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	private void openMax() {
		smoothScrollTo(-getWidth(), getScrollY(), 0);
		mIsOpen = false;
	}

	/**
	 * Open the slide immediately.
	 */
	public void open() {
		smoothScrollTo(-mMaxDistance, getScrollY(), 0);
		mIsOpen = true;
	}

	/**
	 * When the layout slide to the right, do the fragment transaction.
	 * 
	 * @param runnable
	 */
	public void switchFragment(Runnable runnable) {
		mSwitchRunnable = runnable;
		mSwitchFragment = true;
		openMax();
	}

	private Runnable mSwitchRunnable;

	/**
	 * Close the SlideLayout
	 */
	public void close() {
		smoothScrollTo(0, getScrollY(), 0);
		mIsOpen = false;
	}

	public boolean isOpen() {
		return mIsOpen;
	}

	@Override
	public void computeScroll() {

		if (mScroller.isFinished() && mSwitchFragment) {
			mSwitchFragment = false;
			if (mSwitchRunnable != null) {
				mSwitchRunnable.run();
				postDelayed(new Runnable() {
					public void run() {
						close();
					}
				}, 140);
			}
		}

		if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final View childView = getChildAt(0);
		childView.layout(0, 0, childView.getMeasuredWidth(),
				childView.getMeasuredHeight());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY
				&& heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalArgumentException(
					"The width and height spec mode should be exactly.");
		}

		final int childCount = getChildCount();
		if (childCount != 1) {
			throw new IllegalArgumentException(
					"SlideLayout can only hava 1 child.");
		}

		final View childView = getChildAt(0);
		childView.measure(widthMeasureSpec, heightMeasureSpec);
	}

}
