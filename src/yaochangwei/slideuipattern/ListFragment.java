package yaochangwei.slideuipattern;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListFragment extends Fragment implements OnItemClickListener {

	private int id;
	private ListView mListView;

	static String[] colors = { "#ffc682", "#bbe5f9", "#d9eab6", "#f2fafe",
			"#c4e29d" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		id = this.getArguments().getInt("id");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.list_fragment, container,
				false);
		mListView = (ListView) view.findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		int bg = Color.parseColor(colors[id]);
		Log.d("slide", "" + id);
		view.setBackgroundColor(bg);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final ArrayList<String> listItemDatas = new ArrayList<String>();
		for (int i = 0; i < 30; i++) {
			listItemDatas.add(id + ")item" + " - " + i);
		}

		final ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				listItemDatas);
		mListView.setAdapter(aa);
	}

	public static ListFragment newListFragment(int id) {
		final ListFragment fragment = new ListFragment();
		final Bundle args = new Bundle();
		args.putInt("id", id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Toast.makeText(getActivity(), "item " + position + " of page" + id,
				Toast.LENGTH_SHORT).show();

	}

}
