package yaochangwei.slideuipattern;

import yaochangwei.slideuipattern.widget.SlideLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SlideUIActivity extends FragmentActivity {

	private SlideLayout mSlideLayout;

	private int mCurrentPosition;
	private int mLastPosition;

	private Fragment[] mFragments;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slide_ui);

		mFragments = new Fragment[5];

		if (savedInstanceState == null) {
			mFragments[0] = ListFragment.newListFragment(mCurrentPosition);
			this.getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.content,
							mFragments[0],
							ListFragment.class.getSimpleName()
									+ mCurrentPosition).commit();
		}

		mSlideLayout = (SlideLayout) findViewById(R.id.slide_layout);

		RadioGroup rg = (RadioGroup) findViewById(R.id.left_menu);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				mCurrentPosition = id2Position(checkedId);

				if (mLastPosition != mCurrentPosition) {
					mSlideLayout.switchFragment(new SwitchFragment());
				} else {
					mSlideLayout.close();
				}

			}

		});

		final int[] ids = new int[] { R.id.radio_0, R.id.radio_1, R.id.radio_2,
				R.id.radio_3, R.id.radio_4 };
		final OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				int position = id2Position(v.getId());
				if (position == mLastPosition) {
					mSlideLayout.close();
				}
			}
		};

		for (int i = 0; i < ids.length; i++) {
			final View v = findViewById(ids[i]);
			v.setOnClickListener(listener);
		}
	}

	static int id2Position(int checkedId) {
		switch (checkedId) {
		case R.id.radio_0:
			return 0;
		case R.id.radio_1:
			return 1;
		case R.id.radio_2:
			return 2;
		case R.id.radio_3:
			return 3;
		case R.id.radio_4:
			return 4;
		default:
			throw new IllegalArgumentException("invalid checked Id");
		}
	}

	private Fragment newFragment() {
		if (mCurrentPosition % 2 == 0) {
			return ListFragment.newListFragment(mCurrentPosition);
		} else {
			return new ButtonFragment();
		}
	}

	final class SwitchFragment implements Runnable {

		@Override
		public void run() {
			final FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.hide(mFragments[mLastPosition]);
			if (mFragments[mCurrentPosition] == null) {
				mFragments[mCurrentPosition] = newFragment();
				ft.add(R.id.content, mFragments[mCurrentPosition],
						ListFragment.class.getSimpleName() + mCurrentPosition);
			} else {
				ft.show(mFragments[mCurrentPosition]);
			}
			ft.commit();
			mLastPosition = mCurrentPosition;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_slide_ui, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (mSlideLayout.isOpen()) {
			mSlideLayout.close();
		} else {
			mSlideLayout.open();
		}
		return true;
	}

}
