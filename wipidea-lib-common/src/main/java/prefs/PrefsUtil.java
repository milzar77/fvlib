package prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import com.blogspot.fravalle.common.ICommonArea;

/**
 * @author francesco
 * On Windows systems check "HKEY_CURRENT_USER\Software\JavaSoft\Prefs"
 * to see all stored user preferences
 */
public abstract class PrefsUtil implements ICommonArea {

	static final private Logger logger = Logger.getAnonymousLogger();
	
        private static final String prefsFileRoot = "/";
        private static final String prefsFileName = "res/prefs.xml";
            
        private static Preferences prefsRoot;
        //private static Preferences prefsContext;
        
        static {
            PrefsUtil.loadPrefs(prefsFileRoot + prefsFileName);
        }

        public static void init(String prefFileName) {
            PrefsUtil.loadPrefs(prefFileName);
        }
        
        public static Preferences getRootPrefs() {
            if (prefsRoot==null)
                prefsRoot = Preferences.userRoot();
            return prefsRoot;
        }
        
        public static Preferences getContextPrefs(Class c) {
            return Preferences.userNodeForPackage(c);
        }
        
        /**
         * This method store and retrieve all common values similarly to
         * getCommonPrefs, the implementation follows the typical package
         * root structure, it can be useful when some class need
         * access privileges based on package path
         * @return
         */
        public static Preferences getBasePrefs() {
            return Preferences.userNodeForPackage(ICommonArea.class);
        }
        
        /**
         * IMPORTANT: This method must be called by all methods stored inside
         * the main WEEV library package 
         * @param prefGroup
         * The numeric identifier, see ICommonArea
         * @return
         */
        public static Preferences getCommonPrefs(final short prefGroup) {
            String pathName = "/common";
            switch (prefGroup) {
                case PREFS_GROUP_COMMON:
                    break;
                case PREFS_GROUP_COMMON_WINDOW:
                    pathName += "/window";
                    break;
                case PREFS_GROUP_COMMON_WINDOW_EVENT:
                    pathName += "/window/event";
                    break;
                case PREFS_GROUP_COMMON_WINDOW_EVENT_INIT:
                    pathName += "/window/event/init";
                    break;
                case PREFS_GROUP_COMMON_WINDOW_EVENT_START:
                    pathName += "/window/event/start";
                    break;
                case PREFS_GROUP_COMMON_WINDOW_EVENT_END:
                    pathName += "/window/event/end";
                    break;
                case PREFS_GROUP_COMMON_LOGGING:
                    pathName += "/logging";
                    break;
                case PREFS_GROUP_COMMON_LOGGING_DB:
                    pathName += "/logging/db";
                    break;
                default:
                    break;
            }
            return Preferences.userRoot().node( pathName );
        }
        
        public static Preferences getCommonWindowPrefs() {
            return getCommonPrefs(PREFS_GROUP_COMMON_WINDOW);
        }

        public static Preferences getCommonWindowEventInitPrefs() {
            return getCommonPrefs(PREFS_GROUP_COMMON_WINDOW_EVENT_INIT);
        }
        
        public static Preferences getCommonWindowEventStartPrefs() {
            return getCommonPrefs(PREFS_GROUP_COMMON_WINDOW_EVENT_START);
        }

        public static Preferences getCommonWindowEventEndPrefs() {
            return getCommonPrefs(PREFS_GROUP_COMMON_WINDOW_EVENT_END);
        }
        
        public static Preferences getCommonLoggingPrefs() {
            return getCommonPrefs(PREFS_GROUP_COMMON_LOGGING);
        }
        
        public static Preferences getCommonLoggingDbPrefs() {
            return getCommonPrefs(PREFS_GROUP_COMMON_LOGGING_DB);
        }
        
	static public void savePrefs(Preferences prefs, String prefsPath) {
		FileOutputStream fos;
		try {
			prefs.sync();
			fos = new FileOutputStream(new File(prefsPath),false);
			prefs.exportSubtree(fos);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

	}
	
    static public void loadPrefs(String prefsPath) {
    	File f = new File(prefsPath);
    	InputStream is = null;
    	try {
    		if (f.exists())
    		    is = new FileInputStream(f);
    		else
    		    is = Object.class.getClass().getResourceAsStream(prefsPath);
                
                if (is != null) {
                    importPrefs( is );
                    is.close();
                    logger.info( "Using application preference descriptor based on:" + prefsPath );
                } else {
                    logger.warning( "Default prefs.xml and specific application preference descriptor absent, using default values" );
                }
    	} catch (FileNotFoundException e) {
    		logger.severe(e.getMessage());
    		e.printStackTrace();
    	} catch (IOException e) {
    		logger.severe(e.getMessage());
    		e.printStackTrace();
    	}
    }
	
    static private void importPrefs(InputStream fis) {
    	try {
            Preferences.systemRoot().clear();
            Preferences.userRoot().clear();
            Preferences.importPreferences(fis);
        } catch (IOException e) {
        	logger.severe(e.getMessage());
        } catch (InvalidPreferencesFormatException e) {
        	logger.severe(e.getMessage());
        } catch (BackingStoreException e) {
        	logger.severe(e.getMessage());
        }
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
