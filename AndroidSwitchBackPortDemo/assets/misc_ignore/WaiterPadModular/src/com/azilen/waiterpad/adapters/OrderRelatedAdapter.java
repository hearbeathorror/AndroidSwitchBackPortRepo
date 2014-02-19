package com.azilen.waiterpad.adapters;

import java.text.DecimalFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.activities.OrderRelatedActivity;
import com.azilen.waiterpad.data.Guest;
import com.azilen.waiterpad.data.ModifierMaster;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.OrderedItem;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.utils.Global;

/**
 * OrderRelatedAdapter - inflates the list of the current order used by {@link
 * @OrderRelatedActivity}
 * 
 * @author dhara.shah
 * 
 */
public class OrderRelatedAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private int mResource;
	public static int mChildPositionSelected;
	public static int mGroupPositionSelected;
	public static boolean isNewlyAdded;
	private String mGuestSelected;
	private Order mOrder;
	private LayoutInflater inflater;
	private OrderRelatedActivity mOrderRelatedFragment;
	private View view;
	private boolean isSelected;
	private DecimalFormat dcf;

	private List<OrderedItem> orderedItems;
	private String TAG = this.getClass().getSimpleName();

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param resource
	 * @param order
	 * @param guestSelected
	 * @param childPositionSelected
	 * @param orderRelatedFragment
	 */
	public OrderRelatedAdapter(Context context, int resource, Order order,
			String guestSelected, int childPositionSelected,
			OrderRelatedActivity orderRelatedFragment) {
		mContext = context;
		mResource = resource;
		mOrder = order;
		mGuestSelected = guestSelected;
		mChildPositionSelected = childPositionSelected;
		mOrderRelatedFragment = orderRelatedFragment;
		dcf = new DecimalFormat("#.##");
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<Guest> guests = mOrder.getGuests();
		Guest guest = guests.get(groupPosition);

		List<OrderedItem> orderedItems = guest.getOrderedItems();

		if (orderedItems != null) {
			return orderedItems;
		} else {
			return null;
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (mOrder.getGuests().get(groupPosition).getOrderedItems() != null) {
			Log.i("dhara"," " + mOrder.getGuests().get(groupPosition).getOrderedItems()
					.size());
			return mOrder.getGuests().get(groupPosition).getOrderedItems()
					.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mOrder.getGuests();
	}

	@Override
	public int getGroupCount() {
		if (mOrder != null) {
			if (mOrder.getGuests() != null) {
				return mOrder.getGuests().size();
			} else {
				return 0;
			}
		} else {
			return 0;
		}

	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded,
			View convertView, ViewGroup parent) {
		view = convertView;
		final ViewHolder vh;

		if (view == null) {
			inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(mResource, null);
			vh = new ViewHolder();
			vh.txtGuestName = (TextView) view
					.findViewById(R.id.txtGuestNameOrder);
			vh.imgArrow = (ImageView) view.findViewById(R.id.imgArrow);
			view.setTag(vh);
		} else {
			vh = (ViewHolder) view.getTag();
		}
		
		vh.txtGuestName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		final List<Guest> guests = mOrder.getGuests();
		Guest guest = guests.get(groupPosition);

		final RelativeLayout relGuestLayout = (RelativeLayout) view
				.findViewById(R.id.relGuestLayout);

		if (mGuestSelected != null && mGuestSelected.trim().length() > 0) {
			// do nothing
		} else {
			mGuestSelected = "0";
		}

		relGuestLayout.setBackgroundColor(mContext.getResources().getColor(
				android.R.color.white));

		if (isExpanded) {
			if (OrderRelatedAdapter.mGroupPositionSelected == groupPosition) {
				relGuestLayout.setBackgroundColor(mContext.getResources()
						.getColor(android.R.color.darker_gray));
			}
		}

		vh.txtGuestName.setText(guest.getGuestName());
		vh.txtGuestName.setFocusable(false);
		vh.txtGuestName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isNewlyAdded = false;
				if (mOrder.getOrderStatus() == null
						|| (mOrder.getOrderStatus() != null && mOrder
								.getOrderStatus().ordinal() != 2)) {
					if (isExpanded) {
						relGuestLayout.setBackgroundColor(mContext
								.getResources().getColor(
										android.R.color.darker_gray));

						isSelected = true;
						// perform operations needed on the tapped group
						mGuestSelected = String.valueOf(groupPosition);
						OrderRelatedAdapter.mGroupPositionSelected = groupPosition;
						if (mOrderRelatedFragment != null) {
							mOrderRelatedFragment.performOperations(isExpanded,
									groupPosition, isSelected);
						}
					} else {
						Global.logd(TAG + " list is nt expanded ");
						relGuestLayout
								.setBackgroundColor(mContext.getResources()
										.getColor(android.R.color.white));
					}
				} else {
					Global.logd(TAG + " order status is 2 ");
				}
			}
		});

		vh.imgArrow.setFocusable(false);
		vh.imgArrow.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				isNewlyAdded = false;
				if (isExpanded) {
					if (android.os.Build.VERSION.SDK_INT < 16) {
						vh.imgArrow.setBackgroundDrawable(mContext
								.getResources().getDrawable(
										R.drawable.arrow_right));
					} else {
						vh.imgArrow.setBackground(mContext.getResources()
								.getDrawable(R.drawable.arrow_right));
					}
				} else {
					if (android.os.Build.VERSION.SDK_INT < 16) {
						vh.imgArrow.setBackgroundDrawable(mContext
								.getResources().getDrawable(
										R.drawable.arrow_down));
					} else {
						vh.imgArrow.setBackground(mContext.getResources()
								.getDrawable(R.drawable.arrow_down));
					}

					vh.txtGuestName.setClickable(false);
				}

				relGuestLayout.setBackgroundColor(mContext.getResources()
						.getColor(android.R.color.white));

				if (mOrderRelatedFragment != null) {
					mOrderRelatedFragment.changeGroupStatus(isExpanded,
							groupPosition);
				}
			}
		});

		return view;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder vh;

		if (view == null) {
			inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.individual_item_orderscreen, null);
			vh = new ViewHolder();
			vh.txtItemName = (TextView) view
					.findViewById(R.id.txtItemNameOrderScreen);
			vh.txtQuantity = (TextView) view
					.findViewById(R.id.txtItemQuantityOrderScreen);
			vh.txtItemPrice = (TextView) view.findViewById(R.id.txtItemPrice);
			vh.txtModifiersItemOrderScreen = (TextView) view
					.findViewById(R.id.txtModifiersItemOrderScreen);
			vh.relItemLayout = (RelativeLayout) view
					.findViewById(R.id.relOrderItems);
			view.setTag(vh);
		} else {
			vh = (ViewHolder) view.getTag();
		}
		
		vh.txtItemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		vh.txtQuantity.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		vh.txtItemPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		vh.txtModifiersItemOrderScreen.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		final List<Guest> guests = mOrder.getGuests();
		final Guest guest = guests.get(groupPosition);

		orderedItems = guest.getOrderedItems();
		
		final OrderedItem orderedItem = orderedItems.get(childPosition);

		// determine the status of the order
		// ADDED(0), COOKINGSTARTED(1), COOKINGCOMPLETED(2), SERVED(3);

		// changes as on 16th july 2013

		// Log.i(TAG, " value of child item position " +
		// mChildPositionSelected);
		if (isNewlyAdded && childPosition == guest.getOrderedItems().size() - 1
				&& groupPosition == mGroupPositionSelected) {
			final Animation a = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_in_right);
			vh.relItemLayout.setBackgroundColor(mContext.getResources()
					.getColor(R.color.yellow));
			view.clearAnimation();
			view.startAnimation(a);
		}

		if (mOrder.getOrderStatus() != null
				&& mOrder.getOrderStatus().ordinal() == 2) {
			vh.relItemLayout.setBackgroundColor(mContext.getResources()
					.getColor(R.color.checkout_color));
			vh.txtItemName.setTextColor(mContext.getResources().getColor(
					R.color.white));
			vh.txtItemPrice.setTextColor(mContext.getResources().getColor(
					R.color.white));
			vh.txtModifiersItemOrderScreen.setTextColor(mContext.getResources()
					.getColor(R.color.white));
			vh.txtQuantity.setTextColor(mContext.getResources().getColor(
					R.color.white));
		} else {
			if ((orderedItem.isEditable() || orderedItem.getOrderedItemStatus() == 0
					&& orderedItem.isEditable() == false)) {
				vh.relItemLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.yellow));
			} else {
				vh.relItemLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.sent_item_color));
			}

			if (mChildPositionSelected == childPosition
					&& mGroupPositionSelected == groupPosition) {
				isNewlyAdded = false;
				Global.logd(TAG + " selected ");

				vh.relItemLayout.setBackgroundColor(mContext.getResources()
						.getColor(android.R.color.transparent));
				vh.relItemLayout
						.setBackgroundResource(R.drawable.child_item_selector);
				vh.relItemLayout.setSelected(true);
			} else {
				vh.relItemLayout.setSelected(false);
				if (orderedItem.isEditable()
						|| (orderedItem.getOrderedItemStatus() == 0 && orderedItem
								.isEditable() == false)) {
					vh.relItemLayout.setBackgroundColor(mContext.getResources()
							.getColor(R.color.yellow));
				} else {
					vh.relItemLayout.setBackgroundColor(mContext.getResources()
							.getColor(R.color.sent_item_color));
				}
			}
		}

		vh.txtModifiersItemOrderScreen.setText("");

		if (orderedItem.getModifiers() != null) {
			vh.txtModifiersItemOrderScreen.setVisibility(View.VISIBLE);
			for (ModifierMaster modifiers : orderedItem.getModifiers()) {
				vh.txtModifiersItemOrderScreen.append("\n"
						+ modifiers.getModifierName());
			}
		} else {
			vh.txtModifiersItemOrderScreen.setVisibility(View.GONE);
		}

		switch (orderedItem.getOrderedItemStatus()) {
		case 0:

			// changes as on 22nd August 2013
			// change the color of the items that have not been printed
			if (orderedItem.isEditable() == false) {
				if (mChildPositionSelected == childPosition
						&& mGroupPositionSelected == groupPosition) {
					vh.relItemLayout.setBackgroundColor(mContext.getResources()
							.getColor(android.R.color.transparent));
					vh.relItemLayout
							.setBackgroundResource(R.drawable.child_item_selector);
					vh.relItemLayout.setSelected(true);
				} else {
					vh.relItemLayout.setBackgroundColor(mContext.getResources()
							.getColor(R.color.yellow));
				}
			}

			vh.txtItemName.setText(orderedItem.getName());

			double qty = orderedItem.getQuantity();
			String strQty = dcf.format(qty);

			double itemPriceToDisplay = orderedItem.getPrice() * qty;
			String itemPrice = dcf.format(itemPriceToDisplay);

			String currancySymbol = LanguageManager.getInstance()
					.getCurrancySymbol();

			String currancyLocation = LanguageManager.getInstance().IS_AFTER();

			if (!currancyLocation.equalsIgnoreCase("TRUE")) {
				itemPrice = currancySymbol + " " + itemPrice;
			} else {
				itemPrice = itemPrice + " " + currancySymbol;
			}

			vh.txtQuantity.setText(strQty);
			vh.txtItemPrice.setText(itemPrice);

			if ((vh.txtItemName.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
				vh.txtItemName.setPaintFlags(vh.txtItemName.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
			}

			if ((vh.txtQuantity.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
				vh.txtQuantity.setPaintFlags(vh.txtQuantity.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
			}

			if ((vh.txtItemPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
				vh.txtItemPrice.setPaintFlags(vh.txtItemPrice.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
			}

			break;

		case 1:
			// for cases 1 - cooking started

			vh.txtItemName.setText(orderedItem.getName());

			qty = orderedItem.getQuantity();
			strQty = dcf.format(qty);

			itemPrice = dcf.format(orderedItem.getPrice());

			currancySymbol = LanguageManager.getInstance().getCurrancySymbol();

			currancyLocation = LanguageManager.getInstance().IS_AFTER();

			if (!currancyLocation.equalsIgnoreCase("TRUE")) {
				itemPrice = currancySymbol + " " + itemPrice;
			} else {
				itemPrice = itemPrice + " " + currancySymbol;
			}

			vh.txtQuantity.setText(strQty);
			vh.txtItemPrice.setText(itemPrice);
			
			if ((vh.txtItemName.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
				vh.txtItemName.setPaintFlags(vh.txtItemName.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
			}

			if ((vh.txtQuantity.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
				vh.txtQuantity.setPaintFlags(vh.txtQuantity.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
			}

			if ((vh.txtItemPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
				vh.txtItemPrice.setPaintFlags(vh.txtItemPrice.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
			}

			break;

		case 2:
			// for cases 2 - cooking completed

			vh.txtItemName.setText(orderedItem.getName());

			qty = orderedItem.getQuantity();
			strQty = dcf.format(qty);

			itemPrice = dcf.format(orderedItem.getPrice());

			currancySymbol = LanguageManager.getInstance().getCurrancySymbol();

			currancyLocation = LanguageManager.getInstance().IS_AFTER();

			if (!currancyLocation.equalsIgnoreCase("TRUE")) {
				itemPrice = currancySymbol + " " + itemPrice;
			} else {
				itemPrice = itemPrice + " " + currancySymbol;
			}

			vh.txtQuantity.setText(strQty);
			vh.txtItemPrice.setText(itemPrice);
			
			if ((vh.txtItemName.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
				vh.txtItemName.setPaintFlags(vh.txtItemName.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
			}

			if ((vh.txtQuantity.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
				vh.txtQuantity.setPaintFlags(vh.txtQuantity.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
			}

			if ((vh.txtItemPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
				vh.txtItemPrice.setPaintFlags(vh.txtItemPrice.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
			}
			
			break;

		case 3:
			// for cases 3 - served

			vh.txtItemName.setText(orderedItem.getName());
			vh.txtItemName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

			vh.txtQuantity.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			vh.txtItemPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

			qty = orderedItem.getQuantity();
			strQty = dcf.format(qty);

			itemPrice = dcf.format(orderedItem.getPrice());

			currancySymbol = LanguageManager.getInstance().getCurrancySymbol();

			currancyLocation = LanguageManager.getInstance().IS_AFTER();

			if (!currancyLocation.equalsIgnoreCase("TRUE")) {
				itemPrice = currancySymbol + " " + itemPrice;
			} else {
				itemPrice = itemPrice + " " + currancySymbol;
			}

			vh.txtQuantity.setText(strQty);
			vh.txtItemPrice.setText(itemPrice);

			break;

		case 4:
			vh.txtItemName.setText(orderedItem.getName());

			qty = orderedItem.getQuantity();
			strQty = dcf.format(qty);

			itemPrice = dcf.format(orderedItem.getPrice());

			currancySymbol = LanguageManager.getInstance().getCurrancySymbol();

			currancyLocation = LanguageManager.getInstance().IS_AFTER();

			if (!currancyLocation.equalsIgnoreCase("TRUE")) {
				itemPrice = currancySymbol + " " + itemPrice;
			} else {
				itemPrice = itemPrice + " " + currancySymbol;
			}

			vh.txtQuantity.setText(strQty);
			vh.txtItemPrice.setText(itemPrice);
			break;

		default:
			break;
		}

		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	static class ViewHolder {
		private TextView txtGuestName;
		private TextView txtItemName;
		private TextView txtQuantity;
		private TextView txtItemPrice;
		private ImageView imgArrow;
		private TextView txtModifiersItemOrderScreen;
		private RelativeLayout relItemLayout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseExpandableListAdapter#onGroupExpanded(int)
	 */
	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
		Global.logi(TAG + " group expanded");
	}
}
