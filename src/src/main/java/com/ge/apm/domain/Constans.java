package com.ge.apm.domain;

public enum Constans {	
	CREATE("报修",1),APPROVE("派单",2),DISPATCH("领单",3),ACCEPT("维修",4),REPAIR("关单",5),CLOSED("反馈",6),
	TIMEOUT("超时微信推送","templateId1"),REOPEN("二次开单微信推送","templateId2");
	private Constans(String value, int index) {  
        this.value = value;  
        this.index = index;  
    } 
	
	private Constans(String key, String value) {  
        this.value = value;  
        this.key = key;  
    }
	
	private Integer index;
	private String value;
	private String key;
	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
    public static String getName(int index) { 
    	if(index <= 0 || index > Constans.values().length){
    		return null;
    	}
        for (Constans c : Constans.values()) {  
            if (c.getIndex() == index) {  
                return c.value;  
            }  
        }  
        return null;  
    } 
    
}
