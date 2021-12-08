package com.blogspot.fravalle.lib.bl.beans;


public class ModelList extends java.util.Vector implements IModelList {
    
	public ModelList() {
		super();
	}
	
	public final synchronized java.util.Vector getAllSearchedResults(String search) {
		java.util.Vector vtResults = new java.util.Vector();
		
		int y = 0;

		// per default il primo valore � l'id, il secondo il titolo
		// dell'elemento ed il terzo il gruppo di appartenenza
		
		do {
			int e = searchHeader(search, y+1, false);
			if (e != -1) {
			    vtResults.addElement( elementData[e].toString()+"@"+e );
			}
			if (e != -1) y = e;
			else y = y+1;
		} while (y < elementCount-1);
		
	    return vtResults;
	}

	public final synchronized int searchHeader(String search, int index, boolean isCaseSensitive) {
		for (int i = index ; i < elementCount ; i++) {
		    String compareSearch = elementData[i].toString();
		    if (!isCaseSensitive) {
		        compareSearch = compareSearch.toLowerCase();
		        search = search.toLowerCase();
		    }
		    
			if (compareSearch.indexOf( search ) != -1) {
				return i;
			}
		}
		return -1;
	}
    
	public final boolean containsHeader(Object elem) {
		return indexOfHeader(elem, 0) >= 0;
	}

	public final synchronized int indexOfHeader(Object elem, int index) {
		for (int i = index ; i < elementCount ; i++) {
			if (elem.equals(elementData[i].toString())) {
				return i;
			}
		}
		return -1;
	}

	public final synchronized int search(Object elem) {
		for (int i = 0; i < elementCount ; i++) {
			if (elem.equals(elementData[i])) {
				return i;
			}
		}
		return -1;
	}
	
	public final synchronized int search(String elem) {
		for (int i = 0; i < elementCount ; i++) {
			if (elem.equals(elementData[i].toString())) {
				return i;
			}
		}
		return -1;
	}
	
}