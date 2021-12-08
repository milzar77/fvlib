/*
 * MyAction.java - japp (japp.jar)
 * Copyright (C) 2005
 * Source file created on 8-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [MyAction]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;

import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.SettingRes;

/**
 * La ShortcutAction identifica un'azione associata all'esecuzione di un metodo
 * appartenente ad una classe esterna al thread principale della classe che istanzia ShortcutAction
 * @author antares
 */
public class ShortcutAction implements IShortcutAction {
	
		private final static int METHOD_STATIC = 9;
		private final static int METHOD_INSTANCE = 1;
		Object referenceInstance;
		String refMethod;
		Object[] refParams = {};
		Class[] refTypes = {};
		Vector vtParams = new Vector();
		Vector vtTypes = new Vector();
		
		String actionLabel = "Nessuna etichetta";
		String actionGroup = "Nessun gruppo";
		String actionIcon = SettingRes.get("default.mdi.shortcut.icon");
		/**
		 * Oggetto o componente di view, da definire in classe astratta AWindow
		 * Comment for <code>actionOptions</code>
		 */
		Object actionOptions;
		
		public ShortcutAction() {}
		
		public ShortcutAction(Object instance, String methodName) {
			this.referenceInstance = instance;
			this.refMethod = methodName;
		}

		public static final String readShortcutLauncher(String prefix) {
			prefix += SUFFIX_LAUNCHER;
			return SettingRes.get(prefix);
		}
		public static final Vector readShortcutParams(String prefix) {
			Vector vt = new Vector();
			prefix += SUFFIX_PARAMS;
			String values = SettingRes.get(prefix);
			if (values != null && !values.equals("")) {
				StringTokenizer st = new StringTokenizer(values,SettingRes.get("array.separator"));
				while (st.hasMoreTokens()) {
					vt.addElement(st.nextToken());
				}
			}
			return vt;
		}
		public static final String readShortcutGroup(String prefix) {
			prefix += SUFFIX_GROUP;
			return SettingRes.get(prefix);
		}
		public static final String readShortcutLabel(String prefix) {
			prefix += SUFFIX_LABEL;
			return SettingRes.get(prefix);
		}		
		public static final String readShortcutKeyPrefix(String key) {
			final int cut = key.lastIndexOf(SUFFIX_LABEL);
			key = key.substring(0,cut);
			return key;
		}
		public static final String readShortcutOptions(String prefix) {
			prefix += SUFFIX_OPTIONS;
			return SettingRes.get(prefix);
		}
		public static final String readShortcutIcon(String prefix) {
			prefix += SUFFIX_ICON;
			String iconRes = SettingRes.get(prefix);
			if (iconRes==null || "null".equals(iconRes))
				iconRes = SettingRes.get("default.mdi.shortcut.icon");
			return iconRes;
		}
		public static final boolean isShortcutKey(final String key) {
			if (key.endsWith(SUFFIX_LABEL))
				return true;
			else
				return false;
		}
		
		public void launch(){
			refParams = vtParams.toArray();
			refTypes = this.signTypes();
			try {
				Method ilmetodo = this.referenceInstance.getClass().getMethod( this.refMethod ,this.refTypes);
				if ( ilmetodo.getModifiers() == METHOD_STATIC)
					ilmetodo.invoke( this.referenceInstance.getClass(), this.refParams);
				else if ( ilmetodo.getModifiers() == METHOD_INSTANCE)
					ilmetodo.invoke( this.referenceInstance, this.refParams);
				else
					ilmetodo.invoke( this.referenceInstance, this.refParams);				
			} catch (NullPointerException e) {
				Monitor.debug(e);
			} catch (SecurityException e) {
				Monitor.debug(e);
			} catch (NoSuchMethodException e) {
				Monitor.debug(e);
			} catch (IllegalArgumentException e) {
				Monitor.debug(e);
			} catch (IllegalAccessException e) {
				Monitor.debug(e);
			} catch (InvocationTargetException e) {
				Monitor.debug(e);
			}
		}
		
		public void addParam(Object o) {
			if (o instanceof Vector) {
				// Vector vt = ((Vector)o);
				vtParams.addAll((Vector)o);
				for (int i = 0; i < ((Vector)o).size(); i++) {
					Object ob = ((Vector)o).elementAt(i);
					if (ob instanceof String)
						vtTypes.addElement( String.class );
					else
						vtTypes.addElement( Object.class );
				}
			} else {
				vtParams.addElement(o);
				if (o instanceof String)
					vtTypes.addElement( String.class );
				else
					vtTypes.addElement( Object.class );
			}
		}
		
		private Class[] signTypes() {
			Object[] array = vtTypes.toArray();
			Class[] types = new Class[array.length];
			for (int i = 0; i < array.length; i++) {
				types[i]=(Class)array[i];
			}
			return types;
		}

		public final String getActionGroup() {
			return actionGroup;
		}
		public final void setActionGroup(String parActionGroup) {
			actionGroup = parActionGroup;
		}
		public final String getActionLabel() {
			return actionLabel;
		}
		public final void setActionLabel(String parActionLabel) {
			actionLabel = parActionLabel;
		}
		public Object getActionOptions() {
			return actionOptions;
		}
		public void setActionOptions(Object parActionOptions) {
			actionOptions = parActionOptions;
		}
		
		public String getActionIcon() {
			return actionIcon;
		}
		public void setActionIcon(String parActionIcon) {
			actionIcon = parActionIcon;
		}
}
