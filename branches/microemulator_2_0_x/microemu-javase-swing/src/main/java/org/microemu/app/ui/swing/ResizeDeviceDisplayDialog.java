/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class ResizeDeviceDisplayDialog extends SwingDialogPanel {

    private static final long serialVersionUID = 1L;
    
    private class IntegerField extends JTextField {

        private static final long serialVersionUID = 1L;
        
        private int minValue;
        
        private int maxValue;

        public IntegerField(int cols, int minValue, int maxValue) {
            super(cols);
            
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        protected Document createDefaultModel() {
            return new IntegerDocument();
        }

        class IntegerDocument extends PlainDocument {

            private static final long serialVersionUID = 1L;

            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) {
                    return;
                }
                char[] test = str.toCharArray();
                for (int i = 0; i < test.length; i++) {
                    if (!Character.isDigit(test[i])) {
                        return;
                    }
                }
                String prevText = getText(0, getLength());
                super.insertString(offs, str, a);
                int testValue = Integer.parseInt(getText(0, getLength()));
                if (testValue < minValue | testValue > maxValue) {
                    replace(0, getLength(), prevText, a);
                }                
            }
        }
    };    
    
    private IntegerField widthField = new IntegerField(5, 1, 9999);
    
    private IntegerField heightField = new IntegerField(5, 1, 9999);

    public ResizeDeviceDisplayDialog() {
        add(new JLabel("Width:"));
        add(this.widthField);
        add(new JLabel("Height:"));
        add(this.heightField);
        JButton swapButton = new JButton("Swap");
        swapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tmp = widthField.getText();
                widthField.setText(heightField.getText());
                heightField.setText(tmp);
            }
        });
        add(swapButton);
    }

    public void setDeviceDisplaySize(int width, int height) {
        widthField.setText("" + width);
        heightField.setText("" + height);
    }
    
    public int getDeviceDisplayWidth() {
        return Integer.parseInt(widthField.getText());
    }
    
    public int getDeviceDisplayHeight() {
        return Integer.parseInt(heightField.getText());
    }

}
