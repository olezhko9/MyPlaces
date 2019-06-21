package com.example.olegnaumov.myplaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.olegnaumov.myplaces.model.MyPlace;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends ArrayAdapter<MyPlace> {

    private Context context;
    private List<MyPlace> items, tempItems, suggestions;

    public PlaceAdapter(Context context, int resource, int textViewResourceId, List<MyPlace> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.items = items;
        this.tempItems = new ArrayList<>(items);
        this.suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(
                    R.layout.list_item, parent, false);
        }

        MyPlace myPlace = items.get(position);
        if (myPlace != null) {
            TextView placeTitleTV = view.findViewById(R.id.search_place_title_tv);
            placeTitleTV.setText(myPlace.getTitle());

            TextView placeDescriptionTV = view.findViewById(R.id.search_place_description_tv);
            placeDescriptionTV.setText(myPlace.getDescription());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((MyPlace) resultValue).getDescription();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (MyPlace myPlace : tempItems) {
                    if (myPlace.getDescription().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(myPlace);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<MyPlace> filterList = (ArrayList<MyPlace>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (MyPlace myPlace : filterList) {
                    add(myPlace);
                    notifyDataSetChanged();
                }
            }
        }
    };

    public ArrayAdapter updateItems(List<MyPlace> items) {
        this.items = items;
        this.tempItems = new ArrayList<>(items);
        this.suggestions = new ArrayList<>();
        return this;
    }
}