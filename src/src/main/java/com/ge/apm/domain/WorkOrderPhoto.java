/*
 */
package newpackage;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "work_order_photo")
public class WorkOrderPhoto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "site_id")
    private int siteId;
    @Basic(optional = false)
    @Column(name = "work_order_id")
    private int workOrderId;
    @Column(name = "photo_id")
    private Integer photoId;

    public WorkOrderPhoto() {
    }

    public WorkOrderPhoto(Integer id) {
        this.id = id;
    }

    public WorkOrderPhoto(Integer id, int siteId, int workOrderId) {
        this.id = id;
        this.siteId = siteId;
        this.workOrderId = workOrderId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(int workOrderId) {
        this.workOrderId = workOrderId;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkOrderPhoto)) {
            return false;
        }
        WorkOrderPhoto other = (WorkOrderPhoto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.WorkOrderPhoto[ id=" + id + " ]";
    }
    
}
