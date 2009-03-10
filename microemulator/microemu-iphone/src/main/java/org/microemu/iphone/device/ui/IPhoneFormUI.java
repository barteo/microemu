/**
 *  MicroEmulator
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  @version $Id$
 */
package org.microemu.iphone.device.ui;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import joc.Message;
import joc.Static;
import obc.CGRect;
import obc.NSIndexPath;
import obc.NSObject;
import obc.NSString;
import obc.UILabel;
import obc.UINavigationBar;
import obc.UINavigationItem;
import obc.UITableView;
import obc.UITableViewCell;
import obc.UITextField;
import obc.UIToolbar;
import obc.UIView;

import org.microemu.device.ui.FormUI;
import org.microemu.iphone.MicroEmulator;
import org.microemu.iphone.ThreadDispatcher;

public class IPhoneFormUI extends AbstractUI<Form> implements FormUI {

	static final int UIViewAutoresizingFlexibleHeight = 1 << 4;

	static final int UIViewAutoresizingFlexibleWidth = 1 << 1;

	private UITableView tableView;

	private UIView view;

	private UINavigationBar navigtionBar;

	private Map<Item, UIView> itemViewMap = new HashMap<Item, UIView>();

	public IPhoneFormUI(MicroEmulator microEmulator, Form form) {
		super(microEmulator, form);
	}

	@Message
	public final int tableView$numberOfRowsInSection$(UITableView table, int sectionIndex) {
		return displayable.size();
	}

	@Message
	public final UITableViewCell tableView$cellForRowAtIndexPath$(UITableView table, NSIndexPath indexPath) {
		UITableViewCell cell = (UITableViewCell) table.dequeueReusableCellWithIdentifier$("formCell");
		if (cell == null) {
			cell = new UITableViewCell().init();
			cell.setReuseIdentifier$("formCell");
		}
		Item item = displayable.get(indexPath.row());
		UIView itemView = itemViewMap.get(item);
		if (item instanceof TextField) {
			final TextField tf = (TextField) item;
			if (itemView == null) {
				itemView = new UIView().initWithFrame$(new CGRect(0,0,200,100));
				UILabel itemLabelView=new UILabel().initWithFrame$(new CGRect(0,0,100,100));
				itemLabelView.setTag$(100);
				UITextField itemTextField=new UITextField().initWithFrame$(new CGRect(100,0,100,100));
				itemTextField.setTag$(200);
				itemTextField.setDelegate$(new NSObject() {
					@SuppressWarnings({ "unused" })
					@joc.Message(name = "textFieldShouldReturn:", types = "c12@0:4@8")
					public byte textFieldShouldReturn$(UITextField arg0) {
						System.out.println(".textFieldShouldReturn$()");
						arg0.resignFirstResponder();
						try{
						tf.setString(arg0.text().toString());
						}catch (IllegalArgumentException e) {
							arg0.setText$(tf.getString());
						}
						return Static.NO;
					}
				});
				itemView.addSubview$(itemLabelView);
				itemView.addSubview$(itemTextField);
				itemViewMap.put(tf, itemView);
			}
			((UILabel)itemView.viewWithTag$(100)).setText$(tf.getLabel());
			((UITextField)itemView.viewWithTag$(200)).setText$(tf.getString());

			((UIView) cell.contentView()).addSubview$(itemView);
		} else if (item instanceof StringItem) {
			StringItem si = (StringItem) item;
			cell.setText$(si.getText());
		}
		// if(choiceGroup.getType()==List.MULTIPLE&&displayable.isSelected(indexPath.row()))
		// cell.setAccessoryType$(3);
		// else
		// cell.setAccessoryType$(0);
		return cell;
	}

	public void hideNotify() {
	}

	public void invalidate() {
		// TODO Auto-generated method stub

	}

	public void showNotify() {
		System.out.println("showNotify");

		if (view == null) {
			view = new UIView().initWithFrame$(microEmulator.getWindow().bounds());

			navigtionBar = new UINavigationBar().initWithFrame$(new CGRect(0, 0,
					microEmulator.getWindow().bounds().size.width, NAVIGATION_HEIGHT));
			UINavigationItem title = new UINavigationItem().initWithTitle$(displayable.getTitle());
			title.setBackButtonTitle$("Back");
			navigtionBar.pushNavigationItem$(title);
			view.addSubview$(navigtionBar);

			tableView = new UITableView().initWithFrame$style$(new CGRect(0, NAVIGATION_HEIGHT, microEmulator
					.getWindow().bounds().size.width, microEmulator.getWindow().bounds().size.height
					- NAVIGATION_HEIGHT - TOOLBAR_HEIGHT), 0);

			view.addSubview$(tableView);
			toolbar = (UIToolbar) new UIToolbar().initWithFrame$(new CGRect(0,
					microEmulator.getWindow().bounds().size.height - TOOLBAR_HEIGHT,
					microEmulator.getWindow().bounds().size.width, TOOLBAR_HEIGHT));
			view.addSubview$(toolbar);
			updateToolbar();
		}
		tableView.setDataSource$(this);
		tableView.setDelegate$(this);
		tableView.reloadData();

		view.retain();
		microEmulator.getWindow().addSubview$(view);
	}

	public int append(Image img) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int append(Item item) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int append(String str) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(int itemNum) {
		// TODO Auto-generated method stub

	}

	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	public void insert(int itemNum, Item item) {
		// TODO Auto-generated method stub

	}

	public void set(int itemNum, Item item) {
		// TODO Auto-generated method stub

	}

	public void updateLayout() {
		System.out.println("IPhoneFormUI.updateLayout()");
		if (tableView != null)
			ThreadDispatcher.dispatchOnMainThread(new Runnable() {
				public void run() {
					tableView.reloadData();
				}
			}, false);
	}
}
