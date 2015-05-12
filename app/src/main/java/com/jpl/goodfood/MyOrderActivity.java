package com.jpl.goodfood;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joooonho.SelectableRoundedImageView;
import com.jpl.goodfood.objects.Order;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.alexrs.prefs.lib.Prefs;
import timber.log.Timber;

/**
 * Created by chethan on 04/04/15.
 */
public class MyOrderActivity extends Activity implements Constants {

    @InjectView(R.id.order_headerTitle)
    TextView orderHeaderTitle;
    @InjectView(R.id.order_headerBack)
    ImageView orderHeaderBack;
    @InjectView(R.id.order_list_view)
    ListView orderListView;

    ArrayList<Order> myOrderList = new ArrayList<Order>();

    MaterialDialog spinner;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorder);
        ButterKnife.inject(this);
        Timber.plant(new Timber.DebugTree());

        retrieveMyOrders();

        orderHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyOrderActivity.this.finish();
            }
        });

        orderHeaderTitle.setTypeface(Utils.getLatoLightTypeface(getApplicationContext()));



         BaseAdapter orderAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return myOrderList.size();
            }

            @Override
            public Object getItem(int position) {
                return myOrderList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;

                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = getLayoutInflater().inflate(R.layout.order_item, parent, false);
                    viewHolder.orderName = (TextView) convertView.findViewById(R.id.order_orderName);
                    viewHolder.orderDetail = (TextView) convertView.findViewById(R.id.order_orderDetail);
                    viewHolder.orderLocation = (TextView) convertView.findViewById(R.id.order_orderLocation);
                    viewHolder.orderQuantity = (TextView) convertView.findViewById(R.id.order_orderQty);
                    viewHolder.cancelOrder = (Button) convertView.findViewById(R.id.order_delete);

                    viewHolder.orderName.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
                    viewHolder.orderDetail.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
                    viewHolder.orderLocation.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
                    viewHolder.orderQuantity.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));
                    viewHolder.cancelOrder.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));

                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.orderName.setText(myOrderList.get(position).getMenuItem());
                viewHolder.orderLocation.setText(myOrderList.get(position).getLocation());
                viewHolder.orderQuantity.setText("" + myOrderList.get(position).getOrderQty());

                viewHolder.cancelOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        cancelOrder(position);


                        spinner.setContent("Cancelling your order. Please wait.");
                        spinner.show();

                        ParseObject toDelete = new ParseObject(ORDER);
                        toDelete.setObjectId(myOrderList.get(position).getObjectId());

                        toDelete.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {

                                if(e == null){

                                    spinner.cancel();
//                                    new MaterialDialog.Builder(MyOrderActivity.this)
//                                            .title("Good Food")
//                                            .content("Your Order is Cancelled.")
//                                            .typeface("Lato-Regular","Lato-Regular")
//                                            .positiveText("OK")
//                                            .callback(new MaterialDialog.ButtonCallback() {
//                                                @Override
//                                                public void onPositive(MaterialDialog dialog) {
//                                                    myOrderList.remove(position);
//                                                   notifyDataSetChanged();
//
//                                                }
//                                            })
//                                            .show();

                                }else{
                                    //something wrong
                                    spinner.cancel();
                                    Timber.d("Error in cancelling the order "+e.getMessage());
                                    new MaterialDialog.Builder(MyOrderActivity.this)
                                            .title("Good Food")
                                            .content("Something went wrong. Please try again.")
                                            .typeface("Lato-Regular", "Lato-Regular")
                                            .positiveText("OK")
                                            .show();
                                }
                            }
                        });




                    }
                });

                return convertView;
            }
        };

        orderListView.setAdapter(orderAdapter);

    }

    private void cancelOrder(final int position) {
        spinner.setContent("Cancelling your order. Please wait.");
        spinner.show();

        ParseObject toDelete = new ParseObject(ORDER);
        toDelete.setObjectId(myOrderList.get(position).getObjectId());

        toDelete.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    spinner.cancel();
                    new MaterialDialog.Builder(MyOrderActivity.this)
                            .title("Good Food")
                            .content("Your Order is Cancelled.")
                            .typeface("Lato-Regular", "Lato-Regular")
                            .positiveText("OK")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    myOrderList.remove(position);
                                    ((BaseAdapter) orderListView.getAdapter()).notifyDataSetChanged();

                                }
                            })
                            .show();

                } else {
                    //something wrong
                    spinner.cancel();
                    Timber.d("Error in cancelling the order " + e.getMessage());
                    new MaterialDialog.Builder(MyOrderActivity.this)
                            .title("Good Food")
                            .content("Something went wrong. Please try again.")
                            .typeface("Lato-Regular", "Lato-Regular")
                            .positiveText("OK")
                            .show();
                }
            }
        });
    }

    private void retrieveMyOrders() {
        myOrderList.clear();
        spinner = new MaterialDialog.Builder(MyOrderActivity.this)
                .title("Good Food")
                .content("Retrieving your orders for today")
                .typeface("Lato-Regular","Lato-Regular")
                .progress(true, 0)
                .cancelable(false)
                .show();
        ParseQuery<ParseObject> query = new ParseQuery(ORDER);
            query.whereEqualTo(PHONE_NUMBER, Prefs.with(getApplicationContext()).getString(PHONE_NUMBER, null));

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    spinner.cancel();
                    if (e == null) {
                        for (ParseObject po : list) {
                            Order order = new Order(
                                    po.getObjectId(),
                                    po.getString(ORDER_MENU_ITEM),
                                    po.getInt(ORDER_QTY),
                                    po.getString(ORDER_LOCATION)
                            );
                            myOrderList.add(order);
                        }
                        ((BaseAdapter) orderListView.getAdapter()).notifyDataSetChanged();
                    } else {
                        //something is wrong
                        new MaterialDialog.Builder(MyOrderActivity.this)
                                .title("Good Food")
                                .content("Something is not right. \n\nDo you want to retry?")
                                .typeface("Lato-Regular", "Lato-Regular")
                                .positiveText("OK")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        retrieveMyOrders();
                                    }
                                })
                                .show();
                    }
                }
            });

    }


    static class ViewHolder{
        TextView orderName;
        TextView orderDetail;
        TextView orderLocation;
        TextView orderQuantity;
        Button cancelOrder;
    }
}
