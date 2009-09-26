/*
 * 09/19/2005
 *
 * HeapIndicatorOptionPanel.java - Option panel for the heap indicator.
 * Copyright (C) 2005 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://rtext.fifesoft.com
 *
 * This file is a part of RText.
 *
 * RText is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * RText is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.fife.rtext.plugins.heapindicator;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fife.ui.RColorSwatchesButton;
import org.fife.ui.UIUtil;
import org.fife.ui.app.AbstractPluggableGUIApplication;
import org.fife.ui.app.PluginOptionsDialogPanel;
import org.fife.ui.app.Plugin;


/**
 * Option panel for the {@link HeapIndicator} plugin.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class HeapIndicatorOptionPanel extends PluginOptionsDialogPanel
					implements ActionListener, PropertyChangeListener,
							ChangeListener {

	private JCheckBox visibilityCheckBox;
	private JSpinner refreshRateSpinner;
	private JCheckBox systemColorsCheckBox;
	private JLabel foregroundLabel;
	private RColorSwatchesButton foregroundButton;
	private JLabel borderLabel;
	private RColorSwatchesButton borderButton;

	private static final String COLOR_PROPERTY		= "ColorProperty";
	private static final String REFRESH_RATE_PROPERTY	= "RefreshRateProperty";
	private static final String SYSTEM_COLORS_PROPERTY= "SystemColorsProperty";
	private static final String VISIBILITY_PROPERTY	= "VisibilityProperty";


	/**
	 * Constructor.
	 */
	public HeapIndicatorOptionPanel(AbstractPluggableGUIApplication app,
								Plugin plugin) {

		super(plugin);
		ResourceBundle msg = ((HeapIndicatorPlugin)plugin).getBundle();
		setName(plugin.getPluginName());

		ComponentOrientation orientation = ComponentOrientation.
									getOrientation(getLocale());

		Border empty5Border = UIUtil.getEmpty5Border();
		setLayout(new BorderLayout());
		setBorder(empty5Border);

		// A panel to contain everything that will go into our "top" area.
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		JPanel topPanel2 = new JPanel();
		topPanel2.setLayout(new BoxLayout(topPanel2, BoxLayout.Y_AXIS));
		topPanel2.setBorder(BorderFactory.createCompoundBorder(
				new OptionPanelBorder(msg.getString(
								"Plugin.OptionPanel.Title.General")),
				empty5Border));

		// Panel to toggle the indicator's visibility.
		JPanel temp = new JPanel(new BorderLayout());
		visibilityCheckBox = new JCheckBox(
			msg.getString("Plugin.OptionPanel.Visibility.text"));
		visibilityCheckBox.setMnemonic((int)msg.getString(
			"Plugin.OptionPanel.Visibility.mnemonic").charAt(0));
		visibilityCheckBox.addActionListener(this);
		temp.add(visibilityCheckBox, BorderLayout.LINE_START);
		topPanel2.add(temp);
		topPanel2.add(Box.createVerticalStrut(5));

		// Panel for the indicator's refresh rate.
		temp = new JPanel(new BorderLayout());
		JPanel temp2 = new JPanel();
		temp2.setLayout(new BoxLayout(temp2, BoxLayout.LINE_AXIS));
		JLabel label = UIUtil.createLabel(msg,
						"Plugin.OptionPanel.RefreshRate.text",
						"Plugin.OptionPanel.RefreshRate.mnemonic");
		refreshRateSpinner = new JSpinner(new SpinnerNumberModel(1,1,600,1));
		label.setLabelFor(refreshRateSpinner);
		refreshRateSpinner.addChangeListener(this);
		temp2.add(label);
		temp2.add(Box.createHorizontalStrut(5));
		temp2.add(refreshRateSpinner);
		temp2.add(Box.createHorizontalGlue());
		temp.add(temp2, BorderLayout.LINE_START);
		topPanel2.add(temp);
		topPanel.add(topPanel2);
		topPanel.add(Box.createVerticalStrut(5));

		topPanel2 = new JPanel();
		topPanel2.setLayout(new BoxLayout(topPanel2, BoxLayout.Y_AXIS));
		topPanel2.setBorder(BorderFactory.createCompoundBorder(
				new OptionPanelBorder(msg.getString(
								"Plugin.OptionPanel.Title.Appearance")),
				empty5Border));

		// Panel for the "use system colors" checkbox.
		temp = new JPanel(new BorderLayout());
		systemColorsCheckBox = new JCheckBox(msg.getString(
						"Plugin.OptionPanel.SystemColors.text"));
		systemColorsCheckBox.setMnemonic((int)msg.getString(
			"Plugin.OptionPanel.SystemColors.mnemonic").charAt(0));
		systemColorsCheckBox.addActionListener(this);
		temp.add(systemColorsCheckBox, BorderLayout.LINE_START);
		topPanel2.add(temp);
		topPanel2.add(Box.createVerticalStrut(5));

		// Panel for the indicator's foreground color.
		temp = new JPanel(new BorderLayout());
		Border indentBorder = BorderFactory.createEmptyBorder(0,20,0,0);
		temp.setBorder(indentBorder);
		temp2 = new JPanel();
		temp2.setLayout(new BoxLayout(temp2, BoxLayout.LINE_AXIS));
		foregroundLabel = UIUtil.createLabel(msg,
						"Plugin.OptionPanel.ForegroundColor.text",
						"Plugin.OptionPanel.ForegroundColor.mnemonic");
		foregroundButton = new RColorSwatchesButton();
		foregroundButton.addPropertyChangeListener(this);
		foregroundLabel.setLabelFor(foregroundButton);
		temp2.add(foregroundLabel);
		temp2.add(Box.createHorizontalStrut(5));
		temp2.add(foregroundButton);
		temp2.add(Box.createHorizontalGlue());
		temp.add(temp2, BorderLayout.LINE_START);
		topPanel2.add(temp);
		topPanel2.add(Box.createVerticalStrut(5));

		// Panel for the indicator's border color.
		temp = new JPanel(new BorderLayout());
		temp.setBorder(indentBorder);
		temp2 = new JPanel();
		temp2.setLayout(new BoxLayout(temp2, BoxLayout.LINE_AXIS));
		borderLabel = UIUtil.createLabel(msg,
						"Plugin.OptionPanel.BorderColor.text",
						"Plugin.OptionPanel.BorderColor.mnemonic");
		borderButton = new RColorSwatchesButton();
		borderButton.addPropertyChangeListener(this);
		borderLabel.setLabelFor(borderButton);
		temp2.add(borderLabel);
		temp2.add(Box.createHorizontalStrut(5));
		temp2.add(borderButton);
		temp2.add(Box.createHorizontalGlue());
		temp.add(temp2, BorderLayout.LINE_START);
		topPanel2.add(temp);

		topPanel.add(topPanel2);

		// Put it all together!
		add(topPanel, BorderLayout.NORTH);
		applyComponentOrientation(orientation);

	}


	/**
	 * Called when an action is performed in this option panel.
	 *
	 * @param e The action event.
	 */
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source==visibilityCheckBox) {
			hasUnsavedChanges = true;
			boolean visible = visibilityCheckBox.isSelected();
			firePropertyChange(VISIBILITY_PROPERTY, !visible, visible);
		}

		else if (source==systemColorsCheckBox) {
			hasUnsavedChanges = true;
			boolean use = systemColorsCheckBox.isSelected();
			setColorOptionsEnabled(!use);
			firePropertyChange(SYSTEM_COLORS_PROPERTY, !use, use);
		}

	}


	/**
	 * Updates the heap indicator's parameters to reflect those in
	 * this options panel.
	 */
	protected void doApplyImpl(Frame owner) {
		HeapIndicatorPlugin p = (HeapIndicatorPlugin)getPlugin();
		p.setVisible(visibilityCheckBox.isSelected());
		int refresh = ((Integer)refreshRateSpinner.getValue()).intValue();
		p.setRefreshInterval(refresh*1000);
		p.setUseSystemColors(systemColorsCheckBox.isSelected());
		p.setIconBorderColor(borderButton.getColor());
		p.setIconForeground(foregroundButton.getColor());
	}


	/**
	 * Checks whether or not all input the user specified on this panel is
	 * valid.  This should be overridden to check, for example, whether
	 * text fields have valid values, etc.  This method will be called
	 * whenever the user clicks "OK" or "Apply" on the options dialog to
	 * ensure all input is valid.  If it isn't, the component with invalid
	 * data will be given focus and the user will be prompted to fix it.
	 * 
	 * @return <code>null</code> if the panel has all valid inputs, or an
	 *         <code>OptionsPanelCheckResult</code> if an input was invalid.
	 *         This component is the one that had the error and will be
	 *         given focus, and the string is an error message that will be
	 *         displayed.
	 */
	public OptionsPanelCheckResult ensureValidInputs() {
		OptionsPanelCheckResult res = null;
		// I don't think JSpinner allows invalid input, but just in case...
		Number number = (Number)refreshRateSpinner.getValue();
		if (!(number instanceof Integer)) {
			ResourceBundle msg = ((HeapIndicatorPlugin)getPlugin())
													.getBundle();
			String error = msg.getString(
							"Plugin.OptionPanel.Error.RefreshRate.text");
			error = MessageFormat.format(error,
						new Object[] { refreshRateSpinner.getValue() });
			res = new OptionsPanelCheckResult(this,refreshRateSpinner,error);
		}
		return res;
	}


	/**
	 * Returns the <code>JComponent</code> at the "top" of this Options
	 * panel.  This is the component that will receive focus if the user
	 * switches to this Options panel in the Options dialog.  As an added
	 * bonus, if this component is a <code>JTextComponent</code>, its
	 * text is selected for easy changing.
	 */
	public JComponent getTopJComponent() {
		return foregroundButton;
	}


	/**
	 * Listens for property changes of the color buttons in this panel.
	 *
	 * @param e The property change event.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		String name = e.getPropertyName();
		if (name.equals(RColorSwatchesButton.COLOR_CHANGED_PROPERTY)) {
			hasUnsavedChanges = true;
			firePropertyChange(COLOR_PROPERTY, e.getOldValue(),
										e.getNewValue());
		}
	}


	/**
	 * Updates this panel's displayed parameter values to reflect those of
	 * the heap indicator.
	 */
	protected void setValuesImpl(Frame frame) {
		HeapIndicatorPlugin p = (HeapIndicatorPlugin)getPlugin();
		visibilityCheckBox.setSelected(p.isVisible());
		refreshRateSpinner.setValue(new Integer(p.getRefreshInterval()/1000));
		boolean useSystemColors = p.getUseSystemColors();
		systemColorsCheckBox.setSelected(useSystemColors);
		borderButton.setColor(p.getIconBorderColor());
		foregroundButton.setColor(p.getIconForeground());
		setColorOptionsEnabled(!useSystemColors);
	}


	/**
	 * Enables or disables the widgets concerned with setting colors for
	 * this heap indicator.
	 *
	 * @param enabled Whether or not the widgets should be enabled.
	 */
	protected void setColorOptionsEnabled(boolean enabled) {
		if (borderLabel!=null) {
			borderLabel.setEnabled(enabled);
			borderButton.setEnabled(enabled);
			foregroundLabel.setEnabled(enabled);
			foregroundButton.setEnabled(enabled);
		}
	}


	/**
	 * Called when the refresh rate spinner changes.
	 *
	 * @param e The change event.
	 */
	public void stateChanged(ChangeEvent e) {
		hasUnsavedChanges = true;
		firePropertyChange(REFRESH_RATE_PROPERTY,
						null, refreshRateSpinner.getValue());
	}


}