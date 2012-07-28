package yaochangwei.slideuipattern;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class ButtonFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.button_fragment, container,
				false);

		final int[] ids = new int[] { R.id.button0, R.id.button1, R.id.button2 };
		for (int id : ids) {
			view.findViewById(id).setOnClickListener(this);
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(getActivity(), "hello slide view layout",
				Toast.LENGTH_SHORT).show();
	}

}
