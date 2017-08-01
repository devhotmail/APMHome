package com.ge.apm.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/***
 *  默认：设备分类，资产、IT设备、其他，用户可以自定义分类
 * @author 212593079
 *
 */
@Entity
@Table(name = "v2_device_type")
public class V2_DeviceType extends BaseDomain{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
	private Integer id;
    
    @Column(name = "name")
    @Basic(optional = false)
	private Integer name;
    
    @Column(name = "description")
	private String description;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getName() {
		return name;
	}
	public void setName(Integer name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
