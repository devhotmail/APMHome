package com.ge.apm.domain;

public enum CasePriorityNum {
	CREATE("紧急",1),APPROVE("重要",2),ASSIGN("一般",3);
	
	private String value;
	private Integer index;
	
	private CasePriorityNum(String value, int index) {  
        this.value = value;  
        this.index = index;  
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
    public static String getName(int index) { 
    	if(index <= 0 || index > CasePriorityNum.values().length-1){
    		return null;
    	}
        for (CasePriorityNum c : CasePriorityNum.values()) {  
            if (c.getIndex() == index) {  
                return c.value;  
            }  
        }  
        return null;  
    }
}
