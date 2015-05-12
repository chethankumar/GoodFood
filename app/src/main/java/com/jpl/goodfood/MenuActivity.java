package com.jpl.goodfood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alexvasilkov.android.commons.utils.Views;
import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.joooonho.SelectableRoundedImageView;
import com.jpl.goodfood.objects.MenuItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.alexrs.prefs.lib.Prefs;
import timber.log.Timber;


public class MenuActivity extends Activity implements Constants {

    @InjectView(R.id.list_view)
    ListView listView;
    @InjectView(R.id.touch_interceptor_view)
    View touchInterceptorView;
    @InjectView(R.id.menu_orderItemQty)
    TextView menuOrderItemQty;
    @InjectView(R.id.menu_location)
    TextView menuLocation;
    @InjectView(R.id.menu_order)
    Button menuOrder;
    @InjectView(R.id.menu_menuPrice)
    TextView menuMenuPrice;
    @InjectView(R.id.menu_menuDetail)
    TextView menuMenuDetail;
    @InjectView(R.id.menu_menuName)
    TextView menuMenuName;
    @InjectView(R.id.details_layout)
    RelativeLayout detailsLayout;
    @InjectView(R.id.unfoldable_view)
    UnfoldableView unfoldableView;

    ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
    @InjectView(R.id.menu_headerTitle)
    TextView menuHeaderTitle;
    @InjectView(R.id.menu_headerBag)
    ImageView menuHeaderBag;
    @InjectView(R.id.menu_menuImage)
    ImageView menuMenuImage;

    MaterialDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.inject(this);
        Timber.plant(new Timber.DebugTree());

        touchInterceptorView.setClickable(false);
        detailsLayout.setVisibility(View.INVISIBLE);

        menuHeaderTitle.setTypeface(Utils.getLatoLightTypeface(getApplicationContext()));

        menuMenuName.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
        menuMenuDetail.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
        menuMenuPrice.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
        menuLocation.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
        menuOrderItemQty.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
        retrieveMenuItems();

        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return menuItemList.size();
            }

            @Override
            public Object getItem(int position) {
                return menuItemList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;

                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = getLayoutInflater().inflate(R.layout.menu_item, parent, false);
                    viewHolder.menuItemName = (TextView) convertView.findViewById(R.id.menu_item_itemName);
                    viewHolder.menuItemDetail = (TextView) convertView.findViewById(R.id.menu_item_itemdetail);
                    viewHolder.menuItemPrice = (TextView) convertView.findViewById(R.id.menu_item_itemPrice);
                    viewHolder.itemImage = (SelectableRoundedImageView) convertView.findViewById(R.id.menu_item_imageView);
                    //set typeface
                    viewHolder.menuItemName.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
                    viewHolder.menuItemDetail.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
                    viewHolder.menuItemPrice.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.menuItemName.setText(menuItemList.get(position).getMenuName());
                viewHolder.menuItemDetail.setText(menuItemList.get(position).getMenuDetail());
                viewHolder.menuItemPrice.setText("\u20B9 " + menuItemList.get(position).getPrice());
                //set image
                Picasso.with(getApplicationContext()).load(menuItemList.get(position).getImageSrc()).noFade()
                        .placeholder(R.drawable.pasta).error(R.drawable.pasta).into(viewHolder.itemImage);
                return convertView;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //fill in the data for details layout
                menuMenuName.setText(menuItemList.get(position).getMenuName());
                menuMenuDetail.setText(menuItemList.get(position).getMenuDetail());
                menuMenuPrice.setText("\u20B9 " + menuItemList.get(position).getPrice());
                Timber.d("food image src " + menuItemList.get(position).getImageSrc());
                Picasso.with(getApplicationContext()).load(menuItemList.get(position).getImageSrc()).noFade()
                        .placeholder(R.drawable.pasta).error(R.drawable.pasta).into(menuMenuImage);

                View v = Views.find(view, R.id.menu_item_view);
                unfoldableView.unfold(v, detailsLayout);
            }
        });

        menuLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MenuActivity.this)
                        .title("Good Food")
                        .content("Select the delivery Office location")
                        .typeface("Lato-Regular", "Lato-Regular")
                        .items(new String[]{IBM_EGL_C, IBM_EGL_A, IBM_EGL_B, IBM_EGL_D, MICROSOFT, YAHOO})
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                menuLocation.setText(charSequence);
                                return true;
                            }
                        })
                        .positiveText("OK")
                        .show();
            }
        });

        menuOrderItemQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MenuActivity.this)
                        .title("Good Food")
                        .content("Select the quantity of the order")
                        .typeface("Lato-Regular", "Lato-Regular")
                        .items(new String[]{"1", "2", "3", "4", "5", "6"})
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                menuOrderItemQty.setText("" + charSequence);
                                return true;
                            }
                        })
                        .positiveText("OK")
                        .show();
            }
        });

        menuOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MenuActivity.this)
                        .title("Good Food")
                        .content("Please verify the order details before placing the Order." +
                                "\n \nDo you want to place the order?")
                        .typeface("Lato-Regular", "Lato-Regular")
                        .positiveText("PLACE ORDER")
                        .negativeText("CANCEL")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                //show already initialized spinner
                                spinner.setContent("Placing your order directly into the oven!");
                                spinner.show();
                                ParseObject order = new ParseObject(ORDER);
                                order.put(PHONE_NUMBER, Prefs.with(getApplicationContext()).getString(PHONE_NUMBER, null));
                                order.put(ORDER_MENU_ITEM, menuMenuName.getText().toString());
                                order.put(ORDER_LOCATION, menuLocation.getText().toString());
                                order.put(ORDER_QTY, Integer.parseInt(menuOrderItemQty.getText().toString()));

                                order.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        spinner.cancel();
                                        if (e == null) {
                                            //success
                                            new MaterialDialog.Builder(MenuActivity.this)
                                                    .title("Good Food")
                                                    .typeface("Lato-Regular", "Lato-Regular")
                                                    .content("Successfully placed your order.\n\n Your food will be delivered at 1pm at your office")
                                                    .positiveText("OK")
                                                    .show();
                                            Timber.d("saved one order");
                                        } else {
                                            //something went wrong
                                            new MaterialDialog.Builder(MenuActivity.this)
                                                    .title("Good Food")
                                                    .typeface("Lato-Regular", "Lato-Regular")
                                                    .content("Opps!. There seems to be a problem saving your order.\n\n" +
                                                            "Can you please try again to place the order?")
                                                    .positiveText("OK")
                                                    .show();
                                            Timber.d(e.getMessage());
                                        }
                                    }
                                });
                            }
                        })
                        .show();
            }
        });

        menuHeaderBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                menuHeaderBag.setImageResource(R.drawable.bag_green);
                Intent orderIntent = new Intent(getApplicationContext(),MyOrderActivity.class);
                startActivity(orderIntent);
            }
        });

        unfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {

            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(true);
                detailsLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUnfolded(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(false);
            }

            @Override
            public void onFoldingBack(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(true);
            }

            @Override
            public void onFoldedBack(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(false);
                detailsLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void retrieveMenuItems() {
        //start the spinner
        spinner = new MaterialDialog.Builder(MenuActivity.this)
                .title("Good Food")
                .typeface("Lato-Regular", "Lato-Regular")
                .cancelable(false)
                .content("Fetching the yummy menu for today")
                .progress(true,0)
                .show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(MENU_ITEM);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                //stop the spinner
                spinner.cancel();
                if (e == null) {
                    Timber.d("Got menu data from parse : " + list.size());
                    menuItemList.clear();
                    for (ParseObject po : list) {
                        MenuItem menuItem = new MenuItem(
                                po.getString(MENU_NAME),
                                po.getString(MENU_DETAIL),
                                po.getInt(MENU_PRICE),
                                po.getString(MENU_IMG_SRC)
                        );
                        menuItemList.add(menuItem);
                    }

                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                } else {
                    //error
                    Timber.d(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (unfoldableView != null && (unfoldableView.isUnfolded() || unfoldableView.isUnfolding())) {
            unfoldableView.foldBack();
        } else {
            super.onBackPressed();
        }
    }

    static class ViewHolder {
        SelectableRoundedImageView itemImage;
        TextView menuItemName;
        TextView menuItemDetail;
        TextView menuItemPrice;
    }

}
